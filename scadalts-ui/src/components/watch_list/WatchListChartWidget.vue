<template>
	<div class="scada-widget">
		<div class="settings">
			<p class="smallTitle">Modern Chart</p>
		</div>
		<div class="chart-container">
			<watch-list-json-chart
				v-if="visible"
				ref="jsonChart"
				v-bind:point-id="pointId"
				v-bind:watchlist-name="watchlistName"
			/>
		</div>
	</div>
</template>
<script>
import WatchListJsonChart from './WatchListJsonChart';
export default {
	name: 'WatchListChartWidget',
	components: {
		WatchListJsonChart,
	},
	data() {
		return {
			pointId: [],
			watchlistName: undefined,
			visible: false,
		};
	},
	mounted() {
		this.$nextTick(function () {
			window.addEventListener('watchListChanged', this.watchListChanged);
		});
	},
	methods: {
		watchListChanged(event) {
			this.addNewChart();
		},
		addNewChart() {
			this.visible = false;
			let points = [];
			let watchList = document.getElementById('watchListTable');
			this.watchlistName = document.getElementById('newWatchListName').value;
			for (let i = 0; i < watchList.childElementCount; i++) {
				let point = watchList.children.item(i).id;
				if (document.getElementById(`${point}ChartCB`).checked) {
					points.push(point.slice(1));
				}
			}
			this.pointId = points.toString();
			this.visible = true;
			try {
				this.$refs.jsonChart.reset().then(() => {
					this.$refs.pointId = this.pointId;
					this.$refs.jsonChart.init();
				});
			} catch (e) {
				console.error(e);
			}
		},
	},
	beforeDestroy() {
		window.removeEventListener('watchListChanged', this.watchListChanged);
	},
};
</script>
<style scoped>
.chart-container {
	width: 100%;
	display: flex;
	flex-direction: column;
	margin-bottom: 50px;
}
.settings {
	display: flex;
	justify-content: space-between;
	padding: 0 10px;
}
.settings p {
	margin: 0;
}
</style>
