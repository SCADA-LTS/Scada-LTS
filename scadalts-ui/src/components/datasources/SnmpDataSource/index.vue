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
				<OidTester :datasource="ds" />
			</v-col>
		</v-row>
	</DataSourceLoader>
</template>
<script>
import DataSourceBase from '../DataSourceBase.vue';
import config from './config.vue';
import OidTester from './components/OidTester.vue';

export default {
	extends: DataSourceBase,

	components: { config, OidTester },

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
			this.ds = res;
		},
	},
};
</script>
<style scoped></style>
