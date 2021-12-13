<template>
	<div>
		<ConfirmationDialog
			:btnvisible="false"
			:dialog="confirmAckAllToggleDialog"
			@result="confirmAction"
			:title="confirmTitle"
			:message="confirmMessage"
		></ConfirmationDialog>
		<v-row justify="center" class="mt-6">
			<v-dialog
			v-model="eventDetailsDialog"
			scrollable
			max-width="90%"
			>
			<v-card v-if="selectedEvent">
				<v-card-title>
					<v-row>
						<v-col cols="6">Event #{{selectedEvent.id}}</v-col>
						<v-col cols="6" class="text-right">
							<img 
							:src="(!!selectedEvent.rtnTs ? alarmFlags : alarmFlagsOff)[selectedEvent.alarmLevel].image">
						</v-col>
						<v-col cols="12">
							<v-btn class="mr-2" @click="acknowledgeEventSelected" color="blue">
								<v-icon>mdi-checkbox-marked-circle-outline</v-icon>
									{{$t('eventList.acknowledge')}}
							</v-btn>
							<v-btn v-if="!selectedEvent.silenced" @click="silenceEvent({id:selectedEvent.id})"  class="mr-2" color="blue">
								<v-icon>mdi-volume-mute</v-icon>
								{{$t('eventList.silence')}}
							</v-btn>
							<v-btn v-else @click="unsilenceEvent({id:selectedEvent.id})"  class="mr-2" color="blue">
								<v-icon>mdi-volume-mute</v-icon>
								{{$t('eventList.unsilence')}}
							</v-btn>
						</v-col>	
					</v-row>
				</v-card-title>
				<v-row style="margin: 0 2rem" >
					<v-col cols="3"><b>{{$t('eventList.createAt')}}</b>: {{ $date(selectedEvent.activeTs).format('YYYY-MM-DD hh:mm:ss') }}</v-col>
					<v-col cols="3"><b>{{$t('eventList.alarmLevel')}}</b>: 
						{{ $t(`eventList.alarmLevel${selectedEvent.alarmLevel}`) }}
					</v-col>
					<v-col cols="3"><b>{{$t('eventList.sourceType')}}</b>: {{ $t(`eventList.sourceType${selectedEvent.typeId}`) }}</v-col>
					<v-col cols="3"><b>{{$t('eventList.status')}}</b>: 
						<span v-if="selectedEvent.rtnApplicable && !selectedEvent.rtnTs" >{{$t('eventList.STATUS_ACTIVE')}}</span>
						<span v-if="selectedEvent.rtnApplicable && selectedEvent.rtnTs">{{$date(selectedEvent.rtnTs).format('YYYY-MM-DD hh:mm:ss')}}</span>
						<span v-if="!selectedEvent.rtnApplicable">{{$t('eventList.STATUS_NORTN')}}</span>
					</v-col>
					<v-col cols="9"><b>{{$t('eventList.message')}}</b>: {{ eventMessageI18n(selectedEvent.message)}}</v-col>
					<v-col v-if="selectedEvent.eventSourceType===1" cols="3"><b>{{$t('eventList.datapoint')}}</b>: {{selectedEvent.datapoint}}</v-col>
				</v-row>
				<v-divider></v-divider>
				<v-card-text style="height: 300px;">
					<v-row>
						<v-col cols="6">
							<ul v-for="comment in comments">
								<li>{{comment.username}}-{{$date(comment.ts).format('YYYY-MM-DD hh:mm:ss')}}: {{comment.commentText}}</li>
							</ul>
						</v-col>
						<v-col cols="6">
						</v-col>
					</v-row>
				
				</v-card-text>
				<v-divider></v-divider>
				<form @submit.prevent="publishComment"> 
				<v-card-actions>
				 
					<v-text-field v-model="commentText" color="blue darken-1"></v-text-field>
					<v-btn text color="blue darken-1" @click="publishComment">Comment</v-btn>
				
				</v-card-actions>
				</form>
			</v-card>
			</v-dialog>
		</v-row>
		<v-container fluid v-if="!!eventList">	
			<v-row>
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
									:label="$t('eventList.alarmLevel')"
									v-model="searchFilters.alarmLevel"
									:items="alarmLevelOptions"
									item-text="name"
									item-value="id"
									@change="fetchEventList"
								></v-select>
							</v-col>
						
							<v-col cols="3">
								<v-select 
									:label="$t('eventList.status')"
									v-model="searchFilters.status"
									:items="statusOptions"
									item-text="label"
									item-value="value"
									@change="fetchEventList"
								></v-select>
							</v-col>
							
							<v-col cols="3">
								<v-select 
									:label="$t('eventList.sourceType')"
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
			</v-row>
			<v-row class="pb-2">
				<v-col cols="6" v-show="selectedEvents.length">
					<v-btn small @click="acknowledgeSelectedEvents" class="mr-2" color="blue">
						<v-icon class="mr-2" >
							mdi-checkbox-marked-circle-outline
						</v-icon>
						{{$t("eventList.acknowledgeSelectedEvents")}}</v-btn>
					<v-btn small @click="silenceSelectedEvents" title="silence"  color="blue" class="mr-2" >
						<v-icon class="mr-2" >
							mdi-volume-mute
						</v-icon>
						{{$t("eventList.silenceSelected")}}
						</v-btn>
					<v-btn small @click="unsilenceSelectedEvents"  color="blue">
						<v-icon class="mr-2" >
							mdi-volume-high
						</v-icon>
						{{$t("eventList.unsilenceSelected")}}</v-btn>
				</v-col>
				<v-col :cols="selectedEvents.length ? 6 : 12" class="text-right">
					<v-btn small @click="askForAckAll" class="mr-2" color="red">
						<v-icon class="mr-2" >
							mdi-checkbox-marked-circle-outline
						</v-icon>
						{{$t("eventList.acknownledgeAll")}}</v-btn>
					<v-btn small @click="askForSilenceAll" color="red">
						<v-icon class="mr-2" >
							mdi-volume-mute
						</v-icon>
						{{$t("eventList.silenceAll")}}</v-btn>
				</v-col>


    		</v-row>
			<v-data-table
				id='eventList'
				v-model="selectedEvents"
				show-select
				:headers="headers"
				:items="eventList"
				:options.sync="options"
				:loading="loading"
				:server-items-length="totalEvents"
				:footer-props="{
					'items-per-page-options': [10, 20, 50, 100]
				}"
				multi-sort
				class="elevation-1"
				@click:row="open"
				>
					<template v-slot:item.activeTs="{ item }">
						{{ $date(item.activeTs).format('YYYY-MM-DD hh:mm:ss') }}
					</template>
					
					<template v-slot:item.ackTs="{ item }">
						<span v-if="item.ackTs">{{$date(item.ackTs).format('YYYY-MM-DD hh:mm:ss') }}</span>
						<v-icon v-else
							class="mr-2"
							title="acknowledge"
							border=0
							@click.stop="acknowledgeEvent(item);return false"
						>
							mdi-checkbox-marked-circle
						</v-icon>
					</template>
					<template v-slot:item.alarmLevel="{ item }">
						<img :src="(!!item.rtnTs ? alarmFlags : alarmFlagsOff)[item.alarmLevel].image">
					</template>
					<template v-slot:item.typeId="{ item }">
						{{ $t(`eventList.sourceType${item.typeId}`) }}
					</template>
					<template v-slot:item.message="{ item }">
						{{eventMessageI18n(item.message)}}
					</template>

					<template v-slot:item.status="{ item }">
						<span v-if="item.rtnApplicable && !item.rtnTs" >{{$t('eventList.STATUS_ACTIVE')}}</span>
						<span v-if="item.rtnApplicable && item.rtnTs">{{$date(item.rtnTs).format('YYYY-MM-DD hh:mm:ss')}}</span>
						<span v-if="!item.rtnApplicable">{{$t('eventList.STATUS_NORTN')}}</span>	
					</template>
					<template v-slot:item.actions="{ item }">
						<span v-if="!item.ackTs">
							<v-icon class="mr-2" border="0" @click.stop="silenceEvent(item);return false" v-if="!item.silenced" title="silence">
								mdi-volume-mute
							</v-icon>
							<v-icon class="mr-2" border="0" @click.stop="unsilenceEvent(item);return false" v-if="item.silenced" title="unsilence">
								mdi-volume-high
							</v-icon>
						</span>

						<v-icon border="0" @click.stop="$router.push({ name: 'datapoint-details', params: { id: item.typeRef1 } });$router.go();" v-if="item.typeId===1" title="point details">
							mdi-magnify
						</v-icon>

						<v-icon  title="data source" @click.stop="gotoDatasource(item.typeId)" v-if="item.typeId===3">
						mdi-database
						</v-icon>	
						
						<v-icon @click.stop="gotoSystem(event.typeRef1)" v-if="item.typeId===4" title="system">
							mdi-desktop-classic
						</v-icon>

						<v-icon title="Compound even detector" @click.stop="gotoCompoundEvent(item.typeRef1)" v-if="item.typeId===5">
						mdi-ceiling-light-multiple-outline
						</v-icon>
					
						<v-icon  title="Scheduled event" @click.stop="gotoScheduled(item.typeRef1)" v-if="item.typeId===6">
						mdi-calendar
						</v-icon>

						<v-icon  title="Publisher events" @click.stop="gotoPublisher(item.typeRef1)" v-if="item.typeId===7">
						mdi-publish
						</v-icon>

						<v-icon  title="Audit events" @click.stop="gotoAudit(item.typeRef1, item.typeRef2)" v-if="item.typeId===8">
						mdi-glasses
						</v-icon>

						<v-icon  title="Maintenance events" @click.stop="gotoMaintenance(item.typeRef1)" v-if="item.typeId===9">
						mdi-hammer
						</v-icon>		
						<v-badge v-if="item.comments"
							color="blue"
							:content=item.comments
							style="cursor: pointer"
						>
							<v-icon
								class="ml-2"
								:title="$t('eventList.comments') +': ' + item.comments"
								border=0
							>
							mdi-comment
							</v-icon>
						</v-badge>

    				</template>
			</v-data-table>
		</v-container>
		<v-progress-circular v-else indeterminate color="primary"></v-progress-circular>
	</div>
</template>
<style scoped>

.v-icon {
	border: 0!important;
}

tbody tr:nth-of-type(odd) {
	background-color: rgba(0, 0, 0, .05);
}
.historical-alarms {
	z-index: -1;
}
</style>
<script>
import store from '../store';
import RangeChartComponent from '../components/amcharts/RangeChartComponent.vue';
import ConfirmationDialog from '@/layout/dialogs/ConfirmationDialog';

export default {
	name: 'EventList',
	components: {
		RangeChartComponent,
		ConfirmationDialog,
	},
	mounted() {
		this.mountedTs = this.$dayjs()
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
			if (data != null) {
				this.fetchEventSelected();
			}
      	},
		eventDetailsDialog (data) {
			if (data === false) this.selectedEventId=null;
      	},
    },
	data() {
		return {
			mountedTs: null,
			newAlarms: false,
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
				sortBy: ['alarmLevel', 'activeTs'],
				sortDesc: [true, true],
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
					text: this.$t('#'),
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
					text: this.$t('eventList.createAt'),
					align: 'center',
					sortable: true,
					value: 'activeTs',
				},
				{
					text: this.$t('eventList.sourceType'),
					sortable: true,
					align: 'center',
					value: 'typeId',
				},
				{
					text: this.$t('eventList.status'),
					align: 'center',
					sortable: true,
					value: 'status',
				},
				{
					text: this.$t('eventList.message'),
					align: 'center',
					value: 'message',
					sortable: false,
				},				
				{
					text: this.$t('eventList.datapoint'),
					sortable: true,
					align: 'center',
					value: 'xid',
				},
				{
					text: this.$t('eventList.ackTime'),
					align: 'center',
					sortable: true,
					value: 'ackTs',
				},
				{ text: 'Actions', value: 'actions', sortable: false, align: 'center' },
			],
			comments: [
				
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
				{ label: this.$t('eventList.STATUS_ACTIVE'), value: "A" },
				{ label: this.$t('eventList.STATUS_RTN'), value: "R" },
				{ label: this.$t('eventList.STATUS_NORTN'), value: "N" },
			],
			alarmFlagsOff: {
				1: {
					image: "images/flag_blue_off.png"
				},
				2: {
					image: "images/flag_yellow_off.png"
				},
				3: {
					image: "images/flag_orange_off.png"
				},
				4: {
					image: "images/flag_red_off.png"
				}
			},
			alarmFlags: {
				1: {
					image: "images/flag_blue.png"
				},
				2: {
					image: "images/flag_yellow.png"
				},
				3: {
					image: "images/flag_orange.png"
				},
				4: {
					image: "images/flag_red.png"
				}
			},
			confirmAckAllToggleDialog: false,
			confirmTitle: '',
			confirmMessage: '',
			selectedEvents: [],
		};
	},

	filters: {
		capitalize: function (value) {
			if (!value) return ''
			value = value.toString()
			return value.charAt(0).toUpperCase() + value.slice(1)
		},
		truncate(input) {
			if (input.length > 32) {
				return input.substring(0, 32) + '...';
			}
			return input;
		}
	},
	
	methods: {
		eventMessageI18n(eventMessage) {
			const [key, ...args ]= eventMessage.split('|')
			return this.$t(key, args)
		},
		getAlarms() {
			store.dispatch('getLiveAlarms', { offset: 0, limit: 1 }).then((ret) => {
				if (ret.length) this.newAlarms = true
			});
		},
		confirmAction(answer) {
			if (answer) this.actionToConfirm();
			this.confirmAckAllToggleDialog = false
		},
		askForAckAll() {
			this.confirmAckAllToggleDialog = true
			this.confirmTitle = this.$t('eventList.acknownledgeAll')
			this.confirmMessage = this.$t('eventList.confirmAckAllMessage')
			this.actionToConfirm = this.acknowledgeAllEvents;
		},
		askForSilenceAll() {
			this.confirmAckAllToggleDialog = true
			this.confirmTitle = this.$t("eventList.silenceAll")
			this.confirmMessage = this.$t('eventList.silenceAllConfirmMessage')
			this.actionToConfirm = this.silenceAllEvents;
		},
		
		gotoDatasource(id) {
			window.location = `data_source_edit.shtm?dsid=${id}`
		},

		//system
		gotoSystem(type,referenceId2) {
			// window.location = `http://mango.serotoninsoftware.com/download.jsp` //png="bullet_down"
			// if (type = 1)  TYPE_SYSTEM_STARTUP
			if (type = 6) window.location = `compound_events.shtm?cedid=${referenceId2}"` // png="multi_bell" 
			if (type = 7) window.location = `event_handlers.shtm?ehid=${referenceId2}` //png="cog"
			if (type = 9) window.location = `point_links.shtm?plid=${referenceId2}` //png="link"
		},
		gotoCompoundEvent(compoundEventDetectorId) {
			window.location = `compound_events.shtm?cedid=${compoundEventDetectorId}` //png="multi_bell"
		},
		gotoScheduled(scheduleId) {
			window.location = `scheduled_events.shtm?seid=${scheduleId}` // png="clock"
		},
		gotoPublisher(publisherId) {
			window.location = `publisher_edit.shtm?pid=${publisherId}` // png="transmit_edit"
		},
		gotoAudit(referenceId1, referenceId2) {
			if (referenceId1 === 1) {$router.push({ name: 'datapoint-details', params: { id: item.typeRef1 } });$router.go();}
			else if (referenceId1 === 3) window.location = `data_source_edit.shtm?pid=${referenceId2}`
			// else if (referenceId1 === 4)
			else if (referenceId1 === 5) window.location = `compound_events.shtm?cedid=${referenceId2}`
			else if (referenceId1 === 6) window.location = `scheduled_events.shtm?seid=${referenceId2}`
			// else if (referenceId1 === 7)
			// else if (referenceId1 === 8)
			// else if (referenceId1 === 9)
		},
		gotoAuditEventDetector(referenceId2) {
			window.location = `data_point_edit.shtm?pedid=${referenceId2}` //png="icon_comp_edit"
		},	
		gotoAuditEventHandler(referenceId2) {
			window.location = `event_handlers.shtm?ehid=${referenceId2}` //cog
		},
		gotoAuditPointLink(referenceId2) {
			window.location = `point_links.shtm?plid=${referenceId2}` // png="link"
		},
		gotoMaintenance(maintenanceId) {
			window.location = `maintenance_events.shtm?meid=${maintenanceId}` //png="hammer"
		},

		async publishComment() {
			await this.$store.dispatch('addUserComment', {
				comment: {comment:this.commentText},
				typeId: 1,
				refId: this.selectedEventId,
			});
			this.fetchEventSelected()
			this.fetchEventList()
			this.commentText = ''
		},
		async fetchEventList() {
			this.loading = true;
			const result = await this.$store.dispatch('searchEvents', { ...this.searchFilters, itemsPerPage: this.options.itemsPerPage });
			this.eventList = result.rows;
			this.totalEvents = result.total;
			this.loading = false;
			await this.$store.dispatch('getHighestUnsilencedAlarmLevel');
		},
		async fetchEventSelected() {
			this.loading = true;
			this.comments = await this.$store.dispatch('getCommentsByEventId', this.selectedEventId);
			this.loading = false;
		},
		async acknowledgeEventSelected() {
			this.loading = true;
			this.comments = await this.$store.dispatch('acknowledgeEvent', {eventId: this.selectedEventId});
			await this.fetchEventList();
		},
		async acknowledgeAllEvents() {
			this.loading = true;
			await this.$store.dispatch('acknowledgeAll', { ...this.searchFilters, itemsPerPage: this.options.itemsPerPage });
			await this.fetchEventList();
		},
		async silenceAllEvents() {
			this.loading = true;
			await this.$store.dispatch('silenceAll', {eventId: this.selectedEventId});
			await this.fetchEventList();
		},
		async unsilenceAllEvents() {
			this.loading = true;
			await this.$store.dispatch('unsilenceAll', {eventId: this.selectedEventId});
			await this.fetchEventList();
		},
		async acknowledgeEvent(event) {
			await this.$store.dispatch('acknowledgeEvent', {eventId: event.id});
			await this.fetchEventList();
		},
		async silenceEvent(event) {
			await this.$store.dispatch('silenceEvent', {eventId: event.id});
			await this.fetchEventList();
		},
		async unsilenceEvent(event) {
			await this.$store.dispatch('unsilenceEvent', {eventId: event.id});
			await this.fetchEventList();
		},
		async acknowledgeSelectedEvents() {
			await this.$store.dispatch('acknowledgeSelectedEvents', {ids: this.selectedEvents.map(x => x.id).join(',')});
			await this.fetchEventList();
		},
		async silenceSelectedEvents(event) {
			await this.$store.dispatch('silenceSelectedEvents', {ids: this.selectedEvents.map(x => x.id).join(',')});
			await this.fetchEventList();
		},
		async unsilenceSelectedEvents(event) {
			await this.$store.dispatch('unsilenceSelectedEvents', {ids: this.selectedEvents.map(x => x.id).join(',')});
			await this.fetchEventList();
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
