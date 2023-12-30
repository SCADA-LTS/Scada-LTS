<template>
	<v-dialog max-width="500" persistent v-model="dialog">
		<v-card class="dialog-point-hierarchy-create">
			<v-card-title class="headline">
				{{ title }}
			</v-card-title>
			<v-card-text>
                <ExportImportPointHierarchy>
                </ExportImportPointHierarchy>
			</v-card-text>
			<v-card-actions>
				<v-spacer></v-spacer>
				<v-btn text @click="cancel">
					{{ $t('common.cancel') }}
				</v-btn>
				<v-btn text color="success" @click="accept">
					{{ $t('common.ok') }}
				</v-btn>
			</v-card-actions>
		</v-card>
	</v-dialog>
</template>
<script>
import ExportImportPointHierarchy from '@/components/point_hierarchy/ExportImportPointHierarchy.vue';

export default {
	name: 'ImportExportDialog',

    components: {
        ExportImportPointHierarchy,
    },

	data() {
		return {
            dialog: false,
        };
	},

	props: {
		title: {
			type: String,
			default: 'Example title',
		},
	},

	methods: {
        showDialog() {
            this.dialog = true;
        },
        
		cancel() {
			this.dialog = false;
			this.$emit('result', false);
		},

		accept() {
			this.dialog = false;
            this.$emit('result', true);			
		},
	},
};
</script>
<style scoped></style>
