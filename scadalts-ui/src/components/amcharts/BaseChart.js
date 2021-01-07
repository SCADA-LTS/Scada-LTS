/**
 * @fileoverview BaseChart Class Definition.
 * @author <a href="mailto:rjajko@softq.pl">Radoslaw Jajko</a>
 * @version 1.0.0
 *
 * @requires am4core
 * @requires am4charts
 * @requires Axios
 */
import * as am4core from '@amcharts/amcharts4/core';
import * as am4charts from '@amcharts/amcharts4/charts';
import Axios from 'axios';

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
	 * @param {String} chartType [ XYChart | PieChart | GaugeChart | JsonChart] available chart types
	 * @param {String} colors Hex value of base chart color.
	 * @param {String} [domain] Protocol, domain and the address of the API interface
	 * @param {Object} [jsonConfig] Json Chart configuration
	 * @param {String} [jsonChart] [ XYChart | PieChart | GaugeChart] available chart types
	 */
	constructor(
		chartReference,
		chartType,
		colors,
		domain = '.',
		jsonConfig = undefined,
		jsonChart = undefined,
	) {
		if (chartType === 'XYChart') {
			this.chart = am4core.create(chartReference, am4charts.XYChart);
		} else if (chartType === 'PieChart') {
			this.chart = am4core.create(chartReference, am4charts.PieChart);
		} else if (chartType === 'GaugeChart') {
			this.chart = am4core.create(chartReference, am4charts.GaugeChart);
		} else if (chartType === 'JsonChart') {
			if (jsonChart === 'XYChart') {
				this.chart = am4core.createFromConfig(
					jsonConfig,
					chartReference,
					am4charts.XYChart,
				);
			} else if (jsonChart === 'PieChart') {
				this.chart = am4core.createFromConfig(
					jsonConfig,
					chartReference,
					am4charts.PieChart,
				);
			} else if (jsonChart === 'GaugeChart') {
				this.chart = am4core.createFromConfig(
					jsonConfig,
					chartReference,
					am4charts.GaugeChart,
				);
			}
		}
		this.pointPastValues = new Map();
		this.pointCurrentValue = new Map();
		this.liveUpdatePointValues = new Map();
		this.lastUpdate = 0;
		this.lastTimestamp = new Date().getTime();
		this.liveUpdateInterval = 5000;
		this.domain = domain;
		if (jsonConfig != undefined) {
			let colorPallete = [
				am4core.color('#39B54A'),
				am4core.color('#69FF7D'),
				am4core.color('#166921'),
				am4core.color('#690C24'),
				am4core.color('#B53859'),
				am4core.color('#734FC1'),
				am4core.color('#824F1B'),
				am4core.color('#69421B'),
			];
			if (colors !== undefined && colors !== null) {
				colors = colors.split(',');
				if (colors.length > 0) {
					for (let i = colors.length - 1; i >= 0; i--) {
						colorPallete.unshift(am4core.color(colors[i].trim()));
					}
				}
			}
			this.chart.colors.list = colorPallete;
		}
		this.yAxesCount = 0;
	}

	/**
	 * Main method to display chart
	 * Before launch: Load data from API
	 */
	showChart() {
		this.setupChart();
	}

	/**
	 * Download from API DataPoint values from specific time period.
	 * Override this method in children classes to prepare data for displaying in charts
	 *
	 * @param {Number} pointId Data Point ID number
	 * @param {Number} [startTimestamp] Default get values from 1 hour ago
	 * @param {Number} [endTimestamp] Default get values till now
	 */
	loadData(pointId, startTimestamp, endTimestamp, exportId) {
		if (startTimestamp === undefined || startTimestamp === null) {
			startTimestamp = new Date().getTime() - 3600000;
			endTimestamp = new Date().getTime();
		} else if (
			startTimestamp !== undefined &&
			startTimestamp !== null &&
			(endTimestamp === undefined || endTimestamp === null)
		) {
			startTimestamp = BaseChart.convertDate(startTimestamp);
			endTimestamp = new Date().getTime();
			if (isNaN(startTimestamp)) {
				console.warn(
					'Parameter start-date is not valid!\nConnecting to API with default values',
				);
				startTimestamp = new Date().getTime() - 3600000;
			}
		} else if (
			startTimestamp !== undefined &&
			startTimestamp !== null &&
			endTimestamp !== undefined &&
			endTimestamp !== null
		) {
			startTimestamp = new Date(startTimestamp).getTime();
			endTimestamp = new Date(endTimestamp).getTime();
			if (isNaN(startTimestamp) || isNaN(endTimestamp)) {
				console.warn(
					'Parameter [start-date | end-date] are not valid!\nConnecting to API with default values',
				);
				startTimestamp = new Date().getTime() - 3600000;
				endTimestamp = new Date().getTime();
			}
		}
		let url;
		if (exportId) {
			url = `${this.domain}/api/point_value/getValuesFromTimePeriod/xid/${pointId}/${startTimestamp}/${endTimestamp}`;
		} else {
			url = `${this.domain}/api/point_value/getValuesFromTimePeriod/${pointId}/${startTimestamp}/${endTimestamp}`;
		}
		return new Promise((resolve, reject) => {
			try {
				Axios.get(url, {
					timeout: 5000,
					useCredentails: true,
					credentials: 'same-origin',
				})
					.then((response) => {
						resolve(response.data);
					})
					.catch((webError) => {
						reject(webError);
					});
			} catch (error) {
				reject(error);
			}
		});
	}

	/**
	 * Start LiveChart Interval
	 *
	 * @param {Number} refreshRate How often send request for a new data.
	 */
	startLiveUpdate(refreshRate, exportId) {
		this.liveUpdateInterval = setInterval(() => {
			// this.loadLiveData()
			this.refreshPointValues(exportId);
		}, refreshRate);
	}

	/**
	 * Connect with API and parse new data for chart
	 * @deprecated
	 */
	loadLiveData() {
		for (let [k, v] of this.pointCurrentValue) {
			Axios.get(`${this.domain}/api/point_value/getValue/id/${k}`, {
				timeout: 5000,
				useCredentails: true,
				credentials: 'same-origin',
			}).then((response) => {
				if (isNaN(response.data.value)) {
					response.data.value == 'true'
						? (response.data.value = 1)
						: (response.data.value = 0);
				}
				let point = { name: response.data.name, value: response.data.value };
				if (this.liveUpdatePointValues.get(response.data.ts) == undefined) {
					this.liveUpdatePointValues.set(response.data.ts, [point]);
				} else {
					this.liveUpdatePointValues.get(response.data.ts).push(point);
				}
			});
		}
		this.chart.addData(
			BaseChart.prepareChartData(BaseChart.sortMapKeys(this.liveUpdatePointValues)),
		);
		if (this.liveUpdatePointValues != undefined) {
			this.liveUpdatePointValues.clear();
		}
	}

	/**
	 * Get data point data from REST API.
	 *
	 * @param {Number} pointId ID of data point.
	 */
	getPointValue(pointId) {
		return new Promise((resolve, reject) => {
			try {
				Axios.get(`${this.domain}/api/point_value/getValue/id/${pointId}`, {
					timeout: 5000,
					useCredentails: true,
					credentials: 'same-origin',
				})
					.then((resp) => {
						resolve(resp.data);
					})
					.catch((webError) => {
						reject(webError);
					});
			} catch (error) {
				reject(error);
			}
		});
	}

	/**
	 * Get data point data from REST API.
	 *
	 * @param {Number} pointXid Export ID of data point.
	 */
	getPointValueXid(pointXid) {
		return new Promise((resolve, reject) => {
			try {
				Axios.get(`${this.domain}/api/point_value/getValue/${pointXid}`, {
					timeout: 5000,
					useCredentails: true,
					credentials: 'same-origin',
				})
					.then((resp) => {
						resolve(resp.data);
					})
					.catch((webError) => {
						reject(webError);
					});
			} catch (error) {
				reject(error);
			}
		});
	}

	/**
	 *
	 * @param {String} pointId PointExportID or PointID
	 * @param {Number} startTimestamp LastUpdateTime
	 * @param {Boolean} exportId Using XID or ID
	 */
	getPeriodicUpdate(pointId, startTimestamp, exportId) {
		let endTimestamp = new Date().getTime();
		let url;
		if (exportId) {
			url = `${this.domain}/api/point_value/getValuesFromTimePeriod/xid/${pointId}/${startTimestamp}/${endTimestamp}`;
		} else {
			url = `${this.domain}/api/point_value/getValuesFromTimePeriod/${pointId}/${startTimestamp}/${endTimestamp}`;
		}
		return new Promise((resolve, reject) => {
			try {
				Axios.get(url, {
					timeout: 5000,
					useCredentails: true,
					credentials: 'same-origin',
				})
					.then((response) => {
						resolve(response.data);
					})
					.catch((webError) => {
						reject(webError);
					});
			} catch (error) {
				reject(error);
			}
		});
	}

	/**
	 * Stategy how to parse data received from API server.
	 *
	 * @param {Object} pointValue Object of recived point data value.
	 * @param {String} pointName Name of datapoint.
	 * @param {Map} referencedArray Map of values to which we want to save data. [pointPastValues or liveUpdatePoints]
	 */
	addValue(pointValue, pointName, referencedArray) {
		if (isNaN(pointValue.value)) {
			pointValue.value == 'true' ? (pointValue.value = 1) : (pointValue.value = 0);
		}
		let point = { name: pointName, value: pointValue.value };
		if (referencedArray.get(pointValue.ts) == undefined) {
			referencedArray.set(pointValue.ts, [point]);
		} else {
			referencedArray.get(pointValue.ts).push(point);
		}
	}

	/**
	 * For each defined point get current value and update chart when all request has been recived.
	 */
	refreshPointValues(exportId) {
		let pointData = [];
		for (let [k, v] of this.pointCurrentValue) {
			pointData.push(
				this.getPeriodicUpdate(k, this.lastTimestamp, exportId).then((data) => {
					data.values.forEach((e) => {
						this.addValue(e, data.name, this.liveUpdatePointValues);
					});
				}),
			);
		}
		Promise.all(pointData).then(() => {
			let lastData = BaseChart.prepareChartData(this.liveUpdatePointValues);
			if (lastData.length > 0) {
				if (lastData[lastData.length - 1].date > this.lastUpdate) {
					this.chart.addData(lastData, 1);
					this.lastTimestamp = new Date().getTime();
					this.lastUpdate = lastData[lastData.length - 1].date;
					this.liveUpdatePointValues.clear();
					if (lastData != undefined) {
						// console.debug(lastData);
						// lastData.clear();
					}
				}
			}
		});
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
		this.chart.data = BaseChart.prepareChartData(
			BaseChart.sortMapKeys(this.pointPastValues),
		);
		this.createAxisX('DateAxis', null);
		this.createAxisY();
		this.createScrollBarsAndLegend();
		this.createExportMenu();
		for (let [k, v] of this.pointCurrentValue) {
			this.createSeries('StepLine', 'date', v.name, v.name);
		}
	}

	/**
	 * Prepare series for chart.
	 *
	 * @param {String} seriesType [ Column | Pie | Line | StepLine ] types
	 * @param {String} seriesValueX Category name or "date" field in data.
	 * @param {String} seriesValueY Which values disply in series
	 * @param {String} seriesName Name of this series.
	 * @param {string} suffix Additional suffix for series units. [square meteters etc.]
	 */
	createSeries(seriesType, seriesValueX, seriesValueY, seriesName, suffix = '') {
		let series;
		if (seriesType === 'Column') {
			series = this.chart.series.push(new am4charts.ColumnSeries());
		} else if (seriesType === 'Line') {
			series = this.chart.series.push(new am4charts.LineSeries());
		} else if (seriesType === 'StepLine') {
			series = this.chart.series.push(new am4charts.StepLineSeries());
			series.startLocation = 0.5;
		} else if (seriesType === 'Pie') {
			series = this.chart.series.push(new am4charts.PieSeries());
		}

		if (seriesType === 'Column') {
			series.dataFields.categoryX = seriesValueX;
			series.dataFields.valueY = seriesValueY;
			series.columns.template.tooltipText = '{valueY.value}';
			series.columns.template.tooltipY = 0;
			series.columns.template.strokeOpacity = 0;
		} else if (seriesType === 'Pie') {
			series.dataFields.value = seriesValueY;
			series.dataFields.category = seriesValueX;
			series.slices.template.strokeWidth = 2;
			series.slices.template.strokeOpacity = 1;
		} else {
			series.dataFields.dateX = seriesValueX;
			series.dataFields.valueY = seriesValueY;
			if (suffix.trim().startsWith('[')) {
				suffix = `[${suffix}]`;
			}
			series.tooltipText = '{name}: [bold]{valueY}[/] ' + suffix;
			series.tooltip.background.cornerRadius = 20;
			series.tooltip.background.strokeOpacity = 0;
			series.tooltip.pointerOrientation = 'vertical';
			series.tooltip.label.minWidth = 40;
			series.tooltip.label.minHeight = 40;
			series.tooltip.label.textAlign = 'middle';
			series.tooltip.label.textValign = 'middle';
			series.strokeWidth = 3;
			series.fillOpacity = 0.3;
			series.minBulletDistance = 15;
			let bullet = series.bullets.push(new am4charts.CircleBullet());
			bullet.circle.strokeWidth = 2;
			bullet.circle.radius = 5;
			bullet.circle.fill = am4core.color('#fff');
		}

		series.name = seriesName;

		if (this.chart.scrollbarX) {
			this.chart.scrollbarX.series.push(series);
		}

		return series;
	}

	/**
	 * Create X data Axis for chart
	 * @param {String} axisType [ValueAxis | DateAxis | CategoryAxis] Specified types for different chart axes
	 * @param {String} category When Category Axis has been chosen set the name of category to display.
	 */
	createAxisX(axisType, category) {
		let axis;
		if (axisType === 'ValueAxis') {
			axis = this.chart.xAxes.push(new am4charts.ValueAxis());
			axis.min = 0;
			axis.max = 100;
			axis.renderer.grid.template.strokeOpacity = 0.3;
		} else if (axisType === 'DateAxis') {
			axis = this.chart.xAxes.push(new am4charts.DateAxis());
			axis.dateFormats.setKey('second', 'HH:mm:ss');
			axis.dateFormats.setKey('minute', 'HH:mm:ss');
			axis.dateFormats.setKey('hour', 'HH:mm');
			axis.dateFormats.setKey('day', 'MMM dd');
			axis.periodChangeDateFormats.setKey('hour', '[bold]dd MMM HH:mm[/]');
			axis.periodChangeDateFormats.setKey('day', '[bold]MMM[/] dd');
			axis.periodChangeDateFormats.setKey('month', '[bold]yyyy[/] MMM');
		} else if (axisType === 'CategoryAxis') {
			axis = this.chart.xAxes.push(new am4charts.CategoryAxis());
			axis.dataFields.category = category;
			axis.renderer.grid.template.location = 0;
			axis.renderer.minGridDistance = 30;
			axis.renderer.labels.template.horizontalCenter = 'right';
			axis.renderer.labels.template.verticalCenter = 'middle';
			axis.renderer.labels.template.rotation = 315;
			axis.tooltip.disabled = true;
			axis.renderer.minHeight = 110;
		}
	}

	/**
	 * Create Y Value Axis for chart
	 * @param {Map} textLabels Map with key value pair. In keys are values to be converted into label text
	 * @return yAxis definition.
	 */
	createAxisY(textLabels) {
		let axis;
		axis = this.chart.yAxes.push(new am4charts.ValueAxis());
		axis.tooltip.disabled = false;
		axis.renderer.opposite = Boolean(this.yAxesCount % 2);

		//TextRender
		if (textLabels !== undefined) {
			if (textLabels.size > 0) {
				axis.renderer.labels.template.adapter.add('text', function (text) {
					if (textLabels.get(text) !== undefined) {
						return textLabels.get(text);
					} else {
						return '';
					}
				});
			}
		}
		this.yAxesCount = this.yAxesCount + 1;
		return axis;
	}

	/**
	 * Add single line range gudided by label to chart.
	 * @param value Value of line for yAxis
	 * @param color Color of this line
	 * @param label Label for this line (eg. 'average count')
	 */
	addRangeValue(value, color, label) {
		if (color === undefined || color === '') {
			color = '#FF150A';
		}
		if (label === undefined) {
			label = '';
		}
		let range = this.chart.yAxes.getIndex(0).axisRanges.create();
		range.value = value;
		range.grid.stroke = am4core.color(color);
		range.grid.strokeWidth = 2;
		range.grid.strokeOpacity = 1;
		range.label.inside = true;
		range.label.text = label;
		range.label.fill = range.grid.stroke;
		range.label.verticalCenter = 'bottom';
	}

	/**
	 * Create specific elements of chart.
	 *
	 * @param {Boolean} [scrollbarX=true] Show scrollbar for xAxes
	 * @param {Boolean} [scrollbarY=false] Show scrollbar for yAxes
	 * @param {Boolean} [legend=true] Show chart legend
	 * @param {Boolean} [cursor=true] Show cursor over the chart
	 */
	createScrollBarsAndLegend(
		scrollbarX = true,
		scrollbarY = false,
		legend = true,
		cursor = true,
	) {
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
			this.chart.cursor.behavior = 'panXY';
		}
	}

	/**
	 * Add export possibility to chart. Save chart as an image or export chart data to *.csv or *.xlsx format.
	 *
	 * @param {Boolean} [enabled = true] is Export menu enabled in this chart.
	 * @param {String} [filePrefix = "Scada_Chart"] File name to which save exported chart data.
	 */
	createExportMenu(enabled = true, filePrefix = 'Scada_Chart') {
		if (enabled) {
			this.chart.exporting.menu = new am4core.ExportMenu();
			this.chart.exporting.menu.align = 'right';
			this.chart.exporting.menu.vetricalAlign = 'top';
			this.chart.exporting.filePrefix = filePrefix + '_' + String(new Date().getTime());
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
		var sortByKeys = (a, b) => (a[0] > b[0] ? 1 : -1);
		return new Map([...map].sort(sortByKeys));
	}

	/**
	 *
	 * @param {Map} map Sorted keys in chronological order TimeValueMap
	 */
	static prepareChartData(map) {
		let data = []; // [{date:<time>, <datapointName>:<datapointValue>}]
		map.forEach(function (value, key) {
			let jsonString = '{ "date":' + key;
			value.forEach((e) => {
				if (!isNaN(Number(e.value))) {
					jsonString = jsonString + ', "' + e.name + '":' + e.value;
				}
			});
			jsonString = jsonString + '}';
			data.push(JSON.parse(jsonString));
		});
		return data;
	}

	/**
	 * Util for converting from text time to specific timestamp
	 *
	 * @param {String} dateText Text based time period (date format or written format)
	 * @returns Calculated timestamp
	 */
	static convertDate(dateText) {
		let date = new Date(dateText);
		if (date == 'Invalid Date') {
			date = dateText.split('-');
			if (date.length === 2) {
				let dateNow = new Date();
				let multiplier = 1;
				switch (date[1]) {
					case 'hour':
					case 'hours':
						multiplier = 1000 * 3600;
						break;
					case 'day':
					case 'days':
						multiplier = 1000 * 3600 * 24;
						break;
					case 'week':
					case 'weeks':
						multiplier = 1000 * 3600 * 24 * 7;
						break;
					case 'month':
					case 'months':
						multiplier = 1000 * 3600 * 24 * 31;
						break;
				}
				return dateNow.getTime() - Number(date[0]) * multiplier;
			} else {
				console.warn(
					"Not vaild date. Use for example ['1-day' | '2-months' | '3-days']\nReturning default value!",
				);
				return dateNow.getTime() - 3600000;
			}
		} else {
			return date.getTime();
		}
	}
}
