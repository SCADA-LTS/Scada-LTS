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
                <v-col cols="6">
                    <v-switch v-model="componentData.data.showName" label="Show name"/>
                </v-col>
				<v-col cols="6">
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
 * SLTS-Point Component
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
            this.changeComponentText(`${this.componentId}_value`, value);
            this.changeComponentText(`${this.componentId}_label`, this.componentData.data.showName
                ? this.componentData.data.label
                : this.pointDefinition.name);
		},

		onPointEnabledUpdate(enabled) {
			if (enabled) {
                this.changeComponentText(`${this.componentId}_value`, this.pointDefinition.value);
			} else {
                this.changeComponentText(`${this.componentId}_value`, "Disabled");
			}
		},

	},
};
</script>
