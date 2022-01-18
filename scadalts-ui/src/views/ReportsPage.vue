<template>
	<div> 
		<v-container fluid v-if="!!reportList">	
			<v-row justify="center" class="mt-6">
				<v-dialog
				v-model="addDataPointDialog"
				scrollable
				max-width="70%"
				>
					<v-card>
						<v-card-title>
							<v-row>
								<v-col cols="6">{{$t('reports.chooseDataPoints')}}</v-col>
							</v-row>
						</v-card-title>
						<v-card-text>
							<v-row>
								<v-col cols=6>
									<v-text-field
										v-model="searchDatapointsKeywords"
										append-icon="mdi-magnify"
										:label="$t('common.search')"
										single-line
										hide-details
										@input="searchDatapointsByKeywords"
									></v-text-field>
									<v-data-table
										:headers="datapointSearchHeaders"
										:items="searchDatapoints"
										multi-sort
										@click:row="addDatapoint({pointId: $event.id ,name:$event.name})"
									>
										<template v-slot:item.datatype="{ item }">	
											{{$t(`datapoint.type.${item.typeId}`)}}
										</template>
										<template v-slot:item.consolidatedChart="{ item }">	
											<div style="text-align:center">
												<input type=checkbox style="transform: scale(1.4);"
												:checked="item.consolidatedChart"
												:title="$t('reports.consolidated')"
												@change="item.consolidatedChart = !consolidatedChart"/>
											</div>
										</template>
									</v-data-table>
								</v-col>
								<v-col cols="6">
									<v-data-table
										v-if="reportForm.points.length "
										:headers="datapointHeaders"
										:items="reportForm.points"
										:options.sync="datapointOptions"
										:server-items-length="totalReports"
										multi-sort
										:hide-default-footer="true"
										@click:row="selectReport($event.id)"
										>
										<template v-slot:item.datatype="{ item }">	
											{{$t(`datapoint.type.${datapoints.find(x => x.id === item.pointId).typeId}`)}}
										</template>
										<template v-slot:item.color="{ item }">	
											<v-text-field v-model="item.colour"></v-text-field>
										</template>
										<template v-slot:item.consolidatedChart="{ item }">	
											<div style="text-align:center">
												<input type=checkbox   style="transform: scale(1.4);"
												:checked="item.consolidatedChart"
												:title="$t('reports.consolidated')"
												@change="item.consolidatedChart = !consolidatedChart"/>
											</div>
										</template>	
										<template v-slot:item.actions="{ item }">	
											<v-icon border="0" @click.stop="removeDatapoint(item.pointId)" :title="$t('common.delete')">
												mdi-delete
											</v-icon>
										</template>
									</v-data-table>
								</v-col>
							</v-row>
						</v-card-text>
					</v-card>		
				</v-dialog>
			</v-row>
			<v-row>
				<v-col cols="4">
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
						:items="reportList"
						:options.sync="options"
						:server-items-length="totalReports"
						multi-sort
						@click:row="clickRow"
						>
						<template v-slot:item.name="{ item }">	
							{{item.name}}
						</template>
						<template v-slot:item.actions="{ item }">	
							<v-icon class="mr-2" border="0" @click.stop="runReport(item.id)" :title="$t('reports.runNow')">
								mdi-play
							</v-icon>
							<v-icon class="mr-2" border="0" @click.stop="selectReport(item.id, true)" :title="$t('reports.copy')">
								mdi-content-copy
							</v-icon>
							<v-icon border="0" @click.stop="deleteReport(item.id)" :title="$t('reports.delete')" >
								mdi-delete
							</v-icon>
						</template>
					</v-data-table>	
				</v-col>
				<v-col cols="8" style="margin-top:15px">
					<v-card>
						<v-row>
							<v-col cols="6">
								<v-card-title v-if="selectedId!==-1">
									{{$t('reports.reportId')}}{{selectedId}}
								</v-card-title>
								<v-card-title v-else>
									{{$t('reports.newTemplate')}}
								</v-card-title>
							</v-col>
							<v-col cols="6" style="text-align:right" >
								<v-btn class="primary save" title="save" @click="saveReport"  rounded>
									<v-icon>mdi-content-save</v-icon>
								</v-btn>
							</v-col>
						</v-row>
						<v-card-text>
							<v-row>
								<v-col>
									<v-row>
										<v-col cols="3">
											<v-text-field :label="$t('common.name')" v-model="reportForm.name"></v-text-field>
										</v-col>
										<v-col cols="3">
											<v-select 
												:label='$t("reports.events")'
												:placeholder="$t('reports.selectDataPoint')"
												item-text="label"
												v-model="reportForm.includeEvents"
												item-value="value"
												:items='alarmOptions'>
											</v-select>
										</v-col>
										<v-col cols="3" md-cols="2">
											<label style="font-size:1.2em; margin-top:20px">
												<input type=checkbox  class = checkbox
												:checked="reportForm.includeUserComments"
												@change="reportForm.includeUserComments = !reportForm.includeUserComments"
												/>
												{{$t('reports.comments')}}
											</label>
										</v-col>
										<v-col cols="3" md-cols="2">
											<v-btn class="primary" @click="addDataPointDialog = true">
												{{$t('reports.chooseDataPoints')}}
											</v-btn>
										</v-col>
									</v-row>
									<v-row>
										<v-col cols="3">
											<v-select
												v-model="reportForm.dateRangeType"
												item-text="label" 
												item-value="value"
												:items='dateRangeTypeOptions'
											>
											</v-select>
										</v-col>
										<v-col cols="3" v-if="reportForm.dateRangeType===1">
											<v-select item-text="label" v-model="reportForm.relativeDateType" :items='[{ label: $t("reports.previous"), value:1 },{ label:$t("reports.past"), value:2 }]'></v-select>
										</v-col>
										<v-col cols="3" v-if='reportForm.dateRangeType===1 && reportForm.relativeDateType===1'>
											<v-text-field  v-model="reportForm.previousPeriodCount"></v-text-field>
										</v-col>
										<v-col cols="3" v-if='reportForm.dateRangeType===1 &&  reportForm.relativeDateType===1'>
											<v-select item-value="value" v-model="reportForm.previousPeriodType" item-text="text" :items='periodTypes'/>
										</v-col>
										<v-col cols="3" v-if='reportForm.dateRangeType===1 && reportForm.relativeDateType===2'>
											<v-text-field  v-model="reportForm.pastPeriodCount"></v-text-field>
										</v-col>
										<v-col cols="3" v-if='reportForm.dateRangeType===1 &&  reportForm.relativeDateType===2'>
											<v-select item-value="value" v-model="reportForm.pastPeriodType" item-text="text" :items='periodTypes'/>
										</v-col>
										<v-col v-if='reportForm.dateRangeType===2 && !reportForm.fromNone' cols="3" class="flex">
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
										<v-col v-if='reportForm.dateRangeType===2 && reportForm.fromNone' cols="3" class="flex">
											<v-text-field disabled :label="$t('timeperiod.date')" :value="`${reportForm.fromYear.toString().padStart(2,'0')}-${reportForm.fromMonth.toString().padStart(2,'0')}-${reportForm.fromDay.toString().padStart(2,'0')}`"></v-text-field>
										</v-col>
										<v-col cols="3" v-if='reportForm.dateRangeType===2 && !reportForm.fromNone' class="flex">
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
														:disabled=reportForm.fromNone
														v-model="dateRange.startTime"
														@change="updateDateRange"
														label="Start Time"
														prepend-icon="mdi-clock-time-four-outline"
														v-bind="attrs"
														v-on="on"
													></v-text-field>
												</template>
												<v-time-picker 
													:disabled=reportForm.fromNone
													v-model="dateRange.startTime"
													@change="updateDateRange"
													format="24hr" 
													scrollable
													offset-x=""
												></v-time-picker>
											</v-menu>
										</v-col>
										<v-col v-if='reportForm.dateRangeType===2 && reportForm.fromNone' cols="3" class="flex">
											<v-text-field disabled :label="$t('timeperiod.time')" :value="`${reportForm.fromHour.toString().padStart(2, '0')}:${reportForm.fromMinute.toString().padStart(2, '0')}`"></v-text-field>
										</v-col>
										<v-col v-if='reportForm.dateRangeType===2' cols="3" md-cols="2">
											<label style="font-size:1.2em" margin-top:20px>
												<input  type=checkbox class="checkbox" 
												:checked="reportForm.fromNone"
												@change="reportForm.fromNone = !reportForm.fromNone"
												/>
												{{$t('reports.inception')}}
											</label>
										</v-col> 
										<v-col v-if='reportForm.dateRangeType===2' :cols=" 3" ></v-col>
										<v-col v-if='reportForm.dateRangeType===2 && reportForm.toNone' cols="3" class="flex">
										<v-text-field disabled :label="$t('timeperiod.date')" :value="`${reportForm.toYear.toString().padStart(2, '0')}-${reportForm.toMonth.toString().padStart(2, '0')}-${reportForm.toDay.toString().padStart(2, '0')}`"></v-text-field>
									</v-col>
									<v-col v-if='reportForm.dateRangeType===2 && reportForm.toNone' cols="3" class="flex">
										<v-text-field disabled :label="$t('timeperiod.time')" :value="`${reportForm.toHour.toString().padStart(2, '0')}:${reportForm.toMinute.toString().padStart(2, '0')}`"></v-text-field>
									</v-col>
									<v-col cols="3" v-if='reportForm.dateRangeType===2 && !reportForm.toNone'>
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
									<v-col cols="3"  v-if='reportForm.dateRangeType===2 && !reportForm.toNone'>
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
													:disabled=reportForm.toNone
													v-model="dateRange.endTime"
													@change="updateDateRange"
													label="End Time"
													prepend-icon="mdi-clock-time-four-outline"
													v-bind="attrs"
													v-on="on"
												></v-text-field>
											</template>
											<v-time-picker 
												:disabled=reportForm.toNone
												v-model="dateRange.endTime"
												@change="updateDateRange"
												format="24hr" 
												scrollable
												offset-x=""
											></v-time-picker>
										</v-menu>
									</v-col>
									<v-col cols="3" md-cols="2" v-if='reportForm.dateRangeType===2 '>
										<label style="font-size:1.2em" margin-top:20px>
											<input class=checkbox type=checkbox 
											:checked="reportForm.toNone"
											@change="reportForm.toNone = !reportForm.toNone"
											/>
											{{$t('reports.latest')}}
										</label>
									</v-col> 
								</v-row>
							</v-col>
						</v-row>
						<v-row>
							<v-col cols="3" md-cols="2">
								<v-checkbox 
									:label='$t("reports.schedule")'
									v-model="reportForm.schedule">
								</v-checkbox>
							</v-col>
							<v-col cols="3" md-cols="2" v-if="reportForm.schedule">
								<v-select
									label="Run every..."
										item-value="value" item-text="text" 
										v-model="reportForm.schedulePeriod"
										:items='periodTypesPlusCron'
								></v-select>
							</v-col>
							<v-col cols="3" md-cols="2" 
								v-if="reportForm.schedule && reportForm.schedulePeriod != 0">
								<v-text-field 
								:rules="runDalayRules"
								:value="reportForm.runDelayMinutes"
								@change="onChangeReportFormRunDelayMinutes"
								validate-on-blur 
								label="Run delay (minutes)"></v-text-field>
							</v-col>
							<v-col vi cols="3" md-cols="2" 
								v-if="reportForm.schedule && reportForm.schedulePeriod === 0">
								<v-text-field v-model="reportForm.scheduleCron"
								validate-on-blur
								:rules='cronRules'
									placeholder="Cron pattern"></v-text-field>
							</v-col>
						</v-row>
						<v-row>
							<v-col cols="3">
								<v-checkbox 
								:label='$t("reports.emailReport")'
								v-model="reportForm.email"></v-checkbox>
								<v-btn v-if="reportForm.email" class="primary"
								@click="sendTestEmails" :disabled="reportForm.recipients.length">{{$t('reports.sendTestEmails')}}</v-btn>
							</v-col>
							<v-col v-if="reportForm.email" cols="3">
								<v-checkbox v-model="reportForm.includeData" label="Include tabular data"></v-checkbox>
								<v-checkbox v-model="reportForm.zipData" label="Data in .zip format"></v-checkbox>
							</v-col>
							<v-col v-if="reportForm.email" cols="6">		
								<form @submit.prevent="addEmail">{{selectedUser}}
									<v-select
										v-model="selectedUser"
										:items="userList"
										item-value="id"
										item-text="email"
										validate-on-blur
										@change="addUserEmail()"
									>
									</v-select>
									<v-row>
										<v-col cols="9">
											<v-text-field :rules="emailRules" v-model="email"  placeholder="Add email address"></v-text-field>
										</v-col>
										<v-col cols="3" style="text-align:right">
											<v-btn class='primary' @click="addEmail">Add email</v-btn>
										</v-col>
									</v-row>
								</form>
								<ul v-for="e in reportForm.recipients">
									<li>{{e.recipientType === 3 ? e.referenceAddress :  userList.find(x =>  x.id === e.referenceId ).email}} 
										- <a @click="removeEmail(e)">{{$t("reports.recipients.remove")}}</a>
									</li>
								</ul>
							</v-col>	
						</v-row>
					</v-card-text>
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
.checkbox {
	transform: scale(1.4);
	float: left;
	margin: 5px 15px
}
.save {
	border-radius:100px;
	height:60px;
	width:60px; 
	margin: 5px 16px;
}
</style>

<script>
import { keys } from '@amcharts/amcharts4/.internal/core/utils/Object';
const SCHEDULE_CRON = 0
const TYPE_MAILING_LIST = 1
const TYPE_USER = 2
const TYPE_ADDRESS = 3

const POINT_DATA_TYPES = {
	UNKNOWN: 0,
	BINARY: 1,
	MULTISTATE: 2,
	NUMERIC: 3,
	ALPHANUMERIC: 4,
	IMAGE: 5,
}

export default {
	name: 'reportList',
	components: {},
	async mounted() {
		this.fetchReportList();
		this.fetchUserList();
		this.reportInstances = (await this.$store.dispatch('fetchReportInstances')).length;
		this.datapoints = await this.$store.dispatch('getAllDataPointsTable');
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
			datapointResults: [],
			addDataPointDialog: false,
			runDalayRules: [
				v => 
				this.reportForm.schedule && !!v || this.$t('reports.minutesRequired'),
				v => 
				this.reportForm.schedule && !isNaN(v*1) || v *1 > 0 || this.$t('reports.delayMustBePositive') ,
			],
			emailRules: [
				v => 
				(!v || this.validateEmail(v)) || this.$t('reports.emailMustBeValid'),
			],
			cronRules: [
				v => 
				this.reportForm.schedule && !!v || this.$t('reports.cronPatternIsRequired'),
				v =>
				/(@(annually|yearly|monthly|weekly|daily|hourly|reboot))|(@every (\d+(ns|us|µs|ms|s|m|h))+)|((((\d+,)+\d+|(\d+(\/|-)\d+)|\d+|\*) ?){5,7})/.test(v) ||
				$t('reports.cronPatternMustBeValid')
			],
			
			periodTypes:[ 
				{value: 2, text:this.$t("common.timeperiod.minutes")},
				{value: 3, text:this.$t("common.timeperiod.hours")},
				{value: 4, text:this.$t("common.timeperiod.days")},
				{value: 5, text:this.$t("common.timeperiod.weeks")},
				{value: 6, text:this.$t("common.timeperiod.months")},
				{value: 7, text:this.$t("common.timeperiod.years")},
			],
			periodTypesPlusCron:[ {value: 0, text: `cron`},
				{value: 2, text:this.$t("common.timeperiod.minutes")},
				{value: 3, text:this.$t("common.timeperiod.hours")},
				{value: 4, text:this.$t("common.timeperiod.days")},
				{value: 5, text:this.$t("common.timeperiod.weeks")},
				{value: 6, text:this.$t("common.timeperiod.months")},
				{value: 7, text:this.$t("common.timeperiod.years")},
			],
			userList: [],
			selectedUser: null,
			email: '',
			
			alarmOptions: [
				{ label:this.$t("reports.events.none"), value: 1 },
				{ label:this.$t("reports.events.alarms"), value: 2 }, 
				{ label:this.$t("reports.events.all"), value: 3 },
				
			],
			dateRangeTypeOptions: [
				{ label: this.$t("reports.specificDates"), value:2 },
				{ label: this.$t("reports.relative"), value:1 }
			],
			snackbarMessage: '',
			snackbar: false,
			dialog: false,
			search: '',
			searchDatapointsKeywords: '',
			searchDatapoints: [],
			reportList: [],
			datapointToSave: null,
			selectedDatapointId: null,
			datapoints: [],
			options: {},
			datapointOptions: {},
			selectedId: -1,
			selectedReport: null,
			reportForm: {
				points: [],
				relativeRangePreviousQuantity: 0,
				dateRangeType: 1,
				includeUserComments: false,
				schedule: false,
 
            	name: '', 
            	id:-1,
				points:[], 
				includeEvents:1, 
				relativeDateType:0,
				previousPeriodCount:0,
            	previousPeriodType:0,
				pastPeriodCount:0,
				pastPeriodType:0,
				fromNone:false,
				fromYear:0,
            	fromMonth:0,
				fromDay:0,
				fromHour:0,
				fromMinute:0,
				toNone:false,
				toYear:0,
				toMonth:0,
            	toDay:0,
				toHour:0,
				toMinute:0,
				schedule:false,
				schedulePeriod:0,
				runDelayMinutes:0,
            	scheduleCron:'',
				email:false,
				includeData:false,
				zipData:false,
            	recipients:[],//List<RecipientListEntryBean> 
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
			
			datapointSearchHeaders: [
				{
					text: this.$t('common.xid'),
					sortable: true,
					align: 'center',
					value: 'xid',
				},
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
				
			],
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
					value: 'name',
				},
				{
					text: this.$t('common.actions'),
					align: 'center',
					sortable: true,
					value: 'actions',
				},
			],
			reportHeaders: [
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
					return !this.reportForm.points
						.find(sp => dp.xid === sp.xid)
				})
		},
	},
	methods: {
		async searchDatapointsByKeywords() {
			this.loading = true;
			this.searchDatapoints = await this.$store.dispatch('searchDatapoints', this.searchDatapointsKeywords);

			this.loading = false;
		},
		addUserEmail() {
			if (this.selectedUser && !this.reportForm.recipients.find(x =>{ return x.referenceAddress === this.selectedUser})) {
				this.reportForm.recipients.push({
					referenceAddress: null,
					referenceType: TYPE_USER,
					referenceId: this.selectedUser 
				})
			}
			
			this.selectedUser = null
		},
		removeEmail(email) {
			this.reportForm.recipients = this.reportForm.recipients.filter(x => x.referenceAddress != email.referenceAddress)
		},
		validateEmail(email) {
			var re = /\S+@\S+\.\S+/;
			return re.test(email);
		},
		clickRow(ev) {
			this.selectReport(ev.id, false)
		},
		createNewReport() {
			this.selectedId = -1
			
			this.selectedReport = null
		

			this.reportForm.id=-1;
			this.reportForm.name='';
			this.reportForm.points=[];//List<ReportPointVO> 
			this.reportForm.includeEvents=1;
            this.reportForm.includeincludeUserComments=false;
			this.reportForm.dateRangeType=0;
			this.reportForm.relativeDateType=0;
			this.reportForm.previousPeriodCount=0;
            this.reportForm.previousPeriodType=0;
			this.reportForm.pastPeriodCount=0;
			this.reportForm.pastPeriodType=0
			this.reportForm.fromNone=false
			this.reportForm.fromYear=0
            this.reportForm.fromMonth=0
			this.reportForm.fromDay=0
			this.reportForm.fromHour=0
			this.reportForm.fromMinute=0
			this.reportForm.toNone=false
			this.reportForm.toYear=0
			this.reportForm.toMonth=0
            this.reportForm.toDay=0
			this.reportForm.toHour=0
			this.reportForm.toMinute=0
			this.reportForm.schedule=false
			this.reportForm.schedulePeriod=0
			this.reportForm.runDelayMinutes=0
            this.reportForm.scheduleCron=''
			this.reportForm.email=false
			this.reportForm.includeData=false
			this.reportForm.zipData=false

			
		},
		removeDatapoint(pointId) {
			this.reportForm.points = this.reportForm.points
				.filter(p => p.pointId != pointId)
		},
		selectReport(id, copy = false) {
			this.selectedId = id
			this.dialog = true
			this.selectedReport = this.reportList.find(x => x.id === this.selectedId)
			this.reportForm.id = this.selectedReport.id;
			this.reportForm.name = this.selectedReport.name;
			this.reportForm.includeEvents= this.selectedReport.includeEvents; 
			this.reportForm.includeUserComments= this.selectedReport.includeUserComments; 
			this.reportForm.dateRangeType= this.selectedReport.dateRangeType; 
			this.reportForm.relativeDateType= this.selectedReport.relativeDateType; 
			this.reportForm.previousPeriodCount= this.selectedReport.previousPeriodCount; 
            this.reportForm.previousPeriodType= this.selectedReport.previousPeriodType; 
			this.reportForm.pastPeriodCount= this.selectedReport.pastPeriodCount; 
			this.reportForm.pastPeriodType= this.selectedReport.pastPeriodType; 
			this.reportForm.fromNone= this.selectedReport.fromNone; 
			this.reportForm.fromYear= this.selectedReport.fromYear; 
            this.reportForm.fromMonth= this.selectedReport.fromMonth; 
			this.reportForm.fromDay= this.selectedReport.fromDay; 
			this.reportForm.fromHour= this.selectedReport.fromHour; 
			this.reportForm.fromMinute= this.selectedReport.fromMinute; 
			this.reportForm.toNone= this.selectedReport.toNone; 
			this.reportForm.toYear= this.selectedReport.toYear; 
			this.reportForm.toMonth= this.selectedReport.toMonth; 
            this.reportForm.toDay= this.selectedReport.toDay; 
			this.reportForm.toHour= this.selectedReport.toHour; 
			this.reportForm.toMinute= this.selectedReport.toMinute; 
			this.reportForm.schedule= this.selectedReport.schedule; 
			this.reportForm.schedulePeriod= this.selectedReport.schedulePeriod; 
			this.reportForm.runDelayMinutes= this.selectedReport.runDelayMinutes; 
            this.reportForm.scheduleCron= this.selectedReport.scheduleCron;//
			this.reportForm.email= this.selectedReport.email; 
			this.reportForm.includeData= this.selectedReport.includeData; 
			this.reportForm.zipData= this.selectedReport.zipData; 
            this.reportForm.recipients= this.selectedReport.recipients; 
			
			this.reportForm.points = this.selectedReport.points.map(x => {
				const name = this.datapoints.find(p => p.pointId === x.id).name
				return {...x, name}
			});
			if (copy)this.selectedId = -1
			 
		},
		addEmail() {
			if (!this.validateEmail(this.email)) return
			if (this.reportForm.recipients.find(x => this.email === x.referenceAddress)) return  
			this.reportForm.recipients.push({
				referenceAddress: this.email,
				referenceType: TYPE_ADDRESS
			})
			this.email = ''
		},
		addDatapoint({ pointId, name, colour = "", consolidatedChart = true }) {
			if (this.reportForm.points.find(x => { return x.pointId === pointId})) return
			this.reportForm.points
				.push({ pointId, colour, consolidatedChart, name })
		},
		async fetchReportList() {
			this.loading = true;
			this.reportList = await this.$store.dispatch('fetchReports', {...this.options, keywords: this.search});
			this.totalReports = this.reportList.total;
		},
		async fetchUserList() {
			this.loading = true;
			this.userList = [{ id: 0, username: "", email: ''}, ...(await this.$store.dispatch('getAllUsers'))];
		},
		runReport(id) {
			this.$store.dispatch('runReport', id);
			this.snackbar = true
			this.snackbarMessage = `${this.$t('reportList.scriptExecuted')} `
		},
		updateDateRange() {
			let bufferDate;
			if (this.dateRange.endDate < this.dateRange.startDate) {
				bufferDate = this.dateRange.endDate;
				this.dateRange.endDate = this.dateRange.startDate;
				this.dateRange.startDate = bufferDate;
				this.dateRange.startTime = "00:00";
				this.dateRange.endTime = "23:59";
			}
			if (this.dateRange.startDate) {
				this.reportForm.fromYear=this.dateRange.startDate.split('-')[0]*1
				this.reportForm.fromMonth= this.dateRange.startDate.split('-')[1]*1
				this.reportForm.fromDay= this.dateRange.startDate.split('-')[2]*1
			}

			if (this.dateRange.startTime) {
				this.reportForm.fromHour= this.dateRange.startTime.split(':')[0]*1
				this.reportForm.fromMinute= this.dateRange.startTime.split(':')[1]*1
			}

			if (this.dateRange.endDate) {
				this.reportForm.toYear= this.dateRange.endDate.split('-')[0]*1
				this.reportForm.toMonth= this.dateRange.endDate.split('-')[1]*1
				this.reportForm.toDay= this.dateRange.endDate.split('-')[2]	*1
			}
			if (this.dateRange.endTime) { 
				this.reportForm.toHour= this.dateRange.endTime.split(':')[0]*1
				this.reportForm.toMinute= this.dateRange.endTime.split(':')[1]*1
			}
		},
		async saveReport() {
			if (this.selectedId != -1) {
				await this.$store.dispatch('saveReport', this.reportForm);
			} else {
				await this.$store.dispatch('saveReport', this.reportForm);
			}
			this.fetchReportList()
		},
		async deleteReport(id) {
			this.reportList = await this.$store.dispatch('deleteReport', id);
			this.fetchReportList()
			this.dialog = false
			this.snackbar = true
			this.snackbarMessage = `${this.$t('reportList.deletedScript')} #${id}`
		},
		onChangeReportFormRunDelayMinutes (value) {
			this.reportForm.runDelayMinutes = value *1
		},
		async sendTestEmails () {
			await this.$store.dispatch('sendTestEmails', this.reportForm.recipients.map(x => {
				if (x.recipientType === 3) return x.referenceAddress 
				else return this.userList.find(u =>  u.id === x.referenceId ).email
			}))
			this.snackbar = true
			this.snackbarMessage = `${this.$t('reports.emailsSent')} `
		}
	},
};
</script>
