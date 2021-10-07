<template>
	<div>
		{{eventDetailsDialog}}
		<h1>{{ $t('eventList.title') }}</h1>

		<v-row justify="center">
			<v-dialog
			v-model="eventDetailsDialog"
			scrollable
			max-width="90%"
			>
			<v-card>
				<v-card-title>
					<v-row>
						<v-col cols="12">Event details</v-col>
						
						<v-col cols="12">
							<v-btn @click="acknowloedgeEventSelected"  color="blue">Acknowledge</v-btn>&nbsp;
							<v-btn  color="blue">Silence</v-btn>
						</v-col>	
					</v-row>
					
				</v-card-title>
				<v-row style="margin: 0 2rem" v-if="selectedEventId">
					<v-col cols="3"><b>event id</b>: {{selectedEvent.id}}</v-col>
				
					<v-col cols="3"><b>alarm level</b>: {{selectedEvent.alarmLevel}}</v-col>
				
					<v-col cols="3"><b>source type</b>: {{selectedEvent.eventSourceType}}</v-col>

					<v-col cols="3"><b>created at:</b>: {{selectedEvent.datetime}}</v-col>

					<v-col cols="3"><b>status</b>: {{selectedEvent.status}}</v-col>
				
					<v-col cols="3"><b>datapoint</b>: {{selectedEvent.datapoint}}</v-col>
				
					<v-col cols="3"><b>message</b>: {{selectedEvent.message}}</v-col>

					<v-col cols="3"><b>acknowledged at:</b>: {{selectedEvent.datetime}}</v-col>
				</v-row>
				<v-divider></v-divider>
				<v-card-text style="height: 300px;">
					<v-row>
						<v-col cols="6">
							<ul v-for="comment in comments" >
								<li>{{comment.username}}-{{$date(comment.ts).format('YYYY/MM/DD hh:mm')}}: {{comment.commentText}}</li>
							</ul>
						</v-col>
						
						<v-col cols="6">
							
						</v-col>
						
					</v-row>
				
				</v-card-text>
				<v-divider></v-divider>
				<v-card-actions>
				
				<v-text-field v-model="commentText" color="blue darken-1"></v-text-field>
				<v-btn text color="blue darken-1" @click="publishComment">Comment</v-btn>
				
				</v-card-actions>
			</v-card>
			</v-dialog>
		</v-row>
		<v-container fluid v-if="!!eventList">	
			<v-row>
				<v-col cols="12">
					<v-form ref="form" id="user-details--form">	
						<v-row>
							<v-col cols="3">
								<v-text-field
									v-model="searchFilters.keywords"
									append-icon="mdi-magnify"
									:label="$t('common.search')"
									single-line
									hide-details
									@input="fetchEventList"
								></v-text-field>
							</v-col>	
							<v-col cols="3">
								<v-select
									:label="$t('eventList.searchFilter.alarmLevel')"
									v-model="searchFilters.alarmLevel"
									:items="alarmLevelOptions"
									item-text="name"
									item-value="id"
									@change="fetchEventList"
								></v-select>
							</v-col>
						
							<v-col cols="3">
								<v-select 
									:label="$t('eventList.searchFilter.status')"
									v-model="searchFilters.status"
									:items="statusOptions"
									item-text="label"
									item-value="value"
									@change="fetchEventList"
								></v-select>
							</v-col>
							
							<v-col cols="3">
								<v-select 
									:label="$t('eventList.searchFilter.eventSourceType')"
									v-model="searchFilters.eventSourceType"
									:items="eventSourceTypeOptions"
									item-text="name"
									item-value="id"
									@change="fetchEventList"
								></v-select>
							</v-col>
							
						</v-row>
			
						<template v-slot:append-outer>
							<v-btn
								icon
								fab
								x-small
								@click="fetchEventList"
								:loading="loading"
								:disabled="loading"
							>
								<v-icon>mdi-autorenew</v-icon>
							</v-btn>
						</template>
					</v-form>
				</v-col>
			<v-col cols="6" class="flex">
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
								v-model="searchFilters.startDate"
								@change="updateDateRange"
								label="Start Date"
								prepend-icon="mdi-calendar"
								v-bind="attrs"
								v-on="on"
							></v-text-field>
						</template>
						<v-date-picker
							v-model="searchFilters.startDate"
							@change="updateDateRange"
							first-day-of-week="1"
							no-title
							scrollable
						></v-date-picker>
					</v-menu>

					<v-menu ref="start-time-menu"
						:close-on-content-click="false"
						:close-on-click="true"
						:nudge-right="40"
						v-if="searchFilters.startDate"
						transition="scale-transition"
						offset-y
						max-width="290px"
						min-width="290px"
						attach
					>
						<template v-slot:activator="{ on, attrs }">
							<v-text-field 
								v-model="searchFilters.startTime"
								@change="fetchEventList"
								label="Start Time"
								prepend-icon="mdi-clock-time-four-outline"
								v-bind="attrs"
								v-on="on"
							></v-text-field>
						</template>
						<v-time-picker 
							v-model="searchFilters.startTime"
							@change="fetchEventList"
							format="24hr" 
							scrollable
						></v-time-picker>
					</v-menu>
				</v-col>
				<v-col cols="6" class="flex">
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
								v-model="searchFilters.endDate"
								label="End Date"
								@change="fetchEventList"
								prepend-icon="mdi-calendar"
								v-bind="attrs"
								v-on="on"
							></v-text-field>
						</template>
						<v-date-picker
							v-model="searchFilters.endDate"
							@change="fetchEventList"
							first-day-of-week="1"
							no-title
							scrollable
						></v-date-picker>
					</v-menu>

					<v-menu ref="end-time-menu"
						:close-on-content-click="false"
						:close-on-click="true"
						:nudge-right="40"
						v-if="searchFilters.endDate"
						transition="scale-transition"
						offset-y
						max-width="290px"
						min-width="290px"
						attach
					>
						<template v-slot:activator="{ on, attrs }">
							<v-text-field 
								v-model="searchFilters.endTime"
								@change="fetchEventList"
								label="End Time"
								prepend-icon="mdi-clock-time-four-outline"
								v-bind="attrs"
								v-on="on"
							></v-text-field>
						</template>
						<v-time-picker 
							v-model="searchFilters.endTime"
							@change="fetchEventList"
							format="24hr" 
							scrollable
						></v-time-picker>
					</v-menu>
				</v-col>
			</v-row>
			<v-data-table
				:headers="headers"
				:items="eventList"
				:options.sync="options"
				:loading="loading"
				:server-items-length="totalEvents"
				multi-sort
				class="elevation-1"
				@click:row="open"
				></v-data-table>
		</v-container>
		<v-progress-circular v-else indeterminate color="primary"></v-progress-circular>
		<pre>{{searchFilters}} {{eventList}}</pre>
	</div>
</template>
<style scoped>
.historical-alarms {
	z-index: -1;
}
</style>
<script>
import RangeChartComponent from '../components/amcharts/RangeChartComponent.vue'
export default {
	name: 'EventList',
	components: {
		RangeChartComponent,
	},
	mounted() {
		this.fetchEventList();
	},
	watch: {
    	options (data) {
			this.searchFilters.page = data.page;
			this.searchFilters.itemsPerPage = data.itemsPerPage;
			this.searchFilters.sortBy = data.sortBy;
			this.searchFilters.sortDesc = data.sortDesc;
			this.fetchEventList();
      	},
		selectedEventId (data) {
			this.eventDetailsDialog = true;
			if (data != null) this.fetchEventSelected();
      	},
		eventDetailsDialog (data) {
			if (data === false) this.selectedEventId=null;
      	},
    },
	data() {
		return {
			get selectedEvent() {
				return this.eventList.find(event => event.id === this.selectedEventId)
			},
			searchFilters: {
				alarmLevel: 1,
				keywords: '',
				status: "*",
				eventSourceType: 0,
				datapoint: null,
				page: 1,
				itemsPerPage: 10,
				sortBy: ['alarmLevel'],
				sortDesc: [true],
				startDate: null,
				endDate: null,
				startTime: "00:00",
				endTime: "23:59",
			},
			eventDetailsDialog: false,
			selectedEventId: null,
			totalEvents: 100,
        	eventList: [],
			commentText: "",
        	loading: false,
        	options: {},
			headers: [
				{	
					text: this.$t('eventList.id'),
					sortable: true,
					align: 'center',
					value: 'id',
				},
				{
					text: this.$t('eventList.alarmLevel'),
					sortable: true,
					align: 'center',
					value: 'alarmLevel',
				},
				{
					text: this.$t('eventList.datetime'),
					align: 'center',
					sortable: true,
					value: 'activeTs',
				},
				{
					text: this.$t('eventList.eventSourceType'),
					sortable: true,
					align: 'center',
					value: 'typeId',
				},
				{
					text: this.$t('eventList.message'),
					align: 'center',
					value: 'message',
					sortable: true,
				},
				{
					text: this.$t('eventList.status'),
					align: 'center',
					sortable: true,
					value: 'status',
				},
				{
					text: this.$t('eventList.datapoint'),
					sortable: true,
					align: 'center',
					value: 'datapoint',
				},
			],
			comments: [
				{userId: 1, commentType: 1, typeKey:1, ts:"1633495773928", commentText:"hola"}
			],
			sortModeOptions: [
				{label:"Ascending", value:'ASC'},
				{label:"Descending", value:'DEC'}
			],
			alarmLevelOptions: [
				{ name:"Information", id: 1, color: 'blue' },
				{ name:"Urgent", id: 2, color: 'yellow' },
				{ name:"Critical", id: 3, color: 'orange' },			
				{ name:"Life Safety", id: 4, color: 'red' },		
			],
			eventSourceTypeOptions: [
				{ name: "All", id: 0 },
				{ name: "Point event detectors", id: 1 },
				{ name: "Data source events", id: 3 },
				{ name: "System events", id: 4 },
				{ name: "Compound event detectors", id: 5 },
				{ name: "Scheduled events", id: 6 },
				{ name: "Publisher events", id: 7 },
				{ name: "Audit events", id: 8 },
				{ name: "Maintenance events", id: 9 },
			],
			statusOptions: [
				{ label:"All", value: "*" },
				{ label:"Active", value: "A" },
				{ label:"RTN", value: "R" },
				{ label:"No RTN", value: "N" },
			]
		};
	},
	
	methods: {
		async publishComment() {
			this.comments = await this.$store.dispatch('publishEventComment', { typeId: 1, eventId: this.selectedEventId, commentText: this.commentText });
			this.commentText = ''
		},
		async fetchEventList() {
			this.loading = true;
			
			const result = await this.$store.dispatch('searchEvents', this.searchFilters);
			this.eventList = result.rows;
			this.totalEvents = result.total;
			this.loading = false;
		},
		async fetchEventSelected() {
			this.loading = true;
			this.comments = await this.$store.dispatch('getEventById', this.selectedEventId);
			this.loading = false;
		},
		async acknowloedgeEventSelected() {
			this.loading = true;
			this.comments = await this.$store.dispatch('acknowloedgeEvent', {eventId: this.selectedEventId});
			this.loading = false;
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
			this.fetchEventList();
		},
	  	open(item, item2) {
			this.selectedEventId = item.id;
	  	}
	},
};
</script>
