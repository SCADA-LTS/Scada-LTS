<template>
  <div>
    <p>{{label}}</p>
    <div
      class="hello"
      v-bind:style="{height: this.height + 'px', width: this.width + 'px'}"
      ref="chartdiv"
    ></div>
  </div>
</template>
<script>
import * as am4core from "@amcharts/amcharts4/core";
import * as am4charts from "@amcharts/amcharts4/charts";
import am4themes_animated from "@amcharts/amcharts4/themes/animated";
import httpClient from "axios";
import { ColumnChart } from "./ColumnChartComponent.vue";

am4core.useTheme(am4themes_animated);
console.log(ColumnChart);

class PieChart extends ColumnChart {
  constructor(chartReference, color, domain = "http://localhost:8080/ScadaLTS") {
    super(chartReference, color, "PieChart", domain);
  }

  setupChart() {
    let categoryName = "State";
    let countName = "Count";
    this.chart.data = PieChart.prepareChartData(
      this.pointPastValues,
      categoryName,
      countName
    );
    this.createExportMenu(true, "Scada_PieChart");
    this.createSeries("Pie", categoryName, countName, categoryName);
  }
}

export default {
  name: "PieChartComponent",
  props: [
    "pointId",
    "label",
    "startDate",
    "endDate",
    "sumType",
    "sumTimePeriod",
    "width",
    "height"
  ],
  //TODO: Enable multiple ChartInstances in one page
  data() {
    return {
      pointName: "Name",
      chartClass: undefined
    };
  },
  mounted() {
    this.generateChart();
  },
  methods: {
    generateChart() {
      this.chartClass = new PieChart(this.$refs.chartdiv);
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
          console.warn(
            "Not valid date pattern. Try with: <... start-date='YYYY/MM/DD'>"
          );
          promises.push(
            this.chartClass.loadData(
              this.pointId,
              this.sumType,
              this.sumTimePeriod
            )
          );
        }
      } else if (this.startDate !== undefined && this.endDate === undefined) {
        let sDate = new Date(this.startDate);

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
</style>
