<template>
	<div>
		<BasePointComponent
			:component="component"
			@value-update="onValueUpdate"
			@status-update="onStatusUpdate"
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
						<span>
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
import TextRenderer from '../../../../bl/TextRender'
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
