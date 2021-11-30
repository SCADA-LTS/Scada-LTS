<template>
	<BaseViewComponent :component="component" @update="$emit('update')">
		<template v-slot:default="data">
			<slot v-bind:data="data" />
		</template>

		<template v-slot:layout>
			<v-col cols="12">
				<v-text-field
					v-model="component.bkgdColorOverride"
					label="Background color"
				></v-text-field>
			</v-col>
			<v-col cols="12">
				<v-switch label="Display controls" v-model="component.displayControls"></v-switch>
			</v-col>
		</template>
		<template v-slot:renderer>
			<v-row>
				<v-col cols="12">
					<DataPointSerachComponent v-model="component.dataPointXid" :dataTypes="dataTypes" @change="onPointChange"></DataPointSerachComponent>
				</v-col>
				<v-col cols="6">
					<v-switch v-model="component.displayPointName" label="Display name"></v-switch>
				</v-col>
				<v-col cols="6">
					<v-switch
						v-model="component.settableOverride"
						label="Settable override"
					></v-switch>
				</v-col>
				<v-col cols="12">
					<v-text-field
						v-model="component.nameOverride"
						label="Point name override"
					></v-text-field>
				</v-col>
			</v-row>
			<slot name="renderer"> </slot>
		</template>
	</BaseViewComponent>
</template>
<script>
import DataPointSerachComponent from '@/layout/buttons/DataPointSearchComponent';
import BaseViewComponent from './BaseViewComponent.vue';

export default {
	components: {
		BaseViewComponent,
		DataPointSerachComponent,
	},

	props: {
		component: {
			type: Object,
			required: true,
		},
		dataTypes: {
			type: Array,
			required: false,			
		},
	},

	data() {
		return {
			point: {
				wsConnValue: null,
				wsConnEnable: null,
			},
		};
	},

	mounted() {
		this.connectToPointWebSocket(this.component.dataPointXid);
		this.getPointValue();
	},

	destroyed() {
		this.disconnectFromPointWebSocket();
	},

	methods: {
		onPointChange(point) {
			// this.component.dataPointXid = point.xid;
			this.connectToPointWebSocket(point.xid);
		},

		connectToPointWebSocket(pointXid) {
			this.disconnectFromPointWebSocket();
			this.point.wsConnValue = this.$store.state.webSocketModule.webSocket.subscribe(
				`/topic/datapoint/${pointXid}/value`,
				this.updatePointValue,
			);
			this.point.wsConnEnable = this.$store.state.webSocketModule.webSocket.subscribe(
				`/topic/datapoint/${pointXid}/enabled`,
				this.updatePointStatus,
			);
		},

		disconnectFromPointWebSocket() {
			if (!!this.point.wsConnValue) {
				this.point.wsConnValue.unsubscribe();
				this.point.wsConnValue = null;
			}
			if (!!this.point.wsConnEnable) {
				this.point.wsConnEnable.unsubscribe();
				this.point.wsConnEnable = null;
			}
		},

		updatePointValue(data) {
			const value = JSON.parse(data.body).value;
			this.$emit('value-update', value);
		},

		updatePointStatus(data) {
			const status = JSON.parse(data.body).enabled
			this.$emit('status-update', status);
			if(status === true) {
				this.getPointValue();
			}
		},

		async getPointValue() {
			try {
				const res = await this.$store.dispatch('getDataPointValueByXid', this.component.dataPointXid);
				console.log('getPointValue', res.value);
				this.$emit('status-update', res.enabled);
				if(res.enabled === true) {
					this.$emit('value-update', res.value);
				}
			} catch (e) {
				console.error(e);
			}
		}
	},
};
</script>
<style></style>
