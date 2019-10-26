import Vue from 'vue'
import App from './apps/App.vue'
import router from './router'
import store from './store'
import * as uiv from "uiv";

import 'bootstrap/dist/css/bootstrap.min.css'
import Test from "./components/Test"
import IsAlive from './components/graphical_views/IsAlive'


Vue.config.productionTip = false

Vue.use(uiv);

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')

new Vue({
  render: h => h(Test,
    { props:
        { plabel: window.document.getElementById('app-test').getAttribute('plabel') }
    })
}).$mount("#app-test");

new Vue({
  render: h => h(IsAlive,
    { props:
        {
          plabel: window.document.getElementById('app-isalive').getAttribute('plabel'),
          ptimeWarning: window.document.getElementById('app-isalive').getAttribute('ptime-warning'),
          ptimeError: window.document.getElementById('app-isalive').getAttribute('ptime-error'),
          timeRefresh: window.document.getElementById('app-isalive').getAttribute('ptime-refresh'),
        }
    })
}).$mount("#app-isalive")

for (let i=0; i<20; i++) {
  const cmpId = `app-cmp-${i}`
  new Vue({
    render: h => h(CMP,
      { props:
          {
            pLabel: window.document.getElementById(cmpId).getAttribute('plabel'),
            pTimeRefresh: window.document.getElementById(cmpId).getAttribute('ptimeRefresh'),
            pConfig: window.document.getElementById(cmpId).getAttribute('pconfig')
          }
      })
  }).$mount('#'+cmpId)
}
