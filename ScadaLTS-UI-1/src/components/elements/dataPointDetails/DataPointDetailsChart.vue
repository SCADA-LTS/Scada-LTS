<template>
  <div class="flex-container">
    <div class="settings">
      <button @click="showModal = true">Open settings</button>
    </div>
    <div v-if="showModal" class="settings-modal">
      <div class="settings-modal-container">
        <div>
          <label for="live-rbtn">Live Chart</label>
          <input type="radio" class="radio-button" value="live" id="live-rbtn" v-model="chartType" />
          <label for="static-rbtn">Static Chart</label>
          <input
            type="radio"
            class="radio-button"
            value="static"
            id="static-rbtn"
            v-model="chartType"
          />
        </div>
        <div v-if="chartType === 'live'" class="flex-container">
          <div>
            <label for="live-sd">Start Date</label>
            <input type="text" id="live-sd" v-model="chartSettings.startDate" />
          </div>
          <div>
            <label for="live-rr">Refresh Rate</label>
            <input type="number" id="live-rr" v-model="chartSettings.refreshRate" />
          </div>
        </div>
        <div v-if="chartType === 'static'" class="flex-container">
          <div>
            <label for="static-sd">Start Date</label>
            <input type="text" id="static-sd" v-model="chartSettings.startDate" />
          </div>
          <div>
            <label for="static-ed">End Date</label>
            <input type="text" id="static-ed" v-model="chartSettings.endDate" />
          </div>
        </div>
        <div class="flex-container">
          <div>
            <label for="chart-color">Chart color</label>
            <input type="text" id="chart-color" v-model="chartSettings.chartColor" />
          </div>
          <div>
            <label for="chart-rv">Range value</label>
            <input type="text" id="chart-rv" v-model="chartSettings.rangeValue" />
          </div>
          <div v-if="chartSettings.rangeValue">
            <label for="chart-rc">Range color</label>
            <input type="text" id="chart-rc" v-model="chartSettings.rangeColor" />
          </div>
          <div v-if="chartSettings.rangeValue">
            <label for="chart-rl">Range label</label>
            <input type="text" id="chart-rl" v-model="chartSettings.rangeLabel" />
          </div>
          <div>
            <label for="chart-ssbx">Show scrollbar on X axis</label>
            <input type="checkbox" id="chart-ssbx" v-model="chartSettings.showScrollbarX" />
          </div>
          <div>
            <label for="chart-ssby">Show scrollbar on Y axis</label>
            <input type="checkbox" id="chart-ssby" v-model="chartSettings.showScrollbarY" />
          </div>
          <div>
            <label for="chart-sl">Show Legend</label>
            <input type="checkbox" id="chart-sl" v-model="chartSettings.showLegend" />
          </div>
        </div>
        <div>
          <button @click="showModal = false">Close</button>
          <button @click="saveSettings()">Save</button>
        </div>
      </div>
    </div>
    <div v-if="renderChart">
      <step-line-chart
        v-bind:point-id="pointId"
        v-bind:label="chartSettings.chartLabel"
        v-bind:start-date="chartSettings.startDate"
        v-bind:end-date="chartSettings.endDate"
        v-bind:refresh-rate="chartSettings.refreshRate"
        v-bind:color="chartSettings.chartColor"
        v-bind:range-value="chartSettings.rangeValue"
        v-bind:range-color="chartSettings.rangeColor"
        v-bind:range-label="chartSettings.rangeLabel"
        v-bind:show-scrollbar-x="chartSettings.showScrollbarX"
        v-bind:show-scrollbar-y="chartSettings.showScrollbarY"
        v-bind:show-legend="chartSettings.showLegend"
        show-reload="true"
        v-bind:width="width"
        height="600"
      />
    </div>
    <!-- <input type="text" id="label" v-model="label" /> -->
  </div>
</template>
<script>
import Axios from "axios";
import StepLineChart from "../../charts/StepLineChartComponent";
import LineChart from "../../charts/LineChartComponent";
import { width } from "@amcharts/amcharts4/.internal/core/utils/Utils";

export default {
  name: "DataPointDetailsChart",
  components: {
    StepLineChart,
    LineChart
  },
  data() {
    return {
      pointId: undefined,
      renderChart: false,
      chartType: "live",
      showModal: false,
      width: 0,
      chartSettings: {
        chartLabel: undefined,
        chartColor: undefined,
        startDate: "1-hour",
        endDate: undefined,
        refreshRate: "2000",
        rangeValue: undefined,
        rangeColor: undefined,
        rangeLabel: undefined,
        showScrollBarX: true,
        showScrollBarY: false,
        showLegend: true
      }
    };
  },
  created() {
    window.addEventListener("resize", this.handleResize);
    this.handleResize();
  },
  mounted() {
    this.pointId = document.getElementById("pointid").innerHTML;
    // if (
    //   this.$cookie.get(`Point_${this.pointId}`) !== undefined &&
    //   this.$cookie.get(`Point_${this.pointId}`) !== ""
    // ) {
    //   this.chartSettings = JSON.parse(
    //     this.$cookie.get(`Point_${this.pointId}`)
    //   );
    // }
    this.getPointType();
  },
  methods: {
    getPointType() {
      Axios.get(`/ScadaLTS/api/point_value/getValue/id/${this.pointId}`, {
        timeout: 5000,
        useCredentails: true,
        credentials: "same-origin"
      }).then(response => {
        if (response.data.type !== "AlphanumericValue") {
          //   if (this.chartSettings == undefined) {
          //     this.chartSettings = {
          //       chartLabel: undefined,
          //       chartColor: undefined,
          //       startDate: "1-hour",
          //       endDate: undefined,
          //       refreshRate: "2000",
          //       rangeValue: undefined,
          //       rangeColor: undefined,
          //       rangeLabel: undefined,
          //       showScrollBarX: true,
          //       showScrollBarY: false,
          //       showLegend: true
          //     };
          //   }
          this.renderChart = true;
        }
      });
    },
    saveSettings() {
      if (this.chartType == "static") {
        this.chartSettings.refreshRate = undefined;
      } else {
        this.chartSettings.endDate = undefined;
      }
      console.log(this.chartSettings);
      this.showModal = false;
      this.$cookie.set(
        `Point_${this.pointId}`,
        JSON.stringify(this.chartSettings),
        7
      );
    },
    handleResize() {
      this.width = window.innerWidth - 20;
    }
  }
};
</script>
<style scoped>
.flex-container {
  display: flex;
  flex-direction: column;
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
  margin-top: 100px;
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
</style>

