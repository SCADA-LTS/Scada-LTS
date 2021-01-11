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
			eventHandlers: undefined,
			modified: [],
			snackbar: {
				visible: false,
				text: '',
			},
		};
	},

	mounted() {
		this.initMailingLists();
		this.initEventHandlers();
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

		async initEventHandlers() {
			this.eventHandlers = await this.$store.dispatch('getPlcEventHandlers');
		},

		async fetchDataPoints(item) {
			let dp = await this.$store.dispatch('getPlcDataPoints', item.id);
			dp.forEach((e) => {
				let i = { id: e.id, name: e.name, mail: [], sms: [] };
				i.mail.push(this.addConfiguration(e.id, this.activeMailingList, 'mail'));
				i.mail.push(this.addConfiguration(e.id, this.activeMailingList2, 'mail'));
				i.sms.push(this.addConfiguration(e.id, this.activeMailingList, 'sms'));
				i.sms.push(this.addConfiguration(e.id, this.activeMailingList2, 'sms'));

				item.children.push(i);
			});
		},

		bindEmailEventHandler(datapointId, mlId) {
			let find = -1;
			this.eventHandlers.forEach((eh) => {
				if (eh.eventTypeRef1 == datapointId && eh.handlerType === 2) {
					if (!!eh.recipients) {
						eh.recipients.forEach((r) => {
							if (r.referenceId == mlId) {
								find = { ehId: eh.id, edId: eh.eventTypeRef2 };
							}
						});
					}
				}
			});
			return find;
		},

		bindSmsEventHandler(datapointId, mlId) {
			let find = -1;
			this.eventHandlers.forEach((eh) => {
				if (eh.eventTypeRef1 == datapointId && eh.handlerType === 5) {
					if (!!eh.recipients) {
						eh.recipients.forEach((r) => {
							if (r.referenceId == mlId) {
								find = { ehId: eh.id, edId: eh.eventTypeRef2 };
							}
						});
					}
				}
			});
			return find;
		},

		addConfiguration(datapointId, mailingListId, type) {
			let eventHandlerId;
			if (!!mailingListId) {
				if (type === 'mail') {
					eventHandlerId = this.bindEmailEventHandler(datapointId, mailingListId);
				} else if (type === 'sms') {
					eventHandlerId = this.bindSmsEventHandler(datapointId, mailingListId);
				}
				let ehExist = eventHandlerId !== -1;
				return {
					handler: eventHandlerId,
					active: ehExist,
					config: ehExist,
					mlId: mailingListId,
				};
			}
			return { active: false, config: false };
		},

		changeMailingList(item) {
			this.items = [];
			this.modified = [];
			this.initDataSources();
		},

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

		saveConfiguration() {
			console.debug(this.modified)
			if (this.modified.length > 0) {
				this.modified.forEach((change) => {
					let mailingListsLength =
						change.mail.length > change.sms.length
							? change.mail.length
							: change.sms.length;

					console.debug(change)

					//maxLength - 0 = 1st Mailing List, 1 = 2nd ML
					for (let x = 0; x < mailingListsLength; x++) {
						let mlId =
							x % mailingListsLength === 0
								? this.activeMailingList
								: this.activeMailingList2;

						//Check the Mail Communitation Channel
						if (change.mail[x].active !== change.mail[x].config) {
							if (change.mail[x].handler !== -1) {
								this.updateEventHandler(
									change.mail[x].handler.ehId,
									change.mail[x].mlId,
									change.id,
									change.mail[x].handler.edId,
									'delete'
								);
							} else {
								this.prepareEventHandler(mlId, change.id, 2);
							}
						}

						//Check the SMS Communication Channel
						if (change.sms[x].active !== change.sms[x].config) {
							if (change.sms[x].handler !== -1) {
								this.updateEventHandler(
									change.sms[x].handler.ehId,
									change.sms[x].mlId,
									change.id,
									change.sms[x].handler.edId,
									'delete'
								);
							} else {
								this.prepareEventHandler(mlId, change.id, 5);
							}
						}
					}
				});
				this.initEventHandlers();
				this.modified = [];
				this.snackbar.text = this.$t('plcalarms.notification.save');
				this.snackbar.visible = true;
			}
		},

		async updateEventHandler(ehId, mlId, dpId, edId, method) {
			let updateData = {
				ehId: ehId,
				activeMailingList: mlId,
				typeRef1: dpId,
				typeRef2: edId,
				method: method,
			};
			await this.$store.dispatch('updateEventHandler', updateData);
		},

		async createEventHandler(mlId, dpId, handlerType) {
			let createData = {
				datapointId: dpId,
				mailingListId: mlId,
				handlerType: handlerType,
			};
			try {
				this.$store.dispatch('createEventHandler', createData);
				return true;
			} catch (err) {
				this.snackbar.text = this.$t('plcalarms.notification.fail');
				this.snackbar.visible = true;
				return false;
			}
		},

		prepareEventHandler(mlId, dpId, handlerType) {
			let eventHandlerData = this.getExistingEventHandler(dpId, handlerType);
			if (!!eventHandlerData) {
				this.updateEventHandler(
					eventHandlerData.ehId,
					mlId,
					dpId,
					eventHandlerData.edId,
					'add'
				);
			} else {
				this.createEventHandler(mlId, dpId, handlerType);
			}
		},

		getExistingEventHandler(datapointId, handlerType) {
			let result = null;
			this.eventHandlers.forEach((eh) => {
				if (eh.eventTypeRef1 == datapointId && eh.handlerType === handlerType) {
					result = { ehId: eh.id, edId: eh.eventTypeRef2 };
				}
			});
			return result;
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
