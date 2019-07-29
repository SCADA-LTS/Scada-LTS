<template>
  <div>
    <p>Displaying chart for DataPoint {{pointName}} with ID: {{pointId}}</p>
    <div class="hello" ref="chartdiv"></div>
  </div>
</template>
<script>
import * as am4core from "@amcharts/amcharts4/core";
import * as am4charts from "@amcharts/amcharts4/charts";
import am4themes_animated from "@amcharts/amcharts4/themes/animated";
import httpClient from 'axios';

am4core.useTheme(am4themes_animated);

export default {
    name: 'StepLineChart',
    props: [
        'propPointId'
    ],
    data() {
        return {
            chart: undefined,
            pointId: this.propPointId,
            pointName: "Name"
        }
    },
    mounted() {
        this.generateChart();
    },
    methods: {
        generateChart() {
            this.chart = am4core.create(this.$refs.chartdiv, am4charts.XYChart);
            this.loadDataFromApi(this.pointId, new Date().getTime() - 3600000, new Date().getTime()).then(response => {
                this.chart.data = this.parseData(response);

                let dateAxis = this.chart.xAxes.push(new am4charts.DateAxis());
                dateAxis.renderer.grid.template.location = 0;

                this.chart.yAxes.push(new am4charts.ValueAxis());
                let series = this.chart.series.push(new am4charts.StepLineSeries());
                series.dataFields.dateX = "date";
                series.dataFields.valueY = this.pointName;
                series.name = this.pointName;
                series.tooltipText = "{valueY.value}";

                this.chart.cursor = new am4charts.XYCursor();

                let scrollbarX = new am4charts.XYChartScrollbar();
                scrollbarX.series.push(series);
                this.chart.scrollbarX = scrollbarX;
            })
        },
        authenticateApi(username, password) {
            return new Promise((resolve, reject) => {
                let apiUrl = 'http://localhost:8080/ScadaLTS/api/auth/'
                try {
                    httpClient.get(apiUrl + username + '/' + password, {timeout: 5000, useCredentails: true}).then(response => {
                        if(response.data === true) {
                            resolve(true)
                        } else {
                            resolve(false)
                        }
                    }).catch(webError => {
                        reject(webError)
                    })
                } catch (error) {
                    reject(error)
                }
            })
        },
        loadDataFromApi(pointId, startTimestamp, endTimestamp) {
            return new Promise((resolve, reject) => {
                let apiUrl = 'http://localhost:8080/ScadaLTS/api/point_value/getValuesFromTimePeriod/';
                try {
                    httpClient.get(apiUrl + pointId + "/" + startTimestamp + "/" + endTimestamp, {timeout: 5000, useCredentails: true, credentials: 'same-origin'}).then(response => {
                        resolve(response.data)
                    }).catch(webError => {
                        reject(webError)
                    })
                } catch (error) {
                    reject(error)
                }
            })
        },
        parseData(data) {
            let pointPastValues = new Map();
            let newData = []; // [{date:<time>, <datapointName>:<datapointValue>}]
            this.pointName = data.name;
            data.values.forEach(e => {
                if (isNaN(e.value)) {
                    e.value == "true" ? e.value = 1 : e.value = 0;
                }
                let point = { "name": data.name, "value": e.value };

                if (pointPastValues.get(e.ts) == undefined) {
                    pointPastValues.set(e.ts, [point])
                } else {
                    pointPastValues.get(e.ts).push(point)
                }
            })

            pointPastValues.forEach(function (value, key) {
                let jsonString = '{ "date":' + key;
                value.forEach(e => {
                    if (!isNaN(Number(e.value))) {
                        jsonString = jsonString + ', "' + e.name + '":' + e.value;
                    }
                })
                jsonString = jsonString + '}';
                newData.push(JSON.parse(jsonString));
            });
            return newData;
        }
    }
};
</script>
<style scoped>
.hello {
  width: 750px;
  height: 500px;
}
</style>
