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
							{{ component.nameOverride || component.dataPointXid }}:
						</span>
						<span>
							{{ content }}
						</span>
					</div>
				</div>
			</template>

			<template v-slot:renderer>
				<v-row>
					<v-col cols="12">
						<v-textarea label="Script" v-model="component.script"></v-textarea>
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
			content: '(n/a)',
		};
	},

	methods: {
		onValueUpdate(value) {
			this.evaluateScript();
		},
		onStatusUpdate(value) {
			if (value) {
				this.evaluateScript();
			} else {
				this.content = '(N/A)';
			}
		},

		async evaluateScript() {
			try {
				const result = await this.$store.dispatch('evaluateScriptComponent', {
					script: this.component.script,
					context: this.component.dataPointXid,
				});
				this.content = result;
			} catch (e) {
				this.content = '(Error)';
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
