import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import Test from "./components/gb/Test";
import StepLineChartComponent from "./components/charts/StepLineChartComponent";

Vue.config.productionTip = false

new Vue({
  router,
  store,
  components:{
    "chart": StepLineChartComponent
  },
  render: h => h(App)
}).$mount('#app')

new Vue({
  router,
  store,
  components: {Test},
  template: '<Test/>',
  render: h => h(Test)
}).$mount('#test')


