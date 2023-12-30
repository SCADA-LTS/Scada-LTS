<template>
	<v-dialog v-model="dialog" width="1200">
		<ConfirmationDialog
			:btnvisible="false"
			ref="toggleDialogConfirm"
			@result="toggleDataPointDialog"
			:title="$t('datapointDetails.pointProperties.toggle.dialog.title')"
			:message="$t('datapointDetails.pointProperties.toggle.dialog.text')"
		></ConfirmationDialog>
		<template v-slot:activator="{ on, attrs }">
			<v-btn elevation="2" fab v-bind="attrs" v-on="on">
				<v-icon>mdi-pencil</v-icon>
			</v-btn>
		</template>

		<v-card id="dialog-point-properties">
			<v-card-title></v-card-title>
			<v-card-text class="point-properties-box">
				<v-row>
					<v-col cols="6" xs="12">
						<v-row>
							<v-col cols="12">
								<v-row justify="space-between" align="center">
									<v-col>
										<h3>
											<v-btn
												x-small
												fab
												elevation="1"
												@click="toggleDataPoint"
												:color="data.enabled ? 'primary' : 'error'"
											>
												<v-icon v-show="data.enabled">mdi-decagram</v-icon>
												<v-icon v-show="!data.enabled">mdi-decagram-outline</v-icon>
											</v-btn>
											{{ $t('datapointDetails.pointProperties.title') }}
										</h3>
									</v-col>
									<PurgeDataDialog
										:data="data"
										:dialog="purgeDialog"
										@result="purgeDataResult"
									></PurgeDataDialog>
									<v-col class="row justify-end">
										<v-menu bottom offset-y>
											<template v-slot:activator="{ on, attrs }">
												<v-btn icon v-bind="attrs" v-on="on">
													<v-icon>mdi-dots-vertical</v-icon>
												</v-btn>
											</template>

											<v-list>
												<v-list-item @click="purgeData">
													<v-list-item-icon
														><v-icon>mdi-eraser-variant</v-icon></v-list-item-icon
													>
													<v-list-item-title>
														{{ $t('datapointDetails.pointProperties.purgedata') }}
													</v-list-item-title>
												</v-list-item>
											</v-list>
										</v-menu>
									</v-col>
								</v-row>
							</v-col>

							<v-col md="6" cols="12">
								<v-text-field
									v-model="data.name"
									:label="$t('datapointDetails.pointProperties.point.name')"
									dense
								></v-text-field>
							</v-col>

							<v-col md="6" cols="12" @click="navToDataSource">
								<v-btn text block>
									<v-icon>mdi-database </v-icon>
									<span> {{ data.dataSourceName }}</span>
								</v-btn>
							</v-col>
							<v-col cols="12">
								<v-text-field
									v-model="data.description"
									:label="$t('datapointDetails.pointProperties.point.description')"
									dense
								></v-text-field>
							</v-col>
						</v-row>

						<PointPropLogging :data="data"></PointPropLogging>
						<PointPropTextRenderer :data="data"></PointPropTextRenderer>
						<PointPropEventRenderer :data="data"></PointPropEventRenderer>
						<PointPropChartRenderer :data="data"></PointPropChartRenderer>
					</v-col>

					<v-divider vertical class="point-properties-horizontal"></v-divider>

					<v-col cols="5" xs="12">
						<PointPropEventDetectors :data="data"></PointPropEventDetectors>
					</v-col>
				</v-row>
			</v-card-text>
			<v-divider></v-divider>
			<v-card-actions>
				<v-spacer> </v-spacer>
				<v-btn text @click="dialog = false">{{ $t('common.cancel') }}</v-btn>
				<v-btn color="primary" text @click="save">{{ $t('common.save') }}</v-btn>
			</v-card-actions>
		</v-card>
	</v-dialog>
</template>
<script>
import PointPropLogging from './PointPropLogging.vue';
import PointPropTextRenderer from './PointPropTextRenderer.vue';
import PointPropChartRenderer from './PointPropChartRenderer.vue';
import PointPropEventRenderer from './PointPropEventRenderer.vue';
import PointPropEventDetectors from './PointPropEventDetectors.vue';

import PurgeDataDialog from '@/layout/dialogs/PurgeDataDialog.vue';
import ConfirmationDialog from '@/layout/dialogs/ConfirmationDialog.vue';

/**
 * Point Properties
 *
 * Point properties it is a dialog that provide editable settings
 * of specific data point. User can change the name, and oter sub-settings.
 * Main configuration is provided by this component but it also can be
 * extended using additional PointProp componentes.
 *
 * @param {Object} data - Point Details object with data.
 *
 * @author Radoslaw Jajko <rjajko@softq.pl>
 * @version 1.0
 */
export default {
	name: 'PointProperties',

	components: {
		PointPropLogging,
		PointPropTextRenderer,
		PointPropChartRenderer,
		PointPropEventRenderer,
		PointPropEventDetectors,
		PurgeDataDialog,
		ConfirmationDialog,
	},

	props: ['data'],

	data() {
		return {
			dialog: false,
			purgeDialog: false,
		};
	},

	methods: {
		save() {
			this.$emit('saved');
			this.dialog = false;
		},

		toggleDataPoint() {
			this.$refs.toggleDialogConfirm.showDialog();
		},

		async toggleDataPointDialog(e) {
			if (e) {
				let resp = await this.$store.dispatch('toggleDataPoint', this.data.id);
				if (!!resp) {
					this.data.enabled = resp.enabled;
				}
			}
		},

		purgeData() {
			this.purgeDialog = true;
		},

		purgeDataResult(result) {
			if (result) {
				this.$store.dispatch('showSuccessNotification', this.$t('common.snackbar.delete.success'));				
			} else {
				this.$store.dispatch('showSuccessNotification', this.$t('common.snackbar.delete.fail'));
			}
			this.purgeDialog = false;
		},

		applyProperties() {
			console.log('Apply Properties');
		},

		navToDataSource() {
			location.replace(
				`${location.protocol}//${location.host}/${
					location.pathname.split('/')[1]
				}/data_source_edit.shtm?dsid=${this.data.dataSourceId}&pid=${this.data.id}`,
			);
		},
	},
};
</script>
<style scoped>
.point-properties-box {
	max-height: 70vh;
	overflow: auto;
}
.point-properties-horizontal {
	margin: 0 3.5%;
}
</style>
