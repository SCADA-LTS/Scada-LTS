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

class PieChart extends ColumnChart {
  constructor(chartReference, color, domain = "http://localhost:8080/ScadaLTS") {
    super(chartReference, color, "PieChart", domain);
  }

  setupChart() {
    this.chart.data = PieChart.prepareChartData(this.pointPastValues);
    this.createExportMenu(true, "Scada_PieChart");
    for (let [k, v] of this.pointCurrentValue) {
      if (v.type === "Numeric") {
        this.createSeries("Pie", "group", v.name, v.name);
      } else {
        this.createSeries("Pie", "group", "value", v.name);
      }
    }
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
      console.debug("data");
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
