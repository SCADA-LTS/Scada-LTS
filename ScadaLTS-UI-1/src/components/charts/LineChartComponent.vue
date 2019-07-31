<template>
  <div>
    <p>Displaying LineChart for DataPoint {{pointName}} with ID: {{propPointId}}</p>
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
import BaseChart from "./BaseChart";

am4core.useTheme(am4themes_animated);

class LineChart extends BaseChart {
  constructor(chartReference, domain = "http://localhost:8080/ScadaLTS") {
    super(chartReference, "XYChart", domain);
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
    this.chart.data = BaseChart.prepareChartData(
      BaseChart.sortMapKeys(this.pointPastValues)
    );
    this.createAxisX("DateAxis", null);
    this.createAxisY();
    this.createScrollBarsAndLegend();
    this.createExportMenu(true, "Scada_LineChart");
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
  props: [
    "propPointId",
    "startDate",
    "endDate",
    "live",
    "refreshRate",
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
      if (Number(this.polylineStep) > 1) {
        LineChart.setPolylineStep(Number(this.polylineStep));
      }
      this.chartClass = new LineChart(this.$refs.chartdiv);
      let points = this.propPointId.split(",");
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
            console.warn(
              "Not valid date pattern. Try with: <... start-date='YYYY/MM/DD'>"
            );
            promises.push(this.chartClass.loadData(points[i]));
          }
        } else if (this.startDate !== undefined && this.endDate === undefined) {
          let sDate = new Date(this.startDate);
          if (!isNaN(sDate.getDate())) {
            promises.push(this.chartClass.loadData(points[i], sDate.getTime()));
          } else {
            console.warn(
              "Not valid date pattern. Try with: <... start-date='YYYY/MM/DD'>"
            );
          }
        } else {
          promises.push(this.chartClass.loadData(points[i]));
        }
      }
      Promise.all(promises).then(response => {
        for (let i = 0; i < response.length; i++) {
          if (response[i] !== "done") {
            console.error(
              "Point given with index [" + i + "] has not been loaded!"
            );
          }
        }
        this.chartClass.showChart();
        if (this.live == "true" && this.refreshRate == undefined) {
          console.log(
            "Refresh rate for chart has not been set. Add for example: <... refresh-rate='10000'>"
          );
        }
        if (this.live == "true" && this.refreshRate != undefined) {
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
</style>
