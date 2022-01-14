<template>
	<div>
		<v-container fluid v-if="componentEditor">
			<v-row>
				<v-col cols="8">
					<DataPointSearchComponent @change="datapointSelected"/>
				</v-col>

				<v-col cols="4">
					<v-text-field disabled v-model="componentData.data.pointXid" label="Point Xid" />
				</v-col>

				<v-col cols="12" md="4">
					<v-text-field v-model="componentData.data.label" label="Label:" />
				</v-col>
				<v-col cols="12" md="4">
					<v-text-field v-model="componentData.data.minval" label="Min value:" />
				</v-col>
				<v-col cols="12" md="4">
					<v-text-field v-model="componentData.data.maxval" label="Max value:" />
				</v-col>
			</v-row>
		</v-container>
	</div>
</template>
<script>
import CustomComponentsBase from '../CustomComponentBase.vue';
import DataPointSearchComponent from '@/layout/buttons/DataPointSearchComponent';
/**
 * SLTS-WATER_LEVEL Component
 * 
 * @author Radoslaw Jajko <rjajko@softq.pl>
 * @version 1.1.0
 */
export default {
	extends: CustomComponentsBase,
	components: {
		DataPointSearchComponent,
	},
	data() {
		return {
			refreshInterval: undefined,
		};
	},
	mounted() {
		if (!this.componentEditor) {
			this.subscribeToWebSocket();
		}
	},
	
	methods: {
		datapointSelected(datapoint) {
			this.componentData.data.pointXid = datapoint.xid;
		},
		
		onPointValueUpdate(value) {
			this.changeWaterLevel(parseFloat(value));
		},
		
		onPointEnabledUpdate(enabled) {
			enabled
				? this.changeComponentText(`${this.componentId}_value`, this.componentData.data.label)
				: this.changeComponentText(`${this.componentId}_value`, "N/A");
		},

		changeWaterLevel(value) {
			let maxHeight = this.$svg.get(this.componentId).attr('height');
			let minVal = this.componentData.data.minval;
			if (minVal === undefined || minVal === null) minVal = 0;
			let waterLevel = (value * maxHeight) / (this.componentData.data.maxval - minVal);
			this.$svg.get(`${this.componentId}_background`).animate().height(waterLevel);
		},
	},
};
</script>
