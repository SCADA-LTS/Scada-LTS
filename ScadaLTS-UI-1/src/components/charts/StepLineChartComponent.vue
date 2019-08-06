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

  loadData(pointId, startTimestamp, endTimestamp) {
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
    "refreshRate",
    "width",
    "height",
    "polylineStep",
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
      if (Number(this.polylineStep) > 1) {
        StepLineChart.setPolylineStep(Number(this.polylineStep));
      }
      this.chartClass = new StepLineChart(this.$refs.chartdiv, this.color);
      let points = this.pointId.split(",");
      let promises = [];
      for (let i = 0; i < points.length; i++) {
        promises.push(this.chartClass.loadData(points[i], this.startDate, this.endDate))
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
        if (this.refreshRate != undefined) {
          this.chartClass.startLiveUpdate(Number(this.refreshRate));
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
