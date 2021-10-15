<template>
	<v-list-item>
		<v-list-item-icon>
			<img
				v-if="event.alarmLevel === 1 && !event.ackTs"
				src="images/flag_blue.png"
				title="Information"
				alt="Information"
			/>
			<img
				v-if="event.alarmLevel === 1 && !!event.ackTs"
				src="images/flag_blue_off.png"
				title="Information"
				alt="Information"
			/>
			<img
				v-if="event.alarmLevel === 2 && !event.ackTs"
				src="images/flag_yellow.png"
				title="Urgent"
				alt="Urgent"
			/>
			<img
				v-if="event.alarmLevel === 2 && !!event.ackTs"
				src="images/flag_yellow_off.png"
				title="Urgent"
				alt="Urgent"
			/>
			<img
				v-if="event.alarmLevel === 3 && !event.ackTs"
				src="images/flag_orange.png"
				title="Critical"
				alt="Critical"
			/>
			<img
				v-if="event.alarmLevel === 3 && !!event.ackTs"
				src="images/flag_orange_off.png"
				title="Critical"
				alt="Critical"
			/>
			<img
				v-if="event.alarmLevel === 4 && !event.ackTs"
				src="images/flag_red.png"
				title="Life Safety"
				alt="Life Safety"
			/>
			<img
				v-if="event.alarmLevel === 4 && !!event.ackTs"
				src="images/flag_red_off.png"
				title="Life Safety"
				alt="Life Safety"
			/>
		</v-list-item-icon>
		
        <v-list-item-content>
			<v-list-item-title>
				<span>
					{{ $t(getEventMessageType(event.message), prepareEventMessage(event.message)) }}
				</span>
				<span v-if="getEventMessageTimePeriod(event.message)">
					{{ $t(getEventMessageTimePeriod(event.message)) }}
				</span>
			</v-list-item-title>
			<v-list-item-subtitle>
				<span>
					{{ new Date(event.activeTs).toLocaleString() }}
				</span>
				<span v-if="!!event.ackTs">
					- {{ $t('datapointDetails.eventList.acknowledged') }}
					{{ new Date(event.ackTs).toLocaleString() }} by
					{{ event.username }}
				</span>
			</v-list-item-subtitle>
		</v-list-item-content>

		<v-list-item-action>
			<v-menu :close-on-content-click="false" offset-y>
				<template v-slot:activator="{ on, attrs }">
					<v-badge
						overlap
						color="error"
						:value="event.userComments.length > 0"
						:content="event.userComments.length"
					>
						<v-btn icon v-bind="attrs" v-on="on" @click="refresh">
							<v-icon v-if="event.userComments.length === 0"> mdi-comment-plus </v-icon>
							<v-icon v-else>mdi-comment</v-icon>
						</v-btn>
					</v-badge>
				</template>
				<v-card>
					<v-list>
						<v-list-item v-for="comment in event.userComments" :key="comment">
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
								@click="deleteComment(event, comment)"
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
										@click:append="addComment(event)"
										dense
									></v-text-field>
								</v-list-item-title>
							</v-list-item-content>
						</v-list-item>
					</v-list>
				</v-card>
			</v-menu>
			<v-btn v-if="!event.ackTs" icon @click="ackEvent(event)">
				<v-icon> mdi-check </v-icon>
			</v-btn>
		</v-list-item-action>
	</v-list-item>
</template>
<script>
export default {
	name: 'EventScadaItem',

	props: {
		event: {
			type: Object,
			required: true,
		},
		pointId: {
			type: Number,
			required: true,
		},
	},

	data() {
		return {
            activeUserId: -1,
            newComment: '',
        };
	},

	methods: {
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
			this.$store.dispatch('ackEvent', e.id)
				.then(() => {
					this.$emit('acknowledge', e);
				})
				.catch(() => {
					console.error('Not Acknowledged!');
				});
		},

		getEventMessageType(message) {
			return message.split('|')[0];
		},

		prepareEventMessage(message) {
			let response = message.replace(/[\[\]]/g, '');
			response = response.split('|');
			return response.slice(1);
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
<style></style>
