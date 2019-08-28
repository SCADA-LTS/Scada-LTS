<template>
  <div class="scada-widget">
    <div class="settings">
      <img src="/ScadaLTS/images/eye.png" />
      <p>Layout settings</p>
      <div>
        <label for="static-rbtn">Horizontal 1xN</label>
        <input
          type="radio"
          class="radio-button"
          value="horizontal"
          id="layout-horizonal-1"
          v-model="layout"
        />
      </div>
      <div>
        <label for="static-rbtn">Vertical 2xN</label>
        <input
          type="radio"
          class="radio-button"
          value="vertical"
          id="layout-vertical-1"
          v-model="layout"
        />
      </div>
    </div>
    <div class="chart-container" v-bind:class="chartsLayout">
      <watch-list-chart
        v-for="chart in charts"
        v-bind:key="chart.id"
        v-bind:chartdata="chart"
        @saved="chartEdited(chart)"
        @deleted="deleted(chart)"
      />
    </div>
    <div class="chart-add">
      <button @click="addNewChart()">
        Add chart
        <img src="/ScadaLTS/images/add.png" class="settings-btn" />
      </button>
    </div>
  </div>
</template>
<script>
import WatchListChart from "./WatchListChart";

export default {
  name: "WatchListChartWidget",
  components: {
    WatchListChart
  },
  data() {
    return {
      charts: [],
      layout: "horizontal",
      userName: "admin"
    };
  },
  computed: {
    chartsLayout: function() {
      return {
        horizontal: this.layout == "horizontal",
        vertical: this.layout == "vertical"
      };
    }
  },
  mounted() {
    this.userName = document
      .getElementsByClassName("userName")
      .item(0).innerText;
    if (
      this.$cookie.get(`WatchListChartDashboardLayout_${this.userName}`) !==
        null &&
      this.$cookie.get(`WatchListChartDashboardLayout_${this.userName}`) !== ""
    ) {
      this.layout = this.$cookie.get(
        `WatchListChartDashboardLayout_${this.userName}`
      );
    }
    if (
      this.$cookie.get(`WatchListChartDashboard_${this.userName}`) !== null &&
      this.$cookie.get(`WatchListChartDashboard_${this.userName}`) !== ""
    ) {
      this.charts = JSON.parse(
        this.$cookie.get(`WatchListChartDashboard_${this.userName}`)
      );
    }
  },
  methods: {
    addNewChart() {
      let chart = {
        id: 0,
        pointId: undefined,
        chartLabel: undefined,
        chartColor: undefined,
        startDate: "1",
        startDateMultiplier: "hour",
        endDate: undefined,
        refreshRate: "2000",
        rangeValue: undefined,
        rangeColor: undefined,
        rangeLabel: undefined,
        showScrollBarX: true,
        showScrollBarY: false,
        showLegend: true,
        lineChart: "stepLine",
        chartType: "live",
        id: 0
      };
      let points = [];
      let wachList = document.getElementById("watchListTable");
      for (let i = 0; i < wachList.childElementCount; i++) {
        let point = wachList.children.item(i).id;
        if (document.getElementById(`${point}ChartCB`).checked) {
          points.push(point.slice(1));
        }
      }
      chart.pointId = points.toString();
      chart.id = this.charts.length;
      this.charts.push(chart);
      this.$cookie.set(
        `WatchListChartDashboard_${this.userName}`,
        JSON.stringify(this.charts)
      );
      this.$cookie.set(
        `WatchListChartDashboardLayout_${this.userName}`,
        this.layout
      );
    },
    chartEdited(chart) {
      // console.debug(chart);
      this.charts[chart.id - 1] = chart;
      this.$cookie.set(
        `WatchListChartDashboard_${this.userName}`,
        JSON.stringify(this.charts)
      );
      this.$cookie.set(
        `WatchListChartDashboardLayout_${this.userName}`,
        this.layout
      );
    },
    deleted(chart) {
      // console.debug(chart);

      this.charts = this.charts.filter(function(element) {
        return element.id != chart.id;
      });

      this.$cookie.set(
        `WatchListChartDashboard_${this.userName}`,
        JSON.stringify(this.charts)
      );
      this.$cookie.set(
        `WatchListChartDashboardLayout_${this.userName}`,
        this.layout
      );
    }
  }
};
</script>
<style scoped>
.chart-container {
  width: 100%;
  display: flex;
  margin-bottom: 50px;
}
.chart-add {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 300px;
  padding: 1%;
  background-color: #39b54a2b;
  margin: 3% 31%;
  border: dashed 4px gray;
  border-radius: 30px;
  width: auto;
  box-sizing: border-box;
}
.chart-add button {
  color: #333;
  border: 1px solid #39b54a;
  padding: 2px;
  min-width: 100px;
}
.horizontal {
  flex-direction: column;
}
.vertical {
  flex-direction: row;
  flex-wrap: wrap;
}
.vertical > * {
  width: 50%;
}
.settings {
  display: flex;
  align-items: center;
}
.settings p {
    margin: 0;
}
.settings > * {
    padding-left: 5px;
}
.settings-btn {
  width: 16px;
  height: 16px;
}
</style>