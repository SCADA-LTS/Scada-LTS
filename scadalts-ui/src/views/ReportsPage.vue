<template>
	<div>
		<h1>{{ $t('reports.title') }}</h1>
		{{options}}
		<v-container fluid v-if="!!reportList">	
			<v-row>
				<v-col cols="6">
					<v-card>
						<v-card-title>
							<v-text-field
								v-model="search"
								@input="fetchReportList"
								append-icon="mdi-magnify"
								label="Search"
								class="mr-2"
								single-line
								hide-details
							></v-text-field>
							<v-btn
								color="primary"
								@click="createNewReport"
							>{{$t('reports.newReport')}}</v-btn>
						</v-card-title>
						<v-data-table
							:headers="headers"
							:items="reportListFiltered"
							:options.sync="options"
							:server-items-length="totalReports"
							multi-sort
							>
							<template v-slot:item.actions="{ item }">	
								<v-icon class="mr-2" border="0" @click.stop="runScript(item.xid)" title="run">
									mdi-cog
								</v-icon>
								<v-icon border="0" @click.stop="deleteScript(item.id)" title="delete">
									mdi-delete
								</v-icon>
							</template>
						</v-data-table>	
					</v-card>
				</v-col>
				<v-col cols="6">
					<v-row>
						<v-col cols="6">
							<v-text-field :label="$t('common.name')" v-model="reportForm.name"></v-text-field>
						</v-col>
						<v-col cols="6">
							<v-select
								v-model="reportForm.dateRangeType"
								item-text="label" 
								item-value="value"
								:items='dateRangeTypeOptions'
							>
							</v-select>
						</v-col>
					</v-row>
					<v-row>
						<v-col>
							<v-row v-if="reportForm.dateRangeType==='relative'">
								<v-col cols="3">
									<v-select item-text="label" :items='[{ label: $t("reports.previous"), value:"previous" },{ label:$t("reports.past"), value:"past" }]'></v-select>
								</v-col>
								<v-col cols="6">
									<v-text-field v-model="reportForm.relativeRangePreviousQuantity"></v-text-field>
								</v-col>
								<v-col cols="3">
									<v-select :items='[$t("common.timeperiod.days"),
									$t("common.timeperiod.hours"),
									$t("common.timeperiod.miliseconds"),
									$t("common.timeperiod.minutes"),
									$t("common.timeperiod.months"),
									$t("common.timeperiod.seconds"),
									$t("common.timeperiod.title"),
									$t("common.timeperiod.weeks"),
									$t("common.timeperiod.years")]'/>
								</v-col>
							</v-row>
							<v-row v-if="reportForm.dateRangeType==='specific'">
								<v-col cols="3" class="flex">
									<v-menu ref="start-date-menu"
										:close-on-content-click="false"
										:close-on-click="true"
										:nudge-right="40"
										transition="scale-transition"
										offset-y
										min-width="auto"
										attach
									>
										<template v-slot:activator="{ on, attrs }">
											<v-text-field 
												v-model="dateRange.startDate"
												@change="updateDateRange"
												label="Start Date"
												prepend-icon="mdi-calendar"
												v-bind="attrs"
												v-on="on"
											></v-text-field>
										</template>
										<v-date-picker
											v-model="dateRange.startDate"
											@change="updateDateRange"
											first-day-of-week="1"
											no-title
											scrollable
										></v-date-picker>
									</v-menu>
								</v-col>
								<v-col  class="flex">
									<v-menu ref="start-time-menu"
										:close-on-content-click="false"
										:close-on-click="true"
										:nudge-right="40"
										v-if="dateRange.startDate"
										transition="scale-transition"
										offset-y
										max-width="290px"
										min-width="290px"
										attach
									>
										<template v-slot:activator="{ on, attrs }">
											<v-text-field 
												v-model="dateRange.startTime"
												label="Start Time"
												prepend-icon="mdi-clock-time-four-outline"
												v-bind="attrs"
												v-on="on"
											></v-text-field>
										</template>
										<v-time-picker 
											v-model="dateRange.startTime"
											format="24hr" 
											scrollable
										></v-time-picker>
									</v-menu>
								</v-col>
								<v-col cols="3">
									<v-menu ref="end-date-menu"
										:close-on-content-click="false"
										:close-on-click="true"
										:nudge-right="40"
										transition="scale-transition"
										offset-y
										min-width="auto"
										attach
									>
										<template v-slot:activator="{ on, attrs }">
											<v-text-field 
												v-model="dateRange.endDate"
												label="End Date"
												prepend-icon="mdi-calendar"
												v-bind="attrs"
												v-on="on"
											></v-text-field>
										</template>
										<v-date-picker
											v-model="dateRange.endDate"
											first-day-of-week="1"
											no-title
											scrollable
										></v-date-picker>
									</v-menu>
								</v-col>
								<v-col>
									<v-menu ref="end-time-menu"
										:close-on-content-click="false"
										:close-on-click="true"
										:nudge-left="110"
										v-if="dateRange.endDate"
										transition="scale-transition"
										offset-y
										max-width="290px"
										min-width="290px"
										attach
									>
										<template v-slot:activator="{ on, attrs }">
											<v-text-field 
												v-model="dateRange.endTime"
												@change="fetchReportList"
												label="End Time"
												prepend-icon="mdi-clock-time-four-outline"
												v-bind="attrs"
												v-on="on"
											></v-text-field>
										</template>
										<v-time-picker 
											v-model="dateRange.endTime"
											@change="fetchReportList"
											format="24hr" 
											scrollable
											 offset-x=""
										></v-time-picker>
									</v-menu>
								</v-col>
							</v-row>
							<v-row>
								<v-col cols="3">
									<v-select 
										:label='$t("reports.events.alarms")'
										placeholder="select datapoint"
										item-text="label"
										v-model="reportForm.alarms"
										:items='alarmOptions'>
									</v-select>
								</v-col>
								<v-col cols="3" md-cols="2">
									<v-checkbox 
									label='Comments'
									v-model="reportForm.comments"></v-checkbox>
								</v-col>
								<v-col cols="3" md-cols="2">
									<v-checkbox 
									:label='$t("reports.schedule")'
									v-model="reportForm.schedule"></v-checkbox>
								</v-col>
								<v-col cols="3" md-cols="2">
									<v-checkbox 
									:label='$t("reports.emailReport")'
									v-model="reportForm.emailReport"></v-checkbox>
								</v-col>
							</v-row>
							<v-row>
								<v-col cols="12">
									<v-select 
									item-value="id"
									placeholder="select datapoint"
									item-text="name"
									v-model="selectedDatapointId"
									@change="addDatapoint"
									:items="filteredDatapoints"></v-select>

									<v-data-table
										v-if="reportForm.selectedDatapoints"
										:headers="datapointHeaders"
										:items="reportForm.selectedDatapoints"
										:options.sync="datapointOptions"
										:server-items-length="totalReports"
										multi-sort
										@click:row="selectScript($event.id)"
										>
										<template v-slot:item.color="{ item }">	
											<v-text-field></v-text-field>
										</template>
										<template v-slot:item.consolidatedChart="{ item }">	
											<div style="text-align:center">
												<v-checkbox></v-checkbox>
											</div>
										</template>
										<template v-slot:item.actions="{ item }">	
											<v-icon border="0" @click.stop="removeDatapoint(item.xid)" title="delete">
												mdi-delete
											</v-icon>
										</template>
									</v-data-table>		
								</v-col>
							</v-row>
						</v-col>
					</v-row>
				</v-col>
			</v-row>
		</v-container>
		<v-row justify="center">
			<v-dialog
			v-model="dialog"
			v-show="selectedScriptId !== null"
			@change="selectedScriptId = null"	
			max-width="80%"		
			>	
			<v-card>
				<v-card-title>
					<v-row>
						<v-col cols="6">{{$t('reports.criteria')}} </v-col>
					</v-row>
				</v-card-title>
				<v-card-text>
					<form>
					
					
					</form>
				</v-card-text>
				<v-card-actions>
				<v-spacer></v-spacer>
				<v-btn
					color="primary"
					text
					@click="dialog = false"
				>
					Close
				</v-btn>
				<v-btn class="mr-2" color="primary" @click="saveScript()" >
					<v-icon>mdi-content-save</v-icon>
						{{$t('reportList.save')}}
				</v-btn>
				</v-card-actions>
			</v-card>
			</v-dialog>
		</v-row> 

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
.datapoints {
	width: 100%;
}
.datapoints th {
	background: darkslategray;
	color: white;
}
.datapoints th, .datapoints td {
	padding: 4px;
}
.historical-alarms {
	z-index: -1;
}
</style>

<script>
import { keys } from '@amcharts/amcharts4/.internal/core/utils/Object';
export default {
	name: 'reportList',
	components: {},
	async mounted() {
		this.fetchReportList();
		this.datapoints = await this.$store.dispatch('getAllDatapoints');
	},
	watch: {
    	options (data) {
			this.dateRange.page = data.page;
			this.dateRange.itemsPerPage = data.itemsPerPage;
			this.dateRange.sortBy = data.sortBy;
			this.dateRange.sortDesc = data.sortDesc;
			this.fetchReportList()	
      	},
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
			reportListFiltered: [],
			datapointToSave: null,
			selectedDatapointId: null,
			datapoints: [],
			options: {},
			datapointOptions: {},
			selectedScriptId: -1,
			selectedScript: null,
			reportForm: {
				selectedDatapoints: [],
				relativeRangePreviousQuantity: 0,
				dateRangeType: 'specific',
				userComments: false,
				schedule: false,
				emailReport: false,
			},
			dateRange: {
				keywordfs: '',
				page: 1,
				itemsPerPage: 10,
				sortBy: [],
				sortDesc: [],
			},
			totalReports: 100,
        	reportList: [],
        	loading: false,
			datapointHeaders: [
				{
					text: this.$t('reports.pointName'),
					sortable: true,
					align: 'center',
					value: 'name',
				},
				{
					text: this.$t('reports.datatype'),
					sortable: true,
					align: 'center',
					value: 'datatype',
				},
				{
					text: this.$t('reports.color'),
					sortable: true,
					align: 'center',
					value: 'color',
				},
				{
					text: this.$t('reports.consolidatedChart'),
					sortable: true,
					align: 'center',
					value: 'consolidatedChart',
				},
				{
					text: this.$t('reports.actions'),
					sortable: true,
					align: 'center',
					value: 'actions',
				}, 	
			],
			headers: [
				{
					text: this.$t('reports.reportName'),
					sortable: true,
					align: 'center',
					value: 'reportName',
				},
				{
					text: this.$t('reports.runTimeStart'),
					align: 'center',
					sortable: true,
					value: 'activrunTimeStarteTs',
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
					value: 'reportRecords',
				},
				{
					text: this.$t('reports.doNotPurge'),
					align: 'center',
					value: 'doNotPurge',
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
		filteredDatapoints() {
			return this.datapoints
				.filter(dp => {
					return !this.reportForm.selectedDatapoints
						.find(sp => dp.xid === sp.xid)
				})
		},
	},
	methods: {
		
		createNewReport() {
			this.selectedScriptId = -1
			
			this.selectedScript = null
			this.reportForm.id = -1;
			this.reportForm.xid = '';
			this.reportForm.name = '';
			this.reportForm.selectedDatapoints = [];
			this.reportForm.script = ''
			this.reportForm.datasourceContext = ''
			this.reportForm.datapointContext = 'dp'
			this.dialog = true
		},
		removeDatapoint(xid) {
			this.reportForm.selectedDatapoints = this.reportForm.selectedDatapoints
				.filter(p => p.xid != xid)
		},
		selectScript(id) {
			this.selectedScriptId = id
			this.dialog = true
			this.selectedScript = this.reportList.find(x => x.id === this.selectedScriptId)
			this.reportForm.id = this.selectedScript.id;
			this.reportForm.xid = this.selectedScript.xid;
			this.reportForm.name = this.selectedScript.name;
			this.reportForm.selectedDatapoints = this.selectedScript.selectedDatapointIds.map( x => { return {
					varName: x.value ,
					dataPointXid: (this.datapoints.find(dp => dp.id === x.id)).xid
				}
			}	
			);
			const oc = this.selectedScript.objectsOnContext
			if (oc && oc.length) {
				const o1 = oc.find(x => x.key == 1)
				const o2 = oc.find(x => x.key == 2)
				if (o1 && o1.value) {
					this.reportForm.datasourceContext = o1.value;
				} else {
					this.reportForm.datasourceContext = '';
				}
				if (o2 && o2.value) {
					this.reportForm.datapointContext = o2.value;
				} else {
					this.reportForm.datapointContext = '';
				}
				
				this.reportForm.datapointContext = o2.value;
			} else {
				this.reportForm.datasourceContext = '';
				this.reportForm.datapointContext = '';
			}
			
			this.reportForm.script = this.selectedScript.script;
		},
		addDatapoint() {
			this.reportForm.selectedDatapoints
				.push(this.datapoints
					.find(datapoint => datapoint.id === this.selectedDatapointId))
		},
		async fetchReportList() {
			this.loading = true;
			this.reportList = await this.$store.dispatch('fetchReports', {...this.options, keywords: this.search});
			this.totalReports = this.reportList.total;
		},
		runScript(xid) {
			this.$store.dispatch('runScript', xid);
			this.snackbar = true
			this.snackbarMessage = `${this.$t('reportList.scriptExecuted')} `
		},
		updateDateRange() {
			let bufferDate;
			if (this.searchFilters.endDate < this.searchFilters.startDate) {
				bufferDate = this.searchFilters.endDate;
				this.searchFilters.endDate = this.searchFilters.startDate;
				this.searchFilters.startDate = bufferDate;
				this.searchFilters.startTime = "00:00";
				this.searchFilters.endTime = "23:59";
			}
			this.fetchReportList();
		},
		saveScript() {
			if (this.selectedScriptId != -1) {
				this.$store.dispatch('updateScript', this.reportForm);
			} else {
				this.$store.dispatch('createScript', this.reportForm);
			}
		},
		async deleteScript(id) {
			this.reportList = await this.$store.dispatch('deleteScript', id);
			this.fetchReportList()
			this.dialog = false
			this.snackbar = true
			this.snackbarMessage = `${this.$t('reportList.deletedScript')} #${id}`
		}
	},
};
</script>
