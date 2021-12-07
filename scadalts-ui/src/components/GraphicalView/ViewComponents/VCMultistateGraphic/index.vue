<template>
	<BaseImageComponent
		:component="component"
		:dataTypes="[2]"
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
				<v-col
					cols="4"
					v-for="(img, i) in imageArray"
					:key="i"
					@click="onImageSelected(i)"
					class="gv-image-container"
				>
					<span
						class="gv-image-description gv-image-description--default"
						v-if="component.defaultImage === i"
						>Default</span
					>
					<span class="gv-image-description">{{ getState(i) }}</span>
					<img :src="img" class="gv-image-thumbnail" alt="Image" />
				</v-col>
			</v-row>

			<ImageDialog
				ref="imageDialog"
				@result="onImageIndexUpdate"
				@reset="onImageIndexReset"
				@default="onImageIndexDefault"
			></ImageDialog>
		</template>
	</BaseImageComponent>
</template>
<script>
import ImageDialog from './dialog.vue';
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
			imageSet: null,
			activeGraphic: null,
			imageArray: [],
			rendering: 0,
		};
	},

	methods: {
		onValueUpdate(val) {
			let value = Number(val);
			const state = this.component.imageStateList.find((s) => s.value == value);
			if (!!state) {
				this.activeGraphic = this.imageSet.imageFilenames[state.key];
			} else {
				this.activeGraphic = this.imageSet.imageFilenames[this.component.defaultImage];
			}
		},
		onStatusUpdate(value) {
			if (value == 'false') {
				this.activeGraphic = this.imageSet.imageFilenames[this.component.defaultImage];
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
			this.setState(result.image, result.state);
			if (result.default) {
				this.component.defaultImage = result.image;
			}
			this.rendering++;
		},

		onImageIndexDefault(index) {
			this.component.defaultImage = index;
			this.rendering++;
		},

		onImageIndexReset(index) {
			if (this.component.defaultImage === index) {
				this.component.defaultImage = null;
			}
			const arr = this.component.imageStateList;
			if (!!arr && arr.length > 0) {
				this.component.imageStateList = arr.filter((s) => s.key !== index);
			}
			this.rendering++;
		},

		getState(index) {
			const arr = this.component.imageStateList;
			if (!!arr && arr.length > 0) {
				for (let i = 0; i < arr.length; i++) {
					if (arr[i].key === index) {
						return `State: ${arr[i].value}`;
					}
				}
			}
			return '';
		},

		setState(index, state) {
			const arr = this.component.imageStateList || [];
			const arrIndex = arr.findIndex((s) => s.value === state);
			if (arrIndex > -1) {
				arr[arrIndex].key = index;
			} else {
				arr.push({
					value: state,
					key: index,
				});
			}
		},
	},
};
</script>
<style>
.gv-image-description--default {
	top: 0;
	bottom: unset;
}
</style>
