import i18n from '@/i18n';
import Axios from 'axios';
import GenericDataSource from '../../components/datasources/models/GenericDataSource';
import SnmpDataPoint from '../../components/datasources/SnmpDataSource/SnmpDataPoint';
import ScadaVirtualDataPoint from '../../components/datasources/VirtualDataSource/VirtualDataPoint';
import { datasourceDetailsMocks } from './mocks/datasourceapi';

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

		dataSources: new Map()
			.set(1,"virtualdatasource")
			.set(5, "snmpdatasource"),

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
			datasource.description = dataSource.description;
			datasource.connection = dataSource.connection;
			datasource.updatePeriod = dataSource.updatePeriod;
			datasource.updatePeriodType = dataSource.updatePeriodType;
		},
		REMOVE_DATA_SOURCE: (state, dataSourceId) => {
			state.dataSourceList = state.dataSourceList.filter(ds => ds.id !== dataSourceId);
		},
		ADD_DATA_SOURCE: (state, dataSource) => {
			state.dataSourceList.push(dataSource);
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
				setTimeout(() => {
					commit('SET_DATA_SOURCE_LIST', datasourceDetailsMocks);
					resolve()
				}, 2000);
				
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
			return new Promise((resolve) => {
				setTimeout(() => {
					commit('FETCH_DATA_SOURCE_DETAILS', datasourceDetailsMocks[dataSourceId]);
					resolve(datasourceDetailsMocks[dataSourceId]);
				}, 1000);
			})
		},

		async fetchDataPointsForDS({dispatch, commit}, dataSourceId) {
			await commit('SET_DATA_SOURCE_LOADING',{dataSourceId, loading:true});
			return new Promise((resolve, reject) => {
				//Single array of Data Point configuration.
				//http://localhost:8080/ScadaBR/api/datapoint?id=X//
				setTimeout(async () => {
					let p1, p2;
					let dataPoints = [];
					if (dataSourceId === 0) {
						p1 = new ScadaVirtualDataPoint(2);
						p1.initialSetup(3,"AG_T_Numeric_01","AG Test - Numeric",true);
						p1.pointLocator.dataTypeId = 3;
						p1.pointLocator.changeTypeId = 6;
						p1.pointLocator.settable = true;
						p2 = new ScadaVirtualDataPoint(2);
						p2.initialSetup(2,"AG_T_Binary_01","AG Test - Binary", false, 'Extra text');
						p2.pointLocator.dataTypeId = 1;
						p2.pointLocator.changeTypeId = 7;
						p2.pointLocator.settable = true;
						dataPoints = [p1, p2];
					} else if (dataSourceId === 2) {
						p1 = new SnmpDataPoint(2);
						p1.initialSetup(3,"AG_SNMP_Numeric_01","AG Test SNMP - Numeric",true);
						p1.pointLocator.dataTypeId = 3;
						p1.pointLocator.oid = "1.2.1.1.3.2.0"
						p1.pointLocator.settable = true;
						p2 = new SnmpDataPoint(2);
						p2.initialSetup(2,"AG_SNMP_Binary_01","AG Test SNMP - Binary", false, 'Extra text');
						p2.pointLocator.dataTypeId = 1;
						p2.pointLocator.oid = "1.2.1.1.5.3.2"
						p2.pointLocator.settable = true;
						dataPoints = [p1, p2];
					}
					await commit('SET_DATA_POINTS_FOR_DS', {dataSourceId, dataPoints});
					resolve();
				}, 2000)
			});
		},

		/**
		 * Create Data Source
		 * 
		 * Send a POST request to the Core aplication REST API to create a new
		 * DataSource. Based on the typeID of datasource it should create a 
		 * valid DS Type and as a response sould be received DataSourceAPI object.
		 * It sould contain a new generated ID.
		 * 
		 * --- MOCKED ---
		 * 
		 * @param {*} param0 
		 * @param {Object} datasource - DataSource object from Creator component.
		 * @returns {Promise<DataSourceAPI>} DataSource JSON from API
		 */
		createDataSource({commit, dispatch}, datasource) {
			/* Mocking TODO: real method*/
			return new Promise((resolve, reject) => {
				setTimeout(() => {
					const response = {
						id: 10,
						xid: datasource.xid,
						enabled: false,
						name: datasource.name,
						type: datasource.type,
						connection: `${datasource.updatePeriod} ${datasource.updatePeriodType}`,
						description: '',
						activeEvents: null,
						loaded: false,
						datapoints: [],
					}
					commit('ADD_DATA_SOURCE',response);

					resolve(response);
				}, 3000);
			})
		},


		/**
		 * Update Data Source
		 * 
		 * Send a PUT request to the Core aplication REST API to update existing
		 * DataSource. 
		 * 
		 * --- MOCKED ---
		 * 
		 * @param {*} param0 
		 * @param {Object} datasource - DataSource object from Creator component.
		 * @returns 
		 */
		 updateDataSource({commit, dispatch}, datasource) {
			/* Mocking TODO: real method*/
			return new Promise((resolve, reject) => {
				setTimeout(() => {
					console.log(datasource);
					commit('UPDATE_DATA_SOURCE', datasource);
					const response = {
						status: "OK",
					}
					resolve(response);
				}, 1000);
			})
		},

		deleteDataSource({commit, dispatch}, dataSourceId) {
			/* Mocking TODO: real method*/
			return new Promise((resolve, reject) => {
				setTimeout(() => {
					commit("REMOVE_DATA_SOURCE", dataSourceId);
					resolve(response);
				}, 1000);
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

		createDataPointDS({commit, dispatch}, {dataSourceId, dataPoint}) {
			/* Mocking TODO: real method*/
			return new Promise((resolve, reject) => {
				setTimeout(() => {
					commit("ADD_DATA_POINT_IN_DS", {dataSourceId, dataPoint});
					resolve();
				}, 1000);
			});
		},

		updateDataPointDS({commit, dispatch}, {dataSourceId, dataPoint}) {
			/* Mocking TODO: real method*/
			return new Promise((resolve, reject) => {
				setTimeout(() => {
					console.log(dataSourceId, dataPoint);
					// commit("UPDATE_DATA_POINT_IN_DS", {dataSourceId, dataPoint});
					resolve();
				}, 1000);
			});
		},

		deleteDataPointDS({commit, dispatch}, {dataSourceId, dataPointXid}) {
			/* Mocking TODO: real method*/
			return new Promise((resolve, reject) => {
				setTimeout(() => {
					commit("REMOVE_DATA_POINT_IN_DS", {dataSourceId, dataPointXid});
					resolve();
				}, 1000);
			});
		},

		enableAllDataPoints({commit, dispatch}, dataSourceId) {
			return new Promise((resolve, reject) => {
				setTimeout(() => {
					//TODO: SEND REQUEST TO API
					commit('ENABLE_ALL_DATA_POINTS_IN_DS', dataSourceId);
					resolve();
				}, 1000);
			});
		},

		toggleDataSource({commit, dispatch}, dataSourceId) {
			return new Promise((resolve, reject) => {
				setTimeout(() => {
					commit('TOGGLE_DATA_SOURCE', dataSourceId);
					resolve();
				}, 1000);
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
		}
	},
};
export default ds;
