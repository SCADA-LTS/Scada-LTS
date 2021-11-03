<template>
	<v-dialog max-width="500" persistent v-model="dialog">
		<v-card class="dialog-point-hierarchy-create">
			<v-card-title class="headline">
				{{ title }}
			</v-card-title>
			<v-card-text>
                <v-text-field
                    v-model="nodeName"
                    label="Name"
                ></v-text-field>
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
	name: 'CreateNodeDialog',

	data() {
		return {
            dialog: false,
            nodeName: '',
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
	},

	methods: {
        showDialog() {
            this.dialog = true;
        },
        
		cancel() {
			this.dialog = false;
			this.$emit('result');
		},

		accept() {
			this.dialog = false;
			this.$emit('result', this.nodeName);
		},
	},
};
</script>
<style scoped></style>
