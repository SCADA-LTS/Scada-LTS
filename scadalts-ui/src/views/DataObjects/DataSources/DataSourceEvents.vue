<template>
	<v-dialog v-model="dialogVisible" max-width="800">
		<template v-slot:activator="{ on, attrs }">
			<v-badge overlap color="blue" content="23">
				<v-tooltip bottom v-bind="attrs" v-on="on">
					<template v-slot:activator="{ on, attrs }">
						<v-btn
							icon
							elevation="0"
							v-bind="attrs"
							v-on="on"
							@click="dialogVisible = true"
						>
							<v-icon>mdi-bell-circle</v-icon>
						</v-btn>
					</template>
					<span>Events</span>
				</v-tooltip>
			</v-badge>
		</template>

		<v-card>
			<v-card-title> DataPoint Event List </v-card-title>
			<v-card-text>
				<v-list>
					<v-list-item v-for="e in events" :key="e.id">
						<v-list-item-icon>
							<img
								v-if="e.alarmLevel === 1 && !e.ackTs"
								:src="images/flag_blue.png"
								title="Information"
								alt="Information"
							/>
							<img
								v-if="e.alarmLevel === 1 && !!e.ackTs"
								:src="images/flag_blue_off.png"
								title="Information"
								alt="Information"
							/>
							<img
								v-if="e.alarmLevel === 2 && !e.ackTs"
								:src="images/flag_yellow.png"
								title="Urgent"
								alt="Urgent"
							/>
							<img
								v-if="e.alarmLevel === 2 && !!e.ackTs"
								:src="images/flag_yellow_off.png"
								title="Urgent"
								alt="Urgent"
							/>
							<img
								v-if="e.alarmLevel === 3 && !e.ackTs"
								:src="images/flag_orange.png"
								title="Critical"
								alt="Critical"
							/>
							<img
								v-if="e.alarmLevel === 3 && !!e.ackTs"
								:src="images/flag_orange_off.png"
								title="Critical"
								alt="Critical"
							/>
							<img
								v-if="e.alarmLevel === 4 && !e.ackTs"
								:src="images/flag_red.png"
								title="Life Safety"
								alt="Life Safety"
							/>
							<img
								v-if="e.alarmLevel === 4 && !!e.ackTs"
								:src="images/flag_red_off.png"
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
											<v-icon v-if="e.userComments.length === 0">
												mdi-comment-plus
											</v-icon>
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
				Itemss...
			</v-card-text>
		</v-card>
	</v-dialog>
</template>
<script>
export default {
	data() {
		return {
			dialogVisible: false,
			events: [
				{
					id: 1,
					alarmLevel: 1,
					ackTs: '2021-02-01',
					message: 'template|test',
					activeTs: '2021-01-01',
					username: 'Admin',
				},
				{
					id: 2,
					alarmLevel: 2,
					message: 'template|test',
					activeTs: '2021-01-01',
					username: 'Admin',
				},
			],
		};
	},

	methods: {
		addComment(e) {
			console.log(e);
		},

		deleteComment(e, comment) {
			console.log(e, comment);
		},

		ackEvent(e) {
			console.log(e);
		},

		getEventMessageType(message) {
			return message.split('|')[0];
		},

		prepareEventMessage(message) {
			return 'tester';
		},

		getEventMessageTimePeriod(message) {
			const regex = /(?!common.tp.description)common.tp.\w+/g;
			const found = message.match(regex);
			if (!!found) {
				return found[0];
			}
			return false;
		},
	},
};
</script>
<style scoped></style>

<!-- TODO: Create DataSourceEvent component that will receive the props with events array -->
