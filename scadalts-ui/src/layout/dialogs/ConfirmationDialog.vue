<template>
	<v-dialog max-width="500" persistent v-model="dialog">
		<template v-if="btnvisible" v-slot:activator="{ on, attrs }">
			<v-btn v-bind="attrs" v-on="on"> Open </v-btn>
		</template>
		<v-card class="dialog-confirmation">
			<v-card-title class="headline">
				{{ title }}
			</v-card-title>
			<v-card-text>
				{{ message }}
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
	name: 'ConfirmationDialog',

	data() {
		return {
			dialog: false,
		};
	},

	props: {
		btnvisible: {
			type: Boolean,
			default: true,
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
		showDialog() {
			this.dialog = true;
		},

		cancel() {
			this.dialog = false;
			this.$emit('result', false);
		},

		accept() {
			this.dialog = false;
			this.$emit('result', true);
		},
	},
};
</script>
<style scoped></style>
