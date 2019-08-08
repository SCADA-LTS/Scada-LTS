// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import 'bootstrap/dist/css/bootstrap.min.css'

import Vue from 'vue'
import App from './App'
import Test from './components/Test'
import IsAlive from './components/IsAlive'
import ExportImportPointHierarchy from './components/ExportImportPointHierarchy'
import SimpleComponentSVG from './components/SimpleComponentSVG'
import CMP from "./components/CMP";
import JsonChartComponent from './components/charts/JsonChartComponent'
import StepLineChartComponent from "./components/charts/StepLineChartComponent";
import LineChartComponent from './components/charts/LineChartComponent'
import ColumnChartComponent from './components/charts/ColumnChartComponent'
import PieChartComponent from './components/charts/PieChartComponent'
import GaugeChartComponent from './components/charts/GaugeChartComponent'
import SleepAndReactivationDS from './components/form/SleepAndReactivationDS'
import router from './router'
import VJsoneditor from 'vue-jsoneditor';
import Vuetify from 'vuetify';
import * as uiv from 'uiv'

import 'bootstrap/dist/css/bootstrap.min.css'



Vue.config.productionTip = false
Vue.use(VJsoneditor);
Vue.use(Vuetify);
Vue.use(uiv);

/*eslint-disable no-new */
new Vue({
  el: '#app1',
  router,
  components: { App },
  template: '<App/>'
})

new Vue({
  el: '#test',
  components: { Test },
  template: '<Test/>'
})

new Vue({
  el: '#app-is-alive',
  components: {
    "is-alive": IsAlive
  },
})

new Vue({
  el: '#export-import-ph',
  components: { ExportImportPointHierarchy },
  template: '<ExportImportPointHierarchy/>'
})

new Vue({
   el: '#app',
   components: {
     "simple-component-svg": SimpleComponentSVG,
     "export-import-ph": ExportImportPointHierarchy,
     "sleep-reactivation-ds": SleepAndReactivationDS,
   }
})

new Vue({
  el: '#app-cmp',
  components: {
    "cmp": CMP
  },
})

new Vue({
  el:'#chart',
  components: {
    "step-line-chart": StepLineChartComponent,
    "line-chart": LineChartComponent,
    "column-chart": ColumnChartComponent,
    "pie-chart": PieChartComponent,
    "gauge-chart": GaugeChartComponent,
    "json-custom-chart": JsonChartComponent
  },
})

for (let i=0; i<20; i++) {
  new Vue({
    el: `#app-cmp-${i}`,
    components: {
      "cmp": CMP
    },
  })
}

