<template>
	<div>
		<v-container fluid>
			<v-row align="center">
				<v-col cols="6" xs="12">
					<h1>
						{{ $t('plcalarms.notification.title') }}
						<v-tooltip bottom>
							<template v-slot:activator="{ on, attrs }">
								<v-icon small v-bind="attrs" v-on="on">mdi-help</v-icon>
							</template>
							<span class="help-message">{{ $t('plcalarms.notification.help') }}</span>
						</v-tooltip>
					</h1>
				</v-col>
				<v-col cols="2" xs="12" class="row justify-end">
					<v-btn
						elevation="2"
						fab
						dark
						color="primary"
						v-if="modified.length !== 0"
						@click="saveConfiguration"
					>
						<v-icon>mdi-content-save</v-icon>
					</v-btn>
					<CreationSettingsDialog></CreationSettingsDialog>
				</v-col>
				<v-col cols="2" xs="12">
					<v-select
						@change="changeMailingList"
						v-model="activeMailingList"
						:items="mailingLists"
						item-value="id"
						item-text="name"
						:label="$t('plcalarms.notification.select.mailinglist')"
						:hint="$t('plcalarms.notification.select.mailinglist.hint.1')"
						persistent-hint
						solo
						dense
					>
					</v-select>
				</v-col>
				<v-col cols="2" xs="12">
					<v-select
						@change="changeMailingList"
						v-model="activeMailingList2"
						:items="mailingLists"
						item-value="id"
						item-text="name"
						:label="$t('plcalarms.notification.select.mailinglist')"
						:hint="$t('plcalarms.notification.select.mailinglist.hint.2')"
						persistent-hint
						solo
						dense
					>
					</v-select>
				</v-col>
			</v-row>
		</v-container>

		<v-treeview dense :items="items" activatable :load-children="fetchDataPoints">
			<template v-slot:append="{ item }">
				<v-row align="center" class="d-flex" v-if="!item.children">
					<v-checkbox
						v-model="item.mail[0].active"
						on-icon="mdi-email"
						off-icon="mdi-email-outline"
						:disabled="!item.mail[0].handler"
						@click="watchPointChange(item)"
					></v-checkbox>
					<!-- mdi-Android-messages as alternative -->
					<v-checkbox
						v-model="item.sms[0].active"
						on-icon="mdi-cellphone"
						off-icon="mdi-cellphone-off"
						:disabled="!item.sms[0].handler"
						@click="watchPointChange(item)"
					></v-checkbox>
					<v-spacer vertical class="space"></v-spacer>
					<v-checkbox
						v-model="item.mail[1].active"
						on-icon="mdi-email"
						off-icon="mdi-email-outline"
						:disabled="!item.mail[1].handler"
						@click="watchPointChange(item)"
					></v-checkbox>
					<!-- mdi-Android-messages as alternative -->
					<v-checkbox
						v-model="item.sms[1].active"
						on-icon="mdi-cellphone"
						off-icon="mdi-cellphone-off"
						:disabled="!item.sms[1].handler"
						@click="watchPointChange(item)"
					></v-checkbox>
				</v-row>
			</template>
		</v-treeview>
		<v-snackbar v-model="snackbar.visible">{{ snackbar.text }}</v-snackbar>
	</div>
</template>
<script>
import CreationSettingsDialog from './CreationSettings';

/**
 * Alarm notification View-component.
 * Render page that main purpose is management of PLC notifications.
 *
 * @author Radoslaw Jajko <rjajko@softq.pl>
 * @version 1.1
 *
 */
export default {
	name: 'AlarmNotifications',

	components: {
		CreationSettingsDialog,
	},

	data() {
		return {
			items: [],
			activeMailingList: undefined,
			activeMailingList2: undefined,
			mailingLists: undefined,
			modified: [],
			isError: false,
			snackbar: {
				visible: false,
				text: '',
			},
			TYPE_MAIL: 2,
			TYPE_SMS: 5,
		};
	},

	mounted() {
		this.initMailingLists();
	},

	methods: {
		async initDataSources() {
			let ds = await this.$store.dispatch('getAllPlcDataSources');
			ds.forEach((e) => {
				let item = { id: e.id, name: e.name, children: [] };
				this.items.push(item);
			});
		},

		async initMailingLists() {
			this.mailingLists = await this.$store.dispatch('getAllMailingLists');
		},

		async fetchDataPoints(item) {
			let dp = await this.$store.dispatch('getPlcDataPoints', item.id);
			for (let i = 0; i < dp.length; i++) {
				let configuration = await this.$store.dispatch(
					'getPlcDataPointConfiguration',
					dp[i].id,
				);
				let itemName = dp[i].name;
				if (!!dp[i].description) {
					itemName = itemName + ` - ${dp[i].description}`;
				}
				let datapoint = this.prepareDataPoint(dp[i].id, itemName, configuration);

				item.children.push(datapoint);
			}
		},

		/**
		 * Watch Point Change
		 * @param {number} id - DataPoint ID number
		 * @param {string} name - DataPoint Name
		 * @param {array} configuration - List of EventHandlers configuration
		 *
		 * Initialize data to present on the UI for specific datapoint.
		 */
		prepareDataPoint(id, name, configuration) {
			let mail = this.prepareInitialPointConfiguration();
			let sms = this.prepareInitialPointConfiguration();

			if (configuration.length !== 0) {
				configuration.forEach((c) => {
					c.recipients.forEach((r) => {
						if (r.recipientType === 1 && r.referenceId === this.activeMailingList) {
							if (c.handlerType === this.TYPE_MAIL) {
								mail[0].active = true;
								mail[0].config = true;
								mail[0].handler = c.eventTypeRef2;
								mail[0].mlId = r.referenceId;
							}
							if (c.handlerType === this.TYPE_SMS) {
								sms[0].active = true;
								sms[0].config = true;
								sms[0].handler = c.eventTypeRef2;
								sms[0].mlId = r.referenceId;
							}
						}
						if (r.recipientType === 1 && r.referenceId === this.activeMailingList2) {
							if (c.handlerType === this.TYPE_MAIL) {
								mail[1].active = true;
								mail[1].config = true;
								mail[1].handler = c.eventTypeRef2;
								mail[1].mlId = r.referenceId;
							}
							if (c.handlerType === this.TYPE_SMS) {
								sms[1].active = true;
								sms[1].config = true;
								sms[1].handler = c.eventTypeRef2;
								sms[1].mlId = r.referenceId;
							}
						}
					});
				});
			}
			return { id, name, configuration, mail, sms };
		},

		/**
		 * Prepre Initial Point Configuration
		 *
		 * Prepare data based on the active mailing list on the UI.
		 */
		prepareInitialPointConfiguration() {
			let configuration = [];
			if (!!this.activeMailingList) {
				configuration.push({
					active: false,
					config: false,
					handler: -1,
					mlId: this.activeMailingList,
				});
			} else {
				configuration.push({ active: false, config: false });
			}
			if (!!this.activeMailingList2) {
				configuration.push({
					active: false,
					config: false,
					handler: -1,
					mlId: this.activeMailingList2,
				});
			} else {
				configuration.push({ active: false, config: false });
			}
			return configuration;
		},

		changeMailingList(item) {
			this.items = [];
			this.modified = [];
			this.initDataSources();
		},

		/**
		 * Watch Point Change
		 * @param {object} item - Element of UI
		 *
		 * Chceck change made by user while clicking the checkbox icon.
		 */
		watchPointChange(item) {
			this.modified = this.modified.filter((element) => {
				return element.id !== item.id;
			});
			for (let x = 0; x < item.mail.length; x++) {
				if (
					item.mail[x].active !== item.mail[x].config ||
					item.sms[x].active !== item.sms[x].config
				) {
					this.modified.push(item);
					break;
				}
			}
		},

		/**
		 * Update single EventHandler
		 * @param {number} id - DataPoint ID number
		 * @param {object} config - DataPoint EventHandler configuration
		 * @param {object} recipientLists - Active mailing lists
		 * @param {number} type - Type of EventHandler (SMS or Mail)
		 *
		 * Chceck data for specific datapoint and make decision what to do.
		 * If eventHandler do not exists (eventHandler === null) try to create.
		 * If there is any change (config !== active) take steps to delete
		 * or update that handler.
		 */
		updateHandler(id, config, recipientLists, type) {
			return new Promise(async (resolve) => {
				let eventHandler = this.getEventHandler(config, type);
				if (!!eventHandler) {
					let requestChange = [];
					recipientLists.forEach((m) => {
						if (m.config !== m.active) {
							requestChange.push(m.mlId);
							if (m.active) {
								// Add Mailing List to recipients list
								eventHandler.recipients.push({
									recipientType: 1,
									referenceAddress: null,
									referenceId: m.mlId,
								});
							} else {
								// Remove Mailing List from recipients list
								eventHandler.recipients = eventHandler.recipients.filter((e) => {
									return e.referenceId !== m.mlId;
								});
							}
						}
					});

					if (requestChange.length !== 0) {
						if (eventHandler.recipients.length === 0) {
							await this.$store.dispatch('deleteEventHandler', eventHandler.id);
							config = config.filter((e) => {
								return e.id !== eventHandler.id;
							});
							if (config.length === 0) {
								this.$store.dispatch('deleteEventDetector', {
									datapointId: id,
									pointEventDetectorId: eventHandler.eventTypeRef2,
								});
							}
							this.saveDatapoint(id, config, type);
							resolve(config);
						} else {
							let resp = await this.$store.dispatch('updateEventHandlerV2', eventHandler);
							if (!!resp) {
								let index = config.findIndex((x) => x.id === id);
								config[index] = eventHandler;
								this.saveDatapoint(id, config, type);
								resolve(config);
							} else {
								let resp1 = await this.$store.dispatch(
									'createEventHandler',
									this.createEventHandlerData(id, requestChange, type, false),
								);
								config.push(resp1);
								this.saveDatapoint(id, config, type);
								resolve(config);
							}
						}
					}
				} else {
					let creationRequests = [];
					recipientLists.forEach((m) => {
						if (m.config !== m.active) {
							creationRequests.push(m.mlId);
						}
					});

					if (creationRequests.length !== 0) {
						let resp = await this.$store.dispatch(
							'createEventHandler',
							this.createEventHandlerData(
								id,
								creationRequests,
								type,
								creationRequests.length === 2,
							),
						);
						config.push(resp);
						this.saveDatapoint(id, config, type);
						resolve(config);
					}
				}
				resolve('nochange');
			});
		},

		/**
		 * Get Event Handler
		 * @param {array} configuration - List of EventHandlers configuration
		 * @param {type} number - Type of EventHandler (SMS or Mail)
		 *
		 * @returns {object} Valid Event Handler data.
		 */
		getEventHandler(configuration, type) {
			let eventHandler = null;
			if (configuration.length !== 0) {
				configuration.forEach((c) => {
					if (c.handlerType === type) {
						eventHandler = c;
					}
				});
			}
			return eventHandler;
		},

		/**
		 * Save User Configuration
		 *
		 * Save PLC Notification configuration. This method is invoked after
		 * clicking Save button by User on the UI. Save data to database.
		 */
		async saveConfiguration() {
			if (this.modified.length > 0) {
				for (let i = 0; i < this.modified.length; i++) {
					let x = await this.updateHandler(
						this.modified[i].id,
						this.modified[i].configuration,
						this.modified[i].mail,
						this.TYPE_MAIL,
					);
					if (x !== 'nochange') {
						this.modified[i].configuration = x;
					}
					await this.updateHandler(
						this.modified[i].id,
						this.modified[i].configuration,
						this.modified[i].sms,
						this.TYPE_SMS,
					);
				}

				this.modified = [];
				if (this.isError) {
					this.snackbar.text = this.$t('plcalarms.notification.fail');
					this.snackbar.visible = true;
					this.isError = false;
				} else {
					this.snackbar.text = this.$t('plcalarms.notification.save');
					this.snackbar.visible = true;
				}
			}
		},

		/**
		 * Save DataPoint
		 * @param {number} id - DataPoint ID number
		 * @param {object} config - DataPoint EventHandler configuration
		 * @param {number} type - Type of EventHandler (SMS or Mail)
		 *
		 * Save specific data point after change. Add EventHandler configuration
		 * and update the UI checkbox status.
		 */
		saveDatapoint(id, config, type) {
			this.items.forEach((item) => {
				if (!!item.children) {
					item.children.forEach((datapoint) => {
						if (datapoint.id === id) {
							datapoint.configuration = config;
							if (type === this.TYPE_MAIL) {
								datapoint.mail.forEach((mail) => {
									mail.config = mail.active;
								});
							}
							if (type === this.TYPE_SMS) {
								datapoint.sms.forEach((sms) => {
									sms.config = sms.active;
								});
							}
						}
					});
				}
			});
		},

		/**
		 * Create Event Handler payload object
		 * @param {number} datapointId - DataPoint ID number
		 * @param {number} mailingListId - Mailing List ID to be attached
		 * @param {number} handlerType - Type of EventHandler (SMS or Mail)
		 * @param {boolean} dual - Are two mailing list attached at once
		 *
		 * Create payload data for Vuex method.
		 */
		createEventHandlerData(datapointId, mailingListId, handlerType, dual) {
			return { datapointId, mailingListId, handlerType, dual };
		},
	},
};
</script>
<style>
.space {
	margin: 0 6vw;
}
.v-tooltip__content {
	max-width: 25vw;
}
</style>
