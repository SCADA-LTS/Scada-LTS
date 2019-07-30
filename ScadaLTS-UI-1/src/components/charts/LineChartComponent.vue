<template>
  <div>
    <p>Displaying line chart for DataPoint {{pointName}} with ID: {{propPointId}}</p>
    <div class="hello" ref="chartdiv"></div>
  </div>
</template>
<script>
import * as am4core from "@amcharts/amcharts4/core";
import * as am4charts from "@amcharts/amcharts4/charts";
import am4themes_animated from "@amcharts/amcharts4/themes/animated";
import httpClient from "axios";
import BaseChart from "./BaseChart";
import { setInterval } from "timers";

am4core.useTheme(am4themes_animated);

class LineChart extends BaseChart {
  constructor(chartReference, domain = "http://localhost:8080/ScadaLTS") {
    super(chartReference, "XYChart", domain);
  }

  loadData(pointId, startTimestamp = new Date().getTime() - 3600000, endTimestamp = new Date().getTime()) {
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
          if (isNaN(e.value)) {
            e.value == "true" ? (e.value = 1) : (e.value = 0);
          }
          let point = { name: data.name, value: e.value };
          if (this.pointPastValues.get(e.ts) == undefined) {
            this.pointPastValues.set(e.ts, [point]);
          } else {
            this.pointPastValues.get(e.ts).push(point);
          }
        });
        resolve("done");
      });
    });
  }

  setupChart() {
    this.chart.data = BaseChart.prepareChartData(BaseChart.sortMapKeys(this.pointPastValues));
    this.createAxisX("DateAxis", null);
    this.createAxisY();
    this.createScrollBarsAndLegend();
    for (let [k, v] of this.pointCurrentValue) {
      this.createSeries(v.name, v.name);
    }
  }

  createSeries(seriesValueY, seriesName) {
    super.createSeries("Line", "date", seriesValueY, seriesName);
  }
}

export default {
  name: "LineChartComponent",
  props: ["propPointId", "startDate", "endDate", "seriesColor"],
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
      this.chartClass = new LineChart(this.$refs.chartdiv);
      this.chartClass.loadData(this.propPointId).then(response => {
        if (response == "done") {
          this.chartClass.showChart();
          this.chartClass.startLiveUpdate(10000);
        } else {
          console.error("Something went wrong");
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
</style>
