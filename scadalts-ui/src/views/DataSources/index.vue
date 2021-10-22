<template>
	<div>
		<v-container fluid>
			<header>
				<v-row align="center">
					<v-col cols="6">
						<h1>Data Sources</h1>
					</v-col>
					<v-col cols="6" class="row justify-end" v-if="tableLoaded">
						<v-text-field
							v-model="search"
							append-icon="mdi-magnify"
							label="Search"
							single-line
							hide-details
						></v-text-field>
						<v-progress-circular
							v-show="savingData"
							:size="50"
							color="primary"
							indeterminate
						></v-progress-circular>
						<v-btn
							color="primary"
							v-show="!savingData"
							dark
							fab
							small
							@click="createDataSource()"
						>
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
				:search="search"
				item-key="name"
				multi-sort
				show-expand
				@item-expanded="fetchDataPointList"
			>
				<template v-slot:item.enabled="{ item }">
					<v-badge
						overlap
						:color="setAlarmColor(item.maxAlarmLevel)"
						dot
						:value="item.maxAlarmLevel > 0"
					>
						<v-btn
							x-small
							icon
							fab
							elevation="0"
							@click="toggleDataSource(item)"
							:color="item.enabled ? 'primary' : 'error'"
						>
							<v-icon v-show="item.enabled">mdi-decagram</v-icon>
							<v-icon v-show="!item.enabled">mdi-decagram-outline</v-icon>
						</v-btn>
					</v-badge>
				</template>
				<template v-slot:item.type="{ item }">
					{{ $t(`datasource.type.${$store.getters.dataSourceTypeName(item.type)}`) }}
				</template>
				<template v-slot:expanded-item="{ headers, item }">
					<!-- Single Data Source Item Details Row -->
					<td
						:colspan="headers.length"
						class="small-margin-top"
						v-if="!!dataSources.get(item.type)"
					>
						<v-row class="data-source-item--details">
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

						<v-row class="data-source-item--datapoint-list" v-if="!item.loading">
							<DataSourcePointList
								:datasource="item"
								:datasourceType="dataSources.get(item.type)"
								@edit="onDataPointEdit"
								@create="onDataPointCreation"
								@delete="onDataPointDeletion"
							></DataSourcePointList>
						</v-row>
						<v-skeleton-loader v-else type="article"> </v-skeleton-loader>
					</td>
					<td :colspan="headers.length" class="small-margin-top" v-else>
						<div>
							This Data Source Type is not supportet yet. Please use the classic Data
							Source page to edit this item.
						</div>
					</td>
				</template>
			</v-data-table>
			<v-skeleton-loader v-else type="article"></v-skeleton-loader>
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
		<ConfirmationDialog
			:btnvisible="false"
			ref="deleteDataPoint"
			@result="onDataPointDeleteConfirm"
			title="Do you want to delete this data point?"
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
			search: '',
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
					text: 'Export ID',
					align: 'center',
					value: 'xid',
				},
				{ text: '', value: 'data-table-expand' },
			],
			savingData: false,
			operationQueue: null,
		};
	},

	computed: {
		dataSources() {
			return this.$store.state.dataSource.dataSources;
		},
		dataSourceList() {
			return this.$store.state.dataSource.dataSourceList;
		},
	},

	mounted() {
		this.fetchDataSources();
	},

	methods: {
		setAlarmColor(alarmLevel) {
			let color;
			switch (alarmLevel) {
				case 1:
					color = 'blue';
					break;
				case 2:
					color = 'yellow';
					break;
				case 3:
					color = 'orange';
					break;
				case 4:
					color = 'red';
					break;
				default:
					color = 'green';
			}
			return color;
		},

		/**
		 * Load basic and generic DataSource list without details.
		 */
		async fetchDataSources() {
			try {
				this.tableLoaded = false;
				await this.$store.dispatch('getDataSources');
				this.tableLoaded = true;
			} catch (e) {
				console.error(e);
				this.tableLoaded = true;
			}
		},

		async fetchDataPointList({ item, value }) {
			if (value) {
				// Load data from REST API if threre is no datapoints.
				if (!!item.datapoints && item.datapoints.length === 0) {
					item.loading = true;
					await this.$store.dispatch('fetchDataPointsForDS', item.id);
				}
			}
		},

		onDataPointEdit({ item, datapoint }) {
			console.debug('DataSources.index.vue::onDataPoinEdit()');
			this.$refs.pointCreator.showDialog(
				item,
				datapoint,
				this.dataSources.get(item.type),
			);
		},

		onDataPointCreation(item) {
			console.debug('DataSources.index.vue::onDataPointCreation()');
			this.$refs.pointCreator.showDialog(item, null, this.dataSources.get(item.type));
		},

		onDataPointDeletion({ item, datapoint }) {
			console.debug('DataSources.index.vue::onDataPointDeletion()');
			this.$refs.deleteDataPoint.showDialog();
			this.operationQueue = { item, datapoint };
		},

		onDataPointDeleteConfirm(event) {
			if (event) {
				this.$store.dispatch('deleteDataPointDS', {
					dataSourceId: this.operationQueue.item.id,
					dataPointXid: this.operationQueue.datapoint.xid,
				});
			}
		},

		createDataSource() {
			this.$refs.creator.showDialog();
		},

		onDataPointUpdate(event) {
			console.debug('DataSources.index.vue::onDataPointUpdate()');
			this.$store.dispatch('updateDataPointDS', {
				dataSourceType: event.dp.type,
				dataPoint: event.e,
			});
		},

		onDataPointSaved(event) {
			console.debug('DataSources.index.vue::onDataPointSaved()');
			this.$store.dispatch('createDataPointDS', {
				dataSource: event.dp,
				dataPoint: event.e,
			});
		},

		async onDataSourceUpdate(event) {
			console.debug('DataSources.index.vue::onDataSourceUpdate()');
			this.savingData = true;
			// event.type = this.$store.getters.dataSourceTypeId(event.type)
			try {
				let resp = await this.$store.dispatch('updateDataSource', event);
				console.log(resp);
			} catch (error) {
				console.error(error);
			} finally {
				this.savingData = false;
			}
		},

		onDataSourceDelete(event) {
			console.debug('DataSources.index.vue::onDataSourceDelete()');
			this.$refs.deleteDataSource.showDialog();
			this.operationQueue = event;
		},

		onDataSourceDeleteConfirm(evnet) {
			if (evnet) {
				this.$store.dispatch('deleteDataSource', this.operationQueue);
			}
		},

		toggleDataSource(ds) {
			this.$store.dispatch('toggleDataSource', ds.id);
		},

		async onDataSourceSaved(event) {
			console.debug('DataSources.index.vue::onDataSourceSaved()');
			event.type = this.$store.getters.dataSourceTypeId(event.type);
			this.savingData = true;
			try {
				let resp = await this.$store.dispatch('createDataSource', event);
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
.data-source-item--datapoint-list {
	padding: 0 2%;
}

.small-margin-top > .row {
	margin-top: 8px;
}

@media (min-width: 1264px) {
	.data-source-item--datapoint-list {
		padding: 0 3%;
	}
}
</style>
