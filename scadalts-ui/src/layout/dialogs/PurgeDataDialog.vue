<template>
	<v-dialog max-width="600" v-model="dialog">
		<ConfirmationDialog
			:btnvisible="false"
			:dialog="confirmPurgeDialog"
			@result="purgeDialogResult"
			:title="$t('purge.dialog.confirm.title')"
			:message="$t('purge.dialog.confirm.text')"
		></ConfirmationDialog>
		<v-card>
			<v-card-title class="headline"> {{ $t('purge.dialog.title') }} </v-card-title>
			<v-card-text>
				<v-row align="center">
					<v-col cols="4">
						<v-text-field
							v-model="purgePeriod"
							:label="$t('purge.dialog.older')"
							:disabled="purgeAll"
							dense
						></v-text-field>
					</v-col>
					<v-col cols="3">
						<v-select
							v-model="purgeType"
							:items="timePeriods"
							item-value="id"
							item-text="label"
							:disabled="purgeAll"
							dense
						></v-select>
					</v-col>
					<v-col cols="5">
						<v-switch v-model="purgeAll" :label="$t('purge.dialog.all')"></v-switch>
					</v-col>
				</v-row>
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
import ConfirmationDialog from '@/layout/dialogs/ConfirmationDialog';
export default {
	name: 'PurgeDataDialog',

	components: {
		ConfirmationDialog,
	},

	data() {
		return {
			confirmPurgeDialog: false,
			purgeAll: false,
			purgeType: 2,
			purgePeriod: 1,
		};
	},

	computed: {
		timePeriods() {
			return this.$store.state.timePeriods.filter((e) => {
				return !(e.id === 1 || e.id === 8);
			});
		},
	},

	props: {
		data: {
			type: Object,
			default: null,
		},

		dialog: {
			type: Boolean,
			default: false,
		},
	},

	methods: {
		cancel() {
			this.dialog = false;
			this.$emit('result', false);
		},

		purgeDialogResult(e) {
			this.confirmPurgeDialog = false;
			if (e) {
				this.$store
					.dispatch('purgeDataPointValues', {
						datapointId: this.data.id,
						period: this.purgePeriod,
						type: this.purgeType,
						allData: this.purgeAll,
					})
					.then((resp) => {
						if (resp.deleted) {
							this.$emit('result', true);
						} else {
							this.$emit('result', false);
						}
						this.dialog = false;
					});
			}
		},

		accept() {
			this.confirmPurgeDialog = true;
		},
	},
};
</script>
<style scoped></style>
