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
var chartTypes = ["0","1","0"]; //Binary // Numeric //Multistate


am4core.ready();



/**
 * Run after scadaAmChartInitPoint() <- data loader.
 * Initialize chart component after loading data from API
 */
function scadaAmChartInit() {
    am4core.useTheme(am4themes_animated);
    chart = am4core.create("amChartDiv", am4charts.XYChart);

    chart.dateFormatter.inputDateFormat = "yyyy-MM-dd-HH-mm-ss";
    var dateAxis = chart.xAxes.push(new am4charts.DateAxis());
    chart.yAxes.push(new am4charts.ValueAxis());
    dateAxis.renderer.maxGridDistance = 60;
    
    // Create series
    pointCurrentState.forEach(function (value, key) {
        createAxisAndSeries(value.name, value.type, value.suffix);
    })

    // Make a panning cursor
    chart.cursor = new am4charts.XYCursor();
    chart.cursor.behavior = "panXY";
    chart.cursor.xAxis = dateAxis;

    // Create vertical scrollbar and place it before the value axis
    chart.scrollbarY = new am4core.Scrollbar();
    chart.scrollbarY.parent = chart.leftAxesContainer;
    chart.scrollbarY.toBack();

    // Create a horizontal scrollbar with previe and place it underneath the date axis
    chart.scrollbarX = new am4charts.XYChartScrollbar();
    // chart.scrollbarX.series.push(chart.series.get(0));
    chart.scrollbarX.parent = chart.bottomAxesContainer;
    chart.legend = new am4charts.Legend();

    chart.events.on("ready", function () {
        dateAxis.zoom({ start: 1 / 15, end: 1 });
    });

    chart.data = prepareChartData(sortMapKeys(pointPastValues));
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
        } else {
            alert(status)
        }
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
                jsonString = jsonString + ', "' + e.name + '":' + e.value
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
    if(type == "Numeric") {
        if(chartTypes[1] == "1") {
            series = chart.series.push(new am4charts.LineSeries());
        } else {
            series = chart.series.push(new am4charts.StepLineSeries());
        }
    } else if (type == "Multistate") {
        if(chartTypes[2] == "1") {
            series = chart.series.push(new am4charts.LineSeries());
        } else {
            series = chart.series.push(new am4charts.StepLineSeries());
        }
    } else if (type == "Binary") {
        if(chartTypes[0] == "1") {
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

    let series = getChartTypeSeries(type);

    series.dataFields.valueY = field;
    series.dataFields.dateX = "date";
    series.name = field;
    series.tooltipText = "{name}: [bold]{valueY}" + suffix + "[/]";
    series.strokeWidth = 2;
    series.minBulletDistance = 15;

    let bullet = series.bullets.push(new am4charts.CircleBullet());
    bullet.circle.strokeWidth = 2;
    bullet.circle.radius = 4;
    bullet.circle.fill = am4core.color("#fff");

    var bullethover = bullet.states.create("hover");
    bullethover.properties.scale = 1.3;

    series.tooltip.background.cornerRadius = 20;
    series.tooltip.background.strokeOpacity = 0;
    series.tooltip.pointerOrientation = "vertical";
    series.tooltip.label.minWidth = 40;
    series.tooltip.label.minHeight = 40;
    series.tooltip.label.textAlign = "middle";
    series.tooltip.label.textValign = "middle";

}

