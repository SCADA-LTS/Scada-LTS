<template>
	<DataSourceLoader :loaded="detailsLoaded" ref="dialog">
		<template v-slot:config>
			<config
				v-if="detailsLoaded"
				@canceled="closeEditor()"
				@saved="onSaved($event)"
				:datasource="ds"
				:createMode="false"
			/>
		</template>

		<v-row>
			<v-col>
				<PointLocatorTest :datasource="ds"> </PointLocatorTest>
			</v-col>

			<!-- TODO: Implement ReadData Component and NodeScan -->
		</v-row>
	</DataSourceLoader>
</template>
<script>
import DataSourceBase from '../DataSourceBase.vue';
import NodeScan from './components/NodeScan.vue';
import ReadData from './components/ReadData.vue';
import PointLocatorTest from './components/PointLocatorTest.vue';
import config from './config.vue';

export default {
	extends: DataSourceBase,

	components: {
		config,
		NodeScan,
		ReadData,
		PointLocatorTest,
	},

	data() {
		return {
			ds: undefined,
		};
	},

	mounted() {
		this.fetchData();
	},

	methods: {
		async fetchData() {
			let res = await this.fetchDataSourceDetails();
			console.log(res);
			this.ds = res;
		},
	},
};
</script>
<style scoped></style>
