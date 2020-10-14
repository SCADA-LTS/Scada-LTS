import Vuex from 'vuex'
import dataSource from "./dataSource"
import graphicView from "./graphicView"
import pointHierarchy from "./pointHierarchy"
import modernWatchList from './modernWatchList'
import amcharts from './amcharts'
import alarms from './alarms'

const myLoggerForVuexMutation = store => {
  store.subscribe((mutation, state) => {
    //console.log(`vuex state: ${JSON.stringify(state)} type: ${mutation.type} payload: ${mutation.payload}`  )
  })
}

export default Vuex.createStore({
  modules: {
    dataSource,
    graphicView,
    pointHierarchy,
    amcharts,
    modernWatchList,
    alarms
  },
  state: {

  },
  mutations: {

  },
  actions: {

  },
  plugins: [
    myLoggerForVuexMutation
  ]
});
