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
    "color",
    "startDate",
    "endDate",
    "sumType",
    "sumTimePeriod",
    "width",
    "height"
  ],
  data() {
    return {
      chartClass: undefined
    };
  },
  mounted() {
    this.generateChart();
  },
  methods: {
    generateChart() {
      this.chartClass = new PieChart(this.$refs.chartdiv, this.color);

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
