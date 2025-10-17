<template>
	<div>
		<div v-if="componentEditor">
			<v-container fluid>
				<v-row>
					<v-col cols="12" md="6">
						<v-text-field v-model="componentData.data.label" label="Label:" />
					</v-col>
					<v-col cols="12" md="6">
						<v-text-field v-model="componentData.data.dpXid" label="Point XID:" />
					</v-col>
				</v-row>
				<v-row>
					<v-col cols="12" md="3">
						<v-switch v-model="componentData.data.rotationEnabled" label="Rotate" />
					</v-col>
					<v-col cols="12" md="9" v-if="componentData.data.rotationEnabled">
						<v-row>
							<v-col cols="12" md="4">
								<v-text-field
									v-model="componentData.data.rotationDuration"
									label="Duration"
								/>
							</v-col>
							<v-col cols="12" md="4">
								<v-text-field
									v-model="componentData.data.rotationAngle"
									label="Rotation angle"
								/>
							</v-col>
							<v-col cols="12" md="4">
								<v-checkbox
									v-model="componentData.data.rotationLoop"
									label="Loop rotation"
								/>
							</v-col>
						</v-row>
						<v-row>
							<v-col cols="12" md="6">
								<v-select
									v-model="componentData.data.rotationCondition"
									:items="conditions"
									label="Condition"
								/>
							</v-col>
							<v-col cols="12" md="6">
								<v-text-field
									v-model="componentData.data.rotationBoundary"
									label="Condition value"
								/>
							</v-col>
						</v-row>
					</v-col>
				</v-row>
				<v-row>
					<v-col cols="12" md="3">
						<v-switch
							v-model="componentData.data.colorChangeEnabled"
							label="Color Change"
						/>
					</v-col>
					<v-col cols="12" md="9" v-if="componentData.data.colorChangeEnabled">
						<v-row>
							<v-col cols="12" md="3">
								<v-text-field
									v-model="componentData.data.colorChangeBase"
									label="Base color"
								/>
							</v-col>
							<v-col cols="12" md="3">
								<v-text-field
									v-model="componentData.data.colorChangeAlternate"
									label="Alternate color"
								/>
							</v-col>
							<v-col cols="12" md="3">
								<v-select
									v-model="componentData.data.colorChangeCondition"
									:items="conditions"
									label="Condition"
								/>
							</v-col>
							<v-col cols="12" md="3">
								<v-text-field
									v-model="componentData.data.colorChangeBoundary"
									label="Condition value"
								/>
							</v-col>
						</v-row>
					</v-col>
				</v-row>
				<v-row>
					<v-col cols="12" md="2">
						<v-btn @click="saveData">Save</v-btn>
					</v-col>
				</v-row>
			</v-container>
		</div>
	</div>
</template>
<script>
import CustomComponentsBase from './CustomComponentBase.vue';

export default {
	extends: CustomComponentsBase,
	data() {
		return {
			refreshInterval: undefined,
			data: {},
			conditions: ['Less than', 'Equal', 'Greater than'],
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
			this.data.ts = -Infinity;
		}
	},
	methods: {
		updateComponent() {
			if (this.componentData.data.dpXid) {
				if (this.componentData.data.dpXid !== '') {
					this.getDataPointValueXid(this.componentData.data.dpXid).then((data) => {
						if (this.data.ts === undefined) {
							this.data.ts = -Infinity;
						}
						if (this.data.ts < data.ts) {
							if (data.type === 'NumericValue') {
								this.data.value = parseFloat(data.formattedValue);
							} else if (data.type === 'BinaryValue') {
								this.data.value = data.value === 'true' ? 'Enabled' : 'Disabled';
							} else {
								this.data.value = data.formattedValue;
							}
							this.changeComponentText(`${this.componentId}_value`, this.data.value);
							this.data.ts = data.ts;
							this.validateRotation(data.value, data.type);
							this.validateColor(data.value, data.type);
						}
					});
				}
			}
			if (this.componentData.data.label)
				this.changeComponentText(
					`${this.componentId}_label`,
					this.componentData.data.label,
				);
		},
		startRefresh() {
			this.refreshInterval = setInterval(() => {
				this.updateComponent();
			}, 5000);
		},
		validateRotation(value, type) {
			if (this.componentData.data.rotationEnabled) {
				let rotate = false;
				if (this.componentData.data.rotationCondition === 'Equal') {
					if (type === 'NumericValue') {
						rotate =
							parseFloat(value) === parseFloat(this.componentData.data.rotationBoundary);
					} else if (type === 'MultistateValue') {
						rotate =
							parseInt(value) === parseInt(this.componentData.data.colorChangeBoundary);
					} else {
						rotate = value === this.componentData.data.rotationBoundary;
					}
				} else if (this.componentData.data.rotationCondition === 'Less than') {
					if (type === 'NumericValue') {
						rotate =
							parseFloat(value) < parseFloat(this.componentData.data.rotationBoundary);
					} else if (type === 'MultistateValue') {
						rotate =
							parseInt(value) < parseInt(this.componentData.data.colorChangeBoundary);
					} else {
						rotate = value !== this.componentData.data.rotationBoundary;
					}
				} else if (this.componentData.data.rotationCondition === 'Greater than') {
					if (type === 'NumericValue') {
						rotate =
							parseFloat(value) > parseFloat(this.componentData.data.rotationBoundary);
					} else if (type === 'MultistateValue') {
						rotate =
							parseInt(value) > parseInt(this.componentData.data.colorChangeBoundary);
					} else {
						rotate = value !== this.componentData.data.rotationBoundary;
					}
				}
				if (rotate) {
					this.rotateComponent(
						this.componentId,
						this.componentData.data.rotationDuration,
						this.componentData.data.rotationAngle,
						this.componentData.data.rotationLoop,
					);
				} else {
					this.finishComponentAnimation(this.componentId);
				}
			}
		},
		validateColor(value, type) {
			if (this.componentData.data.colorChangeEnabled) {
				let color;
				if (this.componentData.data.colorChangeCondition === 'Equal') {
					if (type === 'NumericValue') {
						color =
							parseFloat(value) ===
							parseFloat(this.componentData.data.colorChangeBoundary)
								? this.componentData.data.colorChangeBase
								: this.componentData.data.colorChangeAlternate;
					} else if (type === 'MultistateValue') {
						color =
							parseInt(value) === parseInt(this.componentData.data.colorChangeBoundary)
								? this.componentData.data.colorChangeBase
								: this.componentData.data.colorChangeAlternate;
					} else {
						color =
							value === this.componentData.data.colorChangeBoundary
								? this.componentData.data.colorChangeBase
								: this.componentData.data.colorChangeAlternate;
					}
				} else if (this.componentData.data.colorChangeCondition === 'Less than') {
					if (type === 'NumericValue') {
						color =
							parseFloat(value) < parseFloat(this.componentData.data.colorChangeBoundary)
								? this.componentData.data.colorChangeBase
								: this.componentData.data.colorChangeAlternate;
					} else if (type === 'MultistateValue') {
						color =
							parseInt(value) < parseInt(this.componentData.data.colorChangeBoundary)
								? this.componentData.data.colorChangeBase
								: this.componentData.data.colorChangeAlternate;
					} else {
						color =
							value !== this.componentData.data.colorChangeBoundary
								? this.componentData.data.colorChangeBase
								: this.componentData.data.colorChangeAlternate;
					}
				} else if (this.componentData.data.colorChangeCondition === 'Greater than') {
					if (type === 'NumericValue') {
						color =
							parseFloat(value) > parseFloat(this.componentData.data.colorChangeBoundary)
								? this.componentData.data.colorChangeBase
								: this.componentData.data.colorChangeAlternate;
					} else if (type === 'MultistateValue') {
						color =
							parseInt(value) > parseInt(this.componentData.data.colorChangeBoundary)
								? this.componentData.data.colorChangeBase
								: this.componentData.data.colorChangeAlternate;
					} else {
						color =
							value !== this.componentData.data.colorChangeBoundary
								? this.componentData.data.colorChangeBase
								: this.componentData.data.colorChangeAlternate;
					}
				}
				if (color !== undefined)
					this.changeComponentColor(`${this.componentId}_background`, color);
			}
		},
	},
};
</script>
