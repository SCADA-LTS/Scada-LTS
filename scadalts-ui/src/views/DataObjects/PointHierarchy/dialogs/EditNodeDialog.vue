<template>
	<v-dialog max-width="500" persistent v-model="dialog">
		<v-card class="dialog-point-hierarchy-create">
			<v-card-title class="headline">
				{{ title }}
			</v-card-title>
			<v-card-text>
				<p v-if="!!message">{{ message }}</p>
				<v-text-field v-model="nodeName" label="Name"></v-text-field>
			</v-card-text>
			<v-card-actions>
				<v-spacer></v-spacer>
				<v-btn text @click="cancel">
					{{ $t('common.cancel') }}
				</v-btn>
				<v-btn text color="success" @click="accept">
					{{ $t('common.ok') }}
				</v-btn>
			</v-card-actions>
		</v-card>
	</v-dialog>
</template>
<script>
export default {
	name: 'EditNodeDialog',

	data() {
		return {
			dialog: false,
			nodeName: '',
            node: null,
		};
	},

	props: {
		title: {
			type: String,
			default: 'Example title',
		},
		message: {
			type: String,
			default: null,
		},
	},

	methods: {
		showDialog(node) {
			this.dialog = true;
            if(!!node) {
                this.nodeName = node.name;
                this.node = node;
            }
		},

		cancel() {
			this.dialog = false;
			this.$emit('result');
		},

		accept() {
			this.dialog = false;
            if(!!this.node) {
                this.node.name = this.nodeName;
                this.$emit('result', this.node);
            } else {
                this.$emit('result', this.nodeName);
            }
		},
	},
};
</script>
<style scoped></style>
