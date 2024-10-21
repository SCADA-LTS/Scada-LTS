<template>
	<v-card v-if="!!report">
        <v-card-title>
            <span v-if="report.id === -1">
                {{ $t('reports.newTemplate') }}
            </span>
            <span v-else>
                {{ $t('reports.reportId') }}{{ report.id }}
            </span>
            <v-spacer></v-spacer>
            <v-btn color="primary" title="save" @click="saveReport">
				<v-icon>mdi-content-save</v-icon>
			</v-btn>
        </v-card-title>
		<v-card-text>
            <v-row>
                <v-col>
                    <v-text-field 
                        :label="$t('common.name')"
						v-model="name"
                    ></v-text-field>
                </v-col>
                <v-col>
                    <v-select
						:label="$t('reports.events')"
						:placeholder="$t('reports.selectDataPoint')"
						v-model="report.includeEvents"
                        item-text="label"
						item-value="value"
						:items="alarmOptions"
					></v-select>
                </v-col>
                
                <v-col>
					<DataPointsSettingsDialog :report="report" />
                </v-col>
            </v-row>
			<v-row>
				

				<v-col cols="8">
					<v-select
						v-model="report.dateRangeType"
						item-text="label"
						item-value="value"
						:items="dateRangeTypeOptions"
					></v-select>
				</v-col>

				<v-col cols="4">
                    <v-checkbox
                        v-model="report.includeUserComments"
                        :label="$t('reports.comments')"
                    ></v-checkbox>
                </v-col>

				<v-col>
					<v-row v-if="report.dateRangeType === 1">

						<v-col>
							<v-select
								item-text="label"
								v-model="report.relativeDateType"
								@change="setReportTime"
								:items="[
									{ label: $t('reports.previous'), value: 1 },
									{ label: $t('reports.past'), value: 2 },
								]"
							></v-select>
						</v-col>
					
						<v-col>
							<v-text-field 
								v-model="periodCount"
								@input="setReportTime"
							></v-text-field>
						</v-col>
						<v-col>
							<v-select
								v-model="periodType"
                        		item-value="id"
								item-text="label"
								:items="timePeriods"
								@change="setReportTime"
							></v-select>
						</v-col>

					</v-row>

					<v-row v-if="report.dateRangeType === 2">
						<v-col cols="4">
							<v-menu
								ref="start-date-menu"
								:close-on-content-click="false"
								:close-on-click="true"
								top
								min-width="auto"
								attach
							>
								<template v-slot:activator="{ on, attrs }">
									<v-text-field
										v-model="startDate"
										@change="updateDateRange"
										label="Start Date"
										prepend-icon="mdi-calendar"
										v-bind="attrs"
										v-on="on"
									></v-text-field>
								</template>
								<v-date-picker
									v-model="startDate"
									@change="updateDateRange"
									first-day-of-week="1"
									no-title
									scrollable
								></v-date-picker>
							</v-menu>
						</v-col>

						<v-col cols="4">
							<v-menu ref="start-time-menu"
								:close-on-content-click="false"
								:close-on-click="true"
								top
								max-width="290px"
								min-width="290px"
								attach
							>
								<template v-slot:activator="{ on, attrs }">
									<v-text-field 
										v-model="startTime"
										label="Start Time"
										prepend-icon="mdi-clock-time-four-outline"
										v-bind="attrs"
										v-on="on"
									></v-text-field>
								</template>
								<v-time-picker 
									v-model="startTime"
									format="24hr" 
									scrollable
								></v-time-picker>
							</v-menu>
						</v-col>

						<v-col cols="4">
							<v-checkbox
                        		v-model="report.fromNone"
                        		:label="$t('reports.inception')"
                    		></v-checkbox>
						</v-col>



						<v-col cols="4">
							<v-menu
								ref="end-date-menu"
								:close-on-content-click="false"
								:close-on-click="true"
								top
								min-width="auto"
								attach
							>
								<template v-slot:activator="{ on, attrs }">
									<v-text-field
										v-model="endDate"
										@change="updateDateRange"
										label="End Date"
										prepend-icon="mdi-calendar"
										v-bind="attrs"
										v-on="on"
									></v-text-field>
								</template>
								<v-date-picker
									v-model="endDate"
									@change="updateDateRange"
									first-day-of-week="1"
									no-title
									scrollable
								></v-date-picker>
							</v-menu>
						</v-col>

						<v-col cols="4">
							<v-menu ref="end-time-menu"
								:close-on-content-click="false"
								:close-on-click="true"
								top
								max-width="290px"
								min-width="290px"
								attach
							>
								<template v-slot:activator="{ on, attrs }">
									<v-text-field 
										v-model="endTime"
										label="End Time"
										prepend-icon="mdi-clock-time-four-outline"
										v-bind="attrs"
										v-on="on"
									></v-text-field>
								</template>
								<v-time-picker 
									v-model="endTime"
									format="24hr" 
									scrollable
								></v-time-picker>
							</v-menu>

						</v-col>

						<v-col cols="4">
							<v-checkbox
                        		v-model="report.fromNone"
                        		:label="$t('reports.latest')"
                    		></v-checkbox>
						</v-col>						
					</v-row>
				</v-col>
			</v-row>

			<v-divider></v-divider>

			<v-row>
				<v-col cols="4">
					<v-checkbox 
						:label="$t('reports.schedule')" 
						v-model="report.schedule">
					</v-checkbox>
				</v-col>

				<v-col cols="8">
					<v-row v-if="report.schedule">
						<v-col cols="6">
							<v-select
								label="Run every..."
								item-value="id"
								item-text="label"
								v-model="report.schedulePeriod"
								:items="shedulePeriods"
							></v-select>
						</v-col>

						<v-col cols="6" v-if="report.schedulePeriod === 0">
							<v-text-field
								v-model="scheduleCron"
								validate-on-blur
								:rules="cronRules"
								label="Cron pattern"
							></v-text-field>
						</v-col>

						<v-col cols="6" v-else>
							<v-text-field
								v-model="report.runDelayMinutes"
								label="Run delay (minutes)"
							></v-text-field>
						</v-col>						
					</v-row>
				</v-col>
			</v-row>

			<v-divider></v-divider>

			<v-row>
				<v-col cols="12">
					<v-checkbox
						:label="$t('reports.emailReport')"
						v-model="report.email"
					></v-checkbox>
				</v-col>

			</v-row>
			<v-row v-if="report.email">
				<v-col cols="4">
					<v-checkbox
						v-model="report.includeData"
						label="Include tabular data"
					></v-checkbox>
				</v-col>

				<v-col cols="4">
					<v-checkbox
						v-model="report.zipData"
						label="Data in .zip format"
					></v-checkbox>
				</v-col>

				<v-col cols="4">
					<v-btn
						class="primary"
						@click="sendTestEmails"
						:disabled="report.recipients.length === 0"
					>{{ $t('reports.sendTestEmails') }}
					</v-btn>
				</v-col>

				<v-col cols="6">
					<v-row>
						<v-col cols="12">
							<v-select
								:items="userList"
								label="Users"
								item-value="id"
								item-text="name"
								@change="addUser"
							></v-select>
						</v-col>

						<v-col cols="12">
							<v-select
								:items="recipientList"
								label="Recipient Lists"
								item-value="id"
								item-text="name"
								validate-on-blur
								@change="addRecipientList"
							></v-select>
						</v-col>

						<v-col cols="12">
							<v-text-field
								v-model="email"
								label="Add email address"
								append-outer-icon="mdi-plus"
								@click:append-outer="addMail"
								:rules="emailRules"
							></v-text-field>
						</v-col>
					</v-row>
				</v-col>

				<v-col cols="6">
					<v-row>
						<v-col cols="12">
							<v-list>
								<v-list-item v-for="(r, index) in activeRecipients" :key="index">
									<v-list-item-icon>
										<v-icon v-show="r.type === 1">
											mdi-email-multiple
										</v-icon>
										<v-icon v-show="r.type === 2">
											mdi-account
										</v-icon>
										<v-icon v-show="r.type === 3">
											mdi-email
										</v-icon>
									</v-list-item-icon>
									<v-list-item-title>
										{{r.name}}
									</v-list-item-title>
									<v-list-item-action>
										<v-btn icon @click="removeRecipient(r)">
											<v-icon>mdi-close</v-icon>
										</v-btn>
									</v-list-item-action>
								</v-list-item>
							</v-list>
						</v-col>
					</v-row>
				</v-col>

			</v-row>
		</v-card-text>
	</v-card>
</template>
<script>

import { RECIPIENT } from '../../store/mailingList/constants';
import { ALARM_OPTIONS, DATE_RANGE_TYPE_OPTIONS } from '../../store/reports/constants';
import DataPointsSettingsDialog from './DataPointsDialog.vue'
import {escapeHtml, unescapeHtml} from '@/utils/common';

export default {
    components: {
		DataPointsSettingsDialog
	},
    data() {
        return {
			report: null,
			startDate: "2022-01-01",
			startTime: "00:00",
			endDate: "2022-01-01",
			endTime: "23:59",
			periodCount: 1,
			periodType: 2,
            alarmOptions: ALARM_OPTIONS,
            dateRangeTypeOptions: DATE_RANGE_TYPE_OPTIONS,
			cronRules: [
				v => this.report.schedule && !!v || this.$t('reports.cronPatternIsRequired'),
				v =>
				/(@(annually|yearly|monthly|weekly|daily|hourly|reboot))|(@every (\d+(ns|us|µs|ms|s|m|h))+)|((((\d+,)+\d+|(\d+(\/|-)\d+)|\d+|\*) ?){5,7})/.test(v) ||
				this.$t('reports.cronPatternMustBeValid')
			],
			emailRules: [
				v =>  /\S+@\S+\.\S+/.test(v) || this.$t('reports.emailMustBeValid')
			],

			userList: [],
			recipientList: [],	
			emailText: '',
			activeRecipients: [],
        }
    },
    computed: {
        timePeriods() {
			return this.$store.state.timePeriods.filter((e) => {
				return !(e.id === 1 || e.id === 8);
			});
		},
		shedulePeriods() {
			let periods = this.$store.state.timePeriods.filter((e) => {
				return !(e.id === 1 || e.id === 8);
			});
			periods.push({id: 0, label: 'Cron pattern'})
			return  periods;
		},
        name: {
          get() {
            return unescapeHtml(this.report.name);
          },
          set(newValue) {
            this.report.name = escapeHtml(newValue);
          }
        },
        email: {
          get() {
            return unescapeHtml(this.emailText);
          },
          set(newValue) {
            this.emailText = escapeHtml(newValue);
          }
        },
        scheduleCron: {
          get() {
            return unescapeHtml(this.report.scheduleCron);
          },
          set(newValue) {
            this.report.scheduleCron = escapeHtml(newValue);
          }
        }
    },

	mounted() {
		this.fetchUserList();
		this.fetchRecipientList();
	},

	methods: {
		selectReport(report) {
			this.report = report;
			this.initDateTimeFields();
			this.initReportTimeFields();
			this.initRecipients();
		},

		saveReport() {
		    this.name = unescapeHtml(this.name);
            this.scheduleCron = unescapeHtml(this.scheduleCron);

            let report = JSON.parse(JSON.stringify(this.report));
			this.setDateTime();
			this.$emit('saved', report);

            this.name = escapeHtml(this.name);
            this.scheduleCron = escapeHtml(this.scheduleCron);
		},

		async fetchUserList() {
			const r = await this.$store.dispatch('getAllUsers');
			this.userList = r.map((u) => { return {id: u.id, email: u.email, name: u.username}});
		},

		async fetchRecipientList() {
			this.recipientList = await this.$store.dispatch('getSimpleMailingLists');
		},

		initDateTimeFields() {

			const todayFormatted = new Date().toISOString();
			const todayDateFormatted = todayFormatted.substring(0, 10);
			const todayTimeFormatted = todayFormatted.substring(11, 16);

			const fromFormatted = new Date(Date.UTC(this.report.fromYear, this.report.fromMonth - 1, this.report.fromDay, this.report.fromHour, this.report.fromMinute)).toISOString();
            const fromDateFormatted = fromFormatted.substring(0, 10);
            const fromTimeFormatted = fromFormatted.substring(11, 16);

            const toFormatted = new Date(Date.UTC(this.report.toYear, this.report.toMonth - 1, this.report.toDay, this.report.toHour, this.report.toMinute)).toISOString();
            const toDateFormatted = toFormatted.substring(0, 10);
            const toTimeFormatted = toFormatted.substring(11, 16);

			if(this.report.fromYear === 0 && this.report.fromMonth === 0 && this.report.fromDay === 0) {
				this.startDate = todayDateFormatted;
			} else {
				this.startDate = fromDateFormatted;
			}

			if(this.report.fromHour === 0 && this.report.fromMinute === 0) {
				this.startTime = `00:00`;
			} else {
				this.startTime = fromTimeFormatted;
			}

			if(this.report.toYear === 0 && this.report.toMonth === 0 && this.report.toDay === 0) {
				this.endDate = todayDateFormatted;
			} else {
				this.endDate = toDateFormatted;
			}

			if(this.report.toHour === 0 && this.report.toMinute === 0) {
				this.endTime = todayTimeFormatted;
			} else {
				this.endTime = toTimeFormatted;
			}
		},

		setDateTime() {
			const startDateValues = this.startDate.split('-');
			this.report.fromYear = startDateValues[0]*1;
			this.report.fromMonth = startDateValues[1]*1;
			this.report.fromDay = startDateValues[2]*1;

			const startTimeValues = this.startTime.split(':');
			this.report.fromHour = startTimeValues[0]*1;
			this.report.fromMinute = startTimeValues[1]*1;

			const endDateValues = this.endDate.split('-');
			this.report.toYear = endDateValues[0]*1;
			this.report.toMonth = endDateValues[1]*1;
			this.report.toDay = endDateValues[2]*1;

			const endTimeValues = this.endTime.split(':');
			this.report.toHour = endTimeValues[0]*1;
			this.report.toMinute = endTimeValues[1]*1;
		},

		initReportTimeFields() {
			if(this.report.relativeDateType === 1) {
				this.periodCount = this.report.previousPeriodCount;
				this.periodType = this.report.previousPeriodType;
			} else {
				this.periodCount = this.report.pastPeriodCount;
				this.periodType = this.report.pastPeriodType;
			}
		},

		setReportTime() {
			if(this.report.relativeDateType === 1) {
				this.report.previousPeriodCount = this.periodCount*1;
				this.report.previousPeriodType = this.periodType;
				this.report.pastPeriodCount = 1;
				this.report.pastPeriodType = 2;
			} else {
				this.report.previousPeriodCount = 1;
				this.report.previousPeriodType = 2;
				this.report.pastPeriodCount = this.periodCount*1;
				this.report.pastPeriodType = this.periodType;
			}
		},

		initRecipients() {
			this.activeRecipients = [];
			this.report.recipients.forEach(r => {
			    r.referenceAddress = unescapeHtml(r.referenceAddress);
				let entry = { 
					type: r.recipientType, 
					name: '', 
					id: r.referenceId, 
					mail: r.referenceAddress
				};

				if(r.recipientType === RECIPIENT.TYPE_USER) {
					entry.name = this.userList.find(u => u.id === r.referenceId).name;
				} else if(r.recipientType === RECIPIENT.TYPE_LIST) {
					entry.name = this.recipientList.find(u => u.id === r.referenceId).name;
				} else if(r.recipientType === RECIPIENT.TYPE_MAIL)  {
					entry.name = r.referenceAddress;
				} else {
					entry = null;
				}
				
				if(entry) {
					this.activeRecipients.push(entry);
				}
			});
		},

		addUser(user) {
			this.addRecipient(RECIPIENT.TYPE_USER, user);
		},

		addRecipientList(list) {
			this.addRecipient(RECIPIENT.TYPE_LIST, list);
		},

		addMail() {
		    this.email = unescapeHtml(this.email);
		    console.log('this.email: ' + this.email);
			if(!!this.email) {
				this.addRecipient(RECIPIENT.TYPE_MAIL, this.email);
				this.email = '';
			}
		},

		addRecipient(type, id) {
			const recipients = this.report.recipients;

			let recipient = {
				recipientType: type,
				referenceId: type === RECIPIENT.TYPE_MAIL ? 0 : id,
				referenceAddress: type !== RECIPIENT.TYPE_MAIL ? null : id
			};

			if(!recipients.find((r) => (r.recipientType === type 
					&& r.referenceId === recipient.referenceId
					&& r.referenceAddress === recipient.referenceAddress))) {
				recipients.push(recipient);			
			}
			this.initRecipients();
		},

		removeRecipient(r) {
			this.report.recipients = this.report.recipients.filter(rec => {
				return !(rec.recipientType === r.type && rec.referenceId === r.id && rec.referenceAddress === r.mail);
			});
			this.initRecipients();
		},

		sendTestEmails() {
			this.$store.dispatch(
				'sendTestEmails',
				this.report.recipients.map((x) => {
					if (x.recipientType === 3) return x.referenceAddress;
					else return this.userList.find((u) => u.id === x.referenceId).email;
				}))
			.then(() => this.$store.dispatch("showSuccessNotification", "Test emails sent successfully"))
			.catch(() => this.$slots.dispatch("showErrorNotification", "Test emails could not be sent"));
		}

		
	}
}
</script>
