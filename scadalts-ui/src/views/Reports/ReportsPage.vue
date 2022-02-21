<template>
	<v-container fluid>
		<v-row>
			<v-col cols="12" md="5" lg="3">
				<v-row>
					<v-col cols="12">
						<v-text-field
							v-model="search"
							append-icon="mdi-magnify"
							label="Search"
							single-line
							hide-details
						></v-text-field>
					</v-col>

					<v-col cols="12">
						<v-btn block color="primary" @click="createNewReport"
							>{{ $t('reports.newReport') }}
						</v-btn>
					</v-col>

					<v-col cols="12" v-if="reportTemplateList.length > 0 && !loading">
						<v-data-table
							:headers="headers"
							:items="reportTemplateList"
							multi-sort
							@click:row="clickRow"
						>
							<template v-slot:item.name="{ item }">
								{{ item.name }}
							</template>
							<template v-slot:item.actions="{ item }">
								<v-btn icon @click.stop="runReport(item.id)">
									<v-icon :title="$t('reports.runNow')"> mdi-play </v-icon>
								</v-btn>

								<v-btn icon @click.stop="selectReport(item, true)">
									<v-icon :title="$t('reports.copy')"> mdi-content-copy </v-icon>
								</v-btn>

								<v-btn icon @click.stop="deleteReport(item.id)">
									<v-icon :title="$t('reports.delete')"> mdi-delete </v-icon>
								</v-btn>
							</template>
						</v-data-table>
					</v-col>

					<v-col cols="12" v-if="!loading && reportTemplateList.length === 0">
						<p>No reports found. Create a new one</p>
					</v-col>

					<v-col v-if="loading">
						<v-skeleton-loader type="article"> </v-skeleton-loader>
					</v-col>
				</v-row>
			</v-col>
			<v-col cols="12" md="7" lg="9">
				<ReportsForm ref="ReportsForm" @saved="onReportSaved"></ReportsForm>
			</v-col>
		</v-row>
	</v-container>
</template>
<script>
/**
 * @author sselvaggi & radek2s
 * 
 */
import ScadaReportForm from '../../models/ScadaReportForm';
import ReportsForm from './ReportsForm';

export default {
	name: 'reportsPage',

	components: {
		ReportsForm,
	},

	async mounted() {
		this.fetchReportTemplates();
		this.allDataPoints = await this.$store.dispatch('getAllDatapoints');
	},

	data() {
		return {
			search: '',
			allDataPoints: [],
			selectedReport: null,
			loading: false,
			headers: [
				{
					text: this.$t('reports.reportName'),
					sortable: true,
					align: 'center',
					value: 'name',
				},
				{
					text: this.$t('common.actions'),
					align: 'end',
					sortable: false,
					value: 'actions',
				},
			],
		};
	},
	computed: {
		reportTemplateList() {
			return this.search.length > 0
				? this.$store.getters.filteredReportTemplates(this.search)
				: this.$store.state.storeReports.reportTemplates;
		},
	},

	methods: {
		fetchReportTemplates() {
			this.loading = true;
			this.$store.dispatch('fetchReportTemplates').finally(() => {
				this.loading = false;
			});
		},

		clickRow(e) {
			this.selectReport(e);
		},
		createNewReport() {
			this.selectReport(new ScadaReportForm());
		},

		selectReport(report, copy = false) {
			if (copy) {
				this.selectedReport = JSON.parse(JSON.stringify(report));
				this.selectedReport.id = -1;
				this.selectedReport.name = '';
			} else {
				this.selectedReport = report;
			}

			if (this.selectedReport.points.length > 0) {
				this.selectedReport.points = this._updateReportPointsDetails(
					this.selectedReport.points,
				);
			}

			this.$refs.ReportsForm.selectReport(this.selectedReport);
		},

		onReportSaved(report) {
			console.debug("Saving report...", report)
			// TODO: Separate saving and updateing logic in Backend API.
			// let action = report.id === -1 ? 'createReport' : 'updateReport';
			// this.$store.dispatch(action, report);
			this.$store.dispatch('saveReport', report)
			.then(() => {
				this.$store.dispatch('showSuccessNotification', "Report saved successfully");
				this.fetchReportTemplates();})
			.catch(() => {
				this.$store.dispatch('showErrorNotification', "Error during saving report");
			});
		},

		runReport(id) {
			this.$store.dispatch('runReport', id)
			.then(() => {
				this.$store.dispatch('showSuccessNotification', "Report created");
				this.fetchReportTemplates();})
			.catch(() => {
				this.$store.dispatch('showErrorNotification', "Failed to create a report");
			});
		},
		
		deleteReport(id) {
			this.$store.dispatch('deleteReport', id)
			.then(() => {
				this.$store.dispatch('showSuccessNotification', "Report deleted successfully");
				this.fetchReportTemplates();})
			.catch(() => {
				this.$store.dispatch('showErrorNotification', "Report deletion failed");
			});
		},

		_updateReportPointsDetails(pointList) {
			return pointList.map((p) => {
				const name = this.allDataPoints.find((dp) => dp.id === p.pointId).name;
				return { ...p, name };
			});
		},
	},
};
</script>
