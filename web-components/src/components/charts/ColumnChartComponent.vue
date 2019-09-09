<template>
    <div>
        <p>{{label}}</p>
        <div
                class="hello"
                v-bind:style="{height: this.height + 'px', width: this.width + 'px'}"
                ref="chartdiv"
        ></div>
        <div v-if="errorMessage">
            <p class="error">{{errorMessage}}</p>
        </div>
    </div>
</template>
<script>
    import * as am4core from "@amcharts/amcharts4/core";
    import * as am4charts from "@amcharts/amcharts4/charts";
    import am4themes_animated from "@amcharts/amcharts4/themes/animated";
    import httpClient from "axios";
    import BaseChart from "./BaseChart";

    am4core.useTheme(am4themes_animated);

    export class ColumnChart extends BaseChart {
        constructor(
            chartReference,
            color,
            chartType = "XYChart",
            domain = "."
        ) {
            super(chartReference, chartType, color, domain);
            this.isAverage = false;
        }

        loadData(pointId, sumType, sumTimePeriod, startTimestamp, endTimestamp) {
            return new Promise((resolve, reject) => {
                super.loadData(pointId, startTimestamp, endTimestamp).then(data => {
                    if (this.pointCurrentValue.get(pointId) == undefined) {
                        this.pointCurrentValue.set(pointId, {
                            name: data.name,
                            suffix: data.textRenderer.suffix,
                            type: data.type
                        });
                    }
                    if (data.type === "Alphanumeric") {
                        this.addAlphanumericValues(data.values);
                    }
                    if (data.type === "Multistate") {
                        this.addMultistateValues(
                            data.values,
                            data.textRenderer.multistateValues
                        );
                    }
                    if (data.type === "Numeric") {
                        if (sumType !== undefined && sumTimePeriod !== undefined) {
                            this.addNumericValue(
                                data.values,
                                data.name,
                                sumType,
                                sumTimePeriod
                            );
                            if (sumType == "avg") {
                                this.isAverage = true;
                            }
                        }
                    }
                    resolve("done");
                });
            });
        }

        addNumericValue(values, name, fn, period) {
            for (let i = 0; i < values.length; i++) {
                let key = this.groupBy(values[i].ts, period);
                values[i].value = Number(values[i].value);
                let lastElement =
                    this.pointPastValues.get(key) !== undefined
                        ? this.pointPastValues.get(key).length - 1
                        : 0;
                let startPoint;
                switch (fn) {
                    case "count":
                        startPoint = { name: name, value: 0 };
                        if (this.pointPastValues.get(key) == undefined) {
                            this.pointPastValues.set(key, [startPoint]);
                        } else if (this.pointPastValues.get(key)[lastElement].name == name) {
                            this.pointPastValues.get(key)[lastElement].value =
                                this.pointPastValues.get(key)[lastElement].value + 1;
                        } else {
                            this.pointPastValues.get(key).push(startPoint);
                        }
                        break;
                    case "sum":
                        startPoint = { name: name, value: values[i].value };
                        if (this.pointPastValues.get(key) == undefined) {
                            this.pointPastValues.set(key, [startPoint]);
                        } else if (this.pointPastValues.get(key)[lastElement].name == name) {
                            this.pointPastValues.get(key)[lastElement].value += values[i].value;
                        } else {
                            this.pointPastValues.get(key).push(startPoint);
                        }
                        break;
                    case "min":
                        startPoint = { name: name, value: values[i].value };
                        if (this.pointPastValues.get(key) == undefined) {
                            this.pointPastValues.set(key, [startPoint]);
                        } else if (this.pointPastValues.get(key)[lastElement].name == name) {
                            let oldPointValue = this.pointPastValues.get(key)[lastElement]
                                .value;
                            if (oldPointValue > values[i].value) {
                                this.pointPastValues.get(key)[lastElement].value =
                                    values[i].value;
                            }
                        } else {
                            this.pointPastValues.get(key).push(startPoint);
                        }
                        break;
                    case "max":
                        startPoint = { name: name, value: values[i].value };
                        if (this.pointPastValues.get(key) == undefined) {
                            this.pointPastValues.set(key, [startPoint]);
                        } else if (this.pointPastValues.get(key)[lastElement].name == name) {
                            let oldPointValue = this.pointPastValues.get(key)[lastElement]
                                .value;
                            if (oldPointValue < values[i].value) {
                                this.pointPastValues.get(key)[lastElement].value =
                                    values[i].value;
                            }
                        } else {
                            this.pointPastValues.get(key).push(startPoint);
                        }
                        break;
                    case "avg":
                        startPoint = { name: name, value: values[i].value, count: 0 };
                        if (this.pointPastValues.get(key) == undefined) {
                            this.pointPastValues.set(key, [startPoint]);
                        } else if (this.pointPastValues.get(key)[lastElement].name == name) {
                            this.pointPastValues.get(key)[lastElement].value += values[i].value;
                            this.pointPastValues.get(key)[lastElement].count =
                                this.pointPastValues.get(key)[lastElement].count + 1;
                        } else {
                            this.pointPastValues.get(key).push(startPoint);
                        }
                        break;
                }
            }
        }

        addAlphanumericValues(values) {
            for (let i = 0; i < values.length; i++) {
                if (this.pointPastValues.get(values[i].value) == undefined) {
                    this.pointPastValues.set(values[i].value, 1);
                } else {
                    this.pointPastValues.set(
                        values[i].value,
                        this.pointPastValues.get(values[i].value) + 1
                    );
                }
            }
        }

        addMultistateValues(values, labels) {
            let labelsMap = new Map();
            if (labels != undefined) {
                for (let i = 0; i < labels.length; i++) {
                    labelsMap.set(String(labels[i].key), labels[i].text);
                }
            }

            for (let i = 0; i < values.length; i++) {
                if (labels != undefined) {
                    if (
                        this.pointPastValues.get(labelsMap.get(values[i].value)) == undefined
                    ) {
                        this.pointPastValues.set(labelsMap.get(values[i].value), 1);
                    } else {
                        this.pointPastValues.set(
                            labelsMap.get(values[i].value),
                            this.pointPastValues.get(labelsMap.get(values[i].value)) + 1
                        );
                    }
                } else {
                    if (this.pointPastValues.get(values[i].value) == undefined) {
                        this.pointPastValues.set(values[i].value, 1);
                    } else {
                        this.pointPastValues.set(
                            values[i].value,
                            this.pointPastValues.get(values[i].value) + 1
                        );
                    }
                }
            }
        }

        /**
         * @param {Number} date Date to convert
         * @param {String} period Type of sum
         * @return {String} Key for map.
         */
        groupBy(date, period) {
            date = new Date(date);
            let key = String(date.getFullYear());
            switch (period) {
                case "yaer":
                    break;
                case "month":
                    key = `${key}-${date.getMonth() + 1}`;
                    break;
                case "day":
                    key = `${key}-${date.getMonth() + 1}-${date.getDate()}`;
                    break;
                case "hour":
                    key = `${key}-${date.getMonth() +
                    1}-${date.getDate()} ${date.getHours()}:00`;
                    break;
                case "minute":
                    key = `${key}-${date.getMonth() +
                    1}-${date.getDate()} ${date.getHours()}:${date.getMinutes()}`;
                    break;
            }
            return key;
        }

        static prepareChartData(map) {
            let data = [];
            map.forEach(function(value, key) {
                let jsonString = `{ "group":"${key}"`;
                if (value instanceof Array) {
                    value.forEach(e => {
                        jsonString = `${jsonString}, "${e.name}":${e.value}`;
                    });
                    jsonString += "}";
                } else {
                    jsonString = `${jsonString}, "value":${value}}`;
                }
                data.push(JSON.parse(jsonString));
            });
            return data;
        }

        setupChart() {
            this.countAverage();
            this.chart.data = ColumnChart.prepareChartData(this.pointPastValues);
            this.createAxisX("CategoryAxis", "group");
            this.createAxisY();
            this.createScrollBarsAndLegend(false, false, true, false);
            this.createExportMenu(true, "Scada_ColumnChart");
            for (let [k, v] of this.pointCurrentValue) {
                if (v.type === "Numeric") {
                    this.createSeries("Column", "group", v.name, v.name);
                } else {
                    this.createSeries("Column", "group", "value", v.name);
                }
            }
        }

        countAverage() {
            if (this.isAverage) {
                let newPPV = new Map();
                this.pointPastValues.forEach(function(points, key) {
                    for (let i = 0; i < points.length; i++) {
                        let point = {
                            name: points[i].name,
                            value: points[i].value / points[i].count
                        };
                        if (newPPV.get(key) === undefined) {
                            newPPV.set(key, [point]);
                        } else {
                            newPPV.get(key).push(point);
                        }
                    }
                });
                this.pointPastValues = newPPV;
            }
        }
    }

    export default {
        name: "ColumnChartComponent",
        props: [
            "pointId",
            "color",
            "label",
            "startDate",
            "endDate",
            "sumType",
            "sumTimePeriod",
            "width",
            "height",
            "rangeValue",
            "rangeColor",
            "rangeLabel"
        ],
        data() {
            return {
                errorMessage: undefined,
                chartClass: undefined
            };
        },
        mounted() {
            this.generateChart();
        },
        methods: {
            generateChart() {
                this.chartClass = new ColumnChart(this.$refs.chartdiv, this.color);
                let points = this.pointId.split(",");
                let promises = [];
                for (let i = 0; i < points.length; i++) {
                    promises.push(
                        this.chartClass.loadData(
                            points[i],
                            this.sumType,
                            this.sumTimePeriod,
                            this.startDate,
                            this.endDate
                        )
                    );
                }
                Promise.all(promises).then(response => {
                    this.chartClass.showChart();
                    if (this.rangeValue !== undefined) {
                        this.chartClass.addRangeValue(
                            Number(this.rangeValue),
                            this.rangeColor,
                            this.rangeLabel
                        );
                    }
                });
            }
        }
    };
</script>
<style scoped>
    .hello {
        width: 750px;
        height: 500px;
    }
    .error {
        color: red;
    }
</style>