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
  constructor(chartReference, chartType="XYChart", domain = "http://localhost:8080/ScadaLTS") {
    super(chartReference, chartType, domain);
  }

  loadData(
    pointId,
    sumType,
    sumTimePeriod,
    startTimestamp = new Date().getTime() - 3600000,
    endTimestamp = new Date().getTime()
  ) {
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
    "label",
    "startDate",
    "endDate",
    "live",
    "sumType",
    "sumTimePeriod",
    "refreshRate",
    "width",
    "height"
  ],
  data() {
    return {
      pointName: "Name",
      errorMessage: undefined,
      chartClass: undefined
    };
  },
  mounted() {
    this.generateChart();
  },
  methods: {
    generateChart() {
      this.chartClass = new ColumnChart(this.$refs.chartdiv);
      let promises = [];
      if (this.startDate !== undefined && this.endDate !== undefined) {
        let sDate = new Date(this.startDate);
        let eDate = new Date(this.endDate);
        if (!isNaN(sDate.getDate()) && !isNaN(eDate.getDate())) {
          promises.push(
            this.chartClass.loadData(
              this.pointId,
              this.sumType,
              this.sumTimePeriod,
              sDate.getTime(),
              eDate.getTime()
            )
          );
        } else {
          this.errorMessage = "Not vaild date. Use for example ['1-day' | '2-months' | '3-days']";
          promises.push(
            this.chartClass.loadData(
              this.pointId,
              this.sumType,
              this.sumTimePeriod
            )
          );
        }
      } else if (this.startDate !== undefined && this.endDate === undefined) {
        promises.push(
          this.chartClass.loadData(
            this.pointId,
            this.sumType,
            this.sumTimePeriod,
            this.calculateDate(this.startDate)
          )
        );
      } else {
        promises.push(
          this.chartClass.loadData(
            this.pointId,
            this.sumType,
            this.sumTimePeriod
          )
        );
      }

      Promise.all(promises).then(response => {
        this.chartClass.showChart();
      });
    },
    calculateDate(dateString) {
      let date = new Date(dateString);
      if (date == "Invalid Date") {
        date = dateString.split("-");
        if (date.length === 2) {
          let dateNow = new Date();
          let multiplier = 1;
          switch (date[1]) {
            case "hour":
            case "hours":
              multiplier = 1000 * 3600;
              break;
            case "day":
            case "days":
              multiplier = 1000 * 3600 * 24;
              break;
            case "week":
            case "weeks":
              multiplier = 1000 * 3600 * 24 * 7;
              break;
            case "month":
            case "months":
              multiplier = 1000 * 3600 * 24 * 31;
              break;
          }
          return dateNow.getTime() - Number(date[0]) * multiplier;
        } else {
          this.errorMessage = "Not vaild date. Use for example ['1-day' | '2-months' | '3-days']";
          return dateNow.getTime() - 3600000;
        }
      } else {
        return date.getTime();
      }
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
  color:red;
}
</style>
