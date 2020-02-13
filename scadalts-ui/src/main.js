import Vue from 'vue'
import App from './apps/App.vue'
import router from './router'
import store from './store'
import * as uiv from 'uiv'
import VueCookie from 'vue-cookie'
import VueLogger from 'vuejs-logger'

import 'bootstrap/dist/css/bootstrap.min.css'
import Test from './components/Test'
import IsAlive from './components/graphical_views/IsAlive'
import CMP from './components/graphical_views/cmp/CMP'
import SimpleComponentSVG from './components/graphical_views/SimpleComponentSVG'
import ExportImportPointHierarchy from './components/point_hierarchy/ExportImportPointHierarchy'
import SleepAndReactivationDS from './components/forms/SleepAndReactivationDS'
// import ExampleChartCmp from './views/components/ExampleChartCmp'
import WatchListChartWidget from './components/watch_list/WatchListChartWidget'
import VueLodash from 'vue-lodash'

import StepLineChartComponent from './components/amcharts/StepLineChartComponent'
import LineChartComponent from './components/amcharts/LineChartComponent'

const isProduction = process.env.NODE_ENV === 'production';

const options = {
  isEnabled: true,
  logLevel: isProduction ? 'error' : 'debug',
  stringifyArguments: false,
  showLogLevel: true,
  showMethodName: true,
  separator: '|',
  showConsoleColors: true
};

Vue.use(VueLogger, options);

const optionsLodash = { name: 'lodash' }

Vue.use(VueLodash, optionsLodash)

Vue.use(uiv)
Vue.use(VueCookie)

Vue.config.devtools = true

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')

if (window.document.getElementById('app-test') != undefined) {
  new Vue({
    render: h => h(Test,
      {
        props:
          { plabel: window.document.getElementById('app-test').getAttribute('plabel') }
      })
  }).$mount('#app-test')
}

if (window.document.getElementById('app-isalive') != undefined) {
  new Vue({
    store,
    render: h => h(IsAlive,
      {
        props:
        {
          plabel: window.document.getElementById('app-isalive').getAttribute('plabel'),
          ptimeWarning: window.document.getElementById('app-isalive').getAttribute('ptime-warning'),
          ptimeError: window.document.getElementById('app-isalive').getAttribute('ptime-error'),
          ptimeRefresh: window.document.getElementById('app-isalive').getAttribute('ptime-refresh'),
        }
      })
  }).$mount('#app-isalive')
}

for (let i = 0; i < 20; i++) {
  const cmpId = `app-cmp-${i}`
  if (window.document.getElementById(cmpId) != undefined) {
    new Vue({
      render: h => h(CMP,
        {
          store,
          props:
          {
            pLabel: window.document.getElementById(cmpId).getAttribute('plabel'),
            pTimeRefresh: window.document.getElementById(cmpId).getAttribute('ptimeRefresh'),
            pConfig: window.document.getElementById(cmpId).getAttribute('pconfig'),
            pxIdViewAndIdCmp: window.document.getElementById(cmpId).getAttribute('pxIdViewAndIdCmp')
          }
        })
    }).$mount('#' + cmpId)
  }
}

if (window.document.getElementById('simple-component-svg') != undefined) {
  new Vue({
    render: h => h(SimpleComponentSVG, {
      props:
      {
        pxidPoint: window.document.getElementById('simple-component-svg').getAttribute('pxidPoint'),
        ptimeRefresh: window.document.getElementById('simple-component-svg').getAttribute('ptimeRefresh'),
        plabel: window.document.getElementById('simple-component-svg').getAttribute('plabel'),
        pvalue: window.document.getElementById('simple-component-svg').getAttribute('pvalue')
      }
    })
  }).$mount('#simple-component-svg')
}

if (window.document.getElementById('sleep-reactivation-ds') != undefined) {
  new Vue({
    render: h => h(SleepAndReactivationDS)
  }).$mount('#sleep-reactivation-ds')
}

if (window.document.getElementById('export-import-ph') != undefined) {
  new Vue({
    render: h => h(ExportImportPointHierarchy)
  }).$mount('#export-import-ph')
}

if (window.document.getElementById('example-chart-cmp') != undefined) {
  new Vue({
    render: h => h(WatchListChartWidget)
  }).$mount('#example-chart-cmp')
}

if (window.document.getElementById('step-line-chart') != undefined) {
  new Vue({
    render: h => h(StepLineChartComponent, {
      props:
      {
        pointId: window.document.getElementById('step-line-chart').getAttribute('point-id'),
        color: window.document.getElementById('step-line-chart').getAttribute('color'),
        label: window.document.getElementById('step-line-chart').getAttribute('label'),
        startDate: window.document.getElementById('step-line-chart').getAttribute('start-date'),
        endDate: window.document.getElementById('step-line-chart').getAttribute('end-date'),
        refreshRate: window.document.getElementById('step-line-chart').getAttribute('refresh-rate'),
        width: window.document.getElementById('step-line-chart').getAttribute('width'),
        height: window.document.getElementById('step-line-chart').getAttribute('height'),
        polylineStep: window.document.getElementById('step-line-chart').getAttribute('polyline-step'),
        rangeValue: window.document.getElementById('step-line-chart').getAttribute('range-value'),
        rangeColor: window.document.getElementById('step-line-chart').getAttribute('range-color'),
        rangeLabel: window.document.getElementById('step-line-chart').getAttribute('range-label'),
        showScrollbarX: window.document.getElementById('step-line-chart').getAttribute('show-scrollbar-x'),
        showScrollbarY: window.document.getElementById('step-line-chart').getAttribute('show-scrollbar-y'),
        showLegend: window.document.getElementById('step-line-chart').getAttribute('show-legned'),
      }
    })
  }).$mount('#step-line-chart')
}

if (window.document.getElementById('line-chart') != undefined) {
  new Vue({
    render: h => h(LineChartComponent, {
      props:
      {
        pointId:      window.document.getElementById('line-chart').getAttribute('point-id'),
        color:        window.document.getElementById('line-chart').getAttribute('color'),
        label:        window.document.getElementById('line-chart').getAttribute('label'),
        startDate:    window.document.getElementById('line-chart').getAttribute('start-date'),
        endDate:      window.document.getElementById('line-chart').getAttribute('end-date'),
        refreshRate:  window.document.getElementById('line-chart').getAttribute('refresh-rate'),
        width:        window.document.getElementById('line-chart').getAttribute('width'),
        height:       window.document.getElementById('line-chart').getAttribute('height'),
        polylineStep: window.document.getElementById('line-chart').getAttribute('polyline-step'),
        rangeValue:   window.document.getElementById('line-chart').getAttribute('range-value'),
        rangeColor:   window.document.getElementById('line-chart').getAttribute('range-color'),
        rangeLabel:   window.document.getElementById('line-chart').getAttribute('range-label'),
        showScrollbarX: window.document.getElementById('line-chart').getAttribute('show-scrollbar-x'),
        showScrollbarY: window.document.getElementById('line-chart').getAttribute('show-scrollbar-y'),
        showLegend:     window.document.getElementById('line-chart').getAttribute('show-legned'),
      }
    })
  }).$mount('#line-chart')
}