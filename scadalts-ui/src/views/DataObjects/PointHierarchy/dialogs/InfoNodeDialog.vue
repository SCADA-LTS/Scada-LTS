<template>
	<v-dialog max-width="500" persistent v-model="dialog">
		<v-card class="dialog-point-hierarchy-create">
			<v-card-title class="headline">
				{{ title }}
			</v-card-title>
			<v-card-text>
				<p v-if="!!message">{{ message }}</p>
                <div v-if="!!node">
                    <v-row>
                        <v-col cols="6">
                            Name:
                        </v-col>
                        <v-col cols="6">
                            {{ node.name }}
                        </v-col>
                        <v-col cols="6">
                            Export ID:
                        </v-col>
                        <v-col cols="6">
                            {{ node.xid }}
                        </v-col>
                        <v-col cols="6" v-if="!!node.dataSource">
                            Data Source:
                        </v-col>
                        <v-col cols="6" v-if="!!node.dataSource">
                            {{ node.dataSource }}
                        </v-col>
                        <v-col cols="6">
                            Element Key:
                        </v-col>
                        <v-col cols="6">
                            {{ node.id.substring(1) }}
                        </v-col>
                        <v-col cols="6">
                            Parent Key:
                        </v-col>
                        <v-col cols="6">
                            {{ node.parentId }}
                        </v-col>
                    </v-row>
                    
                </div>
			</v-card-text>
			<v-card-actions>
				<v-spacer></v-spacer>
				<v-btn text @click="close">
					{{ $t('common.close') }}
				</v-btn>
			</v-card-actions>
		</v-card>
	</v-dialog>
</template>
<script>
export default {
	name: 'InfoNodeDialog',

	data() {
		return {
            node: null,
			dialog: false,
		};
	},

	props: {
		title: {
			type: String,
			default: 'Example title',
		},
		message: {
			type: String,
			default: null,
		},
	},

	methods: {
		showDialog(node) {
            this.node = null;
			this.dialog = true;
            if(!!node) {
                this.node = node;
            }
		},

		close() {
			this.dialog = false;
		},
	},
};
</script>
<style scoped></style>
