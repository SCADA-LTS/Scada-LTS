<template>
	<v-row v-if="!!recipientList" id="recipient-list-details">
		
		<v-col md="12" sm="12" xs="12" id="rl-section-details">
			<v-row>
				<v-col v-if="!edit" cols="12" class="heading-action-buttons">
					<h2>{{ $t('userDetails.title') }}</h2>
					<v-spacer></v-spacer>
					
					<v-btn v-if="!edit" text  color="primary" @click="restore()">{{ $t('userDetails.changePassword') }}</v-btn>
					<v-btn fab elevation="2" color="primary" small v-if="!edit" @click="save()">
						<v-icon>mdi-content-save</v-icon>
					</v-btn>
					
				</v-col>
			
				<v-col md="12" sm="12" xs="12">
					<v-text-field :label="$t('userDetails.field.username')"></v-text-field>
				</v-col>
				
				<v-col v-if="edit"  md="6" sm="12" xs="12">
					<v-text-field  type="password" :label="$t('userDetails.field.password')"></v-text-field>	
				</v-col>
				<v-col v-if="edit"  md="6" sm="12" xs="12">
					<v-text-field  type="password" :label="$t('userDetails.field.password')"></v-text-field>	
				</v-col>
				<v-col md="6" sm="12" xs="12">
					<v-text-field :label="$t('userDetails.field.firstName')"></v-text-field>
				</v-col>
				<v-col md="6" sm="12" xs="12">
					<v-text-field :label="$t('userDetails.field.lastName')"></v-text-field>
				</v-col>
				<v-col md="6" sm="12" xs="12">
					<v-text-field :label="$t('userDetails.field.email')"></v-text-field>
				</v-col>
				<v-col md="6" sm="12" xs="12">
					<v-text-field :label="$t('userDetails.field.phone')"></v-text-field>
				</v-col>
			</v-row>
			<v-row>	
				<v-col md="3" sm="6" xs="12">
					<v-checkbox :label="$t('userDetails.field.admin')"></v-checkbox>
				</v-col>
				<v-col md="3" sm="6" xs="12">
					<v-checkbox :label="$t('userDetails.field.disabled')"></v-checkbox>
				</v-col>
				<v-col md="3" sm="6" xs="12">
					<v-checkbox :label="$t('userDetails.field.receiveOwnAuditEvents')"></v-checkbox>
				</v-col>
				<v-col md="3" sm="6" xs="12">
					<v-checkbox :label="$t('userDetails.field.hideMenu')"></v-checkbox>
				</v-col>
			</v-row>
			<v-row>
				<v-col md="6" sm="12" xs="12">
					<v-select :label="$t('userDetails.field.receiveOwnAuditEvents')"></v-select>
				</v-col>
				<v-col md="6" sm="12" xs="12">
					<v-select :label="$t('userDetails.field.theme')"></v-select>
				</v-col>
				

				<!-- 
					<v-text-field :label="$t('userDetails.field.password')"></v-text-field>


					<v-text-field :label="$t('userDetails.field.admin')"></v-text-field>
					<v-text-field :label="$t('userDetails.field.disabled')"></v-text-field>
					<v-text-field :label="$t('userDetails.field.homeUrl')"></v-text-field>
					<v-text-field :label="$t('userDetails.field.receiveAlarmEmails')"></v-text-field>
					<v-checkbox :label="$t('userDetails.field.receiveOwnAuditEvents')"></v-checkbox>
					<v-checkbox :label="$t('userDetails.field.hideMenu')"></v-checkbox>-->

				

				
			</v-row>
		</v-col>
		<v-col md="12" sm="12" xs="12" v-if="inactiveTime && !edit" id="section-active-time">
			<v-row @mousedown="startSelecting">
				<v-col cols="12" class="heading-action-buttons">
					<h3>{{ $t('userDetails.permission.title') }}</h3>
					<v-spacer></v-spacer>
					<v-btn fab elevation="2" color="primary" small v-if="!edit" @click="save()">
						<v-icon>mdi-content-save</v-icon>
					</v-btn>
				</v-col>
				<v-col cols="12" v-if="!loadingInactiveTime">
					<v-expansion-panels accordion>
					<v-expansion-panel>
						<v-expansion-panel-header>
							<v-col cols="6">
								<h4 style="white-space: nowrap">Datasource looooooooooooooooooooooooooooooooooooooong name {{ key }}</h4>
							</v-col>
							
						</v-expansion-panel-header>
						<v-expansion-panel-content>
							<v-row >
								<v-col cols="9">
								{{$t('datapoint name')}}
							</v-col>
							<v-col cols="1">
								{{$t('none')}}
							</v-col>
							<v-col cols="1">
								{{$t('read')}}
							</v-col>
							<v-col cols="1">
								{{$t('write')}}
							</v-col>
							</v-row>
							<hr>
							<v-row v-for="key in [1,4,5,3,9]" v-bind:key="key">
								<v-col cols="9">
									<p>datapoint {{ key }}</p>
								</v-col>
								<v-col cols="1">
									<v-radio />
								</v-col>
								<v-col cols="1">
									<v-radio />
								</v-col>
								<v-col cols="1">
									<v-radio />
								</v-col>
							</v-row>
						</v-expansion-panel-content>
					</v-expansion-panel>
				</v-expansion-panels>
				</v-col>
				<v-col cols="12" v-else>
					<v-skeleton-loader type="list-item-two-line"></v-skeleton-loader>
				</v-col>
			</v-row>
		</v-col>

		<v-dialog v-model="showRecipientDialog" max-width="300">
			<v-card v-if="entry" id="dialog-recipient-add">
				<v-form ref="form" v-model="valid">
					<v-card-title>
						{{ $t('recipientlistDetails.dialog.recipient.title') }}
					</v-card-title>
					<v-card-text>
						<v-row>
							<v-col cols="12" v-if="entry.recipientType === TYPE_USER">
								<v-select
									v-model="userRecipient"
									:items="userList"
									:rules="[
										(v) =>
											!!v || $t('recipientlistDetails.dialog.recipient.required.item'),
									]"
									item-value="id"
									item-text="username"
									return-object
									required
									dense
								></v-select>
							</v-col>
							<v-col cols="12" v-if="entry.recipientType === TYPE_MAIL">
								<v-text-field
									autofocus
									v-model="plainRecipient"
									:rules="emailRules"
									:label="$t('recipientlistDetails.dialog.recipient.label.mail')"
									required
									dense
								></v-text-field>
							</v-col>
							<v-col cols="12" v-if="entry.recipientType === TYPE_SMS">
								<v-text-field
									autofocus
									v-model="plainRecipient"
									:rules="phoneRules"
									:label="$t('recipientlistDetails.dialog.recipient.label.phone')"
									required
									dense
								></v-text-field>
							</v-col>
						</v-row>
					</v-card-text>
					<v-card-actions>
						<v-spacer></v-spacer>
						<v-btn text @click="showRecipientDialog = false">{{
							$t('common.cancel')
						}}</v-btn>
						<v-btn :disabled="!valid" text color="success" @click="addRecipient()">{{
							$t('common.ok')
						}}</v-btn>
					</v-card-actions>
				</v-form>
			</v-card>
		</v-dialog>
	</v-row>
</template>
<script>
/**
 * Recipient List Details
 *
 * Provide all required specification for that Recipient List.
 * Setup the Inactive Interval or extend the entries list with
 * additional recipients.
 *
 * @param recipientList Object - Reciepent List detailed object
 * @param edit boolean - Enabled "edit" mode
 *
 * @author Radoslaw Jajko <rjajko@softq.pl>
 * @version 1.0.0
 */
export default {
	name: 'RecipientListDetails',

	props: {
		recipientList: {
			type: Object,
		},
		edit: {
			type: Boolean,
			default: false,
		},
	},

	data() {
		return {
			TYPE_USER: 2,
			TYPE_MAIL: 3,
			TYPE_SMS: 4,
			showRecipientDialog: false,
			entry: undefined,
			userList: undefined,
			userRecipient: undefined,
			plainRecipient: '',
			mouseSelecting: false,
			loadingInactiveTime: true,
			inactiveTime: [[], [], [], [], [], [], []],
			valid: true,
			emailRules: [
				(v) =>
					/.+@.+\..+/.test(v) ||
					this.$t('recipientlistDetails.dialog.recipient.valid.mail'),
			],
			phoneRules: [
				(v) =>
					/\+?\d{1,4}?[-.\s]?\(?\d{1,3}?\)?[-.\s]?\d{1,4}[-.\s]?\d{1,4}[-.\s]?\d{1,9}/.test(
						v,
					) || this.$t('recipientlistDetails.dialog.recipient.valid.phone'),
			],
		};
	},

	mounted() {
		this.fetchUserList();
		this.clearInactiveIntervals();
		this.convertInactiveIntervals(this.recipientList.inactiveIntervals);

		window.addEventListener('mouseup', this.endSelecting);
	},

	methods: {
		async fetchUserList() {
			this.userList = await this.$store.dispatch('getAllUsers');
		},

		// Adding and deleting recipients methods
		prepareRecipientDialog(type) {
			this.showRecipientDialog = true;
			this.plainRecipient = '';
			this.entry = {
				recipientType: type,
			};
		},

		addRecipient() {
			if (this.entry.recipientType === this.TYPE_USER) {
				this.entry = {
					recipientType: this.TYPE_USER,
					referenceAddress: '',
					referenceId: this.userRecipient.id,
					user: Object.assign({}, this.userRecipient),
					userId: this.userRecipient.id,
				};
			} else if (
				this.entry.recipientType === this.TYPE_MAIL ||
				this.entry.recipientType === this.TYPE_SMS
			) {
				this.entry = {
					recipientType: this.TYPE_MAIL,
					referenceAddress: this.plainRecipient,
					referenceId: 0,
					address: this.plainRecipient,
				};
			}

			this.recipientList.entries.push(this.entry);
			this.showRecipientDialog = false;
		},

		deleteRecipient(recipient) {
			this.recipientList.entries = this.recipientList.entries.filter((e) => {
				return e !== recipient;
			});
		},

		// Inactive Period methods
		startSelecting() {
			this.mouseSelecting = true;
		},

		endSelecting() {
			this.mouseSelecting = false;
		},

		toggle(day, hour, quarter) {
			if (this.mouseSelecting) {
				this.$set(
					this.inactiveTime[day][hour],
					quarter,
					!this.inactiveTime[day][hour][quarter],
				);
			}
		},

		toggleC(day, hour, quarter) {
			this.$set(
				this.inactiveTime[day][hour],
				quarter,
				!this.inactiveTime[day][hour][quarter],
			);
		},

		/**
		 * Prepare Save
		 *
		 * While creating a new Recipient List
		 * perform the convertion from 3-dimensional inactiveTime array
		 * to 1 dimension InactiveInterval property
		 */
		preSave() {
			this.recipientList.inactiveIntervals = this.convertInactiveIntervals(
				this.inactiveTime,
			);
		},

		/**
		 * Save Recipient List
		 *
		 * After saving emit the event for parent component to handle
		 * the result of that operation
		 */
		async save() {
			this.preSave();
			let resp = await this.$store.dispatch('updateMailingList', this.recipientList);
			this.$emit('saved', resp);
			// console.log(this.convertInactiveIntervals(this.inactiveTime));
		},

		/**
		 * Clear Inactive Intervals
		 *
		 * Prepare the reactive propery for new data.
		 * Initialize the inactive time array.
		 */
		clearInactiveIntervals() {
			this.loadingInactiveTime = true;
			this.inactiveTime = [[], [], [], [], [], [], []];
			for (let d = 0; d < 7; d++) {
				this.$set(this.inactiveTime, d, new Array(24));
				for (let h = 0; h < 24; h++) {
					this.$set(this.inactiveTime[d], h, new Array(4).fill(false));
				}
			}
			this.loadingInactiveTime = false;
		},

		/**
		 * Convert Inactive Intervals
		 *
		 * Convert from Vue 3-dimensional array
		 * to inactiveInterval array from recipientList object
		 * and do the same on the other side.
		 *
		 * @param intervalArray
		 */
		convertInactiveIntervals(intervalArray) {
			if (intervalArray.length > 0) {
				if (typeof intervalArray[0] === 'number') {
					intervalArray.forEach((element) => {
						let day, hour, quarter, temp;
						day = Math.floor(element / (24 * 4));
						temp = element - day * 24 * 4;
						hour = Math.floor(temp / 4);
						quarter = temp - hour * 4;
						this.$set(this.inactiveTime[day][hour], quarter, true);
					});
					return;
				} else {
					let inactiveIntervals = [];
					let index = 0;
					for (let d = 0; d < 7; d++) {
						for (let h = 0; h < 24; h++) {
							for (let q = 0; q < 4; q++) {
								if (intervalArray[d][h][q]) {
									inactiveIntervals.push(index);
								}
								index = index + 1;
							}
						}
					}
					return inactiveIntervals;
				}
			}
		},

		/**
		 * Format Hours time range label
		 *
		 * @private
		 */
		formatHours(hour) {
			if (hour < 10) {
				return `0${hour}:00`;
			} else {
				return `${hour}:00`;
			}
		},
	},
};
</script>

<style scoped>
#section-active-time {
	user-select: none;
}
.day {
	display: flex;
	flex-direction: row;
	justify-content: space-between;
}
.hour {
	margin: 2px 5px;
}
.quarter {
	background-color: green;
	border: solid 1px black;
	margin: 1px;
	padding: 0.2px 3.8px;
}
.quarter:hover {
	background-color: cadetblue;
}
.inactive-time {
	background-color: red;
}
.heading-action-buttons {
	display: flex;
	align-items: center;
}
.small-margin {
	margin: 0 5px;
}
</style>
