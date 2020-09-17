<template>
  <div>
    <div class="container-fluid">
      <div class="row justify-content-between">
        <btn-group class="col-xs-3">
          <btn
            class="col-xs-4"
            input-type="radio"
            input-value="live"
            v-model="chartProperties.chartType"
          >Live</btn>
          <btn
            class="col-xs-4"
            input-type="radio"
            input-value="static"
            v-model="chartProperties.chartType"
          >Static</btn>
          <btn
            class="col-xs-4"
            input-type="radio"
            input-value="compare"
            v-model="chartProperties.chartType"
          >Compare</btn>
        </btn-group>
        <div v-if="chartProperties.chartType === 'live'" class="col-xs-8">
          <div class="col-xs-3">
            <input
              type="number"
              id="live-sd"
              v-model="chartProperties.startTime"
              placeholder="Values from last..."
              class="form-control"
              min="1"
              max="31"
            />
          </div>
          <div class="col-xs-3">
            <select v-model="chartProperties.startTimeMultiplier" class="form-control" id="live-rrs">
              <option
                v-for="option in timeOptions"
                v-bind:value="option.value"
                v-bind:key="option.id"
              >{{option.text}}</option>
            </select>
          </div>
          <div class="col-xs-6">
            <select id="live-rr" v-model="chartProperties.refreshRate" class="form-control">
              <option
                v-for="option in performanceOptions"
                v-bind:value="option.value"
                v-bind:key="option.id"
              >{{option.text}}</option>
            </select>
          </div>
        </div>
        <div v-if="chartProperties.chartType === 'static'" class="col-xs-8">
          <div class="flex-row flex-align-center col-xs-6">
            <label for="static-sd" class="small-padd">Start Date</label>
            <dropdown class="form-group">
              <div class="input-group">
                <input class="form-control" type="text" v-model="chartProperties.startDate" />
                <div class="input-group-btn">
                  <btn class="dropdown-toggle">
                    <i class="glyphicon glyphicon-calendar"></i>
                  </btn>
                </div>
              </div>
              <template slot="dropdown">
                <li>
                  <datepicker
                    v-model="chartProperties.startDate"
                    format="yyyy/MM/dd"
                    :inline="true"
                    :monday-first="true"
                  />
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
          <div class="flex-row flex-align-center col-xs-6">
            <label for="static-ed" class="small-padd">End Date</label>
            <dropdown class="form-group">
              <div class="input-group">
                <input class="form-control" type="text" v-model="chartProperties.endDate" />
                <div class="input-group-btn">
                  <btn class="dropdown-toggle">
                    <i class="glyphicon glyphicon-calendar"></i>
                  </btn>
                </div>
              </div>
              <template slot="dropdown">
                <li>
                  <datepicker
                    v-model="chartProperties.endDate"
                    format="yyyy MM dd"
                    :inline="true"
                    :monday-first="true"
                  />
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
        <div v-if="chartProperties.chartType === 'compare'" class="col-xs-8">
          <div class="container-fluid">
            <div class="row">
              <div class="col-xs-6">
                <select id="comp-el-1" v-model="chartProperties.compareDataPoint[0]" class="form-control col-xs-6">
                  <option
                    v-for="p in datapointList"
                    v-bind:value="p"
                    v-bind:key="p.id"
                  >{{p.name}}: {{p.xid}}</option>
                </select>
              </div>
              <div v-if="chartProperties.compareDataPoint[0]" class="col-xs-6">
                <!-- <label for="comp-el-1-sd" class="small-padd">Start Date</label> -->
                <dropdown class="form-group col-xs-5">
                  <div class="input-group">
                    <input class="form-control" type="text" v-model="chartProperties.compareDataPoint[0].startDate" />
                    <div class="input-group-btn">
                      <btn class="dropdown-toggle">
                        <i class="glyphicon glyphicon-calendar"></i>
                      </btn>
                    </div>
                  </div>
                  <template slot="dropdown">
                    <li>
                      <datepicker
                        v-model="chartProperties.compareDataPoint[0].startDate"
                        :format="formatDate"
                        :inline="true"
                        :monday-first="true"
                      />
                    </li>
                  </template>
                </dropdown>
                <dropdown class="col-xs-1 form-group">
                  <div class="input-group">
                    <div class="input-group-btn">
                      <btn class="dropdown-toggle">
                        <i class="glyphicon glyphicon-time"></i>
                      </btn>
                    </div>
                  </div>
                  <template slot="dropdown">
                    <li style="padding: 10px">
                      <time-picker v-model="chartProperties.compareDataPoint[0].startTime" :show-meridian="false" />
                    </li>
                  </template>
                </dropdown>
                <!-- <label for="comp-el-1-sd" class="small-padd">End Date</label> -->
                <dropdown class="form-group col-xs-5">
                  <div class="input-group">
                    <input class="form-control" type="text" v-model="chartProperties.compareDataPoint[0].endDate" />
                    <div class="input-group-btn">
                      <btn class="dropdown-toggle">
                        <i class="glyphicon glyphicon-calendar"></i>
                      </btn>
                    </div>
                  </div>
                  <template slot="dropdown">
                    <li>
                      <datepicker
                        v-model="chartProperties.compareDataPoint[0].endDate"
                        :format="formatDate"
                        :inline="true"
                        :monday-first="true"
                      />
                    </li>
                  </template>
                </dropdown>
                <dropdown class="col-xs-1 form-group">
                  <div class="input-group">
                    <div class="input-group-btn">
                      <btn class="dropdown-toggle">
                        <i class="glyphicon glyphicon-time"></i>
                      </btn>
                    </div>
                  </div>
                  <template slot="dropdown">
                    <li style="padding: 10px">
                      <time-picker v-model="chartProperties.compareDataPoint[0].endTime" :show-meridian="false" />
                    </li>
                  </template>
                </dropdown>
              </div>
            </div>
            <div class="row">
              <div class="col-xs-6">
                <select id="comp-el-2" v-model="compareElement2" class="form-control">
                  <option
                    v-for="p in datapointList"
                    v-bind:value="p"
                    v-bind:key="p.id"
                  >{{p.name}}: {{p.xid}}</option>
                </select>
              </div>
              <div v-if="compareElement2" class="col-xs-6">
                <!-- <label for="comp-el-1-sd" class="small-padd">Start Date</label> -->
                <dropdown class="form-group col-xs-5">
                  <div class="input-group">
                    <input class="form-control" type="text" v-model="compareElement2.startDate" />
                    <div class="input-group-btn">
                      <btn class="dropdown-toggle">
                        <i class="glyphicon glyphicon-calendar"></i>
                      </btn>
                    </div>
                  </div>
                  <template slot="dropdown">
                    <li>
                      <datepicker
                        v-model="compareElement2.startDate"
                        @selected="formatDate"
                        :inline="true"
                        :monday-first="true"
                      />
                    </li>
                  </template>
                </dropdown>
                <dropdown class="col-xs-1 form-group">
                  <div class="input-group">
                    <div class="input-group-btn">
                      <btn class="dropdown-toggle">
                        <i class="glyphicon glyphicon-time"></i>
                      </btn>
                    </div>
                  </div>
                  <template slot="dropdown">
                    <li style="padding: 10px">
                      <time-picker v-model="compareElement2.startTime" :show-meridian="false" />
                    </li>
                  </template>
                </dropdown>
                <!-- <label for="comp-el-1-sd" class="small-padd">End Date</label> -->
                <dropdown class="form-group col-xs-5">
                  <div class="input-group">
                    <input class="form-control" type="text" v-model="compareElement2.endDate" />
                    <div class="input-group-btn">
                      <btn class="dropdown-toggle">
                        <i class="glyphicon glyphicon-calendar"></i>
                      </btn>
                    </div>
                  </div>
                  <template slot="dropdown">
                    <li>
                      <datepicker
                        v-model="compareElement2.endDate"
                        @selected="formatDate"
                        :inline="true"
                        :monday-first="true"
                      />
                    </li>
                  </template>
                </dropdown>
                <dropdown class="col-xs-1 form-group">
                  <div class="input-group">
                    <div class="input-group-btn">
                      <btn class="dropdown-toggle">
                        <i class="glyphicon glyphicon-time"></i>
                      </btn>
                    </div>
                  </div>
                  <template slot="dropdown">
                    <li style="padding: 10px">
                      <time-picker v-model="compareElement2.endTime" :show-meridian="false" />
                    </li>
                  </template>
                </dropdown>
              </div>
            </div>
          </div>
        </div>
        <div class="col-xs-1">
          <btn class="dropdown-toggle" @click="updateSettings()" id="updateSettingsBtn">
            <i class="glyphicon glyphicon-refresh"></i>
          </btn>
          <tooltip text="Apply changes to the Chart" target="#updateSettingsBtn" />

          <btn class="dropdown-toggle" @click="showSettings()" id="showSettingsBtn">
            <i class="glyphicon glyphicon-cog"></i>
          </btn>
          <tooltip text="More settings" target="#showSettingsBtn" />
        </div>        
      </div>
      <div class="row" v-if="settingsVisible">
        <h2>Data Points settings - {{watchlistName}}:</h2>
        <div class="container">
          <div class="col-xs-12 justify-content-md-center">
            <tabs justified>
              <tab v-for="s in series" :title="s.name" :key="s.id">
                <div class="col-xs-12">
                  <p class="col-xs-6">Name:</p>
                  <div class="col-xs-6">
                    <input class="form-control" v-model="s.name" />
                  </div>
                </div>
                <div class="col-xs-12">
                  <p class="col-xs-6">Series Type:</p>
                  <btn-group class="col-xs-6">
                    <btn
                      class="col-xs-6"
                      input-type="radio"
                      input-value="LineSeries"
                      v-model="s.type"
                    >Line Series</btn>
                    <btn
                      class="col-xs-6"
                      input-type="radio"
                      input-value="StepLineSeries"
                      v-model="s.type"
                    >Step Line Series</btn>
                  </btn-group>
                </div>
                <div class="col-xs-12">
                  <p class="col-xs-6">Y-Axis:</p>
                  <btn-group class="col-xs-6">
                    <btn class="col-xs-3" input-type="radio" input-value="valueAxis1" v-model="s.yAxis">1</btn>
                    <btn class="col-xs-3" input-type="radio" input-value="valueAxis2" v-model="s.yAxis">2</btn>
                    <btn
                      class="col-xs-3"
                      input-type="radio"
                      input-value="logAxis"
                      v-model="s.yAxis"
                    >Logarithmic</btn>
                    <btn
                      class="col-xs-3"
                      input-type="radio"
                      input-value="binAxis"
                      v-model="s.yAxis"
                    >Binary</btn>
                  </btn-group>
                </div>
                <div class="col-xs-12">
                  <p class="col-xs-6">X-Axis:</p>
                  <div class="col-xs-3">
                    <input class="form-control" v-model="s.dataFields.dateX" />
                  </div>
                  <btn-group class="col-xs-3">
                    <btn class="col-xs-6" input-type="radio" input-value="dateAxis1" v-model="s.xAxis">1</btn>
                    <btn class="col-xs-6" input-type="radio" input-value="dateAxis2" v-model="s.xAxis">2</btn>
                  </btn-group>
                </div>
                <div class="col-xs-12">
                  <p class="col-xs-6">Stroke color:</p>
                  <div class="col-xs-6">
                    <verte
                      picker="square"
                      v-model="s.stroke"
                      model="hex"
                      :showHistory="null"
                      menuPosition="top"
                    ></verte>
                  </div>
                </div>
                <div class="col-xs-12">
                  <p class="col-xs-6">Stroke width:</p>
                  <div class="col-xs-6">
                    <input class="form-control" type="number" v-model="s.strokeWidth" />
                  </div>
                </div>
                <div class="col-xs-12">
                  <p class="col-xs-6">Stroke tension:</p>
                  <div class="col-xs-6">
                    <input class="form-control" type="number" :min="0" :max="1" v-model="s.tensionX" />
                  </div>
                </div>
                <div class="col-xs-12">
                  <p class="col-xs-6">Fill color::</p>
                  <div class="col-xs-6">
                    <verte
                      picker="square"
                      v-model="s.fill"
                      model="hex"
                      :showHistory="null"
                      menuPosition="top"
                    ></verte>
                  </div>
                </div>
                <div class="col-xs-12">
                  <p class="col-xs-6">Fill opacity:</p>
                  <div class="col-xs-6">
                    <input
                      class="form-control"
                      type="number"
                      :min="0"
                      :max="1"
                      v-model="s.fillOpacity"
                    />
                  </div>
                </div>
              </tab>
            </tabs>
          </div>
        </div>
        <div class="container">
          <div class="row justify-content-md-center">
            <div class="col-xs-4">
              <btn
                type="primary"
                size="sm"
                class="dropdown-toggle col-xs-12"
                @click="saveSettings()"
                id="saveSettingsBtn"
              >
                <i class="glyphicon glyphicon-ok"></i>
              </btn>
              <tooltip text="Apply settings" target="#saveSettingsBtn" />
            </div>
            <div class="col-xs-4">
              <btn size="sm" class="dropdown-toggle col-xs-12" @click="cancelSettings()">
                <i class="glyphicon glyphicon-remove"></i>
              </btn>
            </div>
            <div class="col-xs-4">
              <btn
                size="sm"
                class="dropdown-toggle col-xs-12"
                @click="clearChart()"
                id="clearChartBtn"
              >
                <i class="glyphicon glyphicon-erase"></i>
              </btn>
              <tooltip text="Reset chart settings to default" target="#clearChartBtn" />
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="hello" v-bind:style="{height: 600 + 'px', width: this.width + 'px'}" ref="chartdiv"></div>
    <div v-if="errorMessage">
      <p class="error">{{errorMessage}}</p>
    </div>
    <div v-if="showReload">
      <button v-on:click="reload()">Reload</button>
    </div>
  </div>
</template>
<script>
import * as am4core from "@amcharts/amcharts4/core";
import * as am4charts from "@amcharts/amcharts4/charts";
import am4themes_animated from "@amcharts/amcharts4/themes/animated";
import Datepicker from "vuejs-datepicker";
import verte from "verte";
import httpClient from "axios";
import Axios from "axios";
import BaseChart from "../amcharts/BaseChart";
import i18n from '../../i18n'
am4core.useTheme(am4themes_animated);

class JsonChart extends BaseChart {
  constructor(chartReference, color, domain = ".", jsonConfig) {
    super(chartReference, "JsonChart", color, ".", jsonConfig, "XYChart");
  }

  loadData(pointId, startTimestamp, endTimestamp, exportId) {
    return new Promise((resolve, reject) => {
      super
        .loadData(pointId, startTimestamp, endTimestamp, exportId)
        .then((data) => {
          if (this.pointCurrentValue.get(pointId) == undefined) {
            this.pointCurrentValue.set(pointId, {
              name: data.name,
              suffix: data.textRenderer.suffix,
              type: data.type,
              labels: new Map(),
            });
          }
          if (data.type === "Multistate") {
            let customLabels = data.textRenderer.multistateValues;
            if (
              data.textRenderer.typeName === "textRendererMultistate" &&
              customLabels !== undefined
            ) {
              let labelsMap = new Map();
              for (let i = 0; i < customLabels.length; i++) {
                labelsMap.set(
                  String(customLabels[i].key),
                  customLabels[i].text
                );
              }
              this.pointCurrentValue.get(pointId).labels = labelsMap;
            }
          }
          if (data.type === "Binary") {
            if (data.textRenderer.typeName === "textRendererBinary") {
              let labelsMap = new Map();
              labelsMap.set("0", data.textRenderer.zeroLabel);
              labelsMap.set("1", data.textRenderer.oneLabel);
              this.pointCurrentValue.get(pointId).labels = labelsMap;
            }
          }
          data.values.forEach((e) => {
            this.addValue(e, data.name, this.pointPastValues);
          });
          resolve("done");
        });
    });
  }

  setupChart(chartType) {
    if (chartType === "compare") {
      this.chart.data = this.prepareCompareChartData(
        BaseChart.sortMapKeys(this.pointPastValues)
      );
    } else {
      this.chart.data = BaseChart.prepareChartData(
        BaseChart.sortMapKeys(this.pointPastValues)
      );
    }
  }
  prepareCompareChartData(map) {
    let data = []; //date|date2:<time>, <datapointName>:<datapointValue>
    let baseKey = undefined;
    map.forEach(function (value, key) {
      let jsonString;
      value.forEach((e) => {
        if (baseKey === undefined) {
          baseKey = e.name;
        }
        if (baseKey === e.name) {
          jsonString = `{"date":${key}, "${e.name}":${e.value}}`;
        } else {
          jsonString = `{"date2":${key}, "${e.name}":${e.value}}`;
        }
        data.push(JSON.parse(jsonString));
      });
    });
    return data;
  }
}
export default {
  name: "WatchListJsonChart",
  components: {
    Datepicker,
  },
  props: [
    "pointId",
    "watchlistName",

    "startDate",
    "endDate",
    "refreshRate",
    "width",
    "height",
    "rangeValue",
    "rangeColor",
    "rangeLabel",
    "showReload",
    "jsonConfig",
  ],
  data() {
    return {
      settingsVisible: false,
      // chartProperties: {
      //   chartType: "live",
      //   refreshRate: 10000,
      //   startDate: undefined,
      //   endDate: undefined,
      //   compareDataPoint: [null, null]
      // },
      errorMessage: undefined,
      chartClass: undefined,
      performanceOptions: [
        { id: 0, text: "Update every 1 second", value: 1000 },
        { id: 1, text: "Update every 2 seconds", value: 2000 },
        { id: 2, text: "Update every 5 seconds", value: 5000 },
        { id: 3, text: "Update every 10 seconds", value: 10000 },
      ],
      timeOptions: [
        { id: 0, text: "Hour(s)", value: "hour" },
        { id: 1, text: "Day(s)", value: "day" },
        { id: 2, text: "Weak(s)", value: "weak" },
        { id: 3, text: "Month(s)", value: "month" },
      ],
      startTime: new Date(),
      endTime: new Date(),
      series: [],
      compareElement1: undefined,
      compareElement2: undefined,
      dualAxis: false,
    };
  },
  mounted() {
    this.initializeDataPoints();
    this.prepareChart();
  },
  computed: {
    chartConfiguration() { return this.$store.state.modernWatchList.chartConfiguration },
    chartProperties() { return this.$store.state.modernWatchList.chartProperties },
    datapointList() { return this.$store.state.modernWatchList.datapointList },
    isChartLoaded() { return this.$store.state.modernWatchList.isChartLoaded },
  },
  methods: {
    showSettings() {
      this.settingsVisible = !this.settingsVisible;
      this.series = this.chartConfiguration.series;
    },
    cancelSettings() {
      this.settingsVisible = false;
    },
    saveSettings() {
      // this.saveChart();
      console.log("savesettings")
      console.log(this.series)
      this.$store.commit('chartConfigurationSeriesUpdate', this.series)
      this.$store.commit('chartSaveConfiguration', this.watchlistName);
      this.settingsVisible = false;
      this.updateSettings();
    },
    initializeDataPoints() {
      let points = this.pointId.split(",");
      for (let i = 0; i < points.length; i++) {
        this.$store.dispatch('getDatapointInfo', points[i]);
      }
    },
    updateSettings() {
      if (this.chartProperties.chartType == "static") {
        this.chartProperties.refreshRate = undefined;
        let sdString = this.formatDate(new Date(this.chartProperties.startDate));
        let edString = this.formatDate(new Date(this.chartProperties.endDate));
        this.chartProperties.startDate = `${sdString} ${this.startTime.getHours()}:${this.startTime.getMinutes()}`;
        this.chartProperties.endDate = `${edString} ${this.endTime.getHours()}:${this.endTime.getMinutes()}`;
      } else if (this.chartProperties.chartType == "live") {
        this.chartProperties.endDate = undefined;
        this.chartProperties.startDate = `${this.chartProperties.startTime}-${this.chartProperties.startTimeMultiplier}`;
      } else {
        this.chartProperties.endDate = undefined;
        this.chartProperties.startDate = undefined;
        this.chartProperties.refreshRate = undefined;
      }
      this.applySettings(this.chartProperties);
    },
    applySettings(chartData) {
      // this.saveChart();
      this.$store.commit('chartPropertiesUpdate', chartData)
      // this.$store.commit('chartSaveConfiguration');
      this.startDate = chartData.startDate;
      this.endDate = chartData.endDate;
      this.refreshRate = chartData.refreshRate;
      this.chartClass.chart.dispose();
      this.prepareChart();
    },
    generateChart(jsonConfig) {
      this.chartClass = new JsonChart(
        this.$refs.chartdiv,
        this.color,
        ".",
        jsonConfig
      );

      let promises = [];
      if (this.chartProperties.chartType === "compare") {
        let ce1sdString = this.formatDate(
          new Date(this.chartProperties.compareDataPoint[0].startDate)
        );
        let ce1edString = this.formatDate(
          new Date(this.chartProperties.compareDataPoint[0].endDate)
        );
        this.chartProperties.compareDataPoint[0].startDate = `${ce1sdString} ${this.chartProperties.compareDataPoint[0].startTime.getHours()}:${this.chartProperties.compareDataPoint[0].startTime.getMinutes()}`;
        this.chartProperties.compareDataPoint[0].endDate = `${ce1edString} ${this.chartProperties.compareDataPoint[0].endTime.getHours()}:${this.chartProperties.compareDataPoint[0].endTime.getMinutes()}`;
        promises.push(
          this.chartClass.loadData(
            this.chartProperties.compareDataPoint[0].id,
            this.chartProperties.compareDataPoint[0].startDate,
            this.chartProperties.compareDataPoint[0].endDate,
            false
          )
        );
        let ce2sdString = this.formatDate(
          new Date(this.compareElement2.startDate)
        );
        let ce2edString = this.formatDate(
          new Date(this.compareElement2.endDate)
        );
        this.compareElement2.startDate = `${ce2sdString} ${this.compareElement2.startTime.getHours()}:${this.compareElement2.startTime.getMinutes()}`;
        this.compareElement2.endDate = `${ce2edString} ${this.compareElement2.endTime.getHours()}:${this.compareElement2.endTime.getMinutes()}`;
        promises.push(
          this.chartClass.loadData(
            this.compareElement2.id,
            this.compareElement2.startDate,
            this.compareElement2.endDate,
            false
          )
        );
      } else {
        let points = this.pointId.split(",");
        for (let i = 0; i < points.length; i++) {
          promises.push(
            this.chartClass.loadData(
              points[i],
              this.startDate,
              this.endDate,
              false
            )
          );
        }
      }

      Promise.all(promises).then((response) => {
        for (let i = 0; i < response.length; i++) {
          if (response[i] !== "done") {
            this.errorMessage =
              "Point given with index [" + i + "] has not been loaded!";
          }
        }

        this.chartClass.setupChart(this.chartProperties.chartType); // Display Chart
        //   if (this.rangeValue !== undefined) {
        //     this.chartClass.addRangeValue(
        //       Number(this.rangeValue),
        //       this.rangeColor,
        //       this.rangeLabel
        //     );
        //   }
        // this.$store.commit('chartSaveConfiguration');
        // this.saveChart();
        if (this.refreshRate != undefined) {
          this.chartClass.startLiveUpdate(
            Number(this.refreshRate),
            false
          );
        }
      });
    },
    prepareChart() {
      this.$store.commit('chartLoadConfiguration', this.watchlistName)
      if(this.isChartLoaded) {
        console.warn("Chart Loaded");
        this.chartProperties = this.chartProperties;
        this.generateChart(this.chartConfiguration);
      } else {
        let initPromises = [];
        if(this.chartType === "compare") {
          // Generate series for both DataPoints //
          // initializePointsPromises.push(
          //   this.initializePoints(this.compareElement1.id)
          // );
          // initializePointsPromises.push(
          //   this.initializePoints(this.compareElement2.id)
          // );
        } else {
          // Generate series for each DataPoint //
          let pointId = this.pointId.split(",");
          for (let i = 0; i < pointId.length; i++) {
            initPromises.push(this.$store.dispatch('chartInitDatapointSeries', pointId[i]));
          }
        }
        Promise.all(initPromises).then((r) => {
          this.generateChart(this.chartConfiguration);
        }).catch((e)=> {
          console.error(e)
        })
      }
    },
    reset() {
      return new Promise((resolve, reject) => {
        this.chartClass.chart.dispose();
        this.chartClass = null;
        this.pointId = null;
        this.series = [];
        resolve(true);
      }).catch(() => {
        reject(false);
      });
    },
    reload() {
      this.prepareChart();
    },
    // loadChart() {
    //   let loadData = JSON.parse(
    //     localStorage.getItem(`MWL_${this.watchlistName}`)
    //   );
    //   if (loadData !== null) {
    //     this.series = loadData.series;
    //     this.chartProperties = loadData.chartProperties;
    //     return true;
    //   } else {
    //     return false;
    //   }
    // },
    // saveChart() {
    //   let saveData = {
    //     chartProperties: this.chartProperties,
    //     series: JSON.parse(JSON.stringify(this.series)),
    //   };
    //   localStorage.setItem(
    //     `MWL_${this.watchlistName}`,
    //     JSON.stringify(saveData)
    //   );
    // },
    clearChart() {
      localStorage.removeItem(`MWL_${this.watchlistName}`);
      this.$notify("Chart settings has been cleared from the memory!");
    },
    formatDate(date) {
      return (
        date.getUTCFullYear() +
        "/" +
        ("0" + (date.getUTCMonth() + 1)).slice(-2) +
        "/" +
        ("0" + date.getUTCDate()).slice(-2)
      );
    },
  },
  beforeDestroy() {
    this.chartClass.chart.dispose();
  },
};
</script>
<style scoped>
.hello {
  min-width: 650px;
  height: 500px;
}
p {
  text-align: center;
  padding-top: 10px;
}
.error {
  color: red;
}
</style>