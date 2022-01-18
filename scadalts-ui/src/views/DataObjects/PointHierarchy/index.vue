<template>
	<div>
		<v-container fluid class="slts-page-header">
			<v-row align="center">
				<v-col cols="8" xs="12" class="slts--title">
					<h1>{{ $t('pointHierarchy.title') }}</h1>
				</v-col>
				<v-col cols="4" xs="12" class="slts--toolbar row justify-end">
					<v-row>
						<v-col cols="12" class="row justify-end">
							<v-tooltip bottom>
								<template v-slot:activator="{ on, attrs }">
									<v-btn
										v-on="on"
										v-bind="attrs"
										@click="addRootFolder"
										fab
										elevation="1"
										class="header-settings--buttons"
									>
										<v-icon>mdi-plus-box</v-icon>
									</v-btn>
								</template>
								{{ $t('pointHierarchy.actions.newFolder') }}
							</v-tooltip>

							<v-tooltip bottom>
								<template v-slot:activator="{ on, attrs }">
									<v-btn
										v-on="on"
										v-bind="attrs"
										@click="openImportExportDialog"
										fab
										elevation="1"
										class="header-settings--buttons"
									>
										<v-icon>mdi-swap-vertical-circle</v-icon>
									</v-btn>
								</template>
								{{ $t('pointHierarchy.actions.importExport') }}
							</v-tooltip>

							<v-tooltip bottom>
								<template v-slot:activator="{ on, attrs }">
									<v-btn
										v-on="on"
										v-bind="attrs"
										@click="showDataSourceName = !showDataSourceName"
										fab
										elevation="1"
										class="header-settings--buttons"
									>
										<v-icon>mdi-database</v-icon>
									</v-btn>
								</template>
								{{ $t('pointHierarchy.actions.toggleDataSourceName') }}
							</v-tooltip>
						</v-col>
					</v-row>
				</v-col>
			</v-row>
		</v-container>
		<v-container fluid>
			<v-row>
				<v-col cols="12">
					<NestedNode
						v-if="datapointHierarchy"
						:list="datapointHierarchy"
						pid="f0"
						:showDataSource="showDataSourceName"
						@error="onHierarchyError"
					>
					</NestedNode>
				</v-col>
			</v-row>

			<ImportExportDialog
				ref="importExportDialog"
				:title="$t('pointHierarchy.dialog.importExport.title')"
				@result="onImportExport"
			></ImportExportDialog>

			<EditNodeDialog
				:title="$t('pointHierarchy.actions.newFolder')"
				ref="createNodeDialog"
				@result="onRootNodeCreated"
			></EditNodeDialog>
		</v-container>

	</div>
</template>
<script>
import PointHierarchyNode from '@/models/PointHierarchyNode';
import NestedNode from './NestedNode';
import ImportExportDialog from './dialogs/ImportExportDialog';
import EditNodeDialog from './dialogs/EditNodeDialog';

export default {
	components: {
		NestedNode,
		ImportExportDialog,
		EditNodeDialog,
	},

	data() {
		return {
			showDataSourceName: false,
		};
	},

	computed: {
		datapointHierarchy: {
			get() {
				return this.$store.state.pointHierarchy.pointHierarchy;
			},
			set(value) {
				this.$store.dispatch('SET_POINT_HIERARCHY', value);
			},
		},
	},

	mounted() {
		this.initPointHierarchy();
	},

	methods: {
		initPointHierarchy() {
			this.$store.dispatch('fetchPointHierarchyNode', 0).then((r) => {
				this.$store.commit('SET_POINT_HIERARCHY_BY_API', r);
			});
		},

		addRootFolder() {
			this.$refs.createNodeDialog.showDialog();
		},

		openImportExportDialog() {
			this.$refs.importExportDialog.showDialog();
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
						this.$store.dispatch('showErrorNotification', e);
					});
			}
		},

		onImportExport(result) {
			if (!!result) {
				this.initPointHierarchy();
			}
		},

		onHierarchyError(e) {
			this.$store.dispatch('showErrorNotification', e);
		},
	},
};
</script>
<style scoped>
.header-settings--buttons {
	margin: 0 5px;
}
.flex-jc-center {
	justify-content: center;
}
.justify-end {
	justify-content: flex-end;
}
</style>
