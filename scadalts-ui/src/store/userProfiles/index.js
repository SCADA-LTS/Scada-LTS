/**
 * User Profile Module
 * 
 * Handle the UserProfile permissions
 * and manage the Request to REST API.
 * 
 * @author Radoslaw Jajko <rjajko@softq.pl>
 * @version 1.0.0
 */
const userProfileModule = {
	state: {
		activeUserProfile: {
			id: -1,
			xid: '',
			name: '',
			dataSourcePermissions: [],
			dataPointPermissions: [],
			viewPermissions: [],
			watchlistPermissions: []
		},
		activeUserProfileRevert: null,
	},

	mutations: {
		SET_ACTIVE_UP(state, userProfile) {
			state.activeUserProfile = userProfile;
			state.activeUserProfileRevert = JSON.parse(JSON.stringify(userProfile));
		},

		SET_CLEAR_UP(state) {
			state.activeUserProfile = {
				id: -1,
				xid: '',
				name: '',
				dataSourcePermissions: [],
				dataPointPermissions: [],
				viewPermissions: [],
				watchlistPermissions: []
			};
			state.activeUserProfileRevert = JSON.parse(JSON.stringify(state.activeUserProfile));
		},

		UPDATE_PERMISSION_UP_DS(state, dataSource) {
			if(dataSource.permission === true) {
				state.activeUserProfile.dataSourcePermissions.push(dataSource.id);
			} else {
				state.activeUserProfile.dataSourcePermissions
					= state.activeUserProfile.dataSourcePermissions.filter(
						(id) => id !== dataSource.id
					);
			}
		},

		UPDATE_PERMISSION_UP_DP(state, dataPoint) {
			if(dataPoint.permission === 0) {
				state.activeUserProfile.dataPointPermissions 
					= state.activeUserProfile.dataPointPermissions
						.filter(dp => dp.dataPointId !== dataPoint.id);
			} else {
				let point = state.activeUserProfile.dataPointPermissions
					.find(dp => dp.dataPointId === dataPoint.id);
				if(!!point) {
					point.permission = dataPoint.permission;
				} else {
					state.activeUserProfile.dataPointPermissions.push({
						dataPointId: dataPoint.id,
						permission: dataPoint.permission
					});
				}
			}
		},

		UPDATE_PERMISSION_UP_GV(state, graphicalView) {
			if(graphicalView.permission === 0) {
				state.activeUserProfile.viewPermissions 
					= state.activeUserProfile.viewPermissions
						.filter(gv => gv.id !== graphicalView.id);
			} else {
				let view = state.activeUserProfile.viewPermissions
					.find(gv => gv.id === graphicalView.id);
				if(!!view) {
					view.permission = graphicalView.permission;
				} else {
					state.activeUserProfile.viewPermissions.push({
						id: graphicalView.id,
						permission: graphicalView.permission
					});
				}
			}
		},

		UPDATE_PERMISSION_UP_WL(state, watchList) {
			if(watchList.permission === 0) {
				state.activeUserProfile.watchlistPermissions 
					= state.activeUserProfile.watchlistPermissions
						.filter(wl => wl.id !== watchList.id);
			} else {
				let watch = state.activeUserProfile.watchlistPermissions
					.find(wl => wl.id === watchList.id);
				if(!!watch) {
					watch.permission = watchList.permission;
				} else {
					state.activeUserProfile.watchlistPermissions.push({
						id: watchList.id,
						permission: watchList.permission
					});
				}
			}
		},


	},

	actions: {
		getUserProfilesList({ dispatch }) {
			return dispatch('requestGet', `/userProfiles/`);
		},

        getUserProfile({ dispatch, commit }, id) {
			return new Promise(async (resolve, reject) => {
				try {
					let profile = await dispatch('requestGet', `/userProfiles/${id}`);
					commit('SET_ACTIVE_UP', profile);
					resolve(profile);
				} catch (e) {
					commit('SET_CLEAR_UP');
					reject(e);
				}
			})
        },

		getUserProfileUniqueXid({ dispatch }) {
			return dispatch('requestGet', `/userProfiles/generateXid`);
		},

		createUserProfile({ state, dispatch }, {name, xid}) {
			let userProfile = state.activeUserProfile;
			userProfile.id = -1;
			userProfile.name = name;
			userProfile.xid = xid;

            return dispatch('requestPost', {
				url: `/userProfiles/`,
				data: userProfile
			});
        },

		updateUserProfile({ state, dispatch }, profileName) {
			let userProfile = state.activeUserProfile;
			userProfile.name = profileName;
            return dispatch('requestPut', {
				url: `/userProfiles/`,
				data: userProfile
			});
        },

		deleteUserProfile({ dispatch }, id) {
            return dispatch('requestDelete', `/userProfiles/${id}`);
        }
	},

	getters: {},
};

export default userProfileModule;
