<template>
  <div class="flex-container">
    <div class="flex-row">
      <div class="flex-row flex-grow-2">
        <div>
          <btn-group>
            <btn input-type="radio" input-value="live" v-model="chartSettings.chartType">Live chart</btn>
            <btn
              input-type="radio"
              input-value="static"
              v-model="chartSettings.chartType"
            >Static chart</btn>
          </btn-group>
        </div>
        <div class="flex-row flex-grow-2">
          <div v-if="chartSettings.chartType === 'live'" class="flex-row flex-grow-2">
            <input
              type="number"
              id="live-sd"
              v-model="chartSettings.startTime"
              placeholder="Values from last..."
              class="form-control"
              min="1"
              max="4"
            />
            <select v-model="chartSettings.startTimeMultiplier" class="form-control" id="live-rrs">
              <option
                v-for="option in timeOptions"
                v-bind:value="option.value"
                v-bind:key="option.id"
              >{{option.text}}</option>
            </select>
            <select id="live-rr" v-model="chartSettings.refreshRate" class="form-control">
              <option
                v-for="option in performanceOptions"
                v-bind:value="option.value"
                v-bind:key="option.id"
              >{{option.text}}</option>
            </select>
          </div>
          <div v-if="chartSettings.chartType === 'static'" class="flex-row">
            <div class="flex-row flex-align-center">
              <label for="static-sd" class="small-padd">Start Date</label>
              <dropdown class="form-group">
                <div class="input-group">
                  <input class="form-control" type="text" v-model="chartSettings.startDate">
                  <div class="input-group-btn">
                    <btn class="dropdown-toggle"><i class="glyphicon glyphicon-calendar"></i></btn>
                  </div>
                </div>
                <template slot="dropdown">
                  <li>
                    <date-picker v-model="chartSettings.startDate" format="yyyy/MM/dd" :week-starts-with="1"/>
                  </li>
                </template>
              </dropdown>
              <dropdown class="form-group">
                <div class="input-group">
                  <div class="input-group-btn">
                    <btn class="dropdown-toggle">
                      <i class="glyphicon glyphicon-time"></i>
                    </btn>
                  </div>
                </div>
                <template slot="dropdown">
                  <li style="padding: 10px">
                    <time-picker v-model="startTime" :show-meridian="false" />
                  </li>
                </template>
              </dropdown>
            </div>
            <div class="flex-row flex-align-center">
              <label for="static-ed" class="small-padd">End Date</label>
              <dropdown class="form-group">
                <div class="input-group">
                  <input class="form-control" type="text" v-model="chartSettings.endDate">
                  <div class="input-group-btn">
                    <btn class="dropdown-toggle"><i class="glyphicon glyphicon-calendar"></i></btn>
                  </div>
                </div>
                <template slot="dropdown">
                  <li>
                    <date-picker v-model="chartSettings.endDate" format="yyyy/MM/dd" :week-starts-with="1"/>
                  </li>
                </template>
              </dropdown>
              <dropdown class="form-group">
                <div class="input-group">
                  <div class="input-group-btn">
                    <btn class="dropdown-toggle">
                      <i class="glyphicon glyphicon-time"></i>
                    </btn>
                  </div>
                </div>
                <template slot="dropdown">
                  <li style="padding: 10px">
                    <time-picker v-model="endTime" :show-meridian="false" />
                  </li>
                </template>
              </dropdown>
            </div>
          </div>
        </div>
        <p class="smallTitle chart-title">{{chartSettings.chartLabel}}</p>
        <btn class="dropdown-toggle" @click="updateSettings()">
          <i class="glyphicon glyphicon-refresh"></i>
        </btn>
        <btn class="dropdown-toggle" @click="showSettings()">
          <i class="glyphicon glyphicon-cog"></i>
        </btn>
        <btn class="dropdown-toggle" @click="deleteChart()">
          <i class="glyphicon glyphicon-trash"></i>
        </btn>
      </div>
    </div>
    <section>
      <modal v-model="open" title="Chart settings" @hide="callback" ref="modal" id="modal-demo" size="sm" :backdrop="false">
        <form class="form-inline">
          <btn-group class="flex-row flex-content-center">
            <btn input-type="radio" input-value="stepLine" v-model="chartSettings.lineChart">Step line chart</btn>
            <btn input-type="radio" input-value="line" v-model="chartSettings.lineChart">Line chart</btn>
          </btn-group>
          <hr />
          <div class="flex-container">
            <label for="chart-label">Chart title</label>
            <input type="text" id="chart-label" v-model="chartSettings.chartLabel" class="form-control" />
          </div>
          <hr />
          <div class="flex-container">
            <label for="chart-color">Chart color</label>
            <ColorPicker
              :width="100"
              :height="100"
              startColor="#39B54A"
              v-model="chartSettings.chartColor"
            ></ColorPicker>
          </div>
          <hr />
          <div class="flex-container">
            <div class="flex-container">
              <label for="chart-rv">Range value</label>
              <input type="text" id="chart-rv" v-model="chartSettings.rangeValue" class="form-control" />
            </div>
            <div v-if="chartSettings.rangeValue" class="flex-container">
              <label for="chart-rc">Range color</label>
              <ColorPicker
                :width="100"
                :height="100"
                startColor="#ff0000"
                v-model="chartSettings.rangeColor"
              ></ColorPicker>
            </div>
            <div v-if="chartSettings.rangeValue" class="flex-container">
              <label for="chart-rl">Range label</label>
              <input type="text" id="chart-rl" v-model="chartSettings.rangeLabel" class="form-control" />
            </div>
          </div>
          <hr />
          <div class="flex-container">
            <label for="chart-height">Chart heigh</label>
            <input type="number" id="chart-height" v-model="chartSettings.height" class="form-control" />
          </div>
        </form>
      </modal>
    </section>
    <div v-if="renderChart">
      <line-chart
        v-bind:point-id="chartdata.pointId"
        v-bind:label="''"
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
        v-bind:height="chartdata.height"
        v-if="chartdata.lineChart == 'line'"
        ref="line_child"
      />
      <step-line-chart
        v-bind:point-id="chartdata.pointId"
        v-bind:label="''"
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
        v-bind:height="chartdata.height"
        v-bind:show-debug="chartdata.debug"
        v-if="chartdata.lineChart == 'stepLine'"
        ref="step_line_child"
      />
    </div>
  </div>
</template>
<script>
import Axios from "axios";
import StepLineChart from "../amcharts/StepLineChartComponent";
import LineChart from "../amcharts/LineChartComponent";
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
      open: false,
      pointId: undefined,
      chartSettings: {
        lineChart: "stepLine",
        chartType: "live"
      },
      points: [],
      renderChart: true,
      showModal: false,
      performanceOptions: [
        { id: 0, text: "Update every 1 second", value: 1000 },
        { id: 1, text: "Update every 2 seconds", value: 2000 },
        { id: 2, text: "Update every 5 seconds", value: 5000 },
        { id: 3, text: "Update every 10 seconds", value: 10000 }
      ],
      timeOptions: [
        { id: 0, text: "Hour(s)", value: "hour" },
        { id: 1, text: "Day(s)", value: "day" },
        { id: 2, text: "Weak(s)", value: "weak" },
        { id: 3, text: "Month(s)", value: "month" }
      ],
      startTime: new Date(),
      endTime: new Date()
    };
  },
  mounted() {
    this.showChart();
    this.chartSettings = this.chartdata;
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
    callback(msg) {
      if (msg === "ok") {
        this.updateSettings();
      } else {
        this.cancelSettings();
      }
    },
    updateSettings() {
      this.renderChart = false;
      this.chartSettings = this.chartdata;
      if (this.chartSettings.chartType == "static") {
        this.chartSettings.refreshRate = undefined;
        this.chartSettings.startDate = `${this.chartSettings.startDate.substring(0,10)} ${this.startTime.getHours()}:${this.startTime.getMinutes()}`;
        this.chartSettings.endDate = `${this.chartSettings.endDate.substring(0,10)} ${this.endTime.getHours()}:${this.endTime.getMinutes()}`;
      } else {
        this.chartSettings.endDate = undefined;
        this.chartSettings.startDate = `${this.chartdata.startTime}-${this.chartdata.startTimeMultiplier}`;
      }
      if (
        this.chartSettings.chartColor == "#3973b5" ||
        this.chartSettings.chartColor == "#39B54A"
      ) {
        this.chartSettings.chartColor = undefined;
      }
      if (this.chartSettings.rangeLabel == "DEBUG_CHART") {
        this.chartSettings.debug = true;
      }
      this.chartdata = this.chartSettings;
      setTimeout(() => {this.renderChart = true}, 1000);
      this.$emit("saved", this.chartdata);
    },
    showChart() {
      this.renderChart = true;
    },
    showSettings() {
      this.chartSettings = this.chartdata;
      this.open = true;
      this.renderChart = false;
    },
    cancelSettings() {
      if (
        this.chartdata.chartColor == "#3973b5" ||
        this.chartdata.chartColor == "#39B54A"
      ) {
        this.chartdata.chartColor = undefined;
      }
      this.showModal = false;
      this.renderChart = true;
    },
    saveSettings() {
      if (this.chartSettings.chartType == "static") {
        this.chartSettings.refreshRate = undefined;
      } else {
        this.chartSettings.endDate = undefined;
        this.chartSettings.startDate = `${this.chartdata.startTime}-${this.chartdata.startTimeMultiplier}`;
      }
      if (
        this.chartSettings.chartColor == "#3973b5" ||
        this.chartSettings.chartColor == "#39B54A"
      ) {
        this.chartSettings.chartColor = undefined;
      }
      if (this.chartSettings.rangeLabel == "DEBUG_CHART") {
        this.chartSettings.debug = true;
      }
      this.chartdata = this.chartSettings;
      this.showModal = false;
      this.renderChart = true;
      this.$emit("saved", this.chartdata);
    },
    deleteChart() {
      this.$emit("deleted", this.chartdata);
    }
  }
};
</script>
<style scoped>
.flex-row {
  display: flex;
  flex-direction: row;
}
.flex-row > .flex-row {
  padding: 0 10px;
}
.flex-grow-2 {
  flex-grow: 2;
}
.form-group {
  margin-bottom: 0;
}
.flex-container {
  display: flex;
  flex-direction: column;
}
.flex-align-center {
  align-items: center;
}
.flex-content-center {
  justify-content: center;
}
.chart-title {
  flex-grow: 1;
  text-align: right;
  padding-right: 15px;
  margin-bottom: 0px;
}
.small-padd {
  padding-right: 10px;
}
#live-sd {
  width: 10%;
}
#live-rrs {
  width: 15%;
}
</style>