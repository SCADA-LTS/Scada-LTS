<template>
  <div class="scada-widget">
    <div class="settings">
      <p class="smallTitle">Modern Chart</p>
      <div>
        <btn class="dropdown-toggle" @click="addNewChart()">Add chart <i class="glyphicon glyphicon-plus"></i></btn>
      </div>
    </div>
    <div class="chart-container">
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
  computed: {
    charts() {
      return this.$store.getters.getCharts;
    },
    chartId() {
      return this.$store.getters.getNextChartId;
    }
  },
  mounted() {
    this.$store.commit('loadActiveUser');
    this.$store.commit('loadCharts');
  },
  methods: {
    addNewChart() {
      this.$store.commit('incrementChartId');
      let chart = {
        id: this.chartId,
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
      this.$store.commit('addChart', chart);
    },
    chartEdited(chart) {
      this.$store.commit('editChart', chart);
    },
    deleted(chart) {
      this.$store.commit('deleteChart', chart);
    },
  }
};
</script>
<style scoped>
.chart-container {
  width: 100%;
  display: flex;
  flex-direction: column;
  margin-bottom: 50px;
}
.settings {
  display: flex;
  justify-content: space-between;
  padding: 0 10px;
}
.settings p {
  margin: 0;
}
</style>