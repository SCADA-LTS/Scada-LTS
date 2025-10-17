<template>
	<v-menu v-model="componentVisible" :close-on-content-click="false" offset-y>
		<template v-slot:activator="{ on, attrs }">
			<v-badge
				overlap
				color="error"
				:value="data.comments.length > 0"
				:content="data.comments.length"
			>
				<v-btn icon fab dark color="primary" v-bind="attrs" v-on="on" @click="refresh">
					<v-icon>mdi-message-alert</v-icon>
				</v-btn>
			</v-badge>
		</template>
		<v-card id="menu-data-point-comment">
			<v-list>
				<v-list-item v-for="comment in data.comments" :key="comment">
					<v-list-item-icon>
						<v-icon>mdi-message</v-icon>
					</v-list-item-icon>
					<v-list-item-content>
						<v-list-item-title>
							<span v-html="comment.comment"></span>
						</v-list-item-title>
						<v-list-item-subtitle>
							<span v-html="comment.username"></span>,
							{{ new Date(comment.ts).toLocaleString() }}
						</v-list-item-subtitle>
					</v-list-item-content>
					<v-list-item-action
						v-if="comment.userId === activeUserId"
						@click="deleteComment(comment)"
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
								@click:append="addComment"
								dense
							></v-text-field>
						</v-list-item-title>
					</v-list-item-content>
				</v-list-item>
			</v-list>
		</v-card>
	</v-menu>
</template>
<script>
/**
 * Data Point User Comment
 *
 * Component that provide the comment feature for Data Point Details page.
 * User can add, delete and browse the comments that are related to specific DP.
 *
 * @param {Object[]} data - Point Details object with data.
 *
 * @author Radoslaw Jajko <rjajko@softq.pl>
 * @version 1.0
 */
export default {
	name: 'DataPointComment',

	props: ['data'],

	data() {
		return {
			componentVisible: false,
			activeUserId: -1,
			newComment: '',
		};
	},

	mounted() {
		this.refresh();
	},

	methods: {
		refresh() {
			this.activeUserId = this.$store.state.loggedUser.id;
		},

		async addComment() {
			let time = new Date();
			let comment = {
				userId: this.$store.state.loggedUser.id,
				ts: time.getTime(),
				comment: this.newComment,
				username: this.$store.state.loggedUser.username,
				prettyTime: time.toLocaleTimeString(),
			};
			let response = await this.$store.dispatch('addUserComment', {
				comment: comment,
				typeId: 2,
				refId: this.data.id,
			});
			this.newComment = '';
			this.data.comments.push(Object.assign({}, response));
		},

		deleteComment(e) {
			this.$store.dispatch('delUserComment', {
				typeId: 2,
				refId: this.data.id,
				userId: this.$store.state.loggedUser.id,
				ts: e.ts,
			});
			this.data.comments = this.data.comments.filter((el) => {
				return el.comment !== e.comment;
			});
		},
	},
};
</script>
<style scoped></style>
