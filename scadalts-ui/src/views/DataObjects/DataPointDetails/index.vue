<template>
	<div v-if="dataPointDetails && datasource">
		<v-container fluid>
			<ConfirmationDialog
				:btnvisible="false"
				ref="toggleDialogConfirm"
				@result="toggleDataPointDialog"
				:title="$t('datapointDetails.pointProperties.toggle.dialog.title')"
				:message="$t('datapointDetails.pointProperties.toggle.dialog.text')"
			></ConfirmationDialog>
			<v-row align="center">
				<v-col cols="8" xs="12">
					<h1>
						<v-tooltip bottom>
							<template v-slot:activator="{ on, attrs }">
								<v-btn
									x-small
									fab
									elevation="1"
									v-bind="attrs"
									v-on="on"
									@click="toggleDataPoint"
									:color="datasource.enabled ? (dataPointDetails.enabled ? 'primary' : 'error') : 'warning'"
								>
									<v-icon v-show="dataPointDetails.enabled">mdi-decagram</v-icon>
									<v-icon v-show="!dataPointDetails.enabled">mdi-decagram-outline</v-icon>
								</v-btn>
							</template>
							<span class="help-message">{{
								datasource.enabled ?
								$t('datapointDetails.pointProperties.toggle.help') :
								$t('datapointDetails.pointProperties.toggle.datasourceDisabled')
							}}</span>
						</v-tooltip>
						{{ dataPointDetails.name }}
						<DataPointComment :data="dataPointDetails"></DataPointComment>
					</h1>
					<p class="thin-top-margin small-description">
						<span>{{ dataPointDetails.xid }}</span>
						<span v-if="dataPointDetails.description">
							<span v-if="dataPointDetails.description.length > 0">
								- {{ dataPointDetails.description }}</span
							>
						</span>
					</p>
				</v-col>
				<v-col cols="2" xs="12" class="row justify-end">
					<PointProperties
						:data="dataPointDetails"
						@saved="saveDataPointDetails"
					></PointProperties>
				</v-col>
				<v-col cols="2">
					<DataPointSearchComponent @change="reload"></DataPointSearchComponent>
				</v-col>
			</v-row>
		</v-container>
		<v-container fluid>
			<v-row align-content="stretch">
				<v-col md="6" sm="12" xs="12">
					<DataPointValueHistory
						:data="dataPointDetails"
						class="pointDetailsCards"
						ref="valueHistory"
					></DataPointValueHistory>
				</v-col>
				<v-col md="6" sm="12" xs="12">
					<DataPointEventList
						:datapointId="dataPointDetails.id"
						class="pointDetailsCards"
					></DataPointEventList>
				</v-col>
			</v-row>
		</v-container>
		<v-container fluid>
			<LineChartComponent :pointIds="this.$route.params.id" :refreshRate="chartRefreshRate"
				:showLegend="true" :showScrollbar="true" :width="chartWidth"
			></LineChartComponent>
		</v-container>
		<v-snackbar v-model="response.status">
			{{ response.message }}
		</v-snackbar>
	</div>
</template>
<script>
import DataPointSearchComponent from '@/layout/buttons/DataPointSearchComponent';
import DataPointComment from './DataPointComment';
import PointProperties from './PointProperties';
import DataPointEventList from './DataPointEventList';
import DataPointValueHistory from './DataPointValueHistory';
import LineChartComponent from '@/components/amcharts/LineChartComponent';

import ConfirmationDialog from '@/layout/dialogs/ConfirmationDialog';
/**
 * Data Point Details page
 *
 * View page for specific Data Point
 * Displays all point related information. Using
 * aditional components users are able to modify
 * the data point properties.
 *
 * The Point Details page can be extended using additional components.
 *
 * @author Radoslaw Jajko <rjajko@softq.pl>
 * @version 1.0
 */
export default {
	name: 'DataPointDetails',

	components: {
		DataPointSearchComponent,
		DataPointComment,
		PointProperties,
		DataPointEventList,
		DataPointValueHistory,
		LineChartComponent,
		ConfirmationDialog,
	},

	data() {
		return {
			newComment: '',
			dataPointDetails: undefined,
			confirmToggleDialog: false,
			datasource: null,
			chartRefreshRate: 10000,
			chartWidth: 500,
			response: {
				status: false,
				message: '',
			},
		};
	},

	created() {
		window.addEventListener('resize', this.handleChartResize);
		this.handleChartResize();
	},

	destroyed() {
		window.removeEventListener('resize', this.handleChartResize);
	},

	mounted() {
		this.fetchDataPointDetails(this.$route.params.id);
	},

	methods: {
		reload(dataPoint) {
			this.$router.push(`${dataPoint.id}`);
			this.$router.go();
		},

		async fetchDataPointDetails(datapointId) {
			this.dataPointDetails = await this.$store.dispatch(
				'getDataPointDetails',
				datapointId,
			);
			this.datasource = await this.$store.dispatch(
				'getDatasourceByXid',
				this.dataPointDetails.dataSourceXid,
			);

		},
		async toggleDataPoint() {
			if (this.datasource.enabled) {
				this.$refs.toggleDialogConfirm.showDialog();
			} else {
				this.datasourceDisabled = true;
			}
		},

		async toggleDataPointDialog(e) {
			if (e) {
				let resp = await this.$store.dispatch(
					'toggleDataPoint',
					this.dataPointDetails.id,
				);
				if (!!resp) {
					this.dataPointDetails.enabled = resp.enabled;
				}
			}
		},

		saveDataPointDetails() {
			this.$store
				.dispatch('saveDataPointDetails', this.dataPointDetails)
				.then((resp) => {
					if (resp === 'saved') {
						this.response.status = true;
						this.response.message = this.$t('common.snackbar.update.success');
						this.$refs.valueHistory.reconnect();

					} else {
						this.response.status = true;
						this.response.message = this.$t('common.snackbar.update.fail');
					}
				})
				.catch(() => {
					this.response.status = true;
					this.response.message = this.$t('common.snackbar.update.fail');
				});
		},

		handleChartResize() {
			this.chartWidth = window.innerWidth - 200;
		}
	},
};
</script>
<style scoped>
.pointDetailsCards {
	height: 100%;
	max-height: 52vh;
	overflow-y: auto;
}
.thin-top-margin {
	margin-top: -24px;
	margin-left: 44px;
}
.small-description {
	color: rgba(0, 0, 0, 0.5);
}
</style>
