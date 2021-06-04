<template>
	<DataSourceInfo :loaded="detailsLoaded" ref="dialog">
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
			<v-col> Radek </v-col>
		</v-row>

	</DataSourceInfo>
</template>
<script>
import BaseDataSourceVue from '../BaseDataSource.vue';
import config from './config.vue';

export default {
    extends: BaseDataSourceVue,
	
    components: {
		config,
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

		onSaved(event) {
			this.$emit('saved', event);
			// console.log(event);
		},
	},
};
</script>
<style scoped></style>