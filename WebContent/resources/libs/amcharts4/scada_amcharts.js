/**
 * Amchart JavaScript module
 * 
 * @author Radek Jajko
 * 
 * To initialize this component required are specified elements definitions:
 * <div id="amChartDiv"></div>
 * var API_NAME
 * $get("refreshPeriodValue")
 * $get("refreshPeriodType")
 * $get("chartPeriodValue")
 * $get("chartPeriodType")
 */

var chart;
var liveChart = true;
var interval;
var pointPastValues = new Map();
var pointCurrentState = new Map();
var lastUpdate = new Map();
var chartTypes = ["0", "1", "0"]; //Binary // Numeric //Multistate

var chartSettings = {
    "export": {
        "enabled": true,
        "horizontalPosition": "left",
        "verticalPosition": "top",
        "filePrefix": "ScadaLTS_" + new Date().getTime()
    },
    "xDateAxis": {
        "title": "Time",
        "skipEmptyPeriods": false,
        "timeUnit": "second",
        "count": 1,
        "maxGridDistance": 60,
    },
    "ySeriesAxis": {
        "paddingTop": 0.05,
        "paddingBottom": 0.1
    },
    "styles": {
        "colors": [
            am4core.color("#39B54A"),
            am4core.color("#69FF7D"),
            am4core.color("#166921"),
            am4core.color("#690C24"),
            am4core.color("#B53859"),
        ],
        "series": {
            "strokeWidth": 3,
            "minBulletDistance": 15,
            "fillOpacity": 0.1,
            "tensionX": 0.9,
            "connect": true,
        },
        "bullets": {
            "strokeWidth": 2,
            "radius": 5,
            "fill": am4core.color("#fff")
        }
    }
}

am4core.ready();

/**
 * Run after scadaAmChartInitPoint() <- data loader.
 * Initialize chart component after loading data from API
 */
function scadaAmChartInit(adjustedStart = 0, adjustedEnd = 1) {

    am4core.useTheme(am4themes_animated);

    chart = am4core.create("amChartDiv", am4charts.XYChart);

    chart.dateFormatter.inputDateFormat = "yyyy-MM-dd-HH-mm-ss";
    var dateAxis = chart.xAxes.push(new am4charts.DateAxis());

    prepareDateAxisRenderer(dateAxis);
    prepareCursor(dateAxis);
    prepareScrollbarsAndLegned();
    if (chartSettings.export.enabled) {
        prepareExport();
    }

    // Create series
    pointCurrentState.forEach(function (value, key) {
        createAxisAndSeries(value.name, value.type, value.suffix);
    })

    chart.data = prepareChartData(sortMapKeys(pointPastValues));

    chart.events.on("ready", function() {
        dateAxis.zoom({start:adjustedStart,end:adjustedEnd})
    })
}

/**
 * Function to be overrided inside app
 * runs getDataPointValuesFromTime
 */
function scadaAmChartInitPoint() {
    //To run this clear chart and gather data from api
}

/**
 * Start live chart update
 *
 * Refresh chart data using REST API. Request for specific dataPoints every refresh rate.
 */
function scadaAmChartLiveUpdatePoints() {

    let refreshInterval = Number($get("refreshPeriodValue")) * 1000
    if ($get("refreshPeriodType") == "2") { refreshInterval = refreshInterval * 60; }

    interval = setInterval(function () {
        pointCurrentState.forEach(function (value, key) {
            jQuery.get(API_NAME + "/api/point_value/getValue/id/" + key, function (data, status) {
                if (status == "success") {
                    data = JSON.parse(data);
                    //Do the same as it was in getDataPointValuesFromTime()
                    if (isNaN(data.value)) {
                        data.value == "true" ? data.value = 1 : data.value = 0;
                    }
                    let point = { 'name': data.name, "value": data.value };
                    if (lastUpdate.get(data.ts) == undefined) {
                        lastUpdate.set(data.ts, [point]);
                    } else {
                        lastUpdate.get(data.ts).push(point);
                    }
                }
            })
        })
        setTimeout(function () {
            chart.addData(prepareChartData(sortMapKeys(lastUpdate)))
            lastUpdate.clear();
        }, 500)

    }, refreshInterval);
}

/**
 * Get point values from time
 *
 * Load data from REST API and populate dataPoint variables.
 *
 * @param {number} pointId - DataPoint ID in database
 * @param {number} startTimestamp - Begining timestamp (default: 1 day)
 * @param {number} endTimestamp - Ending timestamp (default: now)
 */
function scadaAmChartGetDataPointValuesFromTime(pointId, startTimestamp = new Date().getTime() - (24 * 60 * 60 * 1000), endTimestamp = new Date().getTime()) {
    jQuery.get(API_NAME + "/api/point_value/getValuesFromTimePeriod/" + pointId + "/" + startTimestamp + "/" + endTimestamp, function (data, status) {
        if (status == "success") {
            data = JSON.parse(data)
            if (pointCurrentState.get(pointId) == undefined) {
                pointCurrentState.set(pointId, { "name": data.name, "suffix": data.textRenderer.suffix, "type": data.type });
            }
            data.values.forEach(e => {
                //Validate binary values and transform to numeric values
                if (isNaN(e.value)) {
                    e.value == "true" ? e.value = 1 : e.value = 0;
                }
                let point = { "name": data.name, "value": e.value };
                // If point value in time do not exist - create new one
                if (pointPastValues.get(e.ts) == undefined) {
                    pointPastValues.set(e.ts, [point])
                } else {
                    pointPastValues.get(e.ts).push(point)
                }
            })
        } else { alert(status) }
    })
}

// --- UTILS --- //
function sortMapKeys(map) {
    var sortByKeys = (a, b) => a[0] > b[0] ? 1 : -1
    return new Map([...map].sort(sortByKeys))
}

/**
 * Clear chart data before starting another one
 */
function scadaAmChartClearChart() {
    clearInterval(interval);
    pointCurrentState.clear();
    pointPastValues.clear();
    lastUpdate.clear();
}

/**
 * Convert from Map structure to amChart data interface
 *
 * @param {Map} map - Values map to be converted. 
 * @return {Array} amChart data structure.
 */
function prepareChartData(map) {
    let data = []; // [{date:<time>, <datapointName>:<datapointValue>}]
    map.forEach(function (value, key) {
        let jsonString = '{ "date":' + key
        value.forEach(e => {
            if (!isNaN(Number(e.value))) {
                jsonString = jsonString + ', "' + e.name + '":' + e.value;
            }
        })
        jsonString = jsonString + '}';
        data.push(JSON.parse(jsonString));
    });
    return data;
}

function scadaAmChartCalculatePeriod() {
    let period
    if (!isNaN($get("chartPeriodValue")) && $get("chartPeriodValue") > 0) {
        period = $get("chartPeriodValue") * 1000 * 60;
    } else {
        period = 1 * 1000 * 60;
    }

    let type = $get("chartPeriodType");

    if (type > 2)
        period *= 60;
    if (type > 3)
        period *= 24;
    if (type == 5)
        period *= 7;
    else if (type == 6)
        period *= 30;
    else if (type == 7)
        period *= 365;

    return period;
}

function getChartTypeSeries(type) {
    var series;
    if (type == "Numeric") {
        if (chartTypes[1] == "1") {
            series = chart.series.push(new am4charts.LineSeries());
        } else {
            series = chart.series.push(new am4charts.StepLineSeries());
        }
    } else if (type == "Multistate") {
        if (chartTypes[2] == "1") {
            series = chart.series.push(new am4charts.LineSeries());
        } else {
            series = chart.series.push(new am4charts.StepLineSeries());
        }
    } else if (type == "Binary") {
        if (chartTypes[0] == "1") {
            series = chart.series.push(new am4charts.LineSeries());
        } else {
            series = chart.series.push(new am4charts.StepLineSeries());
        }
    } else {
        console.error("DataPoint chart type unrecoginzed!")
    }
    return series;
}

//Create series
function createAxisAndSeries(field, type, suffix) {

    //Generate yAxes
    var yAxis;
    if (chart.yAxes.values.length > 0) {
        yAxis = chart.yAxes.values.find(x => x.title.text === type);
    } else {
        yAxis = undefined;
    }
    if (yAxis === undefined) {
        yAxis = chart.yAxes.push(new am4charts.ValueAxis());
        yAxis.title.text = type;
        yAxis.extraMin = chartSettings.ySeriesAxis.paddingTop;
        yAxis.extraMax = chartSettings.ySeriesAxis.paddingBottom;
        if (chart.yAxes.values.length % 2 == 0) {
            yAxis.renderer.opposite = true;
        }
    }

    //Generate ChartType series
    let series = getChartTypeSeries(type);

    //Config chart series
    series.dataFields.valueY = field;
    series.dataFields.dateX = "date";
    series.name = field;
    series.tooltipText = "{name}: [bold]{valueY}" + suffix + "[/]";
    series.strokeWidth = chartSettings.styles.series.strokeWidth;
    series.minBulletDistance = chartSettings.styles.series.minBulletDistance;
    series.fillOpacity = chartSettings.styles.series.fillOpacity;
    series.tensionX = chartSettings.styles.series.tensionX;
    series.connect = chartSettings.styles.series.connect;
    series.yAxis = yAxis;

    let bullet = series.bullets.push(new am4charts.CircleBullet());
    bullet.circle.strokeWidth = chartSettings.styles.bullets.strokeWidth;
    bullet.circle.radius = chartSettings.styles.bullets.radius;
    bullet.circle.fill = chartSettings.styles.bullets.fill;

    var bullethover = bullet.states.create("hover");
    bullethover.properties.scale = 1.3;

    series.tooltip.background.cornerRadius = 20;
    series.tooltip.background.strokeOpacity = 0;
    series.tooltip.pointerOrientation = "vertical";
    series.tooltip.label.minWidth = 40;
    series.tooltip.label.minHeight = 40;
    series.tooltip.label.textAlign = "middle";
    series.tooltip.label.textValign = "middle";

    chart.scrollbarX.series.push(series);

}

function prepareDateAxisRenderer(xAxis) {
    chart.colors.list = chartSettings.styles.colors;
    xAxis.renderer.maxGridDistance = chartSettings.xDateAxis.maxGridDistance;
    xAxis.baseInterval = {
        timeUnit: chartSettings.xDateAxis.timeUnit,
        count: chartSettings.xDateAxis.count
    }
    xAxis.dateFormats.setKey("second", "HH:mm:ss")
    xAxis.dateFormats.setKey("minute", "HH:mm:ss")
    xAxis.dateFormats.setKey("hour", "HH:mm")
    xAxis.dateFormats.setKey("day", "MMM dd")
    xAxis.periodChangeDateFormats.setKey("hour", "[bold]dd MMM HH:mm[/]");
    xAxis.periodChangeDateFormats.setKey("day", "[bold]MMM[/] dd");
    xAxis.periodChangeDateFormats.setKey("month", "[bold]yyyy[/] MMM");
    xAxis.skipEmptyPeriods = chartSettings.xDateAxis.skipEmptyPeriods;
}

function prepareExport() {
    chart.exporting.menu = new am4core.ExportMenu();
    chart.exporting.menu.align = chartSettings.export.horizontalPosition;
    chart.exporting.menu.vetricalAlign = chartSettings.export.verticalPosition;
    chart.exporting.filePrefix = chartSettings.export.filePrefix;
}

function prepareCursor(xAxis) {
    chart.cursor = new am4charts.XYCursor();
    chart.cursor.behavior = "panXY";
    chart.cursor.xAxis = xAxis;
}

function prepareScrollbarsAndLegned() {
    chart.scrollbarY = new am4core.Scrollbar();
    chart.scrollbarY.parent = chart.leftAxesContainer;
    chart.scrollbarY.toBack();

    chart.scrollbarX = new am4charts.XYChartScrollbar();
    chart.scrollbarX.parent = chart.bottomAxesContainer;

    chart.legend = new am4charts.Legend();
}
