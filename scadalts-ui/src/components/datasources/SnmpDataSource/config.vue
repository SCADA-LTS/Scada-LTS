<template>
	<DataSourceConfig
		title="SNMP Data Source"
		:datasource="datasource"
		:creator="createMode"
		@cancel="cancel()"
		@accept="save()"
	>
		<template v-slot:selector>
			<slot name="selector"></slot>
		</template>

		<v-row>
			<v-col>
				<v-text-field v-model="datasource.host" label="Host Address"></v-text-field>
			</v-col>
			<v-col>
				<v-text-field v-model="datasource.port" label="Port Number"></v-text-field>
			</v-col>
		</v-row>
	</DataSourceConfig>
</template>
<script>
import DataSourceConfig from '../DataSourceConfig';

export default {
	components: {
		DataSourceConfig,
	},

	props: {
		datasource: {
			required: false,
			type: Object,
			default: () => {
				return {
					name: '',
					xid: 'DS_VDS_',
					updatePeriod: 5,
					updatePeriodType: 1,
				};
			},
		},
		createMode: {
			type: Boolean,
			default: true,
		},
	},

	methods: {
		cancel() {
			this.$emit('canceled');
		},

		save() {
			this.$emit('saved', this.datasource);
		},
	},
};
</script>
<style></style>
