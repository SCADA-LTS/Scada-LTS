<template>
	<div>
		<v-container fluid>
			<h1>Recipient List</h1>
		</v-container>
		<v-container fluid>
			<v-card class="slts-card">
				<v-row>
					<v-col cols="4" xs="12">
						<v-list v-if="mailingListLoaded">
							<v-list-item
								v-for="item in mailingLists"
								:key="item.id"
								@click="changeActiveML(item)"
							>
								<v-list-item-content>
									<v-list-item-title>
										{{ item.name }}
									</v-list-item-title>
								</v-list-item-content>
							</v-list-item>
							<v-list-item>
								<v-list-item-content>
									<v-list-item-title>
										<v-fab-transition>
											<v-btn color="primary" dark absolute bottom right x-small fab>
												<v-icon> mdi-plus </v-icon>
											</v-btn>
										</v-fab-transition>
										Create new mailing list
									</v-list-item-title>
								</v-list-item-content>
							</v-list-item>
						</v-list>
						<v-skeleton-loader v-else type="list-item-two-line"></v-skeleton-loader>
					</v-col>
					<v-divider vertical class="divider-horizontal-margin"></v-divider>
					<v-col cols="7" xs="12">
						<v-row v-if="activeMailingList">
							<v-col cols="5">
								<v-row>
									<v-col cols="12">
										<h2>Mailing list details</h2>
									</v-col>
									<v-col cols="6">
										<v-text-field v-model="activeMailingList.xid" label="Export Id" dense>
										</v-text-field>
									</v-col>
									<v-col cols="6">
										<v-text-field v-model="activeMailingList.name" label="Name" dense>
										</v-text-field>
									</v-col>
                                    <v-col cols="12">
                                        <span>Collect inactive messages</span>
                                    </v-col>
									<v-col cols="6">
										<v-switch
											v-model="activeMailingList.collectInactiveEmails"
										></v-switch>
									</v-col>
									<v-col cols="6">
										<v-text-field
											v-model="activeMailingList.cronPattern"
											label="Cron Pattern"
                                            :disabled="!activeMailingList.collectInactiveEmails"
											dense
										>
										</v-text-field>
									</v-col>

									<v-col cols="12">
										<h3>Entries</h3>
									</v-col>
									<v-col cols="12">
										<v-select
											:items="userList"
											item-value="id"
											item-text="username"
											append-outer-icon="mdi-plus"
											@click:append-outer="addUserRecipient"
											dense
										></v-select>
									</v-col>
									<v-col cols="12">
										<v-text-field
											label="Add address"
											append-outer-icon="mdi-plus"
											@click:append-outer="addMailRecipient"
											dense
										>
										</v-text-field>
									</v-col>
									<v-col cols="12">
										<v-list>
											<v-list-item
												v-for="entry in activeMailingList.entries"
												:key="entry"
											>
												<v-list-item-icon>
													<v-icon>mdi-account </v-icon>
												</v-list-item-icon>
												<v-list-item-content>
													<v-list-item-title>
														Username {{ entry.recipientType }}
													</v-list-item-title>
												</v-list-item-content>
												<v-list-item-action>
													<v-icon> mdi-minus-circle </v-icon>
												</v-list-item-action>
											</v-list-item>
										</v-list>
									</v-col>
								</v-row>
							</v-col>
							<v-col cols="7" v-if="inactiveTime" id="section-active-time">
								<v-row @mousedown="startSelecting">
									<v-col cols="12">
										<h3>Active time</h3>
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
						</v-row>
						<v-row v-else>
							<v-col cols="12">
								<h3>Select mailing list to see the preview.</h3>
							</v-col>
						</v-row>
					</v-col>
				</v-row>
			</v-card>
		</v-container>
	</div>
</template>
<script>
export default {
	name: 'RecipientList',

	data() {
		return {
			mailingLists: undefined,
			userList: undefined,
			activeMailingList: undefined,
			mailingListLoaded: false,
			mouseSelecting: false,
			inactiveTime: [[], [], [], [], [], [], []],
		};
	},

	props: ['editable'],

	mounted() {
		this.fetchMailingLists();
		this.fetchUserList();

		for (let d = 0; d < 7; d++) {
			this.$set(this.inactiveTime, d, new Array(24));
			for (let h = 0; h < 24; h++) {
				this.$set(this.inactiveTime[d], h, new Array(4).fill(false));
			}
		}

		window.addEventListener('mouseup', this.endSelecting);
	},

	methods: {
		async fetchMailingLists() {
			this.mailingListLoaded = false;
			this.mailingLists = await this.$store.dispatch('getSimpleMailingLists');
			console.log(this.mailingLists);
			this.mailingListLoaded = true;
		},
		async fetchUserList() {
			this.userList = await this.$store.dispatch('getAllUsers');
		},

		async changeActiveML(item) {
			this.activeMailingList = await this.$store.dispatch('getMailingList', item.id);
		},

		addMailRecipient() {
			console.debug('Adding mail/sms recipient');
		},

		addUserRecipient() {
			console.debug('Adding user recipient');
		},

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
};
</script>
<style scoped>
.slts-card {
	min-height: 72vh;
}
.divider-horizontal-margin {
	margin: 0 3.5%;
	min-height: 72vh;
}
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
/* .active-time {
	
} */
</style>
