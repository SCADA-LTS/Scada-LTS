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

class StepLineChart extends BaseChart {
  constructor(
    chartReference,
    color,
    domain = "http://localhost:8080/ScadaLTS"
  ) {
    super(chartReference, "XYChart", color, domain);
  }

  loadData(
    pointId,
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

        data.values.forEach(e => {
          this.addValue(e, data.name, this.pointPastValues);
        });
        resolve("done");
      });
    });
  }

  setupChart() {
    this.chart.data = BaseChart.prepareChartData(
      BaseChart.sortMapKeys(this.pointPastValues)
    );
    this.createAxisX("DateAxis", null);
    this.createAxisY();
    this.createScrollBarsAndLegend();
    this.createExportMenu(true, "Scada_StepLineChart");
    for (let [k, v] of this.pointCurrentValue) {
      this.createSeries(v.name, v.name);
    }
  }

  createSeries(seriesValueY, seriesName) {
    super.createSeries("StepLine", "date", seriesValueY, seriesName);
  }
}

export default {
  name: "StepLineChartComponent",
  props: [
    "pointId",
    "color",
    "label",
    "startDate",
    "endDate",
    "live",
    "refreshRate",
    "width",
    "height",
    "polylineStep",
    "rangeValue",
    "rangeColor",
    "rangeLabel"
  ],
  //TODO: Enable multiple ChartInstances in one page
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
      if (Number(this.polylineStep) > 1) {
        StepLineChart.setPolylineStep(Number(this.polylineStep));
      }
      this.chartClass = new StepLineChart(this.$refs.chartdiv, this.color);
      let points = this.pointId.split(",");
      let promises = [];
      for (let i = 0; i < points.length; i++) {
        if (this.startDate !== undefined && this.endDate !== undefined) {
          let sDate = new Date(this.startDate);
          let eDate = new Date(this.endDate);
          if (!isNaN(sDate.getDate()) && !isNaN(eDate.getDate())) {
            promises.push(
              this.chartClass.loadData(
                points[i],
                sDate.getTime(),
                eDate.getTime()
              )
            );
          } else {
            this.errorMessage =
              "Not valid date pattern. Try with: <... start-date='YYYY/MM/DD'>";
            promises.push(this.chartClass.loadData(points[i]));
          }
        } else if (this.startDate !== undefined && this.endDate === undefined) {
          promises.push(
            this.chartClass.loadData(
              points[i],
              this.calculateDate(this.startDate)
            )
          );
        } else {
          promises.push(this.chartClass.loadData(points[i]));
        }
      }
      Promise.all(promises).then(response => {
        for (let i = 0; i < response.length; i++) {
          if (response[i] !== "done") {
            this.errorMessage =
              "Point given with index [" + i + "] has not been loaded!";
          }
        }
        this.chartClass.showChart();
        if (this.rangeValue !== undefined) {
          this.chartClass.addRangeValue(
            Number(this.rangeValue),
            this.rangeColor,
            this.rangeLabel
          );
        }
        if (this.live == "true" && this.refreshRate == undefined) {
          this.errorMessage =
            "Refresh rate for chart has not been set. Add for example: <... refresh-rate='10000'>";
        }
        if (this.live == "true" && this.refreshRate != undefined) {
          this.chartClass.startLiveUpdate(Number(this.refreshRate));
        }
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
          this.errorMessage =
            "Not vaild date. Use for example ['1-day' | '2-months' | '3-days']";
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
  color: red;
}
</style>
