<template>
	<BaseViewComponent
		:component="component"
		@update="$emit('update')"
		@click="$emit('click', $event)"
		@mousedown="$emit('mousedown', $event)"
	>
		<template v-slot:default>
			<a v-if="!editMode" :href="component.link" class="gv-cmp--simple-link">{{
				component.text
			}}</a>
			<span v-else>{{ component.text }}</span>
		</template>

		<template v-slot:renderer>
			<v-row>
				<v-col cols="12">
					<v-text-field label="Link location" v-model="component.link"></v-text-field>
				</v-col>
				<v-col cols="12">
					<v-text-field label="Link text" v-model="component.text"></v-text-field>
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

	computed: {
		editMode() {
			return this.$store.state.graphicalViewModule.graphicalPageEdit;
		},
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
