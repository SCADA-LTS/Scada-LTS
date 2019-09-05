import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import Test from "./components/Test";
import StepLineChartComponent from './components/charts/StepLineChartComponent'
import LineChartComponent from './components/charts/LineChartComponent'
import ColumnChartComponent from './components/charts/ColumnChartComponent'
import PieChartComponent from './components/charts/PieChartComponent'
import GaugeChartComponent from './components/charts/GaugeChartComponent'
import CylinderGaugeComponent from './components/charts/CylinderGaugeComponent'
import JsonChartComponent from './components/charts/JsonChartComponent'
import DataPointDetailsChart from "./views/DataPointDetails/DataPointDetailsChart";
import WatchListChart from "./views/WatchListChart";

Vue.config.productionTip = false

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')

new Vue({
  router,
  store,
  components: {Test},
  template: '<Test/>',
  render: h => h(Test)
}).$mount('#test')

new Vue({
  el:'#graphic-view-components',
  components: {
    "step-line-chart": StepLineChartComponent,
    "line-chart": LineChartComponent,
    "column-chart": ColumnChartComponent,
    "pie-chart": PieChartComponent,
    "gauge-chart": GaugeChartComponent,
    "cylinder-gauge-chart": CylinderGaugeComponent,
    "json-custom-chart": JsonChartComponent,
  },
})

/* ScadaLTS UI Vue.js components */
new Vue({
  el: '#vue-ui',
  components: {
    "chart-component": DataPointDetailsChart,
    "wl-chart": WatchListChart
  },
})


