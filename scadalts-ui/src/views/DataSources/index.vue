<template>
	<div>
		<v-container fluid>
			<header>
				<v-row align="center">
					<v-col cols="6">
						<h1>Data Sources</h1>
					</v-col>
					<v-col cols="6" class="row justify-end" v-if="tableLoaded">
						<v-progress-circular
							v-show="savingData"
      						:size="50"
      						color="primary"
      						indeterminate
    					></v-progress-circular>
						<v-btn color="primary" v-show="!savingData" dark fab small @click="createDataSource()">
							<v-icon> mdi-plus </v-icon>
						</v-btn>
					</v-col>
				</v-row>
			</header>

			<v-data-table
				v-if="tableLoaded"
				:headers="headers"
				:items="dataSourceList"
				:single-expand="false"
				:expanded.sync="expanded"
				:sort-by="[]"
				:sort-desc="[]"
				item-key="name"
				multi-sort
				show-expand
				@item-expanded="fetchDataPointList"
			>
				<template v-slot:item.enabled="{ item }">
					<!-- TODO: Make badge button color match the highest alarm level -->
					<v-badge overlap :color="setAlarmColor(item.activeEvents)" dot :value="item.activeEvents > 0">
						<v-btn x-small icon fab elevation="0" @click="toggleDataSource(item)" :color="item.enabled ? 'primary' : 'error'">
							<v-icon v-show="item.enabled">mdi-decagram</v-icon>
							<v-icon v-show="!item.enabled">mdi-decagram-outline</v-icon>
						</v-btn>
					</v-badge>
				</template>
				<template v-slot:item.type="{ item }">
					{{$t(`datasource.type.${dataSources.get(item.type)}`)}}
				</template>
				<template v-slot:expanded-item="{ headers, item }">
					<td :colspan="headers.length" class="small-margin-top">
						<v-row>
							<v-col cols="12" class="flex">
								<DataSourceDetails
									:datasource="item"
									:datasourceType="dataSources.get(item.type)"
									@saved="onDataSourceUpdate"
									@deleted="onDataSourceDelete"
								></DataSourceDetails>
							</v-col>
						</v-row>
						<v-divider></v-divider>
						<v-row class="datapoint-list" v-if="item.loaded">
							<DataSourcePointList
								:datasource="item"
								:datasourceType="dataSources.get(item.type)"
								@create="onDataPointCreation"
								@delete="onDataPointDeletion"
							></DataSourcePointList>
						</v-row>
						<v-skeleton-loader v-else type="article"> </v-skeleton-loader>
					</td>
				</template>
			</v-data-table>
			<v-skeleton-loader v-else type="article"> </v-skeleton-loader>
		</v-container>

		<!-- Related dialog components -->
		<DataSourceCreator
			ref="creator"
			@savedDS="onDataSourceSaved($event)"
		></DataSourceCreator>
		<DataPointCreator
			ref="pointCreator"
			@saved="onDataPointSaved($event)"
			@updated="onDataPointUpdate($event)"
		></DataPointCreator>
		<ConfirmationDialog
			:btnvisible="false"
			ref="deleteDataSource"
			@result="onDataSourceDeleteConfirm"
			title="Do you want to delete this data source?"
			message="This operation connot be undone?"
		></ConfirmationDialog>
	</div>
</template>
<script>
import DataSourceDetails from './DataSourceDetails';
import DataSourceCreator from './DataSourceCreator';
import DataPointCreator from './DataPointCreator';
import DataSourcePointList from './DataSourcePointList';

import ConfirmationDialog from '@/layout/dialogs/ConfirmationDialog';

export default {
	components: {
		DataSourceDetails,
		DataSourceCreator,
		DataPointCreator,
		DataSourcePointList,
		ConfirmationDialog,
	},

	data() {
		return {
			tableLoaded: false,
			expanded: [],
			headers: [
				{
					text: 'Status',
					align: 'center',
					value: 'enabled',
				},
				{
					text: 'Name',
					align: 'center',
					value: 'name',
				},
				{
					text: 'Type',
					align: 'center',
					value: 'type',
				},
				{
					text: 'Connection',
					align: 'center',
					value: 'connection',
				},
				{
					text: 'Status Description',
					align: 'center',
					value: 'description',
				},
				{ text: '', value: 'data-table-expand' },
			],
			dataSourceList: [],
			savingData: false,
			operationQueue: null,
		};
	},

	computed: {
		dataSources() {
			return this.$store.state.dataSource.dataSources;
		}
	},

	mounted() {
		this.fetchDataSources();
	},

	methods: {

		setAlarmColor(alarmLevel) {
			let color;
			switch(alarmLevel) {
				case 1:
					color = "blue"; break;
				case 2:
					color = "yellow"; break;
				case 3:
					color = "orange"; break;
				case 4:
					color = "red"; break;
				default:
					color = "green";
			}
			return color;
		},

		/**
		 * Load basic and generic DataSource list without details.
		 */
		async fetchDataSources() {
			try {
				this.tableLoaded = false;
				this.dataSourceList = await this.$store.dispatch('getDataSources');
				this.tableLoaded = true;
			} catch (e) {
				console.error(e);
				this.tableLoaded = true;
				this.dataSourceList = [];
			}
		},


		async fetchDataPointList({ item, value }) {
			if (value) {
				// Load data from REST API if threre is no datapoints.
				if (!!item.datapoints && item.datapoints.length === 0) {
					item.loaded = false;
					item.datapoints = await this.$store.dispatch('fetchDataPointsForDS', item.name);
					item.loaded = true;
				}
			}
		},

		onDataPointCreation({ item, datapoint }) {
			this.$refs.pointCreator.showDialog(item, datapoint, this.dataSources.get(item.type));
		},

		onDataPointDeletion({ item, datapoint }) {
			//TODO: MAKE CONFIRMATION DIALOG
			console.log(item, datapoint);
			item.datapoints = item.datapoints.filter((e) => {
				return e.xid !== datapoint.xid;
			});
		},

		createDataSource() {
			this.$refs.creator.showDialog();
		},

		onDataPointUpdate(event) {
			console.log(this.dataSourceList);
			console.log(event);
			let x = this.dataSourceList.find((e) => {
				return e.id === event.dp.id;
			});
			let z = x.datapoints.find((e) => {
				return e.xid === event.e.xid;
			});
			z = event.e;
			console.log(z);
		},

		onDataPointSaved(event) {
			console.log(this.dataSourceList);
			console.log(event);
			let x = this.dataSourceList.find((e) => {
				return e.id === event.dp.id;
			});
			x.datapoints.push(event.e);
		},

		async onDataSourceUpdate(event) {
			this.savingData = true;
			event.type = this.$store.getters.dataSourceTypeId(event.type)
			try {
				let resp = await this.$store.dispatch("updateDataSource", event)
				console.log(resp);
			} catch (error) {
				console.error(error);
			} finally {
				this.savingData = false;
			}
		},

		onDataSourceDelete(event) {
			this.$refs.deleteDataSource.showDialog();
			this.operationQueue = event;
		},

		onDataSourceDeleteConfirm(evnet) {
			if(evnet) {
				this.dataSourceList = this.dataSourceList.filter((e) => {
					return e.id !== this.operationQueue;
				});
			}
		},

		toggleDataSource(ds){
			ds.enabled = !ds.enabled;
		},

		async onDataSourceSaved(event) {
			event.type = this.$store.getters.dataSourceTypeId(event.type)
			this.savingData = true;
			try {
				let resp = await this.$store.dispatch("createDataSource", event)
				this.dataSourceList.push(resp);
				console.log(resp);
			} catch (error) {
				console.error(error);
			} finally {
				this.savingData = false;
			}
		},
	},
};
</script>
<style scoped>
.flex {
	display: flex;
}
.datapoint-list {
	padding: 0 2%;
}

.small-margin-top > .row {
	margin-top: 8px;
}

@media (min-width: 1264px) {
	.datapoint-list {
		padding: 0 3%;
	}
}
</style>
