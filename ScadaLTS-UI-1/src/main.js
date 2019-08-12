// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import 'bootstrap/dist/css/bootstrap.min.css'
import './assets/main.css'

import Vue from 'vue'
/* Components for ScadaLTS-UI */
import DataPointDetailsChart from './components/userInterfaceComponents/dataPointDetails/DataPointDetailsChart'
import ExportImportPointHierarchy from './components/userInterfaceComponents/pointHierarchy/ExportImportPointHierarchy'
import SleepAndReactivationDS from './components/userInterfaceComponents/dataSourceEdit/SleepAndReactivationDS'

/* Graphic View custom components */
import CMP from "./components/graphicViewComponents/CMP";
import ColumnChartComponent from './components/graphicViewComponents/charts/ColumnChartComponent'
import CylinderGaugeComponent from './components/graphicViewComponents/charts/CylinderGaugeComponent'
import GaugeChartComponent from './components/graphicViewComponents/charts/GaugeChartComponent'
import IsAlive from './components/graphicViewComponents/IsAlive'
import JsonChartComponent from './components/graphicViewComponents/charts/JsonChartComponent'
import LineChartComponent from './components/graphicViewComponents/charts/LineChartComponent'
import PieChartComponent from './components/graphicViewComponents/charts/PieChartComponent'
import SimpleComponentSVG from './components/graphicViewComponents/SimpleComponentSVG'
import StepLineChartComponent from "./components/graphicViewComponents/charts/StepLineChartComponent";

/* Utils import */
import VJsoneditor from 'vue-jsoneditor';
import VueCookie from 'vue-cookie';
import * as uiv from 'uiv';

Vue.config.productionTip = false
Vue.use(VJsoneditor);
Vue.use(uiv);
Vue.use(VueCookie);

/*eslint-disable no-new */
new Vue({
   el: '#app',
   components: {
     "simple-component-svg": SimpleComponentSVG,
     "export-import-ph": ExportImportPointHierarchy,
     "sleep-reactivation-ds": SleepAndReactivationDS,
   }
})

/* GraphicView custom components */
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
    "is-alive": IsAlive,
    "simple-component-svg": SimpleComponentSVG,
    "cmp": CMP
  },
})

/* ScadaLTS UI Vue.js components */
new Vue({
  el: '#vue-ui',
  components: {
    "chart-component": DataPointDetailsChart
  },
})
