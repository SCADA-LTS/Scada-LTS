<template>
	<v-dialog max-width="500" persistent v-model="dialog">
		<template v-if="btnvisible" v-slot:activator="{ on, attrs }">
			<v-btn v-bind="attrs" v-on="on"> Open </v-btn>
		</template>
		<v-card class="dialog-confirmation">
			<v-card-title class="headline">
				<span v-html="title"></span>
			</v-card-title>
			<v-card-text>
				<span v-html="message"></span>
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
	data() {
		return {
            dialog: false,
            data: null,
        };
	},

	props: {
		btnvisible: {
			type: Boolean,
			default: false,
		},
		title: {
			type: String,
			default: 'Example title',
		},
		message: {
			type: String,
			default: 'Example inner text to be inside dialog box...',
		},
	},

	methods: {
        openDialog(data = null) {
            this.dialog = true;
            this.data = data;
        },

		cancel() {
			this.dialog = false;
			this.$emit('result', {result: false, data: null});
		},

		accept() {
			this.dialog = false;
			this.$emit('result', {result: true, data: this.data});
		},
	},
};
</script>
<style scoped></style>
