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
import DataSourceConfig from '../DataSourceConfig';

export default {
	components: {
		DataSourceConfig,
	},

	props: {
		createMode: {
			type: Boolean,
			default: true,
		},
		datasource: {
			required: false,
			type: Object,
			default: () => {
				return {
					name: '',
					xid: 'DS_VDS_',
					updatePeriod: 5,
					updatePeriodType: 2,
				};
			},
		},
	},

	methods: {
		cancel() {
			this.$emit('canceled');
		},

		save() {
			console.log("VDS::SAVE")
			this.$emit('saved', this.datasource);
		},

		onUpdatePeriodTypeUpdate(value) {
			this.datasource.updatePeriodType = value;
		}
	},
};
</script>
<style></style>
