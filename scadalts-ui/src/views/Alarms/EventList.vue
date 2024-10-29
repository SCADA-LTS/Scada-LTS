<template>
	<div>
		<ConfirmationDialog
			:btnvisible="false"
			ref="confirmDialog"
			@result="confirmAction"
			:title="confirmTitle"
			:message="confirmMessage"
		></ConfirmationDialog>
		<v-row justify="center" class="mt-6">
			<v-dialog v-model="eventDetailsDialog" scrollable max-width="70%">
				<v-card v-if="selectedEvent">
					<v-card-title>
						<v-row>
							<v-col cols="4">
								<img 
								:src="getFlagByEvent(selectedEvent)"> #{{selectedEvent.id}}</v-col>
							<v-col cols="8" class="text-right">
							</v-col>
						</v-row>
					</v-card-title>
					<v-row style="margin: 0 2rem" > 
						<v-col cols="3">
							<div style="position:relative; height: 0; ">
								<v-col><b>{{$t('eventList.createAt')}}</b>:<br> {{ $date(selectedEvent.activeTs).format('YYYY-MM-DD hh:mm:ss') }}</v-col>
								<v-col ><b>{{$t('eventList.alarmLevel')}}</b>:<br> 
									{{ $t(`eventList.alarmLevel${selectedEvent.alarmLevel}`) }}
								</v-col>
								<v-col v-if="selectedEvent.eventSourceType===1" ><b>{{$t('eventList.datapoint')}}</b>:<br> {{selectedEvent.datapoint}}</v-col>
								<v-col ><b>{{$t('eventList.sourceType')}}</b>:<br> {{ $t(`eventList.sourceType${selectedEvent.typeId}`) }}</v-col>
								<v-col ><b>{{$t('eventList.status')}}</b>:<br> 
									<span v-if="selectedEvent.rtnApplicable && !selectedEvent.rtnTs" >{{$t('eventList.STATUS_ACTIVE')}}</span>
									<span v-if="selectedEvent.rtnApplicable && selectedEvent.rtnTs">{{$t('eventList.STATUS_RTN')}} {{$date(selectedEvent.rtnTs).format('YYYY-MM-DD hh:mm:ss')}}</span>
									<span v-if="!selectedEvent.rtnApplicable">{{$t('eventList.STATUS_NORTN')}}</span>
								</v-col>
								<v-col v-if="selectedEvent.ackTs"><b>{{$t('eventList.ackTime')}}</b>:<br> 
									<span >  {{$date(selectedEvent.ackTs).format('YYYY-MM-DD hh:mm:ss')}}</span>
								</v-col>
							</div>
						</v-col>
						<v-col cols="9">
							<p style="padding-top: 10px">
								<b>{{$t('eventList.message')}}</b>:<br>
							<span v-html="selectedEvent.message"></span>
							</p>
							<v-divider></v-divider>
							<textarea style="width:100%;border: green 1px solid;  resize:none" rows="5" v-model="commentText"></textarea>
							<v-btn style=";width:100%" class="primary" :disabled="!commentText.length" @click="publishComment">{{ $t('eventList.addComment') }}</v-btn>
						</v-col>
					</v-row> 
					<v-card-text style="height: 300px;">
						<v-row>
							<v-col cols="3"></v-col>
							<v-col cols="9">
								<ul id=commentList v-for="comment in comments">
									<li>{{comment.username}}-{{$date(comment.ts).format('YYYY-MM-DD hh:mm:ss')}}:  <span v-html="comment.commentText"></span></li>
								</ul>
							</v-col>
						</v-row>
					</v-card-text>
					<v-divider></v-divider>
					<form @submit.prevent="publishComment"> 
						<v-card-actions style="padding: 15px">
							<v-spacer></v-spacer>
							<v-btn :disabled="selectedEvent.ackTs > 0" class="mr-2 primary" @click="acknowledgeEventSelected" >
								<v-icon>mdi-checkbox-marked-circle-outline</v-icon>
								{{$t('eventList.acknowledge')}}
							</v-btn>
							<v-btn :disabled="selectedEvent.ackTs > 0" v-if="!selectedEvent.silenced" @click="silenceEvent({id:selectedEvent.id})"  class="mr-2 primary"  >
								<v-icon>mdi-volume-mute</v-icon>
								{{$t('eventList.silence')}}
							</v-btn>
							<v-btn :disabled="selectedEvent.ackTs > 0" v-else @click="unsilenceEvent({id:selectedEvent.id})"  class="mr-2 primary">
								<v-icon>mdi-volume-mute</v-icon>
								{{$t('eventList.unsilence')}}
							</v-btn>		
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
						{{$t("eventList.acknowledgeSelectedEvents")}}
					</v-btn>
					<v-btn small @click="silenceSelectedEvents" :title="$t('eventList.silence')"  color="blue" class="mr-2" >
						<v-icon class="mr-2" >
							mdi-volume-mute
						</v-icon>
						{{$t("eventList.silenceSelected")}}
					</v-btn>
					<v-btn small @click="unsilenceSelectedEvents" :title="$t('eventList.unsilence')" color="blue">
						<v-icon class="mr-2" >
							mdi-volume-high
						</v-icon>
						{{$t("eventList.unsilenceSelected")}}
					</v-btn>
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
						<img :src="getFlagByEvent(item)">
					</template>
					<template v-slot:item.typeId="{ item }">
						{{ $t(`eventList.sourceType${item.typeId}`) }}
					</template>
					<template v-slot:item.message="{ item }">
						<a :title="(item.message) | clearHtml"><span v-html="item.message"></span></a>
					</template>

					<template v-slot:item.status="{ item }">
						<span v-if="item.rtnApplicable && !item.rtnTs" >{{$t('eventList.STATUS_ACTIVE')}}</span>
						<span v-if="item.rtnApplicable && item.rtnTs">{{$date(item.rtnTs).format('YYYY-MM-DD hh:mm:ss')}}</span>
						<span v-if="!item.rtnApplicable">{{$t('eventList.STATUS_NORTN')}}</span>	
					</template>
					<template v-slot:item.actions="{ item }">
						<span v-if="!item.ackTs">
							<v-icon class="mr-2" border="0" @click.stop="silenceEvent(item);return false" v-if="!item.silenced" title="silence">
								mdi-volume-high
							</v-icon>
							<v-icon class="mr-2" border="0" @click.stop="unsilenceEvent(item);return false" v-if="item.silenced" title="unsilence">
								mdi-volume-mute
							</v-icon>
						</span>

						<v-icon border="0" @click.stop="$router.push({ name: 'datapoint-details', params: { id: item.typeRef1 } });$router.go();" v-if="item.typeId===1" title="point details">
							mdi-magnify
						</v-icon>

						<v-icon  title="data source" @click.stop="gotoDatasource(item.typeId)" v-if="item.typeId===3">
						mdi-database
						</v-icon>	
						
						<v-icon @click.stop="gotoSystem(item.typeRef1, item.typeRef2)" v-if="item.typeId===4" title="system">
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
import store from '../../store';
import RangeChartComponent from '../../components/amcharts/RangeChartComponent.vue';
import ConfirmationDialog from '@/layout/dialogs/ConfirmationDialog';
import {getEventList} from '../../utils/common';

export default {
	name: 'EventList',
	components: {
		RangeChartComponent,
		ConfirmationDialog,
	},
	mounted() {
		this.mountedTs = this.$dayjs()
		let stompClient = this.$store.state.webSocketModule.webSocket;
		let fetchEvent = this.fetchEvent;
		let searchFilters = this.searchFilters;
        stompClient.subscribe("/app/event/update/register", function(register) {
            let subscription = stompClient.subscribe("/topic/event/update/"+register.body, function(message) {fetchEvent(message, searchFilters)});
            if(subscription) {
                setTimeout(function() {stompClient.send("/app/event/update", {priority: 1}, "STOMP - /app/event/update")}, 1500);
            }
        });
	},
	computed: {
		alarmFlags() {
			return this.$store.state.staticResources.alarmFlags;
		},
        commentRows() {
            const lines = this.commentText.split('\n').length
            return lines < 5 ? lines : 5
        }
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
			next: false,
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
      
			confirmAckAllToggleDialog: false,

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
			if (input.length > 45) {
				return input.substring(0, 45) + '...';
			}
			return input;
		},
		clearHtml(str) {
			return str.replace(/<[^>]*>?/gm, '').replaceAll('&nbsp;', ' ').replaceAll('&#39;', ' ').replaceAll('&quot;', ' ')
		}
	},
	
	methods: {
		getFlagByEvent(event) {
			return ((event.rtnApplicable && !event.rtnTs) ? this.alarmFlags : this.alarmFlagsOff)[event.alarmLevel].image
		},
		eventMessageI18n(eventMessage) {
			const [key, ...args ]= eventMessage.split('|')
			return this.$t(key, args)
		},
		nextPage(){
			this.options = {...this.options, page: this.options.page+1}
			this.fetchEventList()
		},
		getAlarms() {
			store.dispatch('getLiveAlarms', { offset: 0, limit: 1 }).then((ret) => {
				if (ret.length) this.newAlarms = true
			});
		},
		confirmAction(answer) {
			if (answer) this.actionToConfirm();
			// this.confirmDialog.showDialog()
		},
		askForAckAll() {
			this.$refs.confirmDialog.showDialog()
			this.confirmTitle = this.$t('eventList.acknownledgeAll')
			this.confirmMessage = this.$t('eventList.confirmAckAllMessage')
			this.actionToConfirm = this.acknowledgeAllEvents;
		},
		askForSilenceAll() {
			this.$refs.confirmDialog.showDialog()
			this.confirmTitle = this.$t("eventList.silenceAll")
			this.confirmMessage = this.$t('eventList.silenceAllConfirmMessage')
			this.actionToConfirm = this.silenceAllEvents;
		},
		
		gotoDatasource(id) {
			window.location = `data_source_edit.shtm?dsid=${id}`
		},

		//system
		gotoSystem(referenceId1,referenceId2) {
			if (referenceId1 == 6) window.location = `compound_events.shtm?cedid=${referenceId2}"`
			else if (referenceId1 == 7) window.location = `event_handlers.shtm?ehid=${referenceId2}`
			else if (referenceId1 == 9) window.location = `point_links.shtm?plid=${referenceId2}`
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
				comment: {comment:this.replaceLineBreaksByBr(this.commentText)},
				typeId: 1,
				refId: this.selectedEventId,
			});
			this.fetchEventSelected()
			this.fetchEventList()
			this.commentText = ''
			document.getElementById('commentList').scrollIntoView();
		},
		replaceLineBreaksByBr: function (x){
			return x.replaceAll("\n",'<br/>')
		},
		async fetchEventList() {
			this.loading = true;
			const result = await this.$store.dispatch('searchEvents', { ...this.searchFilters, itemsPerPage: this.options.itemsPerPage });
            const rows = result.rows;
			if (rows.length > this.options.itemsPerPage) {
				this.eventList = rows.slice(0,this.options.itemsPerPage);
				this.totalEvents = this.options.itemsPerPage * this.options.page +1
			} else {
				this.eventList = rows;
				this.totalEvents = this.options.itemsPerPage * this.options.page
			}
			// document.getElementsByClassName('v-data-footer__pagination')[0].innerHTML=''
			this.loading = false;
		},
		async fetchEventSelected() {
      try {
        this.loading = true;
        this.comments = await this.$store.dispatch('getCommentsByEventId', this.selectedEventId);
        this.loading = false;
      } catch (error) {
        console.error("Error acknowledging event:", error);
      }
		},
		async acknowledgeEventSelected() {
			this.loading = true;
			await this.$store.dispatch('acknowledgeEvent', {eventId: this.selectedEventId});
			await this.fetchEventList();
			return true
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
      try {
			  await this.$store.dispatch('acknowledgeEvent', {eventId: event.id});
			  await this.fetchEventList();
      } catch (error) {
        console.error("Error acknowledging event:", error);
      }
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
	  	},
        fetchEvent(message, searchFilters) {
            if(message) {
                let event = JSON.parse(message.body);
                let sortBy = searchFilters.sortBy;
                let sortDesc = searchFilters.sortDesc;

                if(sortBy.length == 0) {
                    sortBy = ['id'];
                }

                if(sortDesc.length == 0) {
                    sortDesc = [true];
                }

                this.loading = true;

                let temp = getEventList(event, sortBy, sortDesc, this.eventList);

                if(temp.length == 0) {
                    this.fetchEventList();
                } else {
                    this.eventList = temp;
                }

                this.loading = false;

            } else {
                console.log('fetchEvent: no exist');
            }
        }
	}
}
</script>
