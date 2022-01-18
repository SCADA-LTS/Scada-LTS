<template>
	<div>
		<v-container fluid v-if="componentEditor">
			<v-row>
				<v-col cols="12" md="6">
					<v-text-field v-model="componentData.data.pointXid" label="Point XID:" />
				</v-col>
				<v-col cols="12" md="6">
					<v-text-field v-model="componentData.data.label" label="Label:" />
				</v-col>
			</v-row>
		</v-container>
	</div>
</template>
<script>
import CustomComponentsBase from '../CustomComponentBase.vue';

export default {
	extends: CustomComponentsBase,
	data() {
		return {
			lastValue: undefined,
			refreshInterval: undefined,
		};
	},
	mounted() {
		if (!this.componentEditor) {
			this.startRefresh();
		}
	},
	beforeDestroy() {
		if (!this.componentEditor) {
			clearInterval(this.refreshInterval);
		}
	},
	methods: {
		updateComponent() {
			this.getDataPointValueXid(this.componentData.data.pointXid).then((data) => {
				let newValue = parseFloat(data.value).toFixed(2);

				if (data.enabled) {
					this.changeComponentColor(`${this.componentId}_background`, '#1BB350');
					if (this.lastValue !== newValue) {
						this.changeComponentText(`${this.componentId}_value`, newValue);
						this.rotateComponent(this.componentId, 60000, parseInt(newValue) * 360, true);
						this.lastValue = newValue;
					}
				} else {
					this.changeComponentColor(`${this.componentId}_background`, '#B8150E');
					this.changeComponentText(`${this.componentId}_value`, 'N/A');
					this.finishComponentAnimation(this.componentId);
					this.lastValue = null;
				}
			});
		},
		startRefresh() {
			this.refreshInterval = setInterval(() => {
				this.updateComponent();
			}, 5000);
		},
	},
};
</script>
