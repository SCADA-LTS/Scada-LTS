import ScadaNotification from "../models/NotificationAlertEntry";

const ScadaUiNotificationsModule = {
    state: {
        scadaNotifications: [],
        scadaNotificationCount: 5,
        notificationTimeoutDuration: 5000,
        notificationTimeout: null,
        networkError: false,
    },

    mutations: {
        ADD_SCADA_NOTIFICATION(state, notification) {
            state.scadaNotifications.unshift(notification);
            if(state.scadaNotifications.length > state.scadaNotificationCount) {
                state.scadaNotifications.pop();
            }
            if(!!state.notificationTimeout) { clearTimeout(state.notificationTimeout); }
            state.notificationTimeout = setTimeout(() => {
                state.scadaNotifications.forEach(n => n.value = false);
            }, state.notificationTimeoutDuration)
        },
        SET_NETWORK_ERROR(state) {
            state.networkError = true;
            setTimeout(() => {
                state.networkError = false;
            }, 3000);
        }
    },

    actions: {
        showInfoNotification({commit}, text) {
            commit('ADD_SCADA_NOTIFICATION', new ScadaNotification(text));
        },
        showSuccessNotification({commit}, text) {
            commit('ADD_SCADA_NOTIFICATION', new ScadaNotification(text, 'success'));
        },
        showErrorNotification({commit}, text) {
            commit('ADD_SCADA_NOTIFICATION', new ScadaNotification(text, 'error'));
        },
        showCustomNotification({commit}, {text, type, color, icon}) {
            commit('ADD_SCADA_NOTIFICATION', new ScadaNotification(text, type, color, icon));
        },
        showNetworkErrorNotification({state, commit, dispatch}, text) { 
            if(!state.networkError) {
                dispatch('showErrorNotification', text);
                commit('SET_NETWORK_ERROR');
            }    
        }

    },

    getters: { }

}

export default ScadaUiNotificationsModule;