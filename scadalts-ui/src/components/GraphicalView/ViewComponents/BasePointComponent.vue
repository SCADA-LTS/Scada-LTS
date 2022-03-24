<template>
	<BaseViewComponent
		:component="component"
		@update="$emit('update')"
		@send-value="onSendValue"
		@click="$emit('click', $event)"
		@mousedown="$emit('mousedown', $event)"
		@edit-menu="onEditMenu"
	>
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
				<v-col cols="12" v-if="menuVisible">
					<DataPointSerachComponent
						v-model="component.dataPointXid"
						:dataTypes="dataTypes"
						@change="onPointChange"
					></DataPointSerachComponent>
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
		<template v-slot:info>
			<v-row dense>
				<v-col cols="12">
					<v-btn fab @click="showDetails" x-small elevation="0">
						<v-icon>mdi-cube-outline</v-icon>
					</v-btn>
					Show data point details
				</v-col>

				<v-col cols="12">
					<v-btn fab @click="fetchDataPointEvents" x-small elevation="0">
						<v-icon>mdi-refresh</v-icon>
					</v-btn>
					Show data point events
				</v-col>
				<v-col cols="12">
					<v-row>
						<v-col>
							<v-skeleton-loader v-if="eventsLoading" type="article"></v-skeleton-loader>
							<v-list v-else>
								<v-list-item v-for="e in events" :key="e.id">
									<v-list-item-icon>
										<img :src="alarmFlags[e.alarmLevel].image" />
									</v-list-item-icon>
									<v-list-item-content>
										<v-list-item-title>
											<span>
												{{
													$t(
														getEventMessageType(e.message),
														prepareEventMessage(e.message),
													)
												}}
											</span>
										</v-list-item-title>
										<v-list-item-subtitle>
											<span>
												{{ $date(e.activeTs).format('YYYY-MM-DD hh:mm:ss') }}
											</span>
										</v-list-item-subtitle>
									</v-list-item-content>
								</v-list-item>
							</v-list>
						</v-col>
					</v-row>
				</v-col>
			</v-row>
			<slot name="info"></slot>
		</template>
	</BaseViewComponent>
</template>
<script>
import DataPointSerachComponent from '@/layout/buttons/DataPointSearchComponent';
import BaseViewComponent from './BaseViewComponent.vue';
import TextRenderer from '../../../bl/TextRender.js'


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
			menuVisible: false,
			point: {
				wsConnValue: null,
				wsConnEnable: null,
			},
			events: [],
			eventsLoading: false,
			textRenderer: {},
		};
	},

	computed: {
		alarmFlags() {
			return this.$store.state.staticResources.alarmFlags;
		},
	},

	mounted() {
		this.connectToPointWebSocket(this.component.dataPointId);
		this.getDataPointType();
		this.getPointValue();
	},

	destroyed() {
		this.disconnectFromPointWebSocket();
	},

	methods: {
		onPointChange(point) {
			this.connectToPointWebSocket(point.id);
		},

		connectToPointWebSocket(pointId) {
			this.disconnectFromPointWebSocket();
			this.point.wsConnValue = this.$store.state.webSocketModule.webSocket.subscribe(
				`/topic/datapoint/${pointId}/value`,
				this.updatePointValue,
			);
			this.point.wsConnEnable = this.$store.state.webSocketModule.webSocket.subscribe(
				`/topic/datapoint/${pointId}/enabled`,
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
			const value = this.textRenderer.render(JSON.parse(data.body).value)
			this.$emit('value-update', value)
		},

		updatePointStatus(data) {
			const status = JSON.parse(data.body).enabled;
			this.$emit('status-update', status);
			if (status === true) {
				this.getPointValue();
			}
		},


		async getPointValue() {
			try {
				const res = await this.$store.dispatch(
					'getDataPointValueByXid',
					this.component.dataPointXid,
				);
				this.$emit('status-update', res.enabled);
				if (res.enabled === true) {
					const value = this.textRenderer.render(res.value);
					this.$emit('value-update', value);
				}
			} catch (e) {
				console.error(e);
			}
		},

		showDetails() {
			this.$router.push({ path: `/datapoint-details/${this.component.dataPointId}` });
		},

		async fetchDataPointEvents() {
			try {
				this.events = await this.$store.dispatch('fetchDataPointEvents', {
					datapointId: this.component.dataPointId,
					limit: 3,
				});
			} catch (e) {
				console.error(e);
			}
		},

		getEventMessageType(message) {
			return message.split('|')[0];
		},

		prepareEventMessage(message) {
			let response = message.replace(/[\[\]]/g, '');
			response = response.split('|');
			return response.slice(1);
		},

		onEditMenu(visible) {
			this.menuVisible = visible;
		},

		async onSendValue(value) {
			try {
				const type = await this.getDataPointType();
				await this.$store.dispatch('setDataPointValue', {
					xid: this.component.dataPointXid,
					type: type,
					value: value,
				});
			} catch (e) {
				console.error(e);
			}
		},

		async getDataPointType() {
			try {
				const dp = await this.$store.dispatch(
					'getDataPointValueByXid',
					this.component.dataPointXid,
				);

				let renderer = dp.textRenderer
				this.textRenderer = new TextRenderer(renderer)
				
				switch (dp.type) {
					case 'BinaryValue':
						return 1;
					case 'MultistateValue':
						return 2;
					case 'NumericValue':
						return 3;
					case 'AlphanumericValue':
						return 4;
					default:
						return 0;
				}
				
			} catch (e) {
				console.error(e);
				return -1;
			}
		},
	},
};
</script>
<style></style>
