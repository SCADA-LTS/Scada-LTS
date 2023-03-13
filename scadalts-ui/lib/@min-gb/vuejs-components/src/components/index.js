import Vue from "vue"
import Refresh from "./Refresh"
import SimpleTable from "./SimpleTable"
import SimplePagination from "./SimplePagination"
import SimplePanel from "./SimplePanel"
import VueTimers from 'vue-timers'

Vue.use(VueTimers)

Vue.use(require('vue-moment'));

const Components = {
    Refresh,
    SimpleTable,
    SimplePagination,
    SimplePanel
}

Object.keys(Components).forEach(name=>{
    Vue.component(name, Components[name])
})

export default Components
