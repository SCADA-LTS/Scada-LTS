<template>
  <div class="scada-widget">
    <div class="settings">
      <p class="smallTitle">Modern Chart</p>
      <div class="flex-spacer"></div>
      <div>
        <btn class="dropdown-toggle" @click="addNewChart()">Add chart <i class="glyphicon glyphicon-plus"></i></btn>
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
        id: this.generateUniqueChartId(),
        pointId: undefined,
        chartLabel: undefined,
        chartColor: undefined,
        startDate: "1-hour",
        startTime: 1,
        startTimeMultiplier: "hour",
        endDate: undefined,
        refreshRate: "2000",
        rangeValue: undefined,
        rangeColor: undefined,
        rangeLabel: undefined,
        showScrollBarX: true,
        showScrollBarY: false,
        showLegend: true,
        debug: undefined,
        lineChart: "stepLine",
        chartType: "live",
        height: 350
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
      chart.chartLabel = document.getElementById('newWatchListName').value;
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
    },
    generateUniqueChartId() {
      if (this.charts != undefined) {
        if (this.charts.length != 0) {
          let max = 0;
          for (let i = 0; i < this.charts.length; i++) {
            if (this.charts[i].id > max) {
              max = this.charts[i].id;
            }
          }
          return max + 1;
        }
      }
      return 0;
    },
    debug() {
      console.debug(this.charts);
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
.flex-spacer {
  flex-grow: 1;
}
</style>