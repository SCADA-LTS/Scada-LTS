<template>
	<v-dialog v-model="dialogVisible" max-width="500px">
		<template v-slot:activator="{ on, attrs }">
			<v-btn elevation="0" fab v-bind="attrs" v-on="on">
				<v-icon>mdi-plus</v-icon>
			</v-btn>
		</template>

		<v-card id="dialog-synoptic-panel-creation">
			<v-card-title> {{$t('synopticpanels.creator.title')}} </v-card-title>

			<v-card-text>
				<v-row>
					<v-col cols="12">
						<v-text-field :label="$t('common.name')" v-model="name" dense></v-text-field>
					</v-col>
					<v-col cols="12">
						<v-text-field :label="$t('common.xid')" v-model="xid" dense></v-text-field>
					</v-col>
					<v-col cols="12">
						<v-file-input
							:label="$t('synopticpanels.creator.file.upload')"
							@change="handleFileUpload($event)"
							accept="image/svg+xml"
						></v-file-input>
					</v-col>
				</v-row>
			</v-card-text>

			<v-card-actions>
				<v-spacer></v-spacer>
				<v-btn text @click="closeDialog()">{{$t('common.cancel')}}</v-btn>
				<v-btn color="primary" text @click="save()">{{$t('common.create')}}</v-btn>
			</v-card-actions>
		</v-card>
	</v-dialog>
</template>
<script>
/**
 * Synoptic Panel component - Item creator
 *
 * @author Radoslaw Jajko <rjajko@softq.pl>
 * @version 1.0.0
 */
export default {
	name: 'SynopticPanelCreator',

	data() {
		return {
			dialogVisible: false,
			name: '',
			xid: 'SP_',
			vectorImage: '',
			synopticPanel: undefined,
		};
	},

	methods: {
		handleFileUpload(file) {
			this.readFileContent(file)
				.then((content) => {
					this.synopticPanel = {
						id: -1,
						name: this.name,
						xid: this.xid,
						vectorImage: content,
						componentData: '[]',
					};
				})
				.catch((e) => {
					console.error(e);
				});
		},

		closeDialog() {
			this.dialogVisible = false;
			this.name = '';
			this.xid = 'SP_';
			this.vectorImage = '';
		},

		save() {
			this.$emit('created', this.synopticPanel);
			this.closeDialog();
		},

		readFileContent(file) {
			const reader = new FileReader();
			return new Promise((resolve, reject) => {
				reader.onload = (event) => resolve(event.target.result);
				reader.onerror = (error) => reject(error);
				reader.readAsText(file, 'UTF-8');
			});
		},
	},
};
</script>
