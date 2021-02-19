<template>
	<div v-if="dataPointDetails">
		<v-container fluid>
			<ConfirmationDialog
				:btnvisible="false"
				:dialog="confirmToggleDialog"
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
									:color="dataPointDetails.enabled ? 'primary' : 'error'"
								>
									<v-icon v-show="dataPointDetails.enabled">mdi-decagram</v-icon>
									<v-icon v-show="!dataPointDetails.enabled">mdi-decagram-outline</v-icon>
								</v-btn>
							</template>
							<span class="help-message">{{
								$t('datapointDetails.pointProperties.toggle.help')
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
			<DataPointValueHistory
				:data="dataPointDetails"
				class="pointDetailsCards"
			></DataPointValueHistory>

			<DataPointEventList
				:datapointId="dataPointDetails.id"
				class="pointDetailsCards"
			></DataPointEventList>
		</v-container>
		<v-container fluid>
			<LineChartComponent :pointId="this.$route.params.id"> </LineChartComponent>
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
 * @author Radoslaw Jajko <rjajko@softq.pl>
 * @version 1.0
 *
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
			response: {
				status: false,
				message: '',
			},
		};
	},

	mounted() {
		console.log(this.$route.params.id);
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
		},

		toggleDataPoint() {
			this.confirmToggleDialog = true;
		},

		async toggleDataPointDialog(e) {
			this.confirmToggleDialog = false;
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
			this.$store.dispatch('saveDataPointDetails', this.dataPointDetails).then((resp) => {
				if (resp === 'saved') {
					this.response.status = true;
					this.response.message = this.$t('common.snackbar.update.success');
				} else {
					this.response.status = true;
					this.response.message = this.$t('common.snackbar.update.fail');
				}
			});
		},
	},
};
</script>
<style scoped>
.pointDetailsCards {
	width: 49%;
	float: left;
	max-height: 40vh;
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
