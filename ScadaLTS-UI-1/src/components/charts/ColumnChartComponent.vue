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
    domain = "http://localhost:8080/ScadaLTS"
  ) {
    super(chartReference, chartType, color, domain);
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
          data.values.forEach(e => {
            if (this.pointPastValues.get(e.value) == undefined) {
              this.pointPastValues.set(e.value, 1);
            } else {
              this.pointPastValues.set(
                e.value,
                this.pointPastValues.get(e.value) + 1
              );
            }
          });
        }

        if (data.type === "Multistate") {
          let nameMap = new Map();
          if (data.textRenderer.multistateValues != undefined) {
            data.textRenderer.multistateValues.forEach(e => {
              nameMap.set(e.key, e.text);
            });
          }

          data.values.forEach(e => {
            if (data.textRenderer.multistateValues != undefined) {
              e.value = Number(e.value);
              if (this.pointPastValues.get(nameMap.get(e.value)) == undefined) {
                this.pointPastValues.set(nameMap.get(e.value), 1);
              } else {
                this.pointPastValues.set(
                  nameMap.get(e.value),
                  this.pointPastValues.get(nameMap.get(e.value)) + 1
                );
              }
            } else {
              if (this.pointPastValues.get(e.value) == undefined) {
                this.pointPastValues.set(e.value, 1);
              } else {
                this.pointPastValues.set(
                  e.value,
                  this.pointPastValues.get(e.value) + 1
                );
              }
            }
          });
        }

        if (data.type === "Numeric") {
          if (sumType !== undefined && sumTimePeriod !== undefined) {
            this.loadNumericData(data.values, sumType, sumTimePeriod);
          }
        }

        resolve("done");
      });
    });
  }

  loadNumericData(dataArray, sumType, period) {
    let avg = new Map();
    dataArray.forEach(data => {
      data.value = Number(data.value);
      let date = new Date(data.ts);
      let key = String(date.getFullYear());
      switch (period) {
        case "yaer":
          break;
        case "month":
          key = key + "-" + (date.getMonth() + 1);
          break;
        case "day":
          key = key + "-" + (date.getMonth() + 1) + "-" + date.getDate();
          break;
        case "hour":
          key =
            key +
            "-" +
            (date.getMonth() + 1) +
            "-" +
            date.getDate() +
            "_" +
            date.getHours() +
            ":00";
          break;
        case "minute":
          key =
            key +
            "-" +
            (date.getMonth() + 1) +
            "-" +
            date.getDate() +
            "_" +
            date.getHours() +
            ":" +
            date.getMinutes();
          break;
      }
      if (sumType === "count") {
        if (this.pointPastValues.get(key) == undefined) {
          this.pointPastValues.set(key, 1);
        } else {
          this.pointPastValues.set(key, this.pointPastValues.get(key) + 1);
        }
      } else if (sumType === "max") {
        if (this.pointPastValues.get(key) == undefined) {
          this.pointPastValues.set(key, data.value);
        } else {
          if (data.value > this.pointPastValues.get(key)) {
            this.pointPastValues.set(key, data.value);
          }
        }
      } else if (sumType === "min") {
        if (this.pointPastValues.get(key) == undefined) {
          this.pointPastValues.set(key, data.value);
        } else {
          if (data.value < this.pointPastValues.get(key)) {
            this.pointPastValues.set(key, data.value);
          }
        }
      } else if (sumType === "avg") {
        if (this.pointPastValues.get(key) == undefined) {
          this.pointPastValues.set(key, data.value);
          avg.set(key, 1);
        } else {
          if (data.value < this.pointPastValues.get(key)) {
            this.pointPastValues.set(
              key,
              this.pointPastValues.get(key) + data.value
            );
            avg.set(key, avg.get(key) + 1);
          }
        }
      }
    });
    if (sumType === "avg") {
      let newPointPastValues = new Map();
      this.pointPastValues.forEach(function(value, key) {
        newPointPastValues.set(key, value / avg.get(key));
      });
      this.pointPastValues = newPointPastValues;
    }
  }

  static prepareChartData(map, categoryName, countName) {
    let data = [];
    map.forEach(function(value, key) {
      let jsonString =
        '{"' +
        categoryName +
        '":"' +
        key +
        '","' +
        countName +
        '":' +
        value +
        "}";
      data.push(JSON.parse(jsonString));
    });
    return data;
  }

  setupChart() {
    let categoryName = "State";
    let countName = "Count";
    this.chart.data = ColumnChart.prepareChartData(
      this.pointPastValues,
      categoryName,
      countName
    );
    this.createAxisX("CategoryAxis", categoryName);
    this.createAxisY();
    this.createScrollBarsAndLegend(false, false, false, false);
    this.createExportMenu(true, "Scada_ColumnChart");
    this.createSeries("Column", categoryName, countName, categoryName);
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

      this.chartClass
        .loadData(
          this.pointId,
          this.sumType,
          this.sumTimePeriod,
          this.startDate,
          this.endDate
        )
        .then(response => {
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
