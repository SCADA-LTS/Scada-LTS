import i18n from '@/i18n';
import Axios from 'axios';
import GenericDataSource from '../../components/datasources/models/GenericDataSource';


/**
 * Data Source received form REST API
 * @typedef {Object} DataSourceAPI
 * @property {Number} 	id
 * @property {String} 	xid
 * @property {Boolean} 	enabled
 * @property {String} 	name
 * @property {Number} 	type
 * @property {String} 	connection
 * @property {String} 	description
 * @property {Number} 	activeEvents
 * @property {Boolean}	loaded
 * @property {Object}	datapoints
 */

const ds = {
	state: {
		datasourcesApiUrl: './api/datasource',
		requestOptions: {
			timeout: 5000,
			useCredentials: true,
			credentials: 'same-origin',
		},

		/**
		 * Data Source Definitions
		 * Add new Data Source types here
		 */
		dataSources: new Map()
			.set(1,"virtualdatasource")
			.set(3, "modbusdatasource")
			.set(5, "snmpdatasource")
			.set(9, "metadatasource"), // 9

		dataSourceList: [],
	},
	mutations: {
		SET_DATA_SOURCE_LIST: (state, dataSourceList) => {
			state.dataSourceList = [];
			dataSourceList.forEach(dataSource => {
				let ds = new GenericDataSource("DS");
				ds.loadDataFromJson(dataSource);
				state.dataSourceList.push(ds);
			})
		},
		UPDATE_DATA_SOURCE: (state, dataSource) => {
			let datasource = state.dataSourceList.find(ds => ds.id === dataSource.id);
			datasource.name = dataSource.name;
			datasource.xid = dataSource.xid;
			datasource.type = dataSource.type;
			datasource.description = dataSource.description;
			datasource.connection = dataSource.connection;
			datasource.updatePeriod = dataSource.updatePeriod;
			datasource.updatePeriodType = dataSource.updatePeriodType;
		},
		REMOVE_DATA_SOURCE: (state, dataSourceId) => {
			state.dataSourceList = state.dataSourceList.filter(ds => ds.id !== dataSourceId);
		},
		ADD_DATA_SOURCE: (state, dataSource) => {
			let ds = new GenericDataSource("DS");
			ds.loadDataFromJson(dataSource);
			state.dataSourceList.push(ds);
			console.log(state.dataSourceList);
		},
		FETCH_DATA_SOURCE_DETAILS: (state, dataSource) => {
			let datasource = state.dataSourceList.find(ds => ds.id === dataSource.id);
			datasource = {...datasource, ...dataSource};
		},
		TOGGLE_DATA_SOURCE: (state, dataSourceId) => {
			let datasource = state.dataSourceList.find(ds => ds.id === dataSourceId);
			datasource.enabled = !datasource.enabled;
		},
		SET_DATA_SOURCE_LOADING: (state, {dataSourceId, loading}) => {
			let datasource = state.dataSourceList.find(ds => ds.id === dataSourceId);
			datasource.loading = loading;
		},
		SET_DATA_POINTS_FOR_DS: (state, {dataSourceId, dataPoints}) => {
			let datasource = state.dataSourceList.find(ds => ds.id === dataSourceId);
			datasource.datapoints = dataPoints;
			datasource.loading = false;
		},
		ADD_DATA_POINT_IN_DS: (state, {dataSourceId, dataPoint}) => {
			let datasource = state.dataSourceList.find(ds => ds.id === dataSourceId);
			console.log(datasource);
			if(datasource.datapoints === undefined) {
				datasource.datapoints = [];
			}
			datasource.datapoints.push(dataPoint);
		},
		REMOVE_DATA_POINT_IN_DS: (state, {dataSourceId, dataPointXid}) => {
			let datasource = state.dataSourceList.find(ds => ds.id === dataSourceId);
			datasource.datapoints = datasource.datapoints.filter(dp => dp.xid !== dataPointXid);
		},
		ENABLE_ALL_DATA_POINTS_IN_DS: (state, dataSourceId) => {
			let datasource = state.dataSourceList.find(ds => ds.id === dataSourceId);
			if(!!datasource.datapoints && datasource.datapoints.length > 0) {
				datasource.datapoints.forEach(dp => {
					dp.enabled = true;
				});
			}
		}
	},
	actions: {

		/**
		 * Get All DataSources
		 *
		 * @param {*} param0
		 * @returns {Promise<DataSourceAPI>} DataSource JSON from API
		 */
		getDataSources({ dispatch, commit }) {


			return new Promise((resolve, reject) => {
				dispatch('requestGet', '/datasources').then(response => {
					commit('SET_DATA_SOURCE_LIST', response);
					resolve();
				}).catch(error => {
					console.error(error);
					reject();
				});
			});
		},

		/**
		 * Get DataSource Details
		 *
		 * Details are dependend on the DataSource Type.
		 * The logic to parse that data should be written
		 * in speficic datasource component.
		 *
		 * @param {*} param0
		 * @param {Number} dataSourceId - ID number of DataSource
		 * @returns
		 */
		fetchDataSourceDetails({commit, dispatch}, dataSourceId) {
			return new Promise((resolve, reject) => {
				dispatch('requestGet', `/datasource?id=${dataSourceId}`).then(response => {
					commit('FETCH_DATA_SOURCE_DETAILS', response);
					resolve(response);
				}).catch(error => {
					console.error(error);
					reject();
				});
			})
		},

		async fetchDataPointsForDS({dispatch, commit}, dataSourceId) {
			await commit('SET_DATA_SOURCE_LOADING',{dataSourceId, loading:true});
			return new Promise((resolve, reject) => {
				//Single array of Data Point configuration.
				//http://localhost:8080/Scada-LTS/api/datapoint?id=X//
				dispatch('requestGet', `/datapoint/datasource?id=${dataSourceId}`)
				.then(response => {
					commit('SET_DATA_POINTS_FOR_DS', {dataSourceId, dataPoints: response});
					resolve();
				}).catch(error => {
					console.error(error);
					reject();
				});
			});
		},

		getUniqueDataSourceXid({dispatch}) {
			return dispatch("requestGet", "/datasource/generateUniqueXid");
		},

		/**
		 * Create Data Source
		 *
		 * Send a POST request to the Core aplication REST API to create a new
		 * DataSource. Based on the typeID of datasource it should create a
		 * valid DS Type and as a response sould be received DataSourceAPI object.
		 * It sould contain a new generated ID.
		 *
		 * @param {*} param0
		 * @param {Object} datasource - DataSource object from Creator component.
		 * @returns {Promise<DataSourceAPI>} DataSource JSON from API
		 */
		createDataSource({commit, dispatch}, datasource) {
			return new Promise((resolve, reject) => {
				datasource.id = -1;
				dispatch('requestPost', {
					url: `/datasource`,
					data: datasource,
				}).then(response => {
					commit('ADD_DATA_SOURCE',response);
					resolve(response);
				}).catch(error => {
					console.error(error);
					reject();
				});
			})
		},


		/**
		 * Update Data Source
		 *
		 * Send a PUT request to the Core aplication REST API to update existing
		 * DataSource.
		 *
		 * @param {*} param0
		 * @param {Object} datasource - DataSource object from Creator component.
		 * @returns
		 */
		 updateDataSource({commit, dispatch}, datasource) {
			return new Promise((resolve, reject) => {
				dispatch('requestPut', {
					url: `/datasource`,
					data: datasource,
				}).then(() => {
					commit('UPDATE_DATA_SOURCE',datasource);
					resolve(datasource);
				}).catch(error => {
					console.error(error);
					reject();
				});
			})
		},

		deleteDataSource({commit, dispatch}, dataSourceId) {
			return new Promise((resolve, reject) => {
				dispatch('requestDelete', `/datasource?id=${dataSourceId}`)
				.then(response => {
					commit('REMOVE_DATA_SOURCE',dataSourceId);
					resolve(response);
				}).catch(error => {
					console.error(error);
					reject();
				});
			})
		},

		getAllDataSources(context) {
			return new Promise((resolve, reject) => {
				Axios.get(
					`${context.state.datasourcesApiUrl}/getAll`,
					context.state.requestOptions
				)
					.then((r) => {
						if (r.status === 200) {
							resolve(r.data);
						} else {
							reject(false);
						}
					})
					.catch((error) => {
						console.error(error);
						reject(false);
					});
			});
		},

		getAllPlcDataSources(context) {
			return new Promise((resolve, reject) => {
				Axios.get(
					`${context.state.datasourcesApiUrl}/getAllPlc`,
					context.state.requestOptions
				)
					.then((r) => {
						if (r.status === 200) {
							resolve(r.data);
						} else {
							reject(false);
						}
					})
					.catch((error) => {
						console.error(error);
						reject(false);
					});
			});
		},

		fetchDataSourcesList({dispatch}) {
			return dispatch('requestGet', '/datasource/getAll');
		},

		createDataPointDS({commit, dispatch}, {dataSource, dataPoint}) {
			return new Promise((resolve, reject) => {
				dataPoint.dataSourceTypeId = dataSource.type;
				dataPoint.dataSourceId = dataSource.id;
				dataPoint.deviceName = `${dataSource.name} - ${dataPoint.name}`;
				dataPoint.pointLocator.dataSourceTypeId = dataSource.type;
				dispatch('requestPost', {
					url: `/datapoint`,
					data: dataPoint
				}).then(response => {
					commit('ADD_DATA_POINT_IN_DS', {
						dataSourceId: dataSource.id,
						dataPoint: response
					});
					resolve();
				}).catch(error => {
					console.error(error);
					reject();
				});
			});
		},

		updateDataPointDS({commit, dispatch}, {dataSourceType, dataPoint}) {
			return new Promise((resolve, reject) => {
				dataPoint.pointLocator.dataSourceTypeId = dataSourceType;
				dispatch('requestPut', {
					url: `/datapoint`,
					data: dataPoint,
				}).then(response => {
					console.log(response);
					resolve();
					// commit('UPDATE_DATA_SOURCE',response);
					// resolve(response);
				}).catch(error => {
					console.error(error);
					reject();
				});
			});
		},

		deleteDataPointDS({commit, dispatch}, {dataSourceId, dataPointXid}) {
			return new Promise((resolve, reject) => {
				dispatch('requestDelete', `/datapoint?xid=${dataPointXid}`)
				.then(response => {
					commit("REMOVE_DATA_POINT_IN_DS", {dataSourceId, dataPointXid});
					resolve();
				}).catch(error => {
					console.error(error);
					reject();
				});
			});
		},

		enableAllDataPoints({commit, dispatch}, dataSourceId) {
			return new Promise((resolve, reject) => {
				dispatch('requestGet', `/datasource/datapoints/enable?id=${dataSourceId}`)
				.then((resp) => {
					commit('ENABLE_ALL_DATA_POINTS_IN_DS', dataSourceId);
					resolve();
				}).catch(error => {
					console.error(error);
					reject();
				});
			});
		},

		toggleDataSource({commit, dispatch}, dataSourceId) {
			return new Promise((resolve, reject) => {
				dispatch('requestGet', `/datasource/toggle?id=${dataSourceId}`)
				.then(() => {
					commit('TOGGLE_DATA_SOURCE', dataSourceId);
					resolve();
				}).catch(error => {
					console.error(error);
					reject();
				});
			});
		},

		//ModBus DataSource methods
        modbusNodeScan({dispatch}, datasourceId) {
        	return new Promise((resolve, reject) => {
        		setTimeout(() => {
        			if(datasourceId === 1) {
        				let response = [
        					{
        						name: 'Point 1'
        					}
        				];
        				resolve(response);
        			} else {
        				reject();
        			}
        		}, 1000);
        	});
        },

        modbusNodeScanCancel({dispatch}, datasourceId) {
        	return new Promise((resolve, reject) => {
        		setTimeout(() => {
        			resolve();
        		}, 1000);
        	})
        },

        modbusReadData({dispatch}, configuration) {
        	return new Promise((resolve, reject) => {
        		setTimeout(() => {
        			let request = `${configuration.slaveId}/${configuration.range}`
        			console.log(request);
        			resolve();
        		}, 1000);
        	});
        },

        modbusPointLocatorTest({dispatch}, configuration) {
			configuration.pointLocator.dataSourceTypeId = 3;
			return dispatch('requestPost', {
				url: `/datasources/modbusip/testpointlocator`,
				data: configuration
			});
        },

		//SNMP DataSource methods
		snmpTest({dispatch}, {datasource, oid}) {
			console.log(datasource);
			console.log(oid);
			return dispatch('requestPost', {
				url: `/datasources/snmp/testSnmp`,
				data: Object.assign({}, datasource, {oid})
			});


		}
	},

	getters: {
		dataSourceList(state) {
			let datasources = [];
			state.dataSources.forEach(dsType => {
				datasources.push({
					value: `${dsType}`,
					text: i18n.t(`datasource.type.${dsType}`)
				});
			});	
            return datasources;
		},

		dataSourceTypeId:(state) => (datasourceType) => {
			for (let [key, value] of state.dataSources.entries()) {
				if(value === datasourceType) {
					return key;
				}
			}
			return -1;
		},

		dataSourceTypeName:(state) => (datasourceTypeId) => {
			let ds = state.dataSources.get(datasourceTypeId);
			return !!ds ? ds : "unrecognized";
		}

	},
};
export default ds;
