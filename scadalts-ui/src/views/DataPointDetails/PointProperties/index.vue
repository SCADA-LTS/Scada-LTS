<template>
	<v-dialog v-model="dialog" width="1200">
		<template v-slot:activator="{ on, attrs }">
			<v-btn elevation="2" fab v-bind="attrs" v-on="on">
				<v-icon>mdi-pencil</v-icon>
			</v-btn>
		</template>

		<v-card>
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
											Point properties
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
													<v-list-item-title>Purge data</v-list-item-title>
												</v-list-item>
												<!-- <v-list-item @click="applyProperties">
													<v-list-item-icon><v-icon>mdi-content-copy</v-icon></v-list-item-icon>
													<v-list-item-title>Apply properties</v-list-item-title>
												</v-list-item> -->
											</v-list>
										</v-menu>
									</v-col>
								</v-row>
							</v-col>

							<v-col md="6" cols="12">
								<v-text-field v-model="data.name" label="Point Name" dense></v-text-field>
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
									label="Description"
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
				<v-btn text @click="dialog = false">{{ $t('uiv.modal.cancel') }}</v-btn>
				<v-btn color="primary" text @click="save">{{ $t('uiv.modal.ok') }}</v-btn>
			</v-card-actions>
		</v-card>
	</v-dialog>
</template>
<script>
import PointPropLogging from './PointPropLogging';
import PointPropTextRenderer from './PointPropTextRenderer';
import PointPropChartRenderer from './PointPropChartRenderer';
import PointPropEventRenderer from './PointPropEventRenderer';
import PointPropEventDetectors from './PointPropEventDetectors';

import PurgeDataDialog from '@/layout/dialogs/PurgeDataDialog';

export default {
	name: 'PointProperties',

	components: {
		PointPropLogging,
		PointPropTextRenderer,
		PointPropChartRenderer,
		PointPropEventRenderer,
		PointPropEventDetectors,
		PurgeDataDialog,
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

		async toggleDataPoint() {
			let resp = await this.$store.dispatch('toggleDataPoint', this.data.id);
			if (!!resp) {
				this.data.enabled = resp.enabled;
			}
		},

		purgeData() {
			this.purgeDialog = true;
		},

		purgeDataResult() {
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
