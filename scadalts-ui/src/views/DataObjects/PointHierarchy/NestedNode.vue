<template>
	<draggable
		:id="pid"
		class="dragArea"
		tag="ul"
		:list="list"
		:group="{ name: 'g1' }"
		@end="stopDragging"
	>
		<li v-for="el in list" :key="el.name" :id="el.id">
			<div>
				<div class="header">
					<v-icon v-if="!!el.folder" @click="open(el)">
						{{ el.open ? 'mdi-folder-open' : 'mdi-folder' }}
					</v-icon>
					<v-icon v-else>mdi-cube-outline</v-icon>
					<span>{{ el.name }}</span>
					<span v-if="!!el.folder">
						<v-icon @click="create(el)">mdi-plus-box</v-icon>
						<v-icon @click="edit(el)">mdi-pencil</v-icon>
						<v-icon @click="del(el)">mdi-delete</v-icon>
					</span>
				</div>
				<div class="content" v-if="el.open">
					<NestedNode v-if="!!el.children" :list="el.children" :pid="el.id" />
				</div>
			</div>
		</li>
		<CreatePhNode
			ref="createNode"
			title="Create new"
			@result="onNodeCreated"
		></CreatePhNode>
		<CreatePhNode
			ref="editNode"
			title="Edit existing"
			@result="onNodeEdited"
		></CreatePhNode>
	</draggable>
</template>
<script>
import draggable from 'vuedraggable';
import CreatePhNode from './CreatePhNode.vue';
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
	},
	components: {
		draggable,
		CreatePhNode,
	},
	name: 'NestedNode',

	data() {
		return {
			opQueue: null,
		};
	},

	methods: {
		stopDragging(e) {
			console.log(e.from.id);
			console.log(e.to.id);
			console.log(e.item.id);
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
						console.log(e);
					});
			}
		},

		open(el) {
			//Move to Vuex to handle state//
			el.open = !el.open;
			if (el.open) {
				this.$store.dispatch('fetchPointHierarchyNode', el.id.substring(1)).then((r) => {
					this.$store.commit('ADD_POINT_HIERARCHY_NODE', {
						parentNode: el.id,
						apiData: r,
					});
				});
			}
		},

		edit(el) {
			console.log(el);
			this.opQueue = el;
			this.$refs.editNode.showDialog();
		},

		del(el) {},

		create(el) {
			console.log(el);
			this.opQueue = el;
			console.log(this.opQueue);
			this.$refs.createNode.showDialog();
		},

		onNodeCreated(title) {
			//TODO: This methods does not work in Backend...
			console.log(title);
			console.log(this.opQueue);
			let el = this.opQueue;
			if (!!title) {
				this.$store
					.dispatch('createPointHierarchyNode', {
						parentNodeId: el.id.substring(1),
						nodeName: title,
					})
					.then((r) => {
						el.children.push(
							new PointHierarchyNode({
								folder: true,
								key: r,
								parentId: el.id.substring(1),
								title: title,
							}),
						);
					})
					.catch((e) => {
						console.error(e);
					});
			}
		},

		onNodeEdited(title) {
			let el = this.opQueue;
			if (!!title) {
				this.$store
					.dispatch('editPointHierarchyNode', {
						nodeId: el.id.substring(1),
						parentNodeId: el.parentId,
						nodeName: title,
					})
					.then((r) => {
						if (!!r) {
							el.title = title;
						}
					})
					.catch((e) => {
						console.error(e);
					});
			}
		},

		onRootNodeCreated(title) {
			if (!!title) {
				this.$store
					.dispatch('createPointHierarchyNode', {
						parentNodeId: 0,
						nodeName: title,
					})
					.then((r) => {
						this.datapointHierarchy.push(
							new PointHierarchyNode({
								folder: true,
								key: r,
								parentId: 0,
								title: title,
							}),
						);
					})
					.catch((e) => {
						console.error(e);
					});
			}
		},
	},
};
</script>
<style scoped>
.dragArea {
	min-height: 50px;
	outline: 1px dashed;
}
</style>
