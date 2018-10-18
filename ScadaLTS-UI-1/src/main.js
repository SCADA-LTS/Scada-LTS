// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import 'bootstrap/dist/css/bootstrap.min.css'

import Vue from 'vue'
import App from './App'
import Test from './components/Test'
import IsAlive from './components/IsAlive'
import ExportImportPointHierarchy from './components/ExportImportPointHierarchy'
import SimpleComponentSVG from './components/SimpleComponentSVG'
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
     "export-import-ph": ExportImportPointHierarchy
   }
})

