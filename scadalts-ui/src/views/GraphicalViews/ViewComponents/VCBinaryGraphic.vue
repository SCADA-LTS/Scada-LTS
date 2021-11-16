<template>
	<BaseImageComponent
		:component="component"
		@update="$emit('update')"
		@value-update="onValueUpdate"
		@status-update="onStatusUpdate"
		@image-update="onImageUpdate"
	>
		<template v-slot:default>
			<div>
				<img :src="activeGraphic" />
			</div>
		</template>
		<template v-slot:renderer>
			<v-row>
				<v-col> Hi! slot </v-col>
			</v-row>
		</template>
	</BaseImageComponent>
</template>
<script>
import BaseImageComponent from '../BaseImageComponent.vue';
export default {
	components: {
		BaseImageComponent,
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
			imageSet: null,
			activeGraphic: null,
		};
	},

    methods: {
		onValueUpdate(value) {
			console.log('onValueUpdate');
			this.content = value;
			if(value == 'true') {
				this.activeGraphic = this.imageSet.imageFilenames[this.component.oneImageIndex];
			} else {
				this.activeGraphic = this.imageSet.imageFilenames[this.component.zeroImageIndex];
			}
			console.log('onValueUpdate::BaseImage', this.activeGraphic);
		},
		onStatusUpdate(value) {
			if (value == 'false') {
				this.content = 'disabled';
			} else {
				this.content = 'enabled';
			}
		},
		onImageUpdate(value) {
			this.imageSet = value;
			console.log('onImageUpdate', this.imageSet);
		}
	},
};
</script>
<style></style>
