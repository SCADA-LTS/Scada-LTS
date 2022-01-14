<template>
	<div>
		<v-container fluid v-if="componentEditor">
			<v-row>
				<v-col cols="8">
					<DataPointSearchComponent @change="datapointSelected" />
				</v-col>
				<v-col cols="4">
					<v-text-field
						disabled
						v-model="componentData.data.pointXid"
						label="Point Xid"
					/>
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
 * SLTS-VALVE Component
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
		return {};
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
			this.changeComponentText(`${this.componentId}_value`, parseFloat(value).toFixed(2));
		},

		onPointEnabledUpdate(enabled) {
			if (enabled) {
				this.changeComponentColor(`${this.componentId}_background_left`, '#1BB350');
				this.changeComponentColor(`${this.componentId}_background_right`, '#1BB350');
			} else {
				this.changeComponentColor(`${this.componentId}_background_left`, '#B8150E');
				this.changeComponentColor(`${this.componentId}_background_right`, '#B8150E');
			}
		},
	},
};
</script>
