import * as am4core from '@amcharts/amcharts4/core';
import * as am4charts from '@amcharts/amcharts4/charts';
import am4themes_animated from '@amcharts/amcharts4/themes/animated';

import BaseChart from './BaseChart';

am4core.useTheme(am4themes_animated);

export default class JsonChart extends BaseChart {
	constructor(chartReference, color, domain = '.', jsonConfig) {
		super(chartReference, 'JsonChart', color, '.', jsonConfig, 'XYChart');
	}

	loadData(pointId, startTimestamp, endTimestamp, exportId) {
		return new Promise((resolve, reject) => {
			super.loadData(pointId, startTimestamp, endTimestamp, exportId).then((data) => {
				if (this.pointCurrentValue.get(pointId) == undefined) {
					this.pointCurrentValue.set(pointId, {
						name: data.name,
						suffix: data.textRenderer.suffix,
						type: data.type,
						labels: new Map(),
					});
				}
				if (data.type === 'Multistate') {
					let customLabels = data.textRenderer.multistateValues;
					if (
						data.textRenderer.typeName === 'textRendererMultistate' &&
						customLabels !== undefined
					) {
						let labelsMap = new Map();
						for (let i = 0; i < customLabels.length; i++) {
							labelsMap.set(String(customLabels[i].key), customLabels[i].text);
						}
						this.pointCurrentValue.get(pointId).labels = labelsMap;
					}
				}
				if (data.type === 'Binary') {
					if (data.textRenderer.typeName === 'textRendererBinary') {
						let labelsMap = new Map();
						labelsMap.set('0', data.textRenderer.zeroLabel);
						labelsMap.set('1', data.textRenderer.oneLabel);
						this.pointCurrentValue.get(pointId).labels = labelsMap;
					}
				}
				data.values.forEach((e) => {
					this.addValue(e, data.name, this.pointPastValues);
				});
				resolve('done');
			});
		});
	}

	setupChart(chartType) {
		if (chartType === 'compare') {
			this.chart.data = this.prepareCompareChartData(
				BaseChart.sortMapKeys(this.pointPastValues),
			);
		} else {
			this.chart.data = BaseChart.prepareChartData(
				BaseChart.sortMapKeys(this.pointPastValues),
			);
		}
	}
	prepareCompareChartData(map) {
		let data = []; //date|date2:<time>, <datapointName>:<datapointValue>
		let baseKey = undefined;
		map.forEach(function (value, key) {
			let jsonString;
			value.forEach((e) => {
				if (baseKey === undefined) {
					baseKey = e.name;
				}
				if (baseKey === e.name) {
					jsonString = `{"date":${key}, "${e.name}":${e.value}}`;
				} else {
					jsonString = `{"date2":${key}, "${e.name}":${e.value}}`;
				}
				data.push(JSON.parse(jsonString));
			});
		});
		return data;
	}
}
