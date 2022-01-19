<template>
	<div>
		<v-container fluid v-if="!!reportInstanceList">	
			<v-row>
				<v-col cols="5">
					<v-card>
						<v-card-title>
							<v-text-field
								v-model="search"
								@input="fetchReportInstanceList"
								append-icon="mdi-magnify"
								:label="$t('common.search')"
								class="mr-2"
								single-line
								hide-details
							></v-text-field>
							<v-btn
								color="primary" 
							>{{$t('reports.newReport')}}</v-btn>
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
								{{item.runEndTime - item.runStartTime}}
							</template>
							<template v-slot:item.actions="{ item }">	
								<a :href="`/ScadaBR/export/export.csv?instanceId=${item.id}`"><v-icon style="border:0px" title="export data">
									mdi-download-box
								</v-icon></a>
								<a :href="`/ScadaBR/eventExport/events.csv?instanceId=${item.id}`">
								<v-icon style="border:0px" title="events">
									mdi-bell
								</v-icon>
								</a>
								<a :href="`/ScadaBR/userCommentExport/comments.csv?instanceId=${item.id}`">
								<v-icon style="border:0px" title="user comments">
									mdi-comment
								</v-icon>
								</a>
								<a :href="`/ScadaBR/reportChart.shtm?instanceId=${item.id}`">
								<v-icon style="border:0px" title="charts">
									mdi-chart-line
								</v-icon>
								</a>
								<v-icon style="border:0px" @click.stop="deleteReport(item.id)" title="delete">
									mdi-delete-forever
								</v-icon>
							</template>
							<template v-slot:item.preventPurge="{ item }">	
								<input type=checkbox @change="togglePurge(item)"  :checked="item.preventPurge"/>
							</template>
							
						</v-data-table>	
						
					</v-card>
				</v-col>
				<iframe id='reportChart' v-if="selectedItemId != -1"  :src="`/ScadaBR/reportChart.shtm?instanceId=${selectedItemId}`"/>
				
			</v-row>
		</v-container>
		 

		<div class="text-center ma-2">
			<v-snackbar v-model="snackbar">
			{{snackbarMessage}}
			<template v-slot:action="{ attrs }">
				<v-btn
				color="pink"
				text
				v-bind="attrs"
				@click="snackbar = false"
				>{{$t('common.close')}}</v-btn>
			</template>
			</v-snackbar>
		</div>				
	</div>
	
</template>
<style scoped>
 
.historical-alarms {
	z-index: -1;
}
iframe#reportChart {
	width:55%;
	height: 1200px;
	border:0;
	float:right
}
</style>

<script>
/**
 * @author sselvaggi
 */
export default {
	name: 'reportInstanceList',
	components: {},
	async mounted() {
		this.fetchReportInstanceList(); 
	},
	watch: {
    	 
    },
	data() {
		return {
			alarmOptions: [
				{ label:this.$t("reports.events.alarms"), value: 'alarms' }, 
				{ label:this.$t("reports.events.all"), value: 'all' },
				{ label:this.$t("reports.events.none"), value: 'none' }
			],
			dateRangeTypeOptions: [
				{ label: this.$t("reports.specificDates"), value:"specific" },
				{ label: this.$t("reports.relative"), value:"relative" }
			],
			snackbarMessage: '',
			snackbar: false,
			dialog: false,
			search: '',
			 
			datapoints: [],
			options: {},
			datapointOptions: {},
			selectedItemId: -1,
			selectedItem: null,
			  
        	reportInstanceList: [],
        	loading: false,
			 
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
					sortable: true,
					value: 'actions',
				},
			],
			sortModeOptions: [
				{label:"Ascending", value:'ASC'},
				{label:"Descending", value:'DEC'}
			],
		};
	},
	computed: {
		 
	},
	methods: {
		async togglePurge(value) {
			await this.$store.dispatch('setPreventPurge', {id: value.id, preventPurge: !value.preventPurge});
			value.preventPurge != value.preventPurge 
		},
		clickRow(ev) {
			this.selectedItemId=(ev.id)
		},
		async fetchReportInstanceList() {
			this.loading = true;
			this.reportInstanceList = await this.$store.dispatch('fetchReportInstances');
 
		},
		async deleteReport(id) {
			await this.$store.dispatch('deleteReport', id);
			this.fetchReportInstanceList()
		}
	  
	},
};
</script>
