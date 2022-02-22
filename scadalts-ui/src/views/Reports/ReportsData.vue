<template>
	<div>
		<v-container fluid v-if="!!reportInstanceList">
			<v-row>
				<v-col cols="12" lg="5">
					<v-card>
						<v-card-title>
							<v-text-field
								v-model="search"
								append-icon="mdi-magnify"
								:label="$t('common.search')"
								class="mr-2"
								single-line
								hide-details
							></v-text-field>
						</v-card-title>
						<v-data-table
							:headers="headers"
							:items="reportInstanceList"
							:options.sync="options"
							:server-items-length="reportInstanceList.length"
							multi-sort
							@click:row="clickRow"
						>
							<template v-slot:item.runDuration="{ item }">
								{{ item.runEndTime - item.runStartTime }}
							</template>
							<template v-slot:item.actions="{ item }">
								<a :href="`/ScadaBR/export/export.csv?instanceId=${item.id}`">
									<v-icon title="export data"> mdi-download-box </v-icon>
								</a>
								<a :href="`/ScadaBR/eventExport/events.csv?instanceId=${item.id}`">
									<v-icon title="events"> mdi-bell </v-icon>
								</a>
								<a :href="`/ScadaBR/userCommentExport/comments.csv?instanceId=${item.id}`">
									<v-icon title="user comments"> mdi-comment </v-icon>
								</a>
								<a :href="`/ScadaBR/reportChart.shtm?instanceId=${item.id}`">
									<v-icon title="charts"> mdi-chart-line </v-icon>
								</a>
								<v-icon @click.stop="deleteReport(item.id)" title="delete">
									mdi-delete-forever
								</v-icon>
							</template>
							<template v-slot:item.preventPurge="{ item }">
								<input
									type="checkbox"
									@change="togglePurge(item)"
									:checked="item.preventPurge"
								/>
							</template>
						</v-data-table>
					</v-card>
				</v-col>
				<v-col cols="12" lg="7">
					<!-- <iframe id='reportChart' v-if="!!selectedItem"  :src="`/ScadaBR/reportChart.shtm?instanceId=${selectedItem.id}`"/> -->
				</v-col>
			</v-row>
		</v-container>
		<v-skeleton-loader type="article" v-else> </v-skeleton-loader>
	</div>
</template>
<style scoped>
.historical-alarms {
	z-index: -1;
}
iframe#reportChart {
	width: 100%;
	height: 75vh;
	border: 0;
	float: right;
}
</style>

<script>
/**
 * @author sselvaggi
 */
export default {
	name: 'reportInstanceList',

	data() {
		return {
			selectedItem: null,
			search: '',

			headers: [
				// Report name 	Run time start 	Run duration 	From 	To 	Records 	Do not purge
				{
					text: this.$t('reports.reportName'),
					sortable: true,
					align: 'center',
					value: 'name',
				},
				{
					text: this.$t('reports.runTimeStart'),
					align: 'center',
					sortable: true,
					value: 'prettyRunStartTime',
				},
				{
					text: this.$t('reports.runDuration'),
					sortable: true,
					align: 'center',
					value: 'runDuration',
				},
				{
					text: this.$t('reports.reportRecords'),
					align: 'center',
					sortable: true,
					value: 'recordCount',
				},
				{
					text: this.$t('reports.doNotPurge'),
					align: 'center',
					value: 'preventPurge',
					sortable: false,
				},
				{
					text: this.$t('common.actions'),
					align: 'center',
					sortable: false,
					value: 'actions',
				},
			],
		};
	},
	computed: {
		reportInstanceList() {
			return this.search.length > 0
				? this.$store.getters.filteredReportInstances(this.search)
				: this.$store.state.storeReports.reportInstances;
		},
	},
	methods: {
		togglePurge(value) {
			this.$store.dispatch('setPreventPurge', {
				id: value.id, preventPurge: !value.preventPurge,
			});
		},
		
		clickRow(item) {
			this.selectedItem = item;
		},

		deleteReport(id) {
			this.$store.dispatch('deleteReportInstance', id).then(() => {
				this.$store.dispatch('showSuccessNotification', "Deleted successfully");
				this.$store.dispatch('fetchReportInstances');
			});
		},
	},
};
</script>
