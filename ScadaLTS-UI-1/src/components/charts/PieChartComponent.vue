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
import {ColumnChart} from "./ColumnChartComponent.vue";

am4core.useTheme(am4themes_animated);
console.log(ColumnChart);

class PieChart extends ColumnChart {
  constructor(chartReference, domain = "http://localhost:8080/ScadaLTS") {
    super(chartReference, "PieChart", domain);
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
        if (!isNaN(sDate.getDate())) {
          promises.push(
            this.chartClass.loadData(
              this.pointId,
              this.sumType,
              this.sumTimePeriod,
              sDate.getTime()
            )
          );
        } else {
          console.warn(
            "Not valid date pattern. Try with: <... start-date='YYYY/MM/DD'>"
          );
        }
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
