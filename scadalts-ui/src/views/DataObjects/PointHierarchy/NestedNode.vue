<template>
	<draggable
		:id="pid"
		class="dragArea"
		tag="ul"
		:list="list"
		:group="{ name: 'g1' }"
		@end="stopDragging"
	>
		<li v-for="el in list" :key="el.name" :id="el.id" class="slts-list-item">
			<div class="slts-list-item--header">
				<div class="slts-list-item--main-header">
					<v-btn icon class="slts-list-item--icon" @click="open(el)">
						<v-icon v-if="!!el.folder">
							{{ el.open ? 'mdi-folder-open' : 'mdi-folder' }}
						</v-icon>
						<v-icon v-else>mdi-cube-outline</v-icon>
					</v-btn>
					<div class="slts-list-item--title">
						<span v-if="!!el.dataSource && !!showDataSource" class="slts-list-item--ds">
							{{ el.dataSource }} -
						</span>
						{{ el.name }}
					</div>
				</div>
				<div v-if="!!el.folder" class="slts-list-item--action-buttons">
					<v-btn icon @click="edit(el)">
						<v-icon>mdi-pencil</v-icon>
					</v-btn>
					<v-btn icon @click="info(el)">
						<v-icon>mdi-information</v-icon>
					</v-btn>
					<v-btn icon @click="del(el)">
						<v-icon>mdi-delete</v-icon>
					</v-btn>
				</div>
			</div>
			<div class="slts-list-item--content" v-if="el.open">
				<NestedNode
					v-if="!!el.children"
					:list="el.children"
					:pid="el.id"
					:showDataSource="showDataSource"
					@error="onHierarchyError"
				/>
			</div>
		</li>

		<EditNodeDialog
			ref="editNodeDialog"
			:title="$t('pointHierarchy.dialog.editNode.title')"
			@result="onNodeEdited"
		></EditNodeDialog>

		<InfoNodeDialog
			ref="infoNodeDialog"
			:title="$t('pointHierarchy.dialog.infoNode.title')"
		></InfoNodeDialog>

		<ConfirmationDialog
			:btnvisible="false"
			ref="deletionDialog"
			:title="$t('pointHierarchy.dialog.deleteNode.title')"
			:message="$t('pointHierarchy.dialog.deleteNode.message')"
			@result="onNodeDeleted"
		></ConfirmationDialog>

	</draggable>
</template>
<script>
import draggable from 'vuedraggable';
import EditNodeDialog from './dialogs/EditNodeDialog.vue';
import InfoNodeDialog from './dialogs/InfoNodeDialog.vue';
import ConfirmationDialog from '@/layout/dialogs/ConfirmationDialog.vue';

export default {
	props: {
		list: {
			required: true,
			type: Array,
		},
		pid: {
			required: true,
			type: String,
		},
		showDataSource: {
			type: Boolean,
			default: false,
		},
	},

	components: {
		draggable,
		EditNodeDialog,
		InfoNodeDialog,
		ConfirmationDialog,
	},
	name: 'NestedNode',

	data() {
		return {
			opQueue: null,
			deleteDialogVisible: false,
		};
	},

	methods: {
		stopDragging(e) {
			if (e.from.id !== e.to.id) {
				this.$store
					.dispatch('movePointHierarchyNode', {
						nodeId: e.item.id.substring(1),
						parentNodeId: e.from.id.substring(1),
						newParentNodeId: e.to.id.substring(1),
						isFolder: e.item.id.startsWith('f'),
					})
					.then((r) => {
						console.log(r);
					})
					.catch((e) => {
						this.$emit('error', e);
					});
			}
		},

		open(el) {
			if (!!el.folder) {
				el.open = !el.open;
				if (el.open) {
					this.$store
						.dispatch('fetchPointHierarchyNode', el.id.substring(1))
						.then((r) => {
							this.$store.commit('ADD_POINT_HIERARCHY_NODE', {
								parentNode: el.id,
								apiData: r,
							});
						});
				}
			} else {
				this.info(el);
			}
		},

		info(el) {
			this.$refs.infoNodeDialog.showDialog(el);
		},

		edit(el) {
			this.$refs.editNodeDialog.showDialog(el);
		},

		del(el) {
			this.$refs.deletionDialog.showDialog();
			this.opQueue = el;
		},

		onNodeEdited(node) {
			if (!!node) {
				this.$store
					.dispatch('editPointHierarchyNode', {
						nodeId: node.id.substring(1),
						parentNodeId: node.parentId,
						nodeName: node.name,
					})
					.catch((e) => {
						this.$emit('error', e);
					});
			}
		},

		onNodeDeleted(result) {
			this.deleteDialogVisible = false;
			if (!!result) {
				this.$store
					.dispatch('deletePointHierarchyNode', {
						nodeId: this.opQueue.id.substring(1),
						parentNodeId: this.opQueue.parentId,
						isFolder: this.opQueue.id.startsWith('f'),
					})
					.then(() => {
						this.$store.dispatch('fetchPointHierarchyNode', 0).then((r) => {
							this.$store.commit('SET_POINT_HIERARCHY_BY_API', r);
						});
					})
					.catch((e) => {
						this.$emit('error', e);
					});
			}
		},

		onHierarchyError(e) {
			this.$emit('error', e);
		},
	},
};
</script>
<style scoped>
.dragArea {
	min-height: 50px;
}
.slts-list-item {
	display: flex;
	flex: 1 1 100%;
	flex-direction: column;
	justify-content: center;
	padding: 5px 0;
	min-height: 32px;
}
.slts-list-item--header {
	display: flex;
	justify-content: space-between;
	min-height: 32px;
}
.slts-list-item--main-header {
	display: flex;
	align-items: center;
}
.slts-list-item--ds {
	color: #0000008f;
}
</style>
