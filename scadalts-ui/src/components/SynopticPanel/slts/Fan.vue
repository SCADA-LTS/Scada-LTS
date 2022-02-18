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
				<v-col cols="4">
					<p>Enabled Color</p>
				</v-col>
				<v-col cols="2">
					<v-menu offset-y :close-on-content-click="false">
						<template v-slot:activator="{ on }">
							<v-btn :color="enabledColor" v-on="on" block> </v-btn>
						</template>
						<v-color-picker v-model="enabledColor" :close-on-content-click="false">
						</v-color-picker>
					</v-menu>
				</v-col>
				<v-col cols="4">
					<p>Disabled Color</p>
				</v-col>
				<v-col cols="2">
					<v-menu offset-y :close-on-content-click="false">
						<template v-slot:activator="{ on }">
							<v-btn :color="disabledColor" v-on="on" block> </v-btn>
						</template>
						<v-color-picker v-model="disabledColor" :close-on-content-click="false">
						</v-color-picker>
					</v-menu>
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
 * @version 1.1.1
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

	computed: {
		enabledColor: {
			get() { return this.componentData.data.colorEnabled || '#1BB350'; },
			set(value) { this.componentData.data.colorEnabled = value; }
		},
		disabledColor: {
			get() { return this.componentData.data.colorDisabled || '#B8150E'; },
			set(value) { this.componentData.data.colorDisabled = value; }
		}
	},

	methods: {
		datapointSelected(datapoint) {
			this.componentData.data.pointXid = datapoint.xid;
		},

		onPointValueUpdate(value) {
			let newValue = parseFloat(value).toFixed(2);
			this.changeComponentColor(`${this.componentId}_background`, this.enabledColor);
			if (this.lastValue !== newValue) {
				this.changeComponentText(`${this.componentId}_value`, newValue);
				this.rotateComponent(this.componentId, 60000, parseInt(newValue) * 360, true);
				this.lastValue = newValue;
			}
		},

		onPointEnabledUpdate(enabled) {
			if (enabled) {
				this.changeComponentColor(`${this.componentId}_background`, this.enabledColor);
				this.changeComponentText(`${this.componentId}_value`, this.lastValue);
				this.rotateComponent(
					this.componentId,
					60000,
					parseInt(this.lastValue) * 360,
					true,
				);
			} else {
				this.changeComponentColor(`${this.componentId}_background`, this.disabledColor);
				this.changeComponentText(`${this.componentId}_value`, 'N/A');
				this.finishComponentAnimation(this.componentId);
			}
		},
	},
};
</script>
