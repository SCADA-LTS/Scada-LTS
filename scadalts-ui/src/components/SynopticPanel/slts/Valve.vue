<template>
	<div>
		<v-container fluid v-if="componentEditor">
			<v-row>
				<v-col cols="12" md="6">
					<v-text-field v-model="componentData.data.pointXid" label="Point XID:" />
				</v-col>
				<v-col cols="12" md="3">
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
			this.$svg.get(`${this.componentId}_button`).on('click', function () {
				console.log('button1');
			});
			this.$svg.get(`${this.componentId}_button`).on('mouseover', alert, this);
			clearInterval(this.refreshInterval);
		}
	},
	methods: {
		updateComponent() {
			this.getDataPointValueXid(this.componentData.data.pointXid).then((data) => {
				this.changeComponentText(
					`${this.componentId}_value`,
					parseFloat(data.value).toFixed(2)
				);
				if (data.enabled) {
					this.changeComponentColor(`${this.componentId}_background_left`, '#1BB350');
					this.changeComponentColor(`${this.componentId}_background_right`, '#1BB350');
				} else {
					this.changeComponentColor(`${this.componentId}_background_left`, '#B8150E');
					this.changeComponentColor(`${this.componentId}_background_right`, '#B8150E');
				}
			});
		},
		startRefresh() {
			this.refreshInterval = setInterval(() => {
				this.updateComponent();
			}, 5000);
		},
		alert() {
			console.log('button1');
		},
	},
};
</script>
