import Vue from 'vue'
import Vuex from 'vuex'
import axios from 'axios';
import synopticPanel from "./modules/synopticPanel";

Vue.use(Vuex)
const connectionConfig = {timeout: 5000, useCredentails: true, credentials: 'same-origin'}


export default new Vuex.Store({
  modules: {
    synopticPanel
  },
  state: {
    adminPrivilege: false
  },
  mutations: {
    isAdmin(state, admin) {
      state.adminPrivilege = admin
    }
  },
  actions: {
    isAdminPrivilege(context) {
      return new Promise((resolve, reject) => {
        axios.get('./api/auth/isRoleAdmin', connectionConfig).then(resp => {
          context.commit('isAdmin', resp.data)
          resolve();
        })
      })
    }
  }
})
