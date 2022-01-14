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
				<v-col cols="12">
					<v-text-field v-model="componentData.data.label" label="Label:" />
				</v-col>
			</v-row>
		</v-container>
	</div>
</template>
<script>
import CustomComponentsBase from '../CustomComponentBase.vue';
import DataPointSearchComponent from '@/layout/buttons/DataPointSearchComponent';
/**
 * SLTS-FAN Component
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
			lastValue: undefined,
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
			let newValue = parseFloat(value).toFixed(2);
			this.changeComponentColor(`${this.componentId}_background`, '#1BB350');
			if (this.lastValue !== newValue) {
				this.changeComponentText(`${this.componentId}_value`, newValue);
				this.rotateComponent(this.componentId, 60000, parseInt(newValue) * 360, true);
				this.lastValue = newValue;
			}
		},

		onPointEnabledUpdate(enabled) {
			if (enabled) {
				this.changeComponentColor(`${this.componentId}_background`, '#1BB350');
				this.changeComponentText(`${this.componentId}_value`, this.lastValue);
				this.rotateComponent(this.componentId, 60000, parseInt(this.lastValue) * 360, true);
			} else {
				this.changeComponentColor(`${this.componentId}_background`, '#B8150E');
				this.changeComponentText(`${this.componentId}_value`, 'N/A');
				this.finishComponentAnimation(this.componentId);
			}
		},

	},
};
</script>
