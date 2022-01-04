
/*
Base JSON config

Series creation



 */

import axios from "axios";

export class AmChartConfigurator {

    constructor(builder) {
        this.configId = builder.configId;
        if(!!localStorage.getItem(`AmChartConfig_${this.configId}`)) {
            console.warn("Loading from SessionStorage")
            this.loadChartConfiguration();
        } else {
            this.configuration = {
                legend: {},
                cursor: { type: "XYCursor" },
                scrollbarX: { type: 'Scrollbar' },
                xAxes: builder.xAxes,
                yAxes: builder.yAxes,
                series: builder.series,
                valuesLimit: builder.valuesLimit,
                apiLimitValues: 10000,
                apiLimitFactor: 1,
            }
            if(builder.exportMenu){
                this.configuration.exporting = builder.exportMenu;
            }
        }
    }

    getConfiguration() {
        return JSON.parse(JSON.stringify(this.configuration));
    }

    getSeriesConfiguration() {
        return this.configuration.series;
    }

    saveChartConfiguration() {
        localStorage.setItem(
            `AmChartConfig_${this.configId}`, 
            JSON.stringify(this.configuration)
        );
    }

    loadChartConfiguration() {
        this.configuration = JSON.parse(
            localStorage.getItem(`AmChartConfig_${this.configId}`)
        );
    }

    deleteChartConfiguration() {
        localStorage.removeItem(`AmChartConfig_${this.configId}`);
    }

}

export class AmChartConfiguratorBuilder {

    /**
     * Create AmChartJSONConfig step by step;
     * @param {Number} configurationId - Unique Configuration ID
     */
    constructor(configurationId) {
        this.configId = configurationId;
        this.xAxes = [];
        this.yAxes = [];
        this.series = [];
        this.activeColor = 0;
        this.valuesLimit = 1000;
        this.chartDefaultColors = [
            '#39B54A',
	        '#69FF7D',
	        '#166921',
	        '#690C24',
	        '#B53859',
	        '#734FC1',
        ];
    }

    /**
     * 
     * @param {String} id Unique X-Axis ID
     * @param {Number} aggregation Aggegate the data.
     * @param {String} dataField Name of data field to be attached to that Axis
     * @param {String} type [DateAxis] or other Types (now only that is supported)
     * @returns 
     */
    createXAxis(id, aggegation = 0, dataField = 'date', type = 'DateAxis') {
        let xAxis = {
            id: id,
            dataFields: {
                value: dataField,
            },
            dateFormats: {
                second: 'HH:mm:ss',
                minute: 'HH:mm:ss',
                hour: 'HH:mm',
                day: 'MMM dd',
            },
            groupData: false,
            groupCount: 200,
        };
        if(aggegation > 0) {
            xAxis.groupData = true;
            xAxis.groupCount = aggegation;
        }
        switch(type.toLowerCase()) {
            case 'dateaxis':
                xAxis.type = 'DateAxis';
                break;
            default:
                throw new Error("Unknown X-Axis Type!")
        }
        this.xAxes.push(xAxis);
        return this;
    }

    /**
     * 
     * @param {String} id 
     * @param {String} sync 
     * @param {Boolean} opposite 
     * @param {Boolean} logarithmic 
     * @param {String} type 
     * @param {String} dataField 
     * @returns 
     */
    createYAxis(id, sync, opposite, logarithmic, type = 'ValueAxis', dataField = '01') {
        let yAxis =  {
            id: id,
            type: type,
            dataFields: {
                value: dataField,
            }
        }
        if(!!opposite) {
            yAxis.renderer = {
                opposite: true,
            }
        }
        if(!!logarithmic) {
            yAxis.logarithmic = true;
        }
        if(!!sync) {
            yAxis.syncWithAxis = sync;
        }
        this.yAxes.push(yAxis);
        return this;
    }

    /**
     * 
     * @param {Number} pointId 
     * @param {String} yAxis
     * @param {String} xAxis 
     * @param {String} dateX Alternate dateX name for DataFields
     * @param {String} type [LineSeries|StepLineSeries]
     * @param {String} name Custom name for that series
     * @param {Object} style {stroke,strokeWidth,fill,fillOpacity}
     * @param {Number} tension Stroke tension     
     * @param {Boolean} bullets Showbullets
     * @returns 
     */
     async createSeries(pointId, yAxis, xAxis, dateX, type, name, style, tension, bullets) {
        let pointDetails = await this.fetchDataPointDetails(pointId);
        let series = {
            id: `s${pointDetails.id}`,
            type: type || 'LineSeries',
            name: name || pointDetails.name,
            yAxis: yAxis || 'valueAxis1',
            xAxis: xAxis || 'dateAxis1',
            dataFields: {
                dateX: dateX || 'date',
                valueY: `${pointDetails.id}`,
            },
            tooltipText: '{name}: [bold]{valueY}[/]',
            tooltip: {
                pointerOrientation: 'vertical',
                background: {
                    fill: '#F00',
                    cornerRadius: 20,
                    strokeOpacity: 0,
                },
                label: {
                    minWidth: 40,
                    minHeight: 40,
                    textAlign: 'middle',
                    textValign: 'middle',
                }
            },
            tensionX:1,
            startLocation: 0.5,
        }

        if(!type) {
            if(pointDetails.type === 'BinaryValue' || pointDetails.type === 'MultistateValue') {
                series.type = 'StepLineSeries';
            }
        }
        
        if(!style) { style = {}; }
        series.stroke = style.stroke || this.getActiveColor();
        series.fill = style.fill || this.getActiveColor();
        series.strokeWidth = style.strokeWidth || 3;
        series.fillOpacity = style.fillOpacity || 0;
        this.activeColor++;

        if(!!tension) {
            series.tensionX = tension;
        }

        if(!!bullets) {
            series.minBulletDistance = 15;
            series.bullets = [
                {
                    type: 'CircleBullet',
                    circle: {
                        radius: 0,
                        strokeWidth: 2,
                    }
                }
            ]
        }

        this.series.push(series);
        return this;
    }

    createExportMenu(filePrefix) {
        this.exportMenu = {
            filePrefix: filePrefix || `Scada_Chart_${new Date().getTime()}`,
            menu: {
                align: 'right',
                verticalAlign: 'top',
            }
        }
        return this;
    }

    build() {
        return new AmChartConfigurator(this);
    }

    getActiveColor() {
        return this.chartDefaultColors[this.activeColor % this.chartDefaultColors.length];
    }

    /**
	 * @private
	 * @param {Number} datapointId
	 * @returns
	 */
	fetchDataPointDetails(datapointId) {
		let requestUrl = `./api/point_value/getValue/id/${datapointId}`;

		return new Promise((resolve, reject) => {
			axios
				.get(requestUrl)
				.then((resp) => {
					resolve(resp.data);
				})
				.catch((error) => {
					console.error(error);
					reject(new Error('Failed to load Data Point details'));
				});
		});
	}

}

export default AmChartConfiguratorBuilder;