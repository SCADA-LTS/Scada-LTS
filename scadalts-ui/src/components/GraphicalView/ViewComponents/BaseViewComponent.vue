<template>
	<div
		@mousedown="dragStart"
		@click="setActive"
		ref="draggableContainer"
		class="gv-component"
		v-show="visible"
		v-bind:class="{ 'gv-component--edit': editMode, 'gv-component--selected': selected }"
		v-bind:style="{
			left: component.x + 'px',
			top: component.y + 'px',
			zIndex: menuEdit ? 1000 : component.z,
		}"
	>
		<v-hover v-slot="{ hover }">
			<div>
				<div class="gv-component--content">
					<v-icon v-if="iconify"> mdi-cube </v-icon>
					<slot v-else />
				</div>
				<v-expand-transition>
					<div v-if="hover" class="gv-component--buttons">
						<v-btn
							v-if="component.displayControls === true && !editMode"
							fab
							x-small
							@click="showMenuInfo = true"
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
						<v-tab> Layout </v-tab>
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
									<v-btn fab x-small @click="moveComponentDown">
										<v-icon>mdi-chevron-down</v-icon>
									</v-btn>
									<span> {{ component.z }} </span>
									<v-btn fab x-small @click="moveComponentUp">
										<v-icon>mdi-chevron-up</v-icon>
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
						v-model="newValue"
						@click:append="sendValue"
					></v-text-field>
				</v-card-text>
			</v-card>
		</v-menu>
		<v-menu attach right v-model="showMenuInfo" :close-on-content-click="false">
			<v-card class="settings-menu">
				<v-card-text>
					<h3>{{ $t(`view.cmp.${component.defName}`) }} component</h3>
					<slot name="info"> </slot>
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
			selected: false,
			visible: true,
			menuEdit: false,
			showMenuValue: false,
			showMenuInfo: false,
			newValue: '',
		};
	},

	computed: {
		editMode() {
			return this.$store.state.graphicalViewModule.graphicalPageEdit;
		},
		iconify() {
			return this.$store.state.graphicalViewModule.graphicalPageIconify;
		},
	},

	methods: {
		setActive() {
			if (!this.selected && this.editMode) {
				this.selected = true;
				this.$emit('click', this);
			}
		},
		sendValue() {
			this.showMenuValue = false;
			this.$emit('send-value', this.newValue);
			this.newValue = '';
		},
		showMenuEdit() {
			this.$store.commit('SET_COMPONENT_EDIT', this.component);
			this.menuEdit = true;
			this.$emit('edit-menu', true);
		},
		hideMenuEdit() {
			this.menuEdit = false;
			this.$emit('edit-menu', false);
			this.$emit('update');
		},
		menuEditSave() {
			this.hideMenuEdit();
		},
		menuEditCancel() {
			this.$store.commit('REVERT_COMPONENT_EDIT');
			this.hideMenuEdit();
		},
		dragStart(event) {
			this.$emit('mousedown', { event, ref: this });
		},

		moveComponentUp() {
			this.component.z = Number(this.component.z) + 1;
		},

		moveComponentToTop() {
			let max = this.$store.state.graphicalViewModule.graphicalPage.viewComponents.reduce(
				(max, c) => {
					return Math.max(max, c.z);
				},
				0,
			);
			this.component.z = max + 1;
		},

		moveComponentDown() {
			if (this.component.z > 1) {
				this.component.z = Number(this.component.z) - 1;
			}
		},

		moveComponentToBottom() {
			this.component.z = 1;
		},

		deleteComponent() {
			this.$store.commit('DELETE_COMPONENT_EDIT');
		},
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
.gv-component--selected {
	border: dashed 1px var(--v-primary-base);
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
