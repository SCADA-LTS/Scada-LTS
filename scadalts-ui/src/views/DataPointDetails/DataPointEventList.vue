<template>
	<v-card>
		<v-card-title v-if="hideSkeleton">
			<v-row>
				<v-col cols="8"> {{ $t('datapointDetails.eventList.title') }} </v-col>
				<v-col cols="4">
					<v-text-field
						v-model="eventLimit"
						:label="$t('datapointDetails.eventList.controls.latest')"
					>
						<template v-slot:append-outer>
							<v-btn
								icon
								fab
								x-small
								@click="fetchDataPointEvents"
								:loading="fetchingData"
								:disabled="fetchingData"
							>
								<v-icon>mdi-autorenew</v-icon>
							</v-btn>
						</template>
					</v-text-field>
				</v-col>
			</v-row>
		</v-card-title>
		<v-card-text v-if="hideSkeleton">
			<v-list v-if="events.length > 0">
				<v-list-item v-for="e in events" :key="e.id">
					<v-list-item-icon>
						<img
							v-if="e.alarmLevel === 1 && !e.ackTs"
							src="images/flag_blue.png"
							title="Information"
							alt="Information"
						/>
						<img
							v-if="e.alarmLevel === 1 && !!e.ackTs"
							src="images/flag_blue_off.png"
							title="Information"
							alt="Information"
						/>
						<img
							v-if="e.alarmLevel === 2 && !e.ackTs"
							src="images/flag_yellow.png"
							title="Urgent"
							alt="Urgent"
						/>
						<img
							v-if="e.alarmLevel === 2 && !!e.ackTs"
							src="images/flag_yellow_off.png"
							title="Urgent"
							alt="Urgent"
						/>
						<img
							v-if="e.alarmLevel === 3 && !e.ackTs"
							src="images/flag_orange.png"
							title="Critical"
							alt="Critical"
						/>
						<img
							v-if="e.alarmLevel === 3 && !!e.ackTs"
							src="images/flag_orange_off.png"
							title="Critical"
							alt="Critical"
						/>
						<img
							v-if="e.alarmLevel === 4 && !e.ackTs"
							src="images/flag_red.png"
							title="Life Safety"
							alt="Life Safety"
						/>
						<img
							v-if="e.alarmLevel === 4 && !!e.ackTs"
							src="images/flag_red_off.png"
							title="Life Safety"
							alt="Life Safety"
						/>
					</v-list-item-icon>
					<v-list-item-content>
						<v-list-item-title>
							<span>
								{{ $t(getEventMessageType(e.message), prepareEventMessage(e.message)) }}
							</span>
							<span v-if="getEventMessageTimePeriod(e.message)">
								{{ $t(getEventMessageTimePeriod(e.message)) }}
							</span>
						</v-list-item-title>
						<v-list-item-subtitle>
							<span>
								{{ new Date(e.activeTs).toLocaleString() }}
							</span>
							<span v-if="!!e.ackTs">
								- {{ $t('datapointDetails.eventList.acknowledged') }}
								{{ new Date(e.ackTs).toLocaleString() }} by
								{{ e.username }}
							</span>
						</v-list-item-subtitle>
					</v-list-item-content>
					<v-list-item-action>
						<v-menu :close-on-content-click="false" offset-y>
							<template v-slot:activator="{ on, attrs }">
								<v-badge
									overlap
									color="error"
									:value="e.userComments.length > 0"
									:content="e.userComments.length"
								>
									<v-btn icon v-bind="attrs" v-on="on" @click="refresh">
										<v-icon v-if="e.userComments.length === 0"> mdi-comment-plus </v-icon>
										<v-icon v-else>mdi-comment</v-icon>
									</v-btn>
								</v-badge>
							</template>
							<v-card>
								<v-list>
									<v-list-item v-for="comment in e.userComments" :key="comment">
										<v-list-item-icon>
											<v-icon>mdi-message</v-icon>
										</v-list-item-icon>
										<v-list-item-content>
											<v-list-item-title>
												{{ comment.comment }}
											</v-list-item-title>
											<v-list-item-subtitle>
												{{ comment.username }},
												{{ new Date(comment.ts).toLocaleString() }}
											</v-list-item-subtitle>
										</v-list-item-content>
										<v-list-item-action
											v-if="comment.userId === activeUserId"
											@click="deleteComment(e, comment)"
										>
											<v-icon> mdi-minus-circle </v-icon>
										</v-list-item-action>
									</v-list-item>
									<v-list-item>
										<v-list-item-icon>
											<v-icon>mdi-message-reply-text</v-icon>
										</v-list-item-icon>
										<v-list-item-content>
											<v-list-item-title>
												<v-text-field
													v-model="newComment"
													:label="$t('comment.add')"
													append-icon="mdi-check"
													@click:append="addComment(e)"
													dense
												></v-text-field>
											</v-list-item-title>
										</v-list-item-content>
									</v-list-item>
								</v-list>
							</v-card>
						</v-menu>
						<v-btn v-if="!e.ackTs" icon @click="ackEvent(e)">
							<v-icon> mdi-check </v-icon>
						</v-btn>
					</v-list-item-action>
				</v-list-item>
			</v-list>

			<v-list v-else>
				<v-list-item>
					<v-list-item-content>
						<v-list-item-title>
							{{ $t('datapointDetails.eventList.empty') }}
						</v-list-item-title>
					</v-list-item-content>
				</v-list-item>
			</v-list>
		</v-card-text>
		<v-skeleton-loader v-else type="article"></v-skeleton-loader>
	</v-card>
</template>
<script>
import { initWebSocket } from '@/web-socket.js'
/**
 * Event List for Data Point
 * 
 * Display events that are related to specific data point.
 * Allow user to acknowlede them and to browse the historical events.
 * Using Web-Sockets user is informed about all changes without polling.
 *
 * @param {number} datapointId - Point Detail Id
 *
 * @author Radoslaw Jajko <rjajko@softq.pl>
 * @version 1.1
 */
export default {
	name: 'DataPointEventList',

	props: ['datapointId'],

	data() {
		return {
			events: undefined,
			eventLimit: 3,
			fetchingData: false,
			commentVisible: false,
			activeUserId: -1,
			newComment: '',
			hideSkeleton: false,
			stompClient: undefined,
			socket: undefined,
		};
	},

	mounted() {
		this.refresh();
		this.fetchDataPointEvents();
		this.hideSkeleton = true;
		this.connect();
	},

	beforeDestroy() {
		this.disconnect();
	},

	methods: {
		connect() {
			let callback = () => {
				this.stompClient.subscribe("/topic/alarm", tick => {
					if(tick.body === "Event Raised") {
						this.fetchDataPointEvents();
					}
				});
			}

			this.stompClient = initWebSocket(
				this.$store.state.webSocketUrl,
				callback,
			);
		},

		disconnect() {
			if(!!this.stompClient) {
				this.stompClient.disconnect();
			}
		},

		refresh() {
			if (!!this.$store.state.loggedUser) {
				this.activeUserId = this.$store.state.loggedUser.id;
			}
		},

		async fetchDataPointEvents() {
			if (!!this.datapointId) {
				this.fetchingData = true;
				this.events = await this.$store.dispatch('fetchDataPointEvents', {
					datapointId: this.datapointId,
					limit: this.eventLimit,
				});
				this.fetchingData = false;
			}
		},

		addComment(e) {
			let time = new Date();
			let comment = {
				userId: this.$store.state.loggedUser.id,
				ts: time.getTime(),
				comment: this.newComment,
				username: this.$store.state.loggedUser.username,
				prettyTime: time.toLocaleTimeString(),
			};
			e.userComments.push(Object.assign({}, comment));
			this.$store.dispatch('addUserComment', {
				comment: comment,
				typeId: 1,
				refId: e.id,
			});
			this.newComment = '';
		},

		deleteComment(e, comment) {
			this.$store.dispatch('delUserComment', {
				typeId: 1,
				refId: e.id,
				userId: this.$store.state.loggedUser.id,
				ts: comment.ts,
			});
			e.userComments = e.userComments.filter((el) => {
				return el.comment !== comment.comment;
			});
		},

		ackEvent(e) {
			this.$store
				.dispatch('ackEvent', e.id)
				.then(() => {
					this.fetchDataPointEvents(this.datapointId);
				})
				.catch(() => {
					console.error('Not Acknowledged!');
				});
		},

		getEventMessageType(message) {
			return message.split('|')[0]
		},

		prepareEventMessage(message) {
			let response = message.replace(/[\[\]]/g,"");
			response = response.split('|');
			return response.slice(1);
		},

		getEventMessageTimePeriod(message) {
			const regex = /(?!common.tp.description)common.tp.\w+/g;
			const found = message.match(regex);
			if(!!found) {
				return found[0];
			}
			return false
		},

		
	},
};
</script>
<style scoped></style>
