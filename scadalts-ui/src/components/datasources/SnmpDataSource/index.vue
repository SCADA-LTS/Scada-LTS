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
				<div>
					<h3>SNMP Host testing</h3>
					<v-text-field v-model="testingOid" 
						append-outer-icon="mdi-send"
						@click:append-outer="checkOid">
					</v-text-field>
				</div>
			</v-col>
		</v-row>
	</DataSourceLoader>
</template>
<script>
import DataSourceBase from '../DataSourceBase.vue';
import config from './config.vue';

export default {
	extends: DataSourceBase,

	components: { config },

	data() {
		return {
			ds: undefined,
			testingOid: '1.2.3'
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
		checkOid(){
			console.log(this.testingOid);
		}
	},
};
</script>
<style scoped></style>
