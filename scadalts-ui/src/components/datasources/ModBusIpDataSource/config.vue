<template>
	<DataSourceConfig
		title="ModBus IP Data Source"
		:datasource="datasource"
		:creator="createMode"
		@cancel="cancel()"
		@accept="save()"
	>

		<template v-slot:selector>
			<slot name="selector"></slot>
		</template>

        <v-row>
            <v-col cols="4">
                <v-checkbox
					label="Quantize"
					v-model="datasource.quantize"
				></v-checkbox>	
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
                <v-text-field v-model="datasource.maxReadBitCount" label="Max read bit count"></v-text-field>
            </v-col>
            <v-col cols="4">
                <v-text-field v-model="datasource.maxReadRegisterCount" label="Max read register count"></v-text-field>
            </v-col>
            <v-col cols="4">
                <v-text-field v-model="datasource.maxWriteRegisterCount" label="Max write register count"></v-text-field>
            </v-col>
            <v-col cols="3">
                <v-text-field v-model="datasource.timeout" label="Timeout (ms)"></v-text-field>
            </v-col>
            <v-col cols="3">
                <v-text-field v-model="datasource.retries" label="Retries"></v-text-field>
            </v-col>
            <v-col cols="6">
                <v-select 
                    label="Transport type" 
                    v-model="datasource.transportType" 
                    :items="transportTypes">
				</v-select>
            </v-col>
            <v-col cols="3">
                <v-text-field v-model="datasource.host" label="Host" :disabled="datasource.transportType === 3"></v-text-field>
            </v-col>
            <v-col cols="3">
                <v-text-field v-model="datasource.port" label="Port"></v-text-field>
            </v-col>
            <v-col>
                <v-checkbox
					label="Encapsulated"
					v-model="datasource.encapsulated"
				></v-checkbox>	
            </v-col>
            <v-col>
                <v-checkbox
                    v-if="datasource.transportType !== 2"
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
					value: "TCP"
				},
                {
					text: 'TCP with Keep-alive',
					value: "TCP_KEEP_ALIVE"
				},
				{
					text: 'UDP',
					value: "UDP"
				},
				{
					text: 'TCP Listener',
					value: "TCP_LISTENER"
				}
			],
        }
    },

	methods: {
		onUpdatePeriodTypeUpdate(value) {
			this.datasource.updatePeriodType = value;
		}
	},
};
</script>
<style></style>
