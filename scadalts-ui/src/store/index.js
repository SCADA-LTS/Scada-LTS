import Vue from 'vue'
import Vuex from 'vuex'
import dataSource from "./dataSource"
import graphicView from "./graphicView"
import pointHierarchy from "./pointHierarchy"
import amcharts from './amcharts'
import alarms from './alarms'
import systemSettings from './systemSettings'
import plcNotifications from './alarms/notification'
import axios from "axios";

Vue.use(Vuex)

const myLoggerForVuexMutation = store => {
  store.subscribe((mutation, state) => { })
}

export default new Vuex.Store({
  modules: {
    dataSource,
    graphicView,
    pointHierarchy,
    amcharts,
    alarms,
    systemSettings,
    plcNotifications
  },
  state: {
    packageVersion: process.env.PACKAGE_VERSION || '0',
    packageTag: process.env.PACKAGE_TAG || '0',
    scadaLtsMilestone: process.env.SCADA_LTS_MILESTONE || '0',
    scadaLtsBuild: process.env.SCADA_LTS_BUILD || '0',
    scadaLtsBranch: process.env.SCADA_LTS_BRANCH || 'local',
  },
  mutations: {

  },
  actions: {
    getUserRole() {
      return new Promise((resolve, reject) => {
        axios.get("./api/auth/isRoleAdmin",
        {timeout: 5000, useCredentials: true, credentials: 'same-origin'}).then(resp => {
          resolve(resp.data);
        }).catch(error => {
          console.error(error);
          reject(error);
        })
      })
    }

  },
  getters: {
    appVersion: (state) => {
      return state.packageVersion
    },
    appTag: (state) => {
      return state.packageTag
    },
    appMilestone: (state) => {
      return state.scadaLtsMilestone
    },
    appBuild: (state) => {
      return state.scadaLtsBuild
    },
    appBranch: (state) => {
      return state.scadaLtsBranch
    }
  },
  plugins: [
    myLoggerForVuexMutation
  ]
})
