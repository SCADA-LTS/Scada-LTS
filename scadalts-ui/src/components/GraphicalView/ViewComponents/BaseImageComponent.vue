<template>
	<BasePointComponent
		:component="component"
		:dataTypes="dataTypes"
		:isGraphic="true"
		@update="$emit('update')"
		@value-update="onValueUpdate"
		@status-update="onStatusUpdate"
		@click="$emit('click', $event)"
		@mousedown="$emit('mousedown', $event)"
	>
		<template v-slot:default>
			<slot />
		</template>
		<template v-slot:renderer>
			<v-row>
				<v-col cols="6">
					<v-switch label="Display text"></v-switch>
				</v-col>
				<v-col cols="6">
					<v-select
						label="Image set"
						:items="imageSet"
						item-text="name"
						item-value="id"
						v-model="component.imageSetId"
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

	data() {
		return {
			imageArray: [],
		};
	},

	mounted() {
		this.getImageSetDeatils();
	},

	methods: {
		onValueUpdate(value) {
			this.$emit('value-update', value);
		},

		onStatusUpdate(value) {
			this.$emit('status-update', value);
		},
		imageSetChanged(state) {
			this.getImageSetDeatils();
		},

		async getImageSetDeatils() {
			try {
			    if(this.component.imageSetId) {
                    const res = await this.$store.dispatch(
                        'getImageSetDetails',
                        this.component.imageSetId,
                    );
                    this.$emit('image-update', res);
				}
			} catch (e) {
				console.error(e);
			}
		},
	},
};
</script>
<style>
.gv-image-container {
	display: flex;
	justify-content: center;
	align-items: center;
	position: relative;
}
.gv-image-description {
	position: absolute;
	bottom: 15px;
	left: 9%;
	width: 80%;
	text-align: center;
	background: #000000a6;
	color: white;
	border-radius: 6px;
	box-shadow: 1px 2px 2px #0000002e;
}
.gv-image-thumbnail {
	height: 60px;
	width: auto;
	object-fit: scale-down;
}
</style>
