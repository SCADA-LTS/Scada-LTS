<template>
	<div>
		<BasePointComponent
			:component="component"
			:dataTypes="[1]"
			@value-update="onValueUpdate"
			@status-update="onStatusUpdate"
			@update="$emit('update')"
			@click="$emit('click', $event)"
			@mousedown="$emit('mousedown', $event)"
		>
			<template v-slot:default>
				<div class="gv-cmp-container--button">
					<v-btn
						@click="onClicked"
						:disabled="btnDisabled || editMode"
						:style="{
							backgroundColor: component.bkgdColorOverride,
							width: component.width,
							height: component.height,
						}"
					>
						<span v-if="component.displayPointName">
							{{ component.nameOverride || component.dataPointXid }}:
						</span>
						<span>
							{{ btnStatus ? component.whenOnLabel : component.whenOffLabel }}
						</span>
					</v-btn>
				</div>
			</template>

			<template v-slot:renderer>
				<v-row>
					<v-col cols="12">
						<v-text-field 
							label="ON Label"
							v-model="component.whenOnLabel"
						></v-text-field>
					</v-col>
					<v-col cols="12">
						<v-text-field 
							label="OFF Label"
							v-model="component.whenOffLabel"
						></v-text-field>
					</v-col>
				</v-row>
			</template>
		</BasePointComponent>
	</div>
</template>
<script>
import BasePointComponent from '../BasePointComponent.vue';
export default {
	components: {
		BasePointComponent,
	},

	props: {
		component: {
			type: Object,
			required: true,
		},
	},

	data() {
		return {
			btnStatus: false,
			btnDisabled: false,
		};
	},

	computed: {
		editMode() {
			return this.$store.state.graphicalViewModule.graphicalPageEdit;
		},
	},

	methods: {
		onValueUpdate(value) {
			this.btnStatus = value == 'true';
		},
		onStatusUpdate(value) {
			if (value) {
				this.btnDisabled = false;
			} else {
				this.btnDisabled = true;
			}
		},
		async onClicked() {
			try {
				await this.$store.dispatch('setDataPointValue', {
					xid: this.component.dataPointXid,
					type: '1',
					value: this.btnStatus ? 0 : 1,
				});
			} catch (e) {
				console.error(e);
			}
		},
	},
};
</script>
<style></style>
