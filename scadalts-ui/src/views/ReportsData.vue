<template>
	<div>
		<v-container fluid v-if="!!reportInstanceList">	
			<v-row>
				<v-col cols="6">
					<v-card>
						<v-card-title>
							<v-text-field
								v-model="search"
								@input="fetchReportInstanceList"
								append-icon="mdi-magnify"
								label="Search"
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
							>
							<template v-slot:item.runDuration="{ item }">	
								{{item.runEndTime - item.runStartTime}}
							</template>
							<template v-slot:item.actions="{ item }">	
								<v-icon class="mr-2" style="border:0px" @click.stop="runScript(item.xid)" title="export data">
									mdi-download-box
								</v-icon>
								<v-icon style="border:0px" @click.stop="deleteScript(item.id)" title="events">
									mdi-bell
								</v-icon>
								<v-icon style="border:0px" @click.stop="deleteScript(item.id)" title="user comments">
									mdi-comment
								</v-icon>
								<v-icon style="border:0px" @click.stop="deleteScript(item.id)" title="charts">
									mdi-chart-line
								</v-icon>
								<v-icon style="border:0px" @click.stop="deleteReport(item.id)" title="delete">
									mdi-delete-forever
								</v-icon>
							</template>
							<template v-slot:item.preventPurge="{ item }">	
								<v-checkbox disabled :value="item.preventPurge"/>
							</template>
							
						</v-data-table>	
					</v-card>
				</v-col>
				
				
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
</style>

<script>
import { keys } from '@amcharts/amcharts4/.internal/core/utils/Object';
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
			
			datapointToSave: null,
			selectedDatapointId: null,
			datapoints: [],
			options: {},
			datapointOptions: {},
			selectedScriptId: -1,
			selectedScript: null,
			  
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
