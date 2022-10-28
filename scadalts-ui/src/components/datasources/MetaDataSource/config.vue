<template>
	<DataSourceConfig
		title="Meta Data Source"
		:polling="false"
		:datasource="datasource"
		:creator="createMode"
		availablePeriodTypes="1,2,3,8,9,11,26,27"
		@cancel="cancel()"
		@accept="save()"
	>
		<template v-slot:selector>
			<slot name="selector"></slot>
		</template>
	
		<v-row>
			
			<v-col cols="4">
				<v-select
					label="Context point disabled"
					v-model="datasource.contextPointDisabled"
					item-text="name"
					item-value="id"
					:items="alarmLevelOptions"
				>
				</v-select>
			</v-col>
			<v-col cols="4">
				<v-select
					label="Script error"
					v-model="datasource.scriptError"
					item-text="name"
					item-value="id"
					:items="alarmLevelOptions"
				>
				</v-select>
			</v-col>
			<v-col cols="4">
				<v-select
					label="Result type error"
					v-model="datasource.resultTypeError"
					item-text="name"
					item-value="id"
					:items="alarmLevelOptions"
				>
				</v-select>
			</v-col>
			
		</v-row>
	</DataSourceConfig>
</template>
<script>
import DataSourceConfigMixin from '../DataSourceConfigMixin';

export default {
	mixins: [DataSourceConfigMixin],
	data() {
		return {
	 
			alarmLevelOptions: [
				{ name:"Information", id: 1, color: 'blue' },
				{ name:"Urgent", id: 2, color: 'yellow' },
				{ name:"Critical", id: 3, color: 'orange' },			
				{ name:"Life Safety", id: 4, color: 'red' },		
			],
		}
	},

	methods: {
		onUpdatePeriodTypeUpdate(value) {
			this.datasource.updatePeriodType = value;
		},
	},
};
</script>
<style></style>
