import Vue from 'vue'
import Vuex from 'vuex'
import dataSource from "./dataSource"
import graphicView from "./graphicView"
import pointHierarchy from "./pointHierarchy"
import modernWatchList from './modernWatchList'
import amcharts from './amcharts'
import alarms from './alarms'
import systemSettings from './systemSettings'

Vue.use(Vuex)

const myLoggerForVuexMutation = store => {
  store.subscribe((mutation, state) => {
    //console.log(`vuex state: ${JSON.stringify(state)} type: ${mutation.type} payload: ${mutation.payload}`  )
  })
}

export default new Vuex.Store({
  modules: {
    dataSource,
    graphicView,
    pointHierarchy,
    amcharts,
    modernWatchList,
    alarms,
    systemSettings
  },
  state: {
    packageVersion: process.env.PACKAGE_VERSION || '0',
    packageTag: process.env.PACKAGE_TAG || '0'
  },
  mutations: {

  },
  actions: {

  },
  getters: {
    appVersion: (state) => {
      return state.packageVersion
    },
    appTag: (state) => {
      return state.packageTag
    }
  },
  plugins: [
    myLoggerForVuexMutation
  ]
})
