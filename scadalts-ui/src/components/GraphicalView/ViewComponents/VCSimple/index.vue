<template>
	<div>
		<BasePointComponent
			:component="component"
			@value-update="onValueUpdate"
			@status-update="onStatusUpdate"
			@color-update="onColorUpdate"
			@update="$emit('update')"
			@click="$emit('click', $event)"
			@mousedown="$emit('mousedown', $event)"
		>
			<template v-slot:default>
				<div
					class="gv-cmp--simple"
					:style="{ backgroundColor: component.bkgdColorOverride }"
				>
					<div>
						<span v-if="component.displayPointName">
							{{ component.nameOverride || component.name }}:
						</span>
						<span v-bind:style="{color:renderer_color}">
							{{ content }}
						</span>
					</div>
				</div>
			</template>

			<template v-slot:renderer>
				
				<v-row>
					<v-col> Hi! slot </v-col>
				</v-row>
			</template>
			<template v-slot:info>
				<v-row dense>
					<v-col>Current value: {{content}} </v-col>
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
			content: '(n/a)',
			renderer_color: '#212121',
		};
	},

	methods: {
		onValueUpdate(value) {
			this.content = value;

		},
		onStatusUpdate(value) {
			if (value) {
				this.content = 'enabled';
			} else {
				this.content = '(N/A)';
			}
		},
		onColorUpdate(color) {
			this.renderer_color = color;
		}
	},
};
</script>
<style>
.gv-cmp--simple {
	min-width: 10px;
	min-height: 10px;
	border: 1px solid var(--v-primary-base);
	border-radius: 3px;
	padding: 2px 10px;
}
</style>
