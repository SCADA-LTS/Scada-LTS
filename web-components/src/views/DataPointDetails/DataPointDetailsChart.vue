<template>
    <div class="flex-container">
        <div class="settings" v-if="renderChart">
            <button @click="openSettings()">Chart settings</button>
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
                                v-model="chartSettings.lineChart"
                        />
                    </div>
                    <div class="flex-radio">
                        <label for="type-rbtn">Line Chart</label>
                        <input
                                type="radio"
                                class="radio-button"
                                value="line"
                                id="type-rbtn"
                                v-model="chartSettings.lineChart"
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
                                v-model="chartSettings.chartType"
                        />
                    </div>
                    <div class="flex-radio">
                        <label for="static-rbtn">Static Chart</label>
                        <input
                                type="radio"
                                class="radio-button"
                                value="static"
                                id="static-rbtn"
                                v-model="chartSettings.chartType"
                        />
                    </div>
                </div>
                <div v-if="chartSettings.chartType === 'live'" class="flex-container">
                    <div>
                        <label for="live-sd">Values from last: </label>
                        <input type="number" id="live-sd" v-model="chartSettings.startTime" />
                        <select v-model="chartSettings.startTimeMultiplier">
                            <option v-for="option in timeOptions" v-bind:value="option.value" v-bind:key="option.id">
                                {{option.text}}
                            </option>
                        </select>
                    </div>
                    <div>
                        <label for="live-rr">Chart performance</label>
                        <select id="live-rr" v-model="chartSettings.refreshRate">
                            <option v-for="option in performanceOptions" v-bind:value="option.value" v-bind:key="option.id">
                                {{option.text}}
                            </option>
                        </select>
                    </div>
                </div>
                <div v-if="chartSettings.chartType === 'static'" class="flex-container">
                    <div>
                        <label for="static-sd">Start Date</label>
                        <datepicker id="static-sd" format="yyyy/MM/dd" :monday-first="true" v-model="chartSettings.startDate"></datepicker>
                    </div>
                    <div>
                        <label for="static-ed">End Date</label>
                        <datepicker id="static-ed" format="yyyy/MM/dd" :monday-first="true" v-model="chartSettings.endDate"></datepicker>
                    </div>
                </div>
                <div class="flex-container">
                    <div>
                        <label for="chart-color">Chart color</label>
                        <ColorPicker
                                id="chart-color"
                                :width="100"
                                :height="100"
                                startColor="#39B54A"
                                v-model="chartSettings.chartColor"
                        ></ColorPicker>
                    </div>
                    <div>
                        <label for="chart-rv">Range value</label>
                        <input type="text" id="chart-rv" v-model="chartSettings.rangeValue" />
                    </div>
                    <div v-if="chartSettings.rangeValue">
                        <label for="chart-rc">Range color</label>
                        <ColorPicker
                                id="chart-rc"
                                :width="100"
                                :height="100"
                                startColor="#ff0000"
                                v-model="chartSettings.rangeColor"
                        ></ColorPicker>
                    </div>
                    <div v-if="chartSettings.rangeValue">
                        <label for="chart-rl">Range label</label>
                        <input type="text" id="chart-rl" v-model="chartSettings.rangeLabel" />
                    </div>
                    <!-- <div>
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
                    </div> -->
                </div>
                <div class="settings">
                    <button @click="cancelSettings()">Close</button>
                    <button @click="saveSettings()">Save</button>
                </div>
            </div>
        </div>
        <div v-if="renderChart">
            <line-chart
                    v-bind:point-id="pointId"
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
                    v-bind:width="width"
                    height="600"
                    v-if="chartdata.lineChart == 'line'"
            />
            <step-line-chart
                    v-bind:point-id="pointId"
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
                    v-bind:width="width"
                    height="600"
                    v-if="chartdata.lineChart == 'stepLine'"
            />
        </div>

        <!-- <input type="text" id="label" v-model="label" /> -->
    </div>
</template>
<script>
    import Axios from "axios";
    import StepLineChart from "../../components/charts/StepLineChartComponent";
    import LineChart from "../../components/charts/LineChartComponent";
    import Datepicker from "vuejs-datepicker";
    import ColorPicker from "vue-color-picker-wheel";

    export default {
        name: "DataPointDetailsChart",
        components: {
            StepLineChart,
            LineChart,
            Datepicker,
            ColorPicker
        },
        data() {
            return {
                pointId: undefined,
                userName: "test",
                renderChart: false,
                showModal: false,
                width: 0,
                chartdata: undefined,
                chartSettings: {
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
                    lineChart: "stepLine",
                    chartType: "live"
                },
                performanceOptions: [
                    {id: 0, text: "Real Time", value: 1000},
                    {id: 1, text: "High resolution", value: 2000},
                    {id: 2, text: "Standard", value: 5000},
                    {id: 3, text: "Faster performance", value: 10000}
                ],
                timeOptions: [
                    {id: 0, text: "Hour(s)", value: "hour"},
                    {id: 1, text: "Day(s)", value: "day"},
                    {id: 2, text: "Weak(s)", value: "weak"},
                    {id: 3, text: "Month(s)", value: "month"}
                ]
            };
        },
        created() {
            this.chartdata = this.chartSettings;
            window.addEventListener("resize", this.handleResize);
            this.handleResize();
        },
        mounted() {
            this.userName = document.getElementsByClassName("userName").item(0).innerText;
            this.pointId = document.getElementById("pointid").innerHTML;
            if (
                this.$cookie.get(`Point_${this.pointId}_${this.userName}`) !== null &&
                this.$cookie.get(`Point_${this.pointId}_${this.userName}`) !== ""
            ) {
                this.chartdata = JSON.parse(
                    this.$cookie.get(`Point_${this.pointId}_${this.userName}`)
                );
            }
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
                        this.renderChart = true;
                    }
                });
            },
            openSettings() {
                this.showModal = true;
                this.renderChart = false;
            },
            cancelSettings() {
                this.showModal = false;
                this.renderChart = true;
            },
            saveSettings() {
                if (this.chartSettings.chartType == "static") {
                    this.chartSettings.refreshRate = undefined;
                    let date = this.chartSettings.startDate;
                    this.chartSettings.startDate =
                        date.getFullYear() +
                        "/" +
                        (date.getMonth() + 1) +
                        "/" +
                        date.getDate();
                    date = this.chartSettings.endDate;
                    this.chartSettings.endDate =
                        date.getFullYear() +
                        "/" +
                        (date.getMonth() + 1) +
                        "/" +
                        date.getDate();
                } else {
                    this.chartSettings.endDate = undefined;
                    this.chartSettings.startDate = `${this.chartdata.startTime}-${this.chartdata.startTimeMultiplier}`
                }
                this.chartdata = this.chartSettings;
                this.showModal = false;
                this.renderChart = true;
                this.$cookie.set(
                    `Point_${this.pointId}_${this.userName}`,
                    JSON.stringify(this.chartSettings),
                    7
                );
            },
            handleResize() {
                this.width = window.innerWidth - 50;
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
</style>
