<template>
	<BaseImageComponent
		:component="component"
        :dataTypes="[3]"
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
            
			<v-row>
                <v-col cols="6">
                    <v-text-field
                        label="Minimum"
                        v-model="component.min"
                    ></v-text-field>
                </v-col>
                <v-col cols="6">
                    <v-text-field
                        label="Maximum"
                        v-model="component.max"
                    ></v-text-field>
                </v-col>
				<v-col cols="4" v-for="(img,i) in imageArray" :key="i" @click="onImageSelected(i)" class="gv-image-container">
					<img :src="img" class="gv-image-thumbnail" alt="Image"/>
				</v-col>
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
			imageSet: {imageFilenames:[]},
			activeGraphic: null,
            imageArray: [],
		};
	},

    methods: {
		onValueUpdate(val) {
            let value = Number(val);
            this.setActiveImage(value);
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

        setActiveImage(value) {
            let index = Math.floor((value - this.component.min) / (this.component.max - this.component.min) * this.imageArray.length);
            this.activeGraphic = this.imageSet.imageFilenames[index];
        }
	},
};
</script>
<style></style>
