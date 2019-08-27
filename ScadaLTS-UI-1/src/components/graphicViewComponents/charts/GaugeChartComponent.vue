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
import Axios from "axios";
import BaseChart from "./BaseChart";

am4core.useTheme(am4themes_animated);

class GaugeChart {

  constructor(chartReference, pointId, min, max, refreshRate, color, domain = ".") {
    this.chart = am4core.create(chartReference, am4charts.GaugeChart);
    this.chart.innerRadius = -25;
    this.pointId = pointId;
    this.domain = domain;
    this.createAxisX(Number(min), Number(max), color);
    this.hand = this.chart.hands.push(new am4charts.ClockHand());
    this.value = 0;
    this.refreshRate = refreshRate === undefined ? 1000 : Number(refreshRate);
    this.interval = 10000;
  }

  createAxisX(min, max, color) {
    let axis = this.chart.xAxes.push(new am4charts.ValueAxis());
    axis.min = min;
    axis.max = max;
    axis.strictMinMax = true;
    axis.renderer.grid.template.stroke = new am4core.InterfaceColorSet().getFor("background");
    axis.renderer.grid.template.strokeOpacity = 0.3;

    var range0 = axis.axisRanges.create();
    range0.value = min;
    range0.endValue = max;
    range0.axisFill.fillOpacity = 1;
    if(color !== undefined) {
      range0.axisFill.fill = am4core.color(color);
    } else {
      range0.axisFill.fill = am4core.color("#39B54A");
    }
    range0.axisFill.zIndex = - 1;
  }

  showChart() {
      this.interval = setInterval(() => {
          this.loadLiveData()
      }, this.refreshRate);
  }

  loadLiveData() {
      let api = '/api/point_value/getValue/id/';
      Axios.get(this.domain + api + this.pointId, { timeout: 5000, useCredentails: true, credentials: 'same-origin' }).then(response => {
          this.hand.showValue(Number(response.data.value), 1000, am4core.ease.cubicOut);
      });
  }
}

export default {
  name: "GaugeChartComponent",
  props: [
    "pointId",
    "color",
    "label",
    "min",
    "max",
    "refreshRate",
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
      this.chartClass = new GaugeChart(this.$refs.chartdiv, this.pointId, this.min, this.max, this.refreshRate, this.color);
      this.chartClass.showChart();
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
