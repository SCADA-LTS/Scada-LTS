import axios from 'axios';

const gv = {
	state: {},
	mutations: {},
	actions: {

		/**
		 * Fetch point hierarchy data from specific Node
		 * 
		 * @param {Object} Vuex data 
		 * @param {*} nodeId - Node ID for which the hierarchy is to be fetched.
		 * @returns 
		 */
		fetchPointHierarchyNode({dispatch}, nodeId) {
			return new Promise((resolve, reject) => {
				const requestConfiguration = {
					withCredentials: true,
					timeout: 5000,
				}
				axios.get(`.//pointHierarchy/${nodeId}`, requestConfiguration)
					.then(resp => {
						resolve(resp.data);
					})
					.catch(error => {
						reject(error);
					})
			});
		}
	},
	getters: {},
};
export default gv;
