import Vue from 'vue'
import Vuex from 'vuex'
import ds from "./ds"
import gv from "./gv"
import ph from "./ph"

Vue.use(Vuex)

const myLoggerForVuexMutation = store => {
  store.subscribe((mutation, state) => {
    console.log(`vuex state: ${JSON.stringify(state)} type: ${mutation.type} payload: ${mutation.payload}`  )
  })
}

export default new Vuex.Store({
  modules: {
    ds,
    gv,
    ph
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
})
