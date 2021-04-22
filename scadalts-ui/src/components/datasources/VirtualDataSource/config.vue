<template>
	<DataSourceConfig
		title="Virtual Data Source"
		:creator="createMode"
		@cancel="cancel()"
		@accept="save()"
	>
		<template v-slot:selector>
			<slot name="selector"></slot>
		</template>

		<v-row>
			<v-col>
				<v-text-field v-model="datasource.name" label="DataSource Name"></v-text-field>
			</v-col>
			<v-col>
				<v-text-field
					v-model="datasource.xid"
					label="DataSource Export Id"
				></v-text-field>
			</v-col>
			<v-col>
				<v-text-field
					v-model="datasource.updatePeriod"
					label="Update Period"
				></v-text-field>
			</v-col>
			<v-col>
				<v-text-field
					v-model="datasource.updatePeriodType"
					label="Update Period Type"
				></v-text-field>
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
					updatePeriodType: 1,
				};
			},
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
