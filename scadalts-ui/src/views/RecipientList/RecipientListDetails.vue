<template>
	<v-row v-if="recipientList">
		<v-col cols="5">
			<v-row>
				<v-col cols="12">
					<h2>Mailing list details</h2>
				</v-col>
				<v-col cols="6">
					<v-text-field v-model="recipientList.xid" label="Export Id" dense>
					</v-text-field>
				</v-col>
				<v-col cols="6">
					<v-text-field v-model="recipientList.name" label="Name" dense> </v-text-field>
				</v-col>
				<v-col cols="12">
					<v-switch
						v-model="recipientList.collectInactiveEmails"
						label="Collect inactive messages"
						dense
					></v-switch>
				</v-col>
				<v-col cols="12">
					<v-text-field
						v-model="recipientList.cronPattern"
						label="Cron Pattern"
						:disabled="!recipientList.collectInactiveEmails"
						dense
					>
					</v-text-field>
				</v-col>

				<v-col cols="12" class="heading-action-buttons">
					<h3>Entries</h3>
					<v-spacer></v-spacer>
					<v-btn fab elevation="0" small @click="addUserRecipient()" class="small-margin">
						<v-icon>mdi-account-plus</v-icon>
					</v-btn>
					<v-btn fab elevation="0" small @click="addPlainRecipient()" class="small-margin">
						<v-icon>mdi-email-plus</v-icon>
					</v-btn>
                    <v-btn fab elevation="0" small @click="addPlainRecipient()" class="small-margin">
						<v-icon>mdi-phone-plus</v-icon>
					</v-btn>
				</v-col>

				<v-col cols="12">
					<v-list>
						<v-list-item v-for="entry in recipientList.entries" :key="entry">
							<v-list-item-icon>
								<v-icon v-show="entry.recipientType === 2">mdi-account </v-icon>
								<v-icon v-show="entry.recipientType === 3">mdi-email </v-icon>
							</v-list-item-icon>
							<v-list-item-content>
								<v-list-item-title>
									<span v-if="entry.recipientType === 2">
										{{ entry.user.username }}
										{{ entry.user.name }}
									</span>
									<span v-else>
										{{ entry.referenceAddress }}
									</span>
								</v-list-item-title>
								<v-list-item-subtitle v-if="entry.recipientType === 2">
									<span>
										{{ entry.user.email }}
									</span>
								</v-list-item-subtitle>
							</v-list-item-content>
							<v-list-item-action @click="deleteRecipient(entry)">
								<v-icon> mdi-minus-circle </v-icon>
							</v-list-item-action>
						</v-list-item>
					</v-list>
				</v-col>
			</v-row>
		</v-col>
		<v-col cols="7" v-if="inactiveTime" id="section-active-time">
			<v-row @mousedown="startSelecting">
				<v-col cols="12" class="heading-action-buttons">
					<h3>Active time</h3>
					<v-spacer></v-spacer>
					<v-btn fab elevation="2" color="primary" small v-if="!edit" @click="save()">
						<v-icon>mdi-content-save</v-icon>
					</v-btn>
				</v-col>
				<v-col cols="12">
					<div class="day">
						<div>Time</div>
						<div>Mon</div>
						<div>Tue</div>
						<div>Wed</div>
						<div>Thr</div>
						<div>Fri</div>
						<div>Sat</div>
						<div>Sun</div>
					</div>
					<div v-for="h in 24" :key="h" class="day">
						{{ formatHours(h) }}
						<div v-for="d in 7" :key="d" class="hour">
							<span
								v-for="m in 4"
								:key="m"
								v-bind:class="{
									'inactive-time': inactiveTime[d - 1][h - 1][m - 1],
								}"
								class="quarter"
								@mouseover="toggle(d - 1, h - 1, m - 1)"
								@click="toggleC(d - 1, h - 1, m - 1)"
							>
							</span>
						</div>
					</div>
				</v-col>
			</v-row>
		</v-col>
		<v-dialog v-model="showRecipientDialog" max-width="300">
			<v-card v-if="entry">
				<v-card-title> Add recipient </v-card-title>
				<v-card-text>
					<v-row>
						<v-col cols="12" v-if="entry.recipientType === 2">
							<v-select
								v-model="userRecipient"
								:items="userList"
								item-value="id"
								item-text="name"
								dense
							></v-select>
						</v-col>
						<v-col cols="12" v-if="entry.recipientType === 3">
							<v-text-field autofocus v-model="plainRecipient" label="Add address" dense>
							</v-text-field>
						</v-col>
					</v-row>
				</v-card-text>
				<v-card-actions>
					<v-spacer></v-spacer>
					<v-btn text @click="showRecipientDialog = false">{{
						$t('common.cancel')
					}}</v-btn>
					<v-btn text color="success" @click="addRecipient()">{{
						$t('common.ok')
					}}</v-btn>
				</v-card-actions>
			</v-card>
		</v-dialog>
	</v-row>
</template>
<script>
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
			showRecipientDialog: false,
			entry: undefined,
			userList: undefined,
			userRecipient: undefined,
			plainRecipient: '',
			mouseSelecting: false,
			inactiveTime: [[], [], [], [], [], [], []],
		};
	},

	mounted() {
		this.fetchUserList();
		this.clearInactiveIntervals();

		window.addEventListener('mouseup', this.endSelecting);
	},

	methods: {
		async fetchUserList() {
			this.userList = await this.$store.dispatch('getAllUsers');
			console.log(this.userList);
		},

		addPlainRecipient() {
			this.showRecipientDialog = true;
			this.entry = {
				recipientType: 3,
			};
			console.debug('Adding mail/sms recipient');
		},

		addUserRecipient() {
			this.showRecipientDialog = true;
			this.entry = {
				recipientType: 2,
			};
			console.debug('Adding user recipient');
		},

		addRecipient() {
			if (this.entry.recipientType === 2) {
				this.entry = {
					recipientType: 2,
					referenceAddress: '',
					referenceId: this.userRecipient.id,
					user: Object.assign({}, this.userRecipient),
					userId: this.userRecipient.id,
				};
			} else if (this.entry.recipientType === 3) {
				this.entry = {
					recipientType: 3,
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

		startSelecting() {
			this.mouseSelecting = true;
		},

		endSelecting() {
			this.mouseSelecting = false;
		},

		changeActiveRL() {
			this.clearInactiveIntervals();
			this.convertInactiveIntervals(this.recipientList.inactiveIntervals);
		},

		toggle(day, hour, quarter) {
			if (this.mouseSelecting) {
				this.$set(
					this.inactiveTime[day][hour],
					quarter,
					!this.inactiveTime[day][hour][quarter]
				);
			}
		},

		toggleC(day, hour, quarter) {
			this.$set(
				this.inactiveTime[day][hour],
				quarter,
				!this.inactiveTime[day][hour][quarter]
			);
		},

		clearInactiveIntervals() {
			this.inactiveTime = [[], [], [], [], [], [], []];
			for (let d = 0; d < 7; d++) {
				this.$set(this.inactiveTime, d, new Array(24));
				for (let h = 0; h < 24; h++) {
					this.$set(this.inactiveTime[d], h, new Array(4).fill(false));
				}
			}
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

		save() {
			this.recipientList.inactiveIntervals = this.convertInactiveIntervals(
				this.inactiveTime
			);
			console.log(this.recipientList);
			this.$store.dispatch('updateMailingList', this.recipientList);
			// console.log(this.convertInactiveIntervals(this.inactiveTime));
		},

		/**
		 * Format Hours time range label
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

	//emit('saved')
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
/* .active-time {
	
} */
</style>
