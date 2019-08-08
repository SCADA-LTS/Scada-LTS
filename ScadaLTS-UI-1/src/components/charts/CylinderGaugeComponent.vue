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
import Axios from "axios";
import { setInterval } from "timers";

am4core.useTheme(am4themes_animated);

class CylinderGaugeChart {
  constructor(
    chartReference,
    pointId,
    min,
    max,
    refreshRate,
    color,
    domain = "http://localhost:8080/ScadaLTS"
  ) {
    this.chart = am4core.create(chartReference, am4charts.XYChart3D);
    this.pointId = pointId;
    this.domain = domain;
    this.max = max;
    this.min = min;
    if (color !== undefined) {
      this.color = color;
    }
    this.createAxisX();
    this.createAxisY(min, max);
    this.createSeries();
    this.data = {
      category: "PointName",
      value1: min,
      value2: max,
      percent: 0,
    };
    this.lastUpdate = 0;
    this.chart.data = [this.data];
    this.loadLiveData();
    if (refreshRate !== undefined) {
      this.interval = setInterval(() => {
        this.loadLiveData();
      }, Number(refreshRate));
    }
  }

  loadLiveData() {
    Axios.get(`${this.domain}/api/point_value/getValue/id/${this.pointId}`, {
      timeout: 5000,
      useCredentails: true,
      credentials: "same-origin"
    }).then(response => {
      if (response.data.ts > this.lastUpdate) {
        this.lastUpdate = response.data.ts;
        let val = Number(response.data.value);
        let data = {
          category: response.data.xid,
          value1: val,
          value2: this.max - val,
          percent: (val * 100) / this.max
        };
        
        this.chart.addData(data, 1);
      }
    });
  }

  createAxisX() {
    let axis = this.chart.xAxes.push(new am4charts.CategoryAxis());
    axis.dataFields.category = "category";
    axis.renderer.grid.template.location = 0;
    axis.renderer.grid.template.strokeOpacity = 0;
    axis.tooltip.disabled = true;
    return axis;
  }

  createAxisY(min, max) {
    let axis = this.chart.yAxes.push(new am4charts.ValueAxis());
    let range = (max - min) * 0.1;
    axis.renderer.grid.template.strokeOpacity = 0.2;
    axis.renderer.baseGrid.disabled = true;
    axis.min = min - range;
    axis.max = max + range;
    axis.strictMinMax = true;
    this.chart.cursor = new am4charts.XYCursor();
    this.chart.cursor.yAxis = axis;
    this.chart.cursor.lineX.strokeWidth = 0;
    axis.renderer.labels.template.adapter.add("text", function(text) {
      if (text > max || text < min) {
        return "";
      } else {
        return text;
      }
    });

    return axis;
  }

  createSeries() {
    let s1 = this.chart.series.push(new am4charts.ConeSeries());
    s1.dataFields.valueY = "value1";
    s1.dataFields.categoryX = "category";
    if (this.color !== undefined) {
      s1.columns.template.fill = am4core.color(this.color);
    }
    s1.columns.template.fillOpacity = 0.9;
    s1.columns.template.strokeOpacity = 1;
    s1.columns.template.strokeWidth = 1;
    s1.tooltipText = "Value: {valueY.value}\nPercent: {percent}%";

    let s2 = this.chart.series.push(new am4charts.ConeSeries());
    s2.dataFields.valueY = "value2";
    s2.dataFields.categoryX = "category";
    s2.stacked = true;
    s2.columns.template.fill = am4core.color("#000");
    s2.columns.template.fillOpacity = 0.1;
    s2.columns.template.stroke = am4core.color("#000");
    s2.columns.template.strokeOpacity = 0.2;
    s2.columns.template.strokeWidth = 2;
  }
}

export default {
  name: "CylinderGaugeComponent",
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
    this.chartClass = new CylinderGaugeChart(
      this.$refs.chartdiv,
      this.pointId,
      this.min,
      this.max,
      this.refreshRate,
      this.color
    );
  }
};
</script>
<style scoped>
.hello {
  width: 150px;
  height: 500px;
}
.error {
  color: red;
}
</style>
