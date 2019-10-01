import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import VueCookie from 'vue-cookie';
import svgJS from './plugins/svg';
import StepLineChartComponent from "./components/charts/StepLineChartComponent";
import JsonChartComponent from "./components/charts/JsonChartComponent";
import CylinderGaugeComponent from "./components/charts/CylinderGaugeComponent";
import GaugeChartComponent from "./components/charts/GaugeChartComponent";
import PieChartComponent from "./components/charts/PieChartComponent";
import ColumnChartComponent from "./components/charts/ColumnChartComponent";
import LineChartComponent from "./components/charts/LineChartComponent";
import WatchListChart from "./views/WatchListChart";
import DataPointDetailsChart from "./views/DataPointDetails/DataPointDetailsChart";
import VueDraggableResizable from 'vue-draggable-resizable'
// import CMP from "./components/gb/CMP";




Vue.config.productionTip = false
Vue.use(VueCookie)
Vue.use(svgJS);

import 'vue-draggable-resizable/dist/VueDraggableResizable.css'
import SynopticPanel from "./views/SynopticPanel";
import vuetify from './plugins/vuetify';

Vue.component('vue-draggable-resizable', VueDraggableResizable)


new Vue({
    router,
    store,
    vuetify,
    render: h => h(App)
}).$mount('#app')

new Vue({
    el: "#graphicviewchart",
    components: {
        "step-line" : StepLineChartComponent,
        "line-chart": LineChartComponent,
        "column-chart": ColumnChartComponent,
        "pie-chart": PieChartComponent,
        "gauge-chart": GaugeChartComponent,
        "cylinder-gauge-chart": CylinderGaugeComponent,
        "json-custom-chart": JsonChartComponent,
        // "cmp" : CMP
    }
});

new Vue({
    el: "#vue-ui",
    components: {
        "wl-chart" : WatchListChart,
        "chart-component" : DataPointDetailsChart
    }
})


new Vue({
    vuetify,
    store,
    render: h => h(SynopticPanel)
}).$mount('#synopticPanelContent')
