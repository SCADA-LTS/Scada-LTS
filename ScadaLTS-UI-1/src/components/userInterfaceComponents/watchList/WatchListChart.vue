<template>
  <div class="flex-container">
    <div class="settings" v-if="renderChart">
      <img src="/ScadaLTS/images/cog.png" @click="showModal = true" class="settings-btn">
      <img src="/ScadaLTS/images/delete.png" @click="deleteChart()" class="settings-btn">
    </div>
    <div v-if="showModal" class="settings-modal">
      <div class="settings-modal-container">
        <div class="flex-radio">
          <div class="flex-radio">
            <label for="static-rbtn">Step Line Chart</label>
            <input
              type="radio"
              class="radio-button"
              value="stepLine"
              id="typeStep-rbtn"
              v-model="chartdata.lineChart"
            />
          </div>
          <div class="flex-radio">
            <label for="type-rbtn">Line Chart</label>
            <input
              type="radio"
              class="radio-button"
              value="line"
              id="type-rbtn"
              v-model="chartdata.lineChart"
            />
          </div>
        </div>
        <hr />
        <div class="flex-radio">
          <div class="flex-radio">
            <label for="live-rbtn">Live Chart</label>
            <input
              type="radio"
              class="radio-button"
              value="live"
              id="live-rbtn"
              v-model="chartdata.chartType"
            />
          </div>
          <div class="flex-radio">
            <label for="static-rbtn">Static Chart</label>
            <input
              type="radio"
              class="radio-button"
              value="static"
              id="static-rbtn"
              v-model="chartdata.chartType"
            />
          </div>
        </div>
        <div v-if="chartdata.chartType === 'live'" class="flex-container">
          <div>
            <label for="live-sd">Start Date</label>
            <input type="text" id="live-sd" v-model="chartdata.startDate" />
          </div>
          <div>
            <label for="live-rr">Refresh Rate</label>
            <input type="number" id="live-rr" v-model="chartdata.refreshRate" />
          </div>
        </div>
        <div v-if="chartdata.chartType === 'static'" class="flex-container">
          <div>
            <label for="static-sd">Start Date</label>
            <datepicker format="yyyy/MM/dd" :monday-first="true" v-model="chartdata.startDate"></datepicker>
          </div>
          <div>
            <label for="static-ed">End Date</label>
            <datepicker format="yyyy/MM/dd" :monday-first="true" v-model="chartdata.endDate"></datepicker>
          </div>
        </div>
        <div class="flex-container">
          <div>
            <label for="chart-color">Chart color</label>
            <ColorPicker
              :width="100"
              :height="100"
              startColor="#39B54A"
              v-model="chartdata.chartColor"
            ></ColorPicker>
          </div>
          <div>
            <label for="chart-rv">Range value</label>
            <input type="text" id="chart-rv" v-model="chartdata.rangeValue" />
          </div>
          <div v-if="chartdata.rangeValue">
            <label for="chart-rc">Range color</label>
            <ColorPicker
              :width="100"
              :height="100"
              startColor="#ff0000"
              v-model="chartdata.rangeColor"
            ></ColorPicker>
          </div>
          <div v-if="chartdata.rangeValue">
            <label for="chart-rl">Range label</label>
            <input type="text" id="chart-rl" v-model="chartdata.rangeLabel" />
          </div>
          <div>
            <label for="chart-ssbx">Show scrollbar on X axis</label>
            <input type="checkbox" id="chart-ssbx" v-model="chartdata.showScrollbarX" />
          </div>
          <div>
            <label for="chart-ssby">Show scrollbar on Y axis</label>
            <input type="checkbox" id="chart-ssby" v-model="chartdata.showScrollbarY" />
          </div>
          <div>
            <label for="chart-sl">Show Legend</label>
            <input type="checkbox" id="chart-sl" v-model="chartdata.showLegend" />
          </div>
        </div>
        <div class="settings">
          <button @click="showModal = false"  class="modal-settings-btn"><img src="/ScadaLTS/images/cross.png"  class="settings-btn"> Close</button>
          <button @click="saveSettings()"  class="modal-settings-btn"><img src="/ScadaLTS/images/accept.png" class="settings-btn"> Save</button>
        </div>
      </div>
    </div>
    <div v-if="renderChart">
      <line-chart
        v-bind:point-id="chartdata.pointId"
        v-bind:label="chartdata.chartLabel"
        v-bind:start-date="chartdata.startDate"
        v-bind:end-date="chartdata.endDate"
        v-bind:refresh-rate="chartdata.refreshRate"
        v-bind:color="chartdata.chartColor"
        v-bind:range-value="chartdata.rangeValue"
        v-bind:range-color="chartdata.rangeColor"
        v-bind:range-label="chartdata.rangeLabel"
        v-bind:show-scrollbar-x="chartdata.showScrollbarX"
        v-bind:show-scrollbar-y="chartdata.showScrollbarY"
        v-bind:show-legend="chartdata.showLegend"
        show-reload="true"
        height="600"
        v-if="chartdata.lineChart == 'line'"
      />
      <step-line-chart
        v-bind:point-id="chartdata.pointId"
        v-bind:label="chartdata.chartLabel"
        v-bind:start-date="chartdata.startDate"
        v-bind:end-date="chartdata.endDate"
        v-bind:refresh-rate="chartdata.refreshRate"
        v-bind:color="chartdata.chartColor"
        v-bind:range-value="chartdata.rangeValue"
        v-bind:range-color="chartdata.rangeColor"
        v-bind:range-label="chartdata.rangeLabel"
        v-bind:show-scrollbar-x="chartdata.showScrollbarX"
        v-bind:show-scrollbar-y="chartdata.showScrollbarY"
        v-bind:show-legend="chartdata.showLegend"
        show-reload="true"
        height="600"
        v-if="chartdata.lineChart == 'stepLine'"
      />
    </div>
  </div>
</template>
<script>
import Axios from "axios";
import StepLineChart from "../../graphicViewComponents/charts/StepLineChartComponent";
import LineChart from "../../graphicViewComponents/charts/LineChartComponent";
import Datepicker from "vuejs-datepicker";
import ColorPicker from "vue-color-picker-wheel";

export default {
  name: "WatchListChart",
  components: {
    StepLineChart,
    LineChart,
    Datepicker,
    ColorPicker
  },
  props: ["chartdata"],
  data() {
    return {
      pointId: undefined,
      points: [],
      renderChart: true,
      showModal: false
    };
  },
  mounted() {
    // if(this.chartData !== undefined) {
    console.debug(this.chartdata);
    this.showChart();

    // }
  },
  methods: {
    getPointIds() {
      this.points = [];
      let wachList = document.getElementById("watchListTable");
      for (let i = 0; i < wachList.childElementCount; i++) {
        let point = wachList.children.item(i).id;
        if (document.getElementById(`${point}ChartCB`).checked) {
          this.points.push(point.slice(1));
        }
      }
      this.chartdata.pointId = this.points.toString();
    },
    showChart() {
      this.renderChart = true;
    },
    saveSettings() {
      if (this.chartdata.chartType == "static") {
        this.chartdata.refreshRate = undefined;
        let date = this.chartdata.startDate;
        this.chartdata.startDate =
          date.getFullYear() +
          "/" +
          (date.getMonth() + 1) +
          "/" +
          date.getDate();
        date = this.chartdata.endDate;
        this.chartdata.endDate =
          date.getFullYear() +
          "/" +
          (date.getMonth() + 1) +
          "/" +
          date.getDate();
      } else {
        this.chartdata.endDate = undefined;
      }
      this.showModal = false;
      this.$emit("saved", this.chartdata);
    },
    deleteChart() {
      this.$emit("deleted", this.chartdata)
    }
  }
};
</script>
<style scoped>
.flex-container {
  display: flex;
  flex-direction: column;
}
.settings {
  display: flex;
  justify-content: flex-end;
}
.flex-radio {
  display: flex;
  justify-content: space-between;
  width: 98%;
  padding: 0 1%;
}
.settings-modal {
  position: fixed;
  z-index: 999;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.4);
  transition: opacity 0.3s ease-in-out;
}
.settings-modal-container {
  width: 400px;
  margin: 0 auto;
  margin-top: 30px;
  max-height: 700px;
  padding: 20px 30px;
  background-color: #ffffff;
  border-radius: 5px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.33);
  transition: all 0.3s ease;
  display: flex;
  flex-direction: column;
}
.settings-modal-container > .flex-container > div {
  padding: 10px 0;
  display: flex;
  justify-content: space-between;
}
button {
  color: #333333;
  border: 1px solid #39b54a;
  padding: 2px;
  min-width: 100px;
}
.settings-btn {
  width: 16px;
  height: 16px;
}
</style>

