/**
 * @fileoverview LineChart Class Definition
 * @author <a href="mailto:rjajko@softq.pl">Radoslaw Jajko</a>
 * @version 1.1.0
 */
import * as am4core from '@amcharts/amcharts4/core';
import am4themes_animated from '@amcharts/amcharts4/themes/animated';
import BaseChart from './BaseChart';

am4core.useTheme(am4themes_animated);

const TEXT_RENDERER_MULTISTATE = 'textRendererMultistate';
const TEXT_RENDERER_BINARY = 'textRendererBinary';
// Enum for Datapoint Types
const DATAPOINT_TYPE = Object.freeze({
	BINARY: 'Binary',
	MULTISTATE: 'Multistate',
});

/**
 * Line Chart 
 * 
 * Line Chart Class to configure "Line am4chart". 
 * Used by LineChartComponent.vue and RangeChartComponent.vue
 */
export class LineChart extends BaseChart {

	constructor(chartReference, color, domain = '.') {
		super(chartReference, 'XYChart', color, domain);
		this._aggregation = 0;
	}
	
	displayControls(scrollbarX, scrollbarY, legend) {
		this.showScrollbarX = (scrollbarX === 'true' || scrollbarX === true);
		this.showScrollbarY = (scrollbarY === 'true' || scrollbarY === true);
		this.showLegend = (legend === 'true' || legend === true);
	}

	loadData(pointId, startTimestamp, endTimestamp, exportId) {
		return new Promise((resolve) => {
			super.loadData(pointId, startTimestamp, endTimestamp, exportId).then((data) => {
				
				if (this.pointCurrentValue.get(pointId) == undefined) {
					this.pointCurrentValue.set(pointId, {
						name: data.name,
						suffix: data.textRenderer.suffix,
						type: data.type,
						labels: new Map(),
					});
				}

				if (data.type === DATAPOINT_TYPE.MULTISTATE) {
					let customLabels = data.textRenderer.multistateValues;
					if (data.textRenderer.typeName === TEXT_RENDERER_MULTISTATE && !!customLabels) {
						let labelsMap = new Map();
						for (let i = 0; i < customLabels.length; i++) {
							labelsMap.set(`${customLabels[i].key}`, customLabels[i].text);
						}
						this.pointCurrentValue.get(pointId).labels = labelsMap;
					}
				}

				if (data.type === DATAPOINT_TYPE.BINARY) {
					if (data.textRenderer.typeName === TEXT_RENDERER_BINARY) {
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
		this.createAxisX('DateAxis', null, this.aggregation);
		this.createAxisY();
		this.createScrollBarsAndLegend(
			this.showScrollbarX,
			this.showScrollbarY,
			this.showLegend,
		);
		this.createExportMenu(true, 'Scada_LineChart');
		for (let [k, v] of this.pointCurrentValue) {
			let s = this.createSeries(v.name, v.name, v.suffix);
			if (v.type === DATAPOINT_TYPE.MULTISTATE) {
				let mAxis = this.createAxisY(v.labels);
				s.yAxis = mAxis;
				mAxis.renderer.line.stroke = s.stroke;
				mAxis.title.text = v.name;
			}
			if (v.type === DATAPOINT_TYPE.BINARY) {
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
		return super.createSeries('Line', 'date', seriesValueY, seriesName, suffix);
	}

	// GETERS and SETTERS for configuration.
	get aggregation() {
		return this._aggregation;
	}

	set aggregation(aggregation) {
		if(!!aggregation) {
			this._aggregation =	Number(aggregation);
		}
	}
};

export default LineChart