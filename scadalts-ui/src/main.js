import Vue from 'vue'
import App from './apps/App.vue'
import router from './router'
import store from './store'
import * as uiv from 'uiv'
import VueLogger from 'vuejs-logger'

import 'bootstrap/dist/css/bootstrap.min.css'
import Test from './components/Test'
import IsAlive from './components/graphical_views/IsAlive'
import CMP from './components/graphical_views/cmp/CMP'
import SimpleComponentSVG from './components/graphical_views/SimpleComponentSVG'
import ExportImportPointHierarchy from './components/point_hierarchy/ExportImportPointHierarchy'
import SleepAndReactivationDS from './components/forms/SleepAndReactivationDS'

const isProduction = process.env.NODE_ENV === 'production';

const options = {
  isEnabled: true,
  logLevel : isProduction ? 'error' : 'debug',
  stringifyArguments : false,
  showLogLevel : true,
  showMethodName : true,
  separator: '|',
  showConsoleColors: true
};

Vue.use(VueLogger, options);

Vue.use(uiv)

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
          {plabel: window.document.getElementById('app-test').getAttribute('plabel')}
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
