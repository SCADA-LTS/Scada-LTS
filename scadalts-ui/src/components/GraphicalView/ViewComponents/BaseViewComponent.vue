<template>
	<div
		@mousedown="dragStart"
		@mouseup="dragEnd"
		ref="draggableContainer"
		class="gv-component"
		v-bind:class="{ 'gv-component--edit': editMode }"
		v-bind:style="{ left: component.x + 'px', top: component.y + 'px', zIndex: menuEdit ? 1000 : component.z }"
	>
		<v-hover v-slot="{ hover }">
			<div>
				<div class="gv-component--content">
					<v-icon v-if="iconify"> mdi-cube </v-icon>
					<slot v-else/>
				</div>
				<v-expand-transition>
					<div v-if="hover" class="gv-component--buttons">
						<v-btn 
							v-if="component.displayControls === true && !editMode"
							fab x-small
						>
							<v-icon>mdi-information</v-icon>
						</v-btn>
						<v-btn v-if="editMode" fab x-small @click="showMenuEdit">
							<v-icon>mdi-pencil</v-icon>
						</v-btn>
						<v-btn
							v-if="component.displayControls === true && !editMode"
							fab
							x-small
							@click="showMenuValue = true"
						>
							<v-icon>mdi-wrench</v-icon>
						</v-btn>
					</div>
				</v-expand-transition>
			</div>
		</v-hover>
		<v-menu
			attach
			right
			v-model="menuEdit"
			:close-on-content-click="false"
			:close-on-click="false"
			transition="slide-x-transition"
		>
			<v-card class="settings-menu">
				<v-card-title>
					<span> Settings </span>
					<v-spacer> </v-spacer>
					<v-btn icon @click="deleteComponent">
						<v-icon>mdi-delete</v-icon>
					</v-btn>
				</v-card-title>
				<v-card-text>
					<v-tabs>
						<v-tab> Layouy </v-tab>
						<v-tab> Renderer </v-tab>
						<v-tab-item key="1" class="small-margin--vertical">
							<v-row dense>
								<v-col cols="6">
									<v-text-field
										dense
										label="Position X"
										v-model="component.x"
									></v-text-field>
								</v-col>
								<v-col cols="6">
									<v-text-field
										dense
										label="Position Y"
										v-model="component.y"
									></v-text-field>
								</v-col>
								<v-col cols="6">
									<span> Layer </span>
								</v-col>
								<v-col cols="6" class="settings-layers">
									<v-btn fab x-small @click="moveComponentUp">
										<v-icon>mdi-arrow-up</v-icon>
									</v-btn>
									<span> {{component.z}} </span>
									<v-btn fab x-small @click="moveComponentDown">
										<v-icon>mdi-arrow-down</v-icon>
									</v-btn>
								</v-col>

								<slot name="layout"> </slot>
							</v-row>
						</v-tab-item>
						<v-tab-item key="2">
							<slot name="renderer"> </slot>
						</v-tab-item>
					</v-tabs>
				</v-card-text>
				<v-card-actions>
					<v-spacer> </v-spacer>
					<v-btn text @click="menuEditCancel"> Cancel </v-btn>
					<v-btn text @click="menuEditSave"> Save </v-btn>
				</v-card-actions>
			</v-card>
		</v-menu>
		<v-menu attach right v-model="showMenuValue" :close-on-content-click="false">
			<v-card class="settings-menu">
				<v-card-text>
					<v-text-field
						dense
						label="Set value"
						append-icon="mdi-send"
						@click:append="sendValue"
					></v-text-field>
				</v-card-text>
			</v-card>
		</v-menu>
	</div>
</template>
<script>
export default {
	props: {
		component: {
			type: Object,
			required: true,
		},
	},

	data() {
		return {
			menuEdit: false,
			showMenuValue: false,

			draggableProperties: {
				dragging: false,
				clientX: null,
				clientY: null,
				movementX: 0,
				movementY: 0,
				width: 0,
				height: 0,
			},
		};
	},

	computed: {
		editMode() {
			return this.$store.state.graphicalViewModule.graphicalPageEdit;
		},
		iconify() {
			return this.$store.state.graphicalViewModule.graphicalPageIconify;
		},
		canvasWidth() {
			let width = this.$store.state.graphicalViewModule.resolution.width;
			return Number(width);

		},
		canvasHeight() {
			let height = this.$store.state.graphicalViewModule.resolution.height;
			return Number(height);
		},
	},

	methods: {
		sendValue() {
			this.showMenuValue = false;
			console.log('value');
		},
		showMenuEdit() {
			this.$store.commit('SET_COMPONENT_EDIT', this.component);
			this.menuEdit = true;
		},
		hideMenuEdit() {
			this.menuEdit = false;
			this.$emit('update');
		},
		menuEditSave() {
			console.log('save');
			console.log(this.component);
			this.hideMenuEdit();
		},
		menuEditCancel() {
			console.log('cancl');
			console.log(this.component);
			this.$store.commit('REVERT_COMPONENT_EDIT');
			this.hideMenuEdit();
		},
		dragStart(event) {
			if (this.editMode) {
				event.preventDefault();
				this.draggableProperties.dragging = true;
				this.draggableProperties.clientX = event.clientX;
				this.draggableProperties.clientY = event.clientY;
				this.draggableProperties.width = this.$refs.draggableContainer.clientWidth;
				this.draggableProperties.height = this.$refs.draggableContainer.clientHeight;
				document.onmousemove = this.dragMove;
				document.onmouseup = this.dragEnd;
			}
		},
		dragMove(event) {
			const cmp = this.$refs.draggableContainer;
			const props = this.draggableProperties;
			event.preventDefault();
			props.movementX = props.clientX - event.clientX;
			props.movementY = props.clientY - event.clientY;
			props.clientX = event.clientX;
			props.clientY = event.clientY;

			if (cmp.offsetTop >= 0) {
				cmp.style.top = cmp.offsetTop - props.movementY + 'px';
			} else {
				cmp.style.top = '0px';
			}

			if( cmp.offsetTop + cmp.clientHeight >= this.canvasHeight ) {
				cmp.style.top = this.canvasHeight - cmp.clientHeight + 'px';
			}

			if (cmp.offsetLeft >= 0) {
				cmp.style.left = cmp.offsetLeft - props.movementX + 'px';
			} else {
				cmp.style.left = '0px';
			}

			if( cmp.offsetLeft + cmp.clientWidth >= this.canvasWidth ) {
				cmp.style.left = this.canvasWidth - cmp.clientWidth + 'px';
			}
		},
		dragEnd() {
			this.draggableProperties.dragging = false;
			this.component.x =
				this.$refs.draggableContainer.offsetLeft >= 0
					? this.$refs.draggableContainer.offsetLeft
					: 0;
			this.component.y =
				this.$refs.draggableContainer.offsetTop >= 0
					? this.$refs.draggableContainer.offsetTop
					: 0;
			document.onmouseup = null;
			document.onmousemove = null;
		},

		moveComponentUp() {
			this.component.z = Number(this.component.z) + 1;
		},

		moveComponentDown() {
			this.component.z = Number(this.component.z) - 1;
		},

		deleteComponent() {
			this.$store.commit('DELETE_COMPONENT_EDIT');
		}
	},
};
</script>
<style>
.small-margin--vertical {
	margin-top: 15px;
	margin-bottom: 10px;
}
.gv-component {
	position: absolute;
	padding: 5px;
}
.gv-component--edit:hover {
	background-color: white;
	border: dashed 1px black;
	z-index: 1000;
}
.gv-component .v-menu--attached > div {
	left: 100px !important;
	top: -10px;
}
.gv-component--buttons {
	position: absolute;
	top: 0;
	right: -25px;
	display: flex;
	flex-direction: column;
}
.gv-component--buttons > * {
	margin-bottom: 5px;
}
.settings-menu {
	width: 400px;
}
.settings-layers {
	display: flex;
	justify-content: space-around;
    align-items: center;
}
</style>
