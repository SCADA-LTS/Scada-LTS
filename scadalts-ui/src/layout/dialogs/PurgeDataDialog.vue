<template>
	<v-dialog max-width="600" v-model="dialog">
		<ConfirmationDialog
			:btnvisible="false"
			ref="purgeDialogConfrim"
			@result="purgeDialogResult"
			:title="$t('purge.dialog.confirm.title')"
			:message="$t('purge.dialog.confirm.text')"
		></ConfirmationDialog>
		<v-card>
			<v-card-title class="headline">
				<v-row align="center">
					<v-col cols="8">
						<span>{{ $t('purge.dialog.title') }} </span>
					</v-col>
					<v-col cols="4">
						<v-select
							dense
							:label="$t('purge.dialog.strategy')"
							v-model="selectedPurge"
							:items="purgeStrategies"
						></v-select>
					</v-col>
				</v-row>
			</v-card-title>
			<v-card-text>
				<v-row align="center" v-if="selectedPurge === 1">
					<v-col cols="6">
						<v-text-field
							v-model="purgePeriod"
							:label="$t('purge.dialog.older')"
							dense
						></v-text-field>
					</v-col>
					<v-col cols="6">
						<v-select
							v-model="purgeType"
							:items="timePeriods"
							item-value="id"
							item-text="label"
							dense
						></v-select>
					</v-col>
				</v-row>
				<v-row align="center" v-if="selectedPurge === 2">
					<v-col cols="12">
						<v-text-field
							v-model="valuesLimit"
							:label="$t('purge.dialog.values')"
							dense
						></v-text-field>
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
			purgeAll: false,
			purgeType: 2,
			purgePeriod: 1,
			valuesLimit: 100,
			selectedPurge: 0,
			purgeStrategies: [
				{
					text: 'Period',
					value: 1,
				},
				{
					text: 'Values limit',
					value: 2,
				},
				{
					text: 'All',
					value: 3,
				},
			],
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

		async purgeDialogResult(e) {
			if (e) {
				let response;
				try {
					if(this.selectedPurge === 1) {
						response = await this.$store.dispatch('purgeNowPeriod', {
							datapointId: this.data.id,
							type: this.purgeType,
							period: this.purgePeriod,
						});
					} else if (this.selectedPurge === 2) {
						response = await this.$store.dispatch('purgeNowLimit', {
							datapointId: this.data.id,
							limit: this.valuesLimit,
						});
					} else {
						response = await this.$store.dispatch('purgeNowAll', this.data.id);
					}
					this.$emit('result', true);
				} catch(error) {
					console.error(error);
					this.$emit('result', false);
				} finally {
					this.dialog = false;
				}
			}
		},

		accept() {
			this.$refs.purgeDialogConfrim.showDialog();
		},
	},
};
</script>

<template>
	<input
		:value="dialog"
		@input="$emit('update:dialog', $event.target.value)"
	/>
</template>

