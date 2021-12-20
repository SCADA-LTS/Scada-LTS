import axios from 'axios';
import PointHierarchyNode from '@/models/PointHierarchyNode';

const requestConfiguration = {
	withCredentials: true,
	timeout: 5000,
};

const gv = {
	state: {
		pointHierarchy: [],
	},
	mutations: {
		SET_POINT_HIERARCHY_BY_API(state, apiData) {
			state.pointHierarchy = [];
			apiData.forEach((item) => {
				state.pointHierarchy.push(new PointHierarchyNode(item));
			});
		},

		SET_POINT_HIERARCHY(state, pointHierarchy) {
			state.pointHierarchy = pointHierarchy;
		},

		ADD_POINT_HIERARCHY_ROOT_NODE(state, node) {
			state.pointHierarchy.push(node);
		},

		ADD_POINT_HIERARCHY_NODE(state, { parentNode, apiData }) {
			let result = searchFolderInHierarchy(state.pointHierarchy, parentNode);
			if (!!result) {
				result.children = [];
				apiData.forEach((item) => {
					result.children.push(new PointHierarchyNode(item));
				});
			}
		},
	},
	actions: {
		/**
		 * Fetch point hierarchy data from specific Node
		 *
		 * @param {Object} Vuex data
		 * @param {*} nodeId - Node ID for which the hierarchy is to be fetched.
		 * @returns
		 */
		fetchPointHierarchyNode({ dispatch }, nodeId) {
			return new Promise((resolve, reject) => {
				axios
					.get(`.//pointHierarchy/${nodeId}`, requestConfiguration)
					.then((resp) => {
						resolve(resp.data);
					})
					.catch((error) => {
						reject(error);
					});
			});
		},

		createPointHierarchyNode({ dispatch }, { parentNodeId, nodeName }) {
			return new Promise((resolve, reject) => {
				axios
					.post(`.//pointHierarchy/new/${parentNodeId}/${nodeName}`, requestConfiguration)
					.then((resp) => {
						resolve(resp.data);
					})
					.catch((error) => {
						reject(error);
					});
			});
		},

		editPointHierarchyNode({ dispatch }, { nodeId, parentNodeId, nodeName }) {
			return new Promise((resolve, reject) => {
				axios
					.post(
						`.//pointHierarchy/edit/${parentNodeId}/${nodeId}/${nodeName}`,
						requestConfiguration,
					)
					.then((resp) => {
						resolve(resp.data);
					})
					.catch((error) => {
						reject(error);
					});
			});
		},

		movePointHierarchyNode(
			{ dispatch },
			{ nodeId, parentNodeId, newParentNodeId, isFolder },
		) {
			return new Promise((resolve, reject) => {
				axios
					.post(
						`.//pointHierarchy/move/${nodeId}/${parentNodeId}/${newParentNodeId}/${isFolder}`,
						requestConfiguration,
					)
					.then((resp) => {
						resolve(resp.data);
					})
					.catch((error) => {
						reject(error);
					});
			});
		},

		deletePointHierarchyNode({ dispatch }, { nodeId, parentNodeId, isFolder }) {
			return new Promise((resolve, reject) => {
				axios
					.post(
						`.//pointHierarchy/del/${parentNodeId}/${nodeId}/${isFolder}`,
						requestConfiguration,
					)
					.then((resp) => {
						resolve(resp.data);
					})
					.catch((error) => {
						reject(error);
					});
			});
		},
	},
	getters: {},
};
export default gv;

export function searchFolderInHierarchy(hierarchyEntries, folderId, debug = false) {
	if (debug) console.debug('UTILS::SearchDataPointInPointHierarchy\nSearching...');
	if (!!hierarchyEntries && hierarchyEntries.length > 0) {
		let result = hierarchyEntries.find((item) => !!item.folder && item.id === folderId);
		if (!!result) {
			return result;
		} else {
			for (let hierarchyEntry of hierarchyEntries) {
				if (
					hierarchyEntry.folder &&
					!!hierarchyEntry.children &&
					hierarchyEntry.children.length > 0
				) {
					return searchFolderInHierarchy(hierarchyEntry.children, folderId);
				}
			}
		}
	}
	return null;
}
