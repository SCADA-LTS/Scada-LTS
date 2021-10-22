<template>
	<DataSourceConfig
		title="Virtual Data Source"
		:datasource="datasource"
		:creator="createMode"
		@cancel="cancel()"
		@accept="save()"
	>
		<template v-slot:selector>
			<slot name="selector"></slot>
		</template>
	</DataSourceConfig>
</template>
<script>
import DataSourceConfigMixin from '../DataSourceConfigMixin';

export default {
	mixins: [DataSourceConfigMixin],

	methods: {
		onUpdatePeriodTypeUpdate(value) {
			this.datasource.updatePeriodType = value;
		},
	},
};
</script>
<style></style>
