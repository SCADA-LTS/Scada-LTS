<template>
	<BaseViewComponent
		:component="component"
		@update="$emit('update')"
		@click="$emit('click', $event)"
		@mousedown="$emit('mousedown', $event)"
	>
		<template v-slot:default>
			<div v-html="component.content"></div>
		</template>

		<template v-slot:renderer>
			<v-row>
				<v-col cols="12">
					<v-textarea v-model="component.content"></v-textarea>
				</v-col>
			</v-row>
		</template>
	</BaseViewComponent>
</template>
<script>
import BaseViewComponent from '../BaseViewComponent.vue';
export default {
	components: {
		BaseViewComponent,
	},

	props: {
		component: {
			type: Object,
			required: true,
		},
	},
};
</script>
<style></style>
