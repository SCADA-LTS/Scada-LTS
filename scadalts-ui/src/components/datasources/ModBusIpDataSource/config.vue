<template>
	<DataSourceConfig
		title="ModBus IP Data Source"
		:datasource="datasource"
		:creator="createMode"
		availablePeriodTypes="1,2,3,8"
		@cancel="cancel()"
		@accept="save()"
	>
		<template v-slot:selector>
			<slot name="selector"></slot>
		</template>

		<v-row>
			<v-col cols="4">
				<v-checkbox label="Quantize" v-model="datasource.quantize"></v-checkbox>
			</v-col>
			<v-col cols="4">
				<v-checkbox
					label="Contiguous batches only"
					v-model="datasource.contiguousBatches"
				></v-checkbox>
			</v-col>
			<v-col cols="4">
				<v-checkbox
					label="Create slave monitor points"
					v-model="datasource.createSlaveMonitorPoints"
				></v-checkbox>
			</v-col>

			<v-col cols="4">
				<v-text-field
					v-model="datasource.maxReadBitCount"
					placeholder="2000"
					label="Max read bit count"
				></v-text-field>
			</v-col>
			<v-col cols="4">
				<v-text-field
					v-model="datasource.maxReadRegisterCount"
					placeholder="125"
					label="Max read register count"
				></v-text-field>
			</v-col>
			<v-col cols="4">
				<v-text-field
					v-model="datasource.maxWriteRegisterCount"
					placeholder="120"
					label="Max write register count"
				></v-text-field>
			</v-col>
			<v-col cols="3">
				<v-text-field
					v-model="datasource.timeout"
					placeholder="500"
					label="Timeout (ms)"
				></v-text-field>
			</v-col>
			<v-col cols="3">
				<v-text-field
					v-model="datasource.retries"
					placeholder="2"
					label="Retries"
				></v-text-field>
			</v-col>
			<v-col cols="6">
				<v-select
					label="Transport type"
					v-model="datasource.transportType"
					:items="transportTypes"
				>
				</v-select>
			</v-col>
			<v-col cols="3">
				<v-text-field
					v-model="datasource.host"
					placeholder="127.0.0.1"
					label="Host"
					:disabled="datasource.transportType === 'TCP_LISTENER'"
				></v-text-field>
			</v-col>
			<v-col cols="3">
				<v-text-field
					v-model="datasource.port"
					placeholder="502"
					label="Port"
				></v-text-field>
			</v-col>
			<v-col>
				<v-checkbox label="Encapsulated" v-model="datasource.encapsulated"></v-checkbox>
			</v-col>
			<v-col>
				<v-checkbox
					v-if="datasource.transportType !== 'UDP'"
					label="Create socket monitoring point"
					v-model="datasource.createSockerMonitorPort"
				></v-checkbox>
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
			transportTypes: [
				{
					text: 'TCP',
					value: 'TCP',
				},
				{
					text: 'TCP with Keep-alive',
					value: 'TCP_KEEP_ALIVE',
				},
				{
					text: 'UDP',
					value: 'UDP',
				},
				{
					text: 'TCP Listener',
					value: 'TCP_LISTENER',
				},
			],
		};
	},

	methods: {
		onUpdatePeriodTypeUpdate(value) {
			this.datasource.updatePeriodType = value;
		},
	},
};
</script>
<style></style>
