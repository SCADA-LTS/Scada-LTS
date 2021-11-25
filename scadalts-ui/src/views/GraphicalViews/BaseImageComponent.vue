<template>
	<BasePointComponent
		:component="component"
		:dataTypes="dataTypes"
		@update="$emit('update')"
		@value-update="onValueUpdate"
		@status-update="onStatusUpdate"
	>
		<template v-slot:default>
			<slot />
		</template>
		<template v-slot:renderer>
			<v-row>
				<v-col cols="12">
					<v-switch label="Display text"></v-switch>
				</v-col>
				<v-col cols="12">
					<v-select
						label="Image set"
						:items="imageSet"
						v-model="component.imageSet"
						@change="imageSetChanged"
					></v-select>
				</v-col>
			</v-row>
			<slot name="renderer"> </slot>
		</template>
	</BasePointComponent>
</template>
<script>
import BasePointComponent from './BasePointComponent.vue';
export default {
	components: {
		BasePointComponent,
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

	computed: {
		imageSet() {
			return this.$store.state.graphicalViewModule.imageSets;
		},
	},

	mounted() {
		this.getImageSetDeatils();
	},

	methods: {
		onValueUpdate(value) {
			console.log('onValueUpdate::BaseImage', value);
			this.$emit('value-update', value);
		},

		onStatusUpdate(value) {
			this.$emit('status-update', value);
		},
		imageSetChanged(state) {
			console.log('imageSetChanged', state);
			this.getImageSetDeatils();
		},

		async getImageSetDeatils() {
			try {
				const res = await this.$store.dispatch('getImageSetDetails', this.component.imageSet);
				console.log('getImageSetDeatils', res);
				this.$emit('image-update', res);
			} catch (e) {
				console.error(e);
			}
		}
	},
};
</script>
<style></style>
