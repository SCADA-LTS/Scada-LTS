<template>
	<BaseImageComponent
		:component="component"
		:dataTypes="[1]"
		@update="$emit('update')"
		@value-update="onValueUpdate"
		@status-update="onStatusUpdate"
		@image-update="onImageUpdate"
		@click="$emit('click', $event)"
		@mousedown="$emit('mousedown', $event)"
	>
		<template v-slot:default>
			<div>
				<img :src="activeGraphic" />
			</div>
		</template>
		<template v-slot:renderer>
			<v-row :key="rendering">
				<v-col cols="4" v-for="(img,i) in imageArray" :key="i" @click="onImageSelected(i)" class="gv-image-container">
					<span class="gv-image-description" v-if="component.oneImage === i">One Image</span>
					<span class="gv-image-description" v-if="component.zeroImage === i">Zero Image</span>
					<img :src="img" class="gv-image-thumbnail" alt="Image"/>
				</v-col>
			</v-row>

			<ImageDialog ref="imageDialog" @result="onImageIndexUpdate">
			</ImageDialog>
		</template>
	</BaseImageComponent>
</template>
<script>
import ImageDialog from './dialog.vue'
import BaseImageComponent from '../BaseImageComponent.vue';
export default {
	components: {
		BaseImageComponent,
		ImageDialog,
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
			imageArray: [],
			rendering: 0,
		};
	},

	methods: {
		onValueUpdate(value) {
			this.content = value;
			if (value == 'true') {
				this.activeGraphic = this.imageSet.imageFilenames[this.component.oneImage];
			} else {
				this.activeGraphic = this.imageSet.imageFilenames[this.component.zeroImage];
			}
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
			this.imageArray = value.imageFilenames;
		},

		onImageSelected(image) {
			this.$refs.imageDialog.openDialog(image);
		},

		onImageIndexUpdate(result) {
			if(result.index === 0) {
				this.component.zeroImage = result.image;
			}
			if(result.index === 1) {
				this.component.oneImage = result.image;
			}
			this.rendering++;
		}
	},
};
</script>

