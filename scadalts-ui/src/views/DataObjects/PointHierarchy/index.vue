<template>
	<div>
		<v-container fluid>
			<v-row align="center">
				<v-col cols="8" xs="12">
					<h1>Point Hierarchy</h1>
				</v-col>
			</v-row>
		</v-container>
		<v-container fluid>
			<v-row>
				<v-col cols="12">
					<v-icon @click="addRootFolder">mdi-plus-box</v-icon>
					
					<NestedNode v-if="datapointHierarchy" :list="datapointHierarchy" pid="f0">
					</NestedNode>
				</v-col>
				
			</v-row>
			<CreatePhNode
				ref="createRootNode"
				@result="onRootNodeCreated"
			></CreatePhNode>
		</v-container>
	</div>
</template>
<script>
import PointHierarchyNode from '@/models/PointHierarchyNode'
import NestedNode from './NestedNode'
import CreatePhNode from './CreatePhNode'

export default {

	components: {
		NestedNode,
		CreatePhNode,
	},

	data() {
		return { };
	},

	computed: {
		datapointHierarchy: {
			get() {
				return this.$store.state.pointHierarchy.pointHierarchy;
			},
			set(value) {
				this.$store.dispatch("SET_POINT_HIERARCHY", value);
			}
		}
	},

	mounted() {
		this.$store.dispatch('fetchPointHierarchyNode', 0).then(r => {
			this.$store.commit('SET_POINT_HIERARCHY_BY_API', r);			
		});
	},

	methods: {
		fetchDataPointList(item) {
			
			console.log(item);
		},

		logdata(i) {
			console.log("Moved!");
			console.log(i);
		},

		addRootFolder() {
			this.$refs.createRootNode.showDialog();			
		},

		onRootNodeCreated(title) {
			if(!!title) {
				this.$store.dispatch('createPointHierarchyNode', {
					parentNodeId: 0,
					nodeName: title,
				}).then(r => {
					this.datapointHierarchy.push(new PointHierarchyNode({
						folder: true,
						key: r,
						parentId: 0,
						title: title,
					}));
				}).catch(e => {
					console.error(e);
				});
			}
		}
	}
};
</script>
