import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import VueCookie from 'vue-cookie';
import StepLineChartComponent from "./components/charts/StepLineChartComponent";
import JsonChartComponent from "./components/charts/JsonChartComponent";
import CylinderGaugeComponent from "./components/charts/CylinderGaugeComponent";
import GaugeChartComponent from "./components/charts/GaugeChartComponent";
import PieChartComponent from "./components/charts/PieChartComponent";
import ColumnChartComponent from "./components/charts/ColumnChartComponent";
import LineChartComponent from "./components/charts/LineChartComponent";

Vue.config.productionTip = false
Vue.use(VueCookie)

new Vue({
    router,
    store,
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
        "json-custom-chart": JsonChartComponent
    }
})
