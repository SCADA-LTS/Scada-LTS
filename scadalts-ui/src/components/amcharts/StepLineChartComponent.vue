<template>
	<div>
		<p>{{ label }}</p>
		<div
			class="hello"
			v-bind:style="{ height: this.height + 'px', width: this.width + 'px' }"
			ref="chartdiv"
		></div>
		<div v-if="errorMessage">
			<p class="error">{{ errorMessage }}</p>
		</div>
		<div v-if="showReload">
			<button v-on:click="reload()">Reload</button>
		</div>
		<div v-if="showDebug">
			<button v-on:click="debug()">Debug</button>
		</div>
	</div>
</template>
<script>
import * as am4core from '@amcharts/amcharts4/core';
import * as am4charts from '@amcharts/amcharts4/charts';
import am4themes_animated from '@amcharts/amcharts4/themes/animated';
import httpClient from 'axios';
import BaseChart from './BaseChart';
am4core.useTheme(am4themes_animated);
class StepLineChart extends BaseChart {
	constructor(chartReference, color, domain = '.') {
		super(chartReference, 'XYChart', color, domain);
	}
	displayControls(scrollbarX, scrollbarY, legend) {
		if (scrollbarX !== undefined && scrollbarX == 'true') {
			this.showScrollbarX = true;
		} else if (scrollbarX !== undefined && scrollbarX == 'false') {
			this.showScrollbarX = false;
		}
		if (scrollbarY !== undefined && scrollbarY == 'true') {
			this.showScrollbarY = true;
		} else if (scrollbarY !== undefined && scrollbarY == 'false') {
			this.showScrollbarY = false;
		}
		if (legend !== undefined && legend == 'true') {
			this.showLegend = true;
		} else if (legend !== undefined && legend == 'false') {
			this.showLegend = false;
		}
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
	setupChart() {
		this.chart.data = BaseChart.prepareChartData(
			BaseChart.sortMapKeys(this.pointPastValues),
		);
		this.createAxisX('DateAxis', null);
		this.createAxisY();
		this.createScrollBarsAndLegend(
			this.showScrollbarX,
			this.showScrollbarY,
			this.showLegend,
		);
		this.createExportMenu(true, 'Scada_StepLineChart');
		for (let [k, v] of this.pointCurrentValue) {
			let s = this.createSeries(v.name, v.name, v.suffix);
			if (v.type === 'Multistate') {
				let mAxis = this.createAxisY(v.labels);
				s.yAxis = mAxis;
				mAxis.renderer.line.stroke = s.stroke;
				mAxis.title.text = v.name;
			}
			if (v.type === 'Binary') {
				if (v.labels.size > 0) {
					let bAxis = this.createAxisY(v.labels);
					s.yAxis = bAxis;
					bAxis.renderer.line.stroke = s.stroke;
					bAxis.title.text = v.name;
				}
			}
		}
	}
	createSeries(seriesValueY, seriesName, suffix) {
		return super.createSeries('StepLine', 'date', seriesValueY, seriesName, suffix);
	}
}
export default {
	name: 'StepLineChartComponent',
	props: [
		'pointId',
		'pointXid',
		'color',
		'label',
		'startDate',
		'endDate',
		'refreshRate',
		'width',
		'height',
		'polylineStep',
		'rangeValue',
		'rangeColor',
		'rangeLabel',
		'showScrollbarX',
		'showScrollbarY',
		'showLegend',
		'showReload',
		'showDebug',
	],
	data() {
		return {
			errorMessage: undefined,
			chartClass: undefined,
			isExportId: false,
		};
	},
	mounted() {
		this.generateChart();
	},
	methods: {
		generateChart() {
			if (Number(this.polylineStep) > 1) {
				StepLineChart.setPolylineStep(Number(this.polylineStep));
			}
			this.chartClass = new StepLineChart(this.$refs.chartdiv, this.color);
			this.chartClass.displayControls(
				this.showScrollbarX,
				this.showScrollbarY,
				this.showLegend,
			);
			if (
				this.pointXid !== undefined &&
				this.pointXid !== null &&
				(this.pointId === null || this.pointId === undefined)
			) {
				this.isExportId = true;
			}
			let promises = [];
			let points;
			if (this.isExportId) {
				points = this.pointXid.split(',');
			} else {
				points = this.pointId.split(',');
			}
			for (let i = 0; i < points.length; i++) {
				promises.push(
					this.chartClass.loadData(
						points[i],
						this.startDate,
						this.endDate,
						this.isExportId,
					),
				);
			}
			Promise.all(promises).then((response) => {
				for (let i = 0; i < response.length; i++) {
					if (response[i] !== 'done') {
						this.errorMessage = 'Point given with index [' + i + '] has not been loaded!';
					}
				}
				this.chartClass.showChart();
				if (this.rangeValue !== undefined && this.rangeValue !== null) {
					this.chartClass.addRangeValue(
						Number(this.rangeValue),
						this.rangeColor,
						this.rangeLabel,
					);
				}
				if (this.refreshRate != undefined && this.refreshRate !== null) {
					this.chartClass.startLiveUpdate(Number(this.refreshRate), this.isExportId);
				}
			});
		},
		reload() {
			this.generateChart();
		},
		debug() {
			console.debug(this.chartClass);
		},
	},
};
</script>
<style scoped>
.hello {
	min-width: 650px;
	height: 500px;
}
.error {
	color: red;
}
p {
	text-align: center;
	padding-top: 10px;
}
</style>
