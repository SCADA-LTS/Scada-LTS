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
import Axios from "axios";
import am4themes_animated from "@amcharts/amcharts4/themes/animated";

am4core.useTheme(am4themes_animated);

export class JsonChart {
  constructor(
    chartReference,
    chartType,
    jsonConfig,
    domain = "."
  ) {
    if (chartType === "XYChart") {
      chartType = am4charts.XYChart;
    } else if (chartType === "PieChart") {
      chartType = am4charts.PieChart;
    } else if (chartType === "GaugeChart") {
      chartType = am4charts.GaugeChart;
    }
    jsonConfig = JSON.parse(jsonConfig);
    this.chart = am4core.createFromConfig(
      jsonConfig,
      chartReference,
      chartType
    );
    this.domain = domain;
  }

  getData(pointId, startTimestamp, endTimestamp) {
    if (startTimestamp !== undefined && endTimestamp !== undefined) {
      startTimestamp = new Date(startTimestamp).getTime();
      endTimestamp = new Date(endTimestamp).getTime();
      if (isNaN(startTimestamp) || isNaN(endTimestamp)) {
        startTimestamp = new Date().getTime - 3600000;
        endTimestamp = new Date().getTime();
      }
    } else {
      startTimestamp = new Date().getTime - 3600000;
      endTimestamp = new Date().getTime();
    }
    return new Promise((resolve, reject) => {
      try {
        Axios.get(
          `${this.domain}/api/point_value/getValuesFromTimePeriod/${pointId}/${startTimestamp}/${endTimestamp}`,
          { timeout: 5000, useCredentails: true, credentials: "same-origin" }
        )
          .then(response => {
            resolve(response.data);
          })
          .catch(webError => {
            reject(webError);
          });
      } catch (error) {
        reject(error);
      }
    });
  }

  loadDateLineData(pointId, startTimestamp, endTimestamp) {
    this.getData(pointId, startTimestamp, endTimestamp).then(data => {
      this.chart.data = data.values;
      let title = this.chart.titles.create();
      title.text = data.name;
      let label = this.chart.chartContainer.createChild(am4core.Label);
      label.text = `DP_XID - ${data.xid}`;
      label.align = "center";
    });
  }
}

export default {
  name: "JsonChartComponent",
  props: [
    "pointId",
    "label",
    "startDate",
    "endDate",
    "width",
    "height",
    "chartType",
    "jsonConfig"
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
      this.chartClass = new JsonChart(
        this.$refs.chartdiv,
        this.chartType,
        this.jsonConfig
      );

      this.chartClass.loadDateLineData(
        this.pointId,
        this.startDate,
        this.endDate
      );
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
