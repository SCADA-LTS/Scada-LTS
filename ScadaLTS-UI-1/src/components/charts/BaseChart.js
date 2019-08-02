/**
 * @fileoverview BaseChart Class Definition.
 * @author <a href="mailto:rjajko@softq.pl">Radoslaw Jajko</a>
 * @version 1.0.0
 * 
 * @requires am4core
 * @requires am4charts
 * @requires Axios
 */
import * as am4core from "@amcharts/amcharts4/core";
import * as am4charts from "@amcharts/amcharts4/charts";
import Axios from "axios";

/**
 * BaseChart class allows to create many of am4chart chart types. Base chart is based on line chart
 * but it could be extended by child classes to handle more complex chart definitions. 
 * @class
 */
export default class BaseChart {

    //xcopy .\dist\static C:\services\tomcat7.0\webapps\ScadaLTS\resources\new-ui\ /E /K /Y 
    /**
     * 
     * @param {any} chartReference Id of DOM element where this char will be initialized
     * @param {String} chartType [ XYChart | PieChart | GaugeChart] available chart types
     * @param {String} [domain] Protocol, domain and the address of the API interface
     */
    constructor(chartReference, chartType, domain = 'http://localhost:8080/ScadaLTS') {

        if (chartType === "XYChart") {
            this.chart = am4core.create(chartReference, am4charts.XYChart)
        } else if (chartType === "PieChart") {
            this.chart = am4core.create(chartReference, am4charts.PieChart);
        } else if (chartType === "GaugeChart") {
            this.chart = am4core.create(chartReference, am4charts.GaugeChart);
        }
        this.pointPastValues = new Map();
        this.pointCurrentValue = new Map();
        this.liveUpdatePointValues = new Map();
        this.liveUpdateInterval = 5000;
        this.domain = domain;
        this.chart.colors.list = [
            am4core.color("#39B54A"),
            am4core.color("#69FF7D"),
            am4core.color("#166921"),
            am4core.color("#690C24"),
            am4core.color("#B53859"),
        ];
    }

    /**
     * Main method to display chart
     * Before launch: Load data from API
     */
    showChart() {
        this.setupChart()
    }

    /**
     * Download from API DataPoint values from specific time period.
     * Override this method in children classes to prepare data for displaying in charts
     * 
     * @param {Number} pointId Data Point ID number
     * @param {Number} [startTimestamp] Default get values from 1 hour ago
     * @param {Number} [endTimestamp] Default get values till now
     */
    loadData(pointId, startTimestamp = new Date().getTime() - 3600000, endTimestamp = new Date().getTime()) {
        return new Promise((resolve, reject) => {
            let api = '/api/point_value/getValuesFromTimePeriod/';
            try {
                Axios.get(this.domain + api + pointId + '/' + startTimestamp + '/' + endTimestamp, { timeout: 5000, useCredentails: true, credentials: 'same-origin' }).then(response => {
                    resolve(response.data);
                }).catch(webError => {
                    reject(webError)
                })
            } catch (error) {
                reject(error)
            }
        })
    }

    /**
     * Start LiveChart Interval
     * 
     * @param {Number} refreshRate How often send request for a new data.
     */
    startLiveUpdate(refreshRate) {
        this.liveUpdateInterval = setInterval(() => {
            this.loadLiveData()
        }, refreshRate);
    }

    /**
     * Connect with API and parse new data for chart
     */
    loadLiveData() {

        let api = '/api/point_value/getValue/id/';
        for (let [k, v] of this.pointCurrentValue) {
            Axios.get(this.domain + api + k, { timeout: 5000, useCredentails: true, credentials: 'same-origin' }).then(response => {
                if (isNaN(response.data.value)) {
                    response.data.value == "true" ? response.data.value = 1 : response.data.value = 0;
                }
                let point = { "name": response.data.name, "value": response.data.value };
                if (this.liveUpdatePointValues.get(response.data.ts) == undefined) {
                    this.liveUpdatePointValues.set(response.data.ts, [point]);
                } else {
                    this.liveUpdatePointValues.get(response.data.ts).push(point);
                }
            })
        }
        this.chart.addData(BaseChart.prepareChartData(BaseChart.sortMapKeys(this.liveUpdatePointValues)))
        this.liveUpdatePointValues.clear();
    }

    /**
     * Clear all data associated with this chart.
     */
    stopLiveUpdate() {
        clearInterval(this.liveUpdateInterval);
        this.pointCurrentValue.clear();
        this.pointPastValues.clear();
        this.liveUpdatePointValues.clear();
    }

    /**
     * When all point data has been downloaded, add this to chart data.
     */
    setupChart() {
        this.chart.data = BaseChart.prepareChartData(BaseChart.sortMapKeys(this.pointPastValues));
        this.createAxisX("DateAxis", null);
        this.createAxisY();
        this.createScrollBarsAndLegend();
        this.createExportMenu();
        for (let [k, v] of this.pointCurrentValue) {
            this.createSeries("StepLine", "date", v.name, v.name)
        }
    }


    /**
     * Prepare series for chart.
     * 
     * @param {String} seriesType [ Column | Pie | Line | StepLine ] types
     * @param {String} seriesValueX Category name or "date" field in data. 
     * @param {String} seriesValueY Which values disply in series
     * @param {String} seriesName Name of this series. 
     */
    createSeries(seriesType, seriesValueX, seriesValueY, seriesName) {
        let series;
        if (seriesType === "Column") {
            series = this.chart.series.push(new am4charts.ColumnSeries());
        } else if (seriesType === "Line") {
            series = this.chart.series.push(new am4charts.LineSeries());
        } else if (seriesType === "StepLine") {
            series = this.chart.series.push(new am4charts.StepLineSeries());
        } else if (seriesType === "Pie") {
            series = this.chart.series.push(new am4charts.PieSeries());
        }

        if (seriesType === "Column") {
            series.dataFields.categoryX = seriesValueX;
            series.dataFields.valueY = seriesValueY;
            series.columns.template.tooltipText = "{valueY.value}";
            series.columns.template.tooltipY = 0;
            series.columns.template.strokeOpacity = 0;
        } else if (seriesType === "Pie") {
            series.dataFields.value = seriesValueY;
            series.dataFields.category = seriesValueX;
            series.slices.template.strokeWidth = 2;
            series.slices.template.strokeOpacity = 1;
        } else {
            series.dataFields.dateX = seriesValueX;
            series.dataFields.valueY = seriesValueY;
            series.tooltipText = "{name}: [bold]{valueY}[/]";
            series.tooltip.background.cornerRadius = 20;
            series.tooltip.background.strokeOpacity = 0;
            series.tooltip.pointerOrientation = "vertical";
            series.tooltip.label.minWidth = 40;
            series.tooltip.label.minHeight = 40;
            series.tooltip.label.textAlign = "middle";
            series.tooltip.label.textValign = "middle";
            series.strokeWidth = 3;
            series.fillOpacity = 0.3;
            series.tensionX = 0.9;
            series.minBulletDistance = 15;
            let bullet = series.bullets.push(new am4charts.CircleBullet());
            bullet.circle.strokeWidth = 2;
            bullet.circle.radius = 5;
            bullet.circle.fill = am4core.color("#fff");
        }

        series.name = seriesName;

        if (this.chart.scrollbarX) {
            this.chart.scrollbarX.series.push(series);
        }

    }

    /**
     * Create X data Axis for chart
     * @param {String} axisType [ValueAxis | DateAxis | CategoryAxis] Specified types for different chart axes
     * @param {String} category When Category Axis has been chosen set the name of category to display.
     */
    createAxisX(axisType, category) {
        let axis;
        if (axisType === "ValueAxis") {
            axis = this.chart.xAxes.push(new am4charts.ValueAxis());
            axis.min = 0;
            axis.max = 100;
            axis.renderer.grid.template.strokeOpacity = 0.3;
        } else if (axisType === "DateAxis") {
            axis = this.chart.xAxes.push(new am4charts.DateAxis());
            axis.dateFormats.setKey("second", "HH:mm:ss")
            axis.dateFormats.setKey("minute", "HH:mm:ss")
            axis.dateFormats.setKey("hour", "HH:mm")
            axis.dateFormats.setKey("day", "MMM dd")
            axis.periodChangeDateFormats.setKey("hour", "[bold]dd MMM HH:mm[/]");
            axis.periodChangeDateFormats.setKey("day", "[bold]MMM[/] dd");
            axis.periodChangeDateFormats.setKey("month", "[bold]yyyy[/] MMM");
        } else if (axisType === "CategoryAxis") {
            axis = this.chart.xAxes.push(new am4charts.CategoryAxis());
            axis.dataFields.category = category;
            axis.renderer.grid.template.location = 0;
            axis.renderer.minGridDistance = 30;
            axis.renderer.labels.template.horizontalCenter = "right";
            axis.renderer.labels.template.verticalCenter = "middle";
            axis.renderer.labels.template.rotation = 315;
            axis.tooltip.disabled = true;
            axis.renderer.minHeight = 110;

        }
    }

    /**
     * Create Y Value Axis for chart
     */
    createAxisY() {
        let axis;
        axis = this.chart.yAxes.push(new am4charts.ValueAxis());
        axis.tooltip.disabled = false;
    }

    /**
     * Create specific elements of chart. 
     * 
     * @param {Boolean} [scrollbarX=true] Show scrollbar for xAxes
     * @param {Boolean} [scrollbarY=false] Show scrollbar for yAxes
     * @param {Boolean} [legend=true] Show chart legend
     * @param {Boolean} [cursor=true] Show cursor over the chart
     */
    createScrollBarsAndLegend(scrollbarX = true, scrollbarY = false, legend = true, cursor = true) {
        if (scrollbarX) {
            this.chart.scrollbarX = new am4charts.XYChartScrollbar();
            this.chart.scrollbarX.parent = this.chart.bottomAxesContainer;
        }
        if (scrollbarY) {
            this.chart.scrollbarY = new am4core.Scrollbar();
            this.chart.scrollbarY.parent = this.chart.leftAxesContainer;
        }
        if (legend) {
            this.chart.legend = new am4charts.Legend();
        }
        if (cursor) {
            this.chart.cursor = new am4charts.XYCursor();
            this.chart.cursor.behavior = "panXY";
        }
    }

    /**
     * Add export possibility to chart. Save chart as an image or export chart data to *.csv or *.xlsx format. 
     * 
     * @param {Boolean} [enabled = true] is Export menu enabled in this chart.
     * @param {String} [filePrefix = "Scada_Chart"] File name to which save exported chart data.
     */
    createExportMenu(enabled = true, filePrefix = "Scada_Chart") {
        if (enabled) {
            this.chart.exporting.menu = new am4core.ExportMenu();
            this.chart.exporting.menu.align = "right"
            this.chart.exporting.menu.vetricalAlign = "top"
            this.chart.exporting.filePrefix = filePrefix + "_" + String(new Date().getTime());
        }
    }

    /**
     * Improving  perfromance of chart.  Display only a points every "step" pixels omitting this between. 
     * It is useful for charts presenting huge amount of data. For example charts displaying values from one month period. 
     * 
     * @param {Number} step - Ommit all line point if they are closer than "step" pixels to the last point drawn 
     */
    static setPolylineStep(step) {
        am4core.options.minPolylineStep = step;
    }

    /**
     * Order values stored inside Map by keys (key == timestamp)
     * 
     * @param {Map} map Scada Data Point Values Map
     */
    static sortMapKeys(map) {
        var sortByKeys = (a, b) => a[0] > b[0] ? 1 : -1
        return new Map([...map].sort(sortByKeys))
    }

    /**
     * 
     * @param {Map} map Sorted keys in chronological order TimeValueMap 
     */
    static prepareChartData(map) {
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


}
