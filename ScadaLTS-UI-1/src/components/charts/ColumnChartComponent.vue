<template>
  <div>
    <p>Displaying ColumnChart for DataPoint {{pointName}} with ID: {{propPointId}}</p>
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

class ColumnChart extends BaseChart {
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
        if (data.type === "Alphanumeric") {
          data.values.forEach(e => {
            if (this.pointPastValues.get(e.value) == undefined) {
              this.pointPastValues.set(e.value, 1);
            } else {
              this.pointPastValues.set(
                e.value,
                this.pointPastValues.get(e.value) + 1
              );
            }
          });
        }

        //TODO: Add avg, count, max, min function to columnt chart for specific period

        resolve("done");
      });
    });
  }

  static prepareChartData(map, categoryName, countName) {
    let data = [];
    map.forEach(function(value, key) {
      let jsonString =
        '{"' +
        categoryName +
        '":"' +
        key +
        '","' +
        countName +
        '":' +
        value +
        "}";
      data.push(JSON.parse(jsonString));
    });
    return data;
  }

  setupChart() {
    let categoryName = "State";
    let countName = "Count";
    this.chart.data = ColumnChart.prepareChartData(
      this.pointPastValues,
      categoryName,
      countName
    );
    this.createAxisX("CategoryAxis", categoryName);
    this.createAxisY();
    this.createScrollBarsAndLegend(false, false, false, false);
    this.createExportMenu(true, "Scada_ColumnChart");
    this.createSeries("Column", categoryName, countName, categoryName);
  }
}

export default {
  name: "ColumnChartComponent",
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
      this.chartClass = new ColumnChart(this.$refs.chartdiv);
      let promises = [];

      if (this.startDate !== undefined && this.endDate !== undefined) {
        let sDate = new Date(this.startDate);
        let eDate = new Date(this.endDate);
        if (!isNaN(sDate.getDate()) && !isNaN(eDate.getDate())) {
          promises.push(
            this.chartClass.loadData(
              this.propPointId,
              sDate.getTime(),
              eDate.getTime()
            )
          );
        } else {
          console.warn(
            "Not valid date pattern. Try with: <... start-date='YYYY/MM/DD'>"
          );
          promises.push(this.chartClass.loadData(this.propPointId));
        }
      } else if (this.startDate !== undefined && this.endDate === undefined) {
        let sDate = new Date(this.startDate);
        if (!isNaN(sDate.getDate())) {
          promises.push(
            this.chartClass.loadData(this.propPointId, sDate.getTime())
          );
        } else {
          console.warn(
            "Not valid date pattern. Try with: <... start-date='YYYY/MM/DD'>"
          );
        }
      } else {
        promises.push(this.chartClass.loadData(this.propPointId));
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
