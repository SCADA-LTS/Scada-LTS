<template>
  <div>
    <div class="container-fluid">
      <div class="row justify-content-between">
        <btn-group class="col-xs-3">
          <btn
            class="col-xs-4"
            input-type="radio"
            input-value="live"
            v-model="chartSettings.chartType"
          >Live chart</btn>
          <btn
            class="col-xs-4"
            input-type="radio"
            input-value="static"
            v-model="chartSettings.chartType"
          >Static chart</btn>
          <btn
            class="col-xs-4"
            input-type="radio"
            input-value="compare"
            v-model="chartSettings.chartType"
          >Compare chart</btn>
        </btn-group>
        <div v-if="chartSettings.chartType === 'live'" class="col-xs-7">
          <div class="col-xs-3">
            <input
              type="number"
              id="live-sd"
              v-model="chartSettings.startTime"
              placeholder="Values from last..."
              class="form-control"
              min="1"
              max="31"
            />
          </div>
          <div class="col-xs-3">
            <select v-model="chartSettings.startTimeMultiplier" class="form-control" id="live-rrs">
              <option
                v-for="option in timeOptions"
                v-bind:value="option.value"
                v-bind:key="option.id"
              >{{option.text}}</option>
            </select>
          </div>
          <div class="col-xs-6">
            <select id="live-rr" v-model="chartSettings.refreshRate" class="form-control">
              <option
                v-for="option in performanceOptions"
                v-bind:value="option.value"
                v-bind:key="option.id"
              >{{option.text}}</option>
            </select>
          </div>
        </div>
        <div v-if="chartSettings.chartType === 'static'" class="flex-row col-xs-7">
          <div class="flex-row flex-align-center col-xs-6">
            <label for="static-sd" class="small-padd">Start Date</label>
            <dropdown class="form-group">
              <div class="input-group">
                <input class="form-control" type="text" v-model="chartSettings.startDate" />
                <div class="input-group-btn">
                  <btn class="dropdown-toggle">
                    <i class="glyphicon glyphicon-calendar"></i>
                  </btn>
                </div>
              </div>
              <template slot="dropdown">
                <li>
                  <datepicker
                    v-model="chartSettings.startDate"
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
                <input class="form-control" type="text" v-model="chartSettings.endDate" />
                <div class="input-group-btn">
                  <btn class="dropdown-toggle">
                    <i class="glyphicon glyphicon-calendar"></i>
                  </btn>
                </div>
              </div>
              <template slot="dropdown">
                <li>
                  <datepicker
                    v-model="chartSettings.endDate"
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
        <div v-if="chartSettings.chartType === 'compare'" class="col-xs-7">
          <div class="container-fluid">
            <div class="row">
              <div class="col-xs-6">
                <select id="comp-el-1" v-model="compareElement1" class="form-control col-xs-6">
                  <option
                    v-for="p in pointData"
                    v-bind:value="p"
                    v-bind:key="p.id"
                  >{{p.name}}: {{p.xid}}</option>
                </select>
              </div>
              <div v-if="compareElement1" class="col-xs-6">
                <!-- <label for="comp-el-1-sd" class="small-padd">Start Date</label> -->
                <dropdown class="form-group col-xs-5">
                  <div class="input-group">
                    <input class="form-control" type="text" v-model="compareElement1.startDate" />
                    <div class="input-group-btn">
                      <btn class="dropdown-toggle">
                        <i class="glyphicon glyphicon-calendar"></i>
                      </btn>
                    </div>
                  </div>
                  <template slot="dropdown">
                    <li>
                      <datepicker
                        v-model="compareElement1.startDate"
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
                      <time-picker v-model="compareElement1.startTime" :show-meridian="false" />
                    </li>
                  </template>
                </dropdown>
                <!-- <label for="comp-el-1-sd" class="small-padd">End Date</label> -->
                <dropdown class="form-group col-xs-5">
                  <div class="input-group">
                    <input class="form-control" type="text" v-model="compareElement1.endDate" />
                    <div class="input-group-btn">
                      <btn class="dropdown-toggle">
                        <i class="glyphicon glyphicon-calendar"></i>
                      </btn>
                    </div>
                  </div>
                  <template slot="dropdown">
                    <li>
                      <datepicker
                        v-model="compareElement1.endDate"
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
                      <time-picker v-model="compareElement1.endTime" :show-meridian="false" />
                    </li>
                  </template>
                </dropdown>
              </div>
            </div>
            <div class="row">
              <div class="col-xs-6">
                <select id="comp-el-2" v-model="compareElement2" class="form-control">
                  <option
                    v-for="p in pointData"
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
        <btn class="dropdown-toggle col-xs-1" @click="updateSettings()" id="updateSettingsBtn">
          <i class="glyphicon glyphicon-refresh"></i>
        </btn>
        <tooltip text="Apply changes to the Chart" target="#updateSettingsBtn" />
        <btn class="dropdown-toggle col-xs-1" @click="showSettings()" id="showSettingsBtn">
          <i class="glyphicon glyphicon-cog"></i>
        </btn>
        <tooltip text="More settings" target="#showSettingsBtn" />
      </div>
      <div class="modal-container" v-if="modalSettings">
        <h2>{{watchlistName}} - Data Points settings:</h2>
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
                    <btn class="col-xs-3" input-type="radio" input-value="v1" v-model="s.yAxis">1</btn>
                    <btn class="col-xs-3" input-type="radio" input-value="v2" v-model="s.yAxis">2</btn>
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
                    <input class="form-control" v-model="s.dataFields.dateX">
                  </div>
                  <btn-group class="col-xs-3">
                    <btn class="col-xs-6" input-type="radio" input-value="d1" v-model="s.xAxis">1</btn>
                    <btn class="col-xs-6" input-type="radio" input-value="d2" v-model="s.xAxis">2</btn>
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
                    <input class="form-control" type="number" min="0" max="1" v-model="s.tensionX" />
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
                      min="0"
                      max="1"
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
am4core.useTheme(am4themes_animated);

class JsonChart extends BaseChart {
  constructor(chartReference, color, domain = ".", jsonConfig) {
    console.log("Constructor");
    console.log(jsonConfig);
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
      )
    } else {
      this.chart.data = BaseChart.prepareChartData(
        BaseChart.sortMapKeys(this.pointPastValues)
      );
    }
    console.log(this.chart.data)
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
    console.log(data)
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
    "color",
    "startDate",
    "endDate",
    "refreshRate",
    "width",
    "height",
    "polylineStep",
    "rangeValue",
    "rangeColor",
    "rangeLabel",
    "showReload",
    "jsonConfig",
  ],
  data() {
    return {
      chartSettings: {
        chartType: "live",
        refreshRate: 10000,
        startDate: undefined,
        endDate: undefined,
      },
      modalSettings: false,
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
      colors: [
        "#39B54A",
        "#69FF7D",
        "#166921",
        "#690C24",
        "#B53859",
        "#734FC1",
      ],
      currentColor: 1,
      startTime: new Date(),
      endTime: new Date(),
      isExportId: false,
      series: [],
      pointData: [],
      compareElement1: undefined,
      compareElement2: undefined,
      dualAxis: false,
    };
  },
  mounted() {
    this.initializeDataPoints();
    this.generateChart();
  },
  methods: {
    showSettings() {
      this.modalSettings = !this.modalSettings;
    },
    cancelSettings() {
      console.log("canceled");
      this.modalSettings = false;
    },
    saveSettings() {
      console.log("saved");
      this.saveChart();
      this.modalSettings = false;
      this.updateSettings();
    },
    getDataPointData(pointId) {
      try {
        Axios.get(`./api/point_value/getValue/id/${pointId}`, {
          timeout: 5000,
          useCredentails: true,
          credentials: "same-origin",
        }).then((resp) => {
          let point = {
            id: pointId,
            xid: resp.data.xid,
            name: resp.data.name,
            type: resp.data.type,
            startDate: null,
            endDate: null,
            startTime: new Date(),
            endTime: new Date(),
          };
          this.pointData.push(point);
        });
      } catch (error) {
        console.error(error);
      }
    },
    initializeDataPoints() {
      let points = this.pointId.split(",");
      for (let i = 0; i < points.length; i++) {
        this.getDataPointData(points[i]);
      }
    },
    updateSettings() {
      if (this.chartSettings.chartType == "static") {
        this.chartSettings.refreshRate = undefined;
        let sdString = this.formatDate(new Date(this.chartSettings.startDate));
        let edString = this.formatDate(new Date(this.chartSettings.endDate));
        this.chartSettings.startDate = `${sdString} ${this.startTime.getHours()}:${this.startTime.getMinutes()}`;
        this.chartSettings.endDate = `${edString} ${this.endTime.getHours()}:${this.endTime.getMinutes()}`;
      } else if (this.chartSettings.chartType == "live") {
        this.chartSettings.endDate = undefined;
        this.chartSettings.startDate = `${this.chartSettings.startTime}-${this.chartSettings.startTimeMultiplier}`;
      } else {
        this.chartSettings.endDate = undefined;
        this.chartSettings.startDate = undefined;
        this.chartSettings.refreshRate = undefined;
      }
      this.applySettings(this.chartSettings);
      console.log(this.chartSettings);
    },
    applySettings(chartData) {
      this.saveChart();
      this.startDate = chartData.startDate;
      this.endDate = chartData.endDate;
      this.refreshRate = chartData.refreshRate;
      this.chartClass.chart.dispose();
      this.generateChart();
    },
    initializePoints(pointId) {
      this.series = [];
      return new Promise((resolve, reject) => {
        try {
          Axios.get(`./api/point_value/getValue/id/${pointId}`, {
            timeout: 5000,
            useCredentails: true,
            credentials: "same-origin",
          })
            .then((resp) => {
              let series = {
                id: `s${pointId}`,
                type: undefined,
                stroke: this.colors[this.currentColor % 6],
                fill: this.colors[this.currentColor % 6],
                strokeWidth: 3,
                name: resp.data.name,
                xAxis: "d1",
                yAxis: undefined,
                dataFields: {
                  dateX: "date",
                  valueY: resp.data.name,
                },
                minBulletDistance: 15,
                tooltipText:
                  "{name}: [bold]{valueY}[/] " + resp.data.textRenderer.suffix,
                tooltip: {
                  pointerOrientation: "vertical",
                  background: {
                    fill: "#F00",
                    cornerRadius: 20,
                    strokeOpacity: 0,
                  },
                  label: {
                    minWidth: 40,
                    minHeight: 40,
                    textAlign: "middle",
                    textValign: "middle",
                  },
                },
                tensionX: 1,
                startLocation: 0.5,
                fillOpacity: 0,
              };
              series.tooltip.fill = series.stroke;
              series.fill = series.stroke;
              this.currentColor = this.currentColor + 1;
              if (resp.data.type == "NumericValue") {
                series.yAxis = "v1";
                series.type = "LineSeries";
              } else if (resp.data.type == "BinaryValue") {
                series.yAxis = "binAxis";
                series.type = "StepLineSeries";
              }
              console.log(this.series);
              console.log(this.chartSettings);
              if (this.chartSettings.chartType == "compare" && !this.dualAxis) {
                this.dualAxis = !this.dualAxis;
                series.xAxis = "d2";
                series.dataFields.dateX = "date2";
              }
              this.series.push(series);
              resolve("done");
            })
            .catch((webError) => {
              reject(webError);
            });
        } catch (error) {
          reject(error);
        }
      });
    },
    initializeChart(jsonConfig) {
      this.chartClass = new JsonChart(
        this.$refs.chartdiv,
        this.color,
        ".",
        jsonConfig
      );

      let promises = [];
      if (this.chartSettings.chartType === "compare") {
        let ce1sdString = this.formatDate(
          new Date(this.compareElement1.startDate)
        );
        let ce1edString = this.formatDate(
          new Date(this.compareElement1.endDate)
        );
        this.compareElement1.startDate = `${ce1sdString} ${this.compareElement1.startTime.getHours()}:${this.compareElement1.startTime.getMinutes()}`;
        this.compareElement1.endDate = `${ce1edString} ${this.compareElement1.endTime.getHours()}:${this.compareElement1.endTime.getMinutes()}`;
        promises.push(
          this.chartClass.loadData(
            this.compareElement1.id,
            this.compareElement1.startDate,
            this.compareElement1.endDate,
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
        console.log(points);
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

        this.chartClass.setupChart(this.chartSettings.chartType); // Display Chart
        //   if (this.rangeValue !== undefined) {
        //     this.chartClass.addRangeValue(
        //       Number(this.rangeValue),
        //       this.rangeColor,
        //       this.rangeLabel
        //     );
        //   }
        this.saveChart();
        if (this.refreshRate != undefined) {
          this.chartClass.startLiveUpdate(
            Number(this.refreshRate),
            this.isExportId
          );
        }
      });
    },
    generateChart() {
      if (Number(this.polylineStep) > 1) {
        LineChart.setPolylineStep(Number(this.polylineStep));
      }

      let jsonConfig = {
        legend: {},
        cursor: {},
        scrollbarX: {
          type: "Scrollbar",
        },
        xAxes: [
          {
            id: "d1",
            type: "DateAxis",
            dataFields: {
              value: "date",
            },
          },
          {
            id: "d2",
            type: "DateAxis",
            dataFields: {
              value: "date2",
            },
          },
        ],
        yAxes: [
          {
            id: "v1",
            type: "ValueAxis",
            dataFields: {
              value: "01",
            },
          },
          {
            id: "v2",
            type: "ValueAxis",
            syncWithAxis: "v1",
            dataFields: {
              value: "01",
            },
          },
          {
            id: "logAxis",
            type: "ValueAxis",
            logarithmic: true,
            dataFields: {
              value: "01",
            },
          },
          {
            id: "binAxis",
            type: "ValueAxis",
            dataFields: {
              value: "01",
            },
            syncWithAxis: "v1",
            renderer: {
              opposite: true,
            },
          },
        ],
        series: undefined,
      };

      if (this.loadChart()) {
        console.debug("Chart Loaded");
        jsonConfig.series = this.series;
        jsonConfig = JSON.stringify(jsonConfig);
        jsonConfig = JSON.parse(jsonConfig);
        console.log(jsonConfig);
        this.initializeChart(jsonConfig);
      } else {
        console.debug("Chart Initialized");
        let initializePointsPromises = [];
        if (this.chartType === "compare") {
          initializePointsPromises.push(
            this.initializePoints(this.compareElement1.id)
          );
          initializePointsPromises.push(
            this.initializePoints(this.compareElement2.id)
          );
        } else {
          let pointId = this.pointId.split(",");
          for (let i = 0; i < pointId.length; i++) {
            initializePointsPromises.push(this.initializePoints(pointId[i]));
          }
        }
        Promise.all(initializePointsPromises).then((resp) => {
          let seriesString = JSON.stringify(this.series);
          jsonConfig.series = this.series;

          jsonConfig = JSON.stringify(jsonConfig);
          jsonConfig = JSON.parse(jsonConfig);
          console.log(jsonConfig);
          this.initializeChart(jsonConfig);
        });
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
      this.generateChart();
    },
    loadChart() {
      let loadData = JSON.parse(
        localStorage.getItem(`MWL_${this.watchlistName}`)
      );
      if (loadData !== null) {
        this.series = loadData.series;
        this.chartSettings = loadData.chartSettings;
        return true;
      } else {
        return false;
      }
    },
    saveChart() {
      console.log(JSON.parse(JSON.stringify(this.series)));
      console.log(this.chartSettings);
      console.log(JSON.stringify(this.series));
      console.log(JSON.stringify(this.chartSettings));
      let saveData = {
        chartSettings: this.chartSettings,
        series: JSON.parse(JSON.stringify(this.series)),
      };
      localStorage.setItem(
        `MWL_${this.watchlistName}`,
        JSON.stringify(saveData)
      );
    },
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