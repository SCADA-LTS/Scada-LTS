<template>
	<div>
		<v-container fluid>
			<v-tabs v-model="tab">
				<v-tab> Template </v-tab>
				<v-tab :class="{ 'blink': isUnreadedReport }" @click="move"> Instances </v-tab>
			</v-tabs>

			<v-tabs-items v-model="tab">
				<v-tab-item>
					<ReportsPage v-if="tab === 0" />
				</v-tab-item>
				<v-tab-item>
					<ReportsData v-if="tab === 1" />
				</v-tab-item>
			</v-tabs-items>
		</v-container>
	</div>
</template>

<script>
import { READED_REPORT_INSTANCE } from '../../store/reports/types';
/**
 * @author sselvaggi, radek2s
 * @version 1.1.0
 * Simplified version of reports tabs.
 */
import ReportsData from './ReportsData';
import ReportsPage from './ReportsPage';
export default {
	name: 'ReportsTabs',

	components: {
		ReportsPage,
		ReportsData,
	},

	mounted() {
		this.interval = setInterval(() => {
			this.fetchIntances();
		}, 5000);
	},

	destroyed() {
		clearInterval(this.interval);
	},

	data() {
		return {
			tab: 0,
		};
	},

	computed: {
		isUnreadedReport() { return this.$store.state.storeReports.unreadedInstance; }
	},
	
	methods: {
		fetchIntances() {
			this.$store.dispatch('fetchReportInstances');
		},
		move() {
			this.$store.commit(READED_REPORT_INSTANCE);
		}
	},
};
</script>
