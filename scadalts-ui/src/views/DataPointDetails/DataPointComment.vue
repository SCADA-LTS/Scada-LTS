<template>
	<v-menu v-model="componentVisible" :close-on-content-click="false" offset-y>
		<template v-slot:activator="{ on, attrs }">
			<v-badge
				overlap
				color="error"
				:value="data.comments.length > 0"
				:content="data.comments.length"
			>
				<v-btn icon fab dark color="primary" v-bind="attrs" v-on="on">
					<v-icon>mdi-message-alert</v-icon>
				</v-btn>
			</v-badge>
		</template>
		<v-card>
			<v-list>
				<v-list-item v-for="comment in data.comments" :key="comment">
					<v-list-item-icon>
						<v-icon>mdi-message</v-icon>
					</v-list-item-icon>
					<v-list-item-content>
						<v-list-item-title
							>{{ comment.username }}, {{ comment.prettyTime }}</v-list-item-title
						>
						<v-list-item-subtitle>{{ comment.comment }}</v-list-item-subtitle>
					</v-list-item-content>
				</v-list-item>
				<v-list-item>
					<v-list-item-icon>
						<v-icon>mdi-message-reply-text</v-icon>
					</v-list-item-icon>
					<v-list-item-content>
						<v-list-item-title>
							<v-text-field
								v-model="newComment"
								label="Add comment..."
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
export default {
    name: 'DataPointComment',

    props: ['data'],

    data() {
        return {
            componentVisible: false,
            newComment: '',
        }
    },

    methods: {
        addComment() {
            let time = new Date();
            let comment = {
                userId: this.$store.state.loggedUser.id,
                ts: time.getTime(),
                comment: this.newComment,
                username: this.$store.state.loggedUser.username,
                prettyTime: time.toLocaleTimeString()
            }
            this.data.comments.push(Object.assign({}, comment));
            this.newComment = '';
        }
    }
};
</script>
<style scoped></style>
