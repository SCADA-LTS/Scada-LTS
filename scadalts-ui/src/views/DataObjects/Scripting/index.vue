<template>
	<div>
		<h1>{{ $t('scriptList.title') }}</h1>

		<v-container fluid v-if="!!scriptList">
			<v-card>
				<v-card-title>
					<v-text-field
						v-model="search"
						@input="fetchScriptList"
						append-icon="mdi-magnify"
						:label="$t('common.search')"
						class="mr-2"
						single-line
						hide-details
					></v-text-field>
					<v-btn color="primary" @click="createNewScript">{{
						$t('scriptList.newScript')
					}}</v-btn>
				</v-card-title>
				<v-data-table
					:headers="headers"
					:items="scriptListFiltered"
					:options.sync="options"
					:server-items-length="totalScripts"
					multi-sort
					class="elevation-1"
					@click:row="selectScript($event.id)"
				>
					<template v-slot:item.actions="{ item }">
						<v-btn icon @click.stop="runScript(item.xid)">
							<v-icon title="run">mdi-cog</v-icon>
						</v-btn>

						<v-btn icon @click.stop="onScriptDelete(item.id)">
							<v-icon title="delete">mdi-delete</v-icon>
						</v-btn>
					</template>
				</v-data-table>
			</v-card>
		</v-container>

		<v-row justify="center">
			<v-dialog
				v-model="dialog"
				v-show="selectedScriptId !== null"
				@change="selectedScriptId = null"
				max-width="800px"
			>
				<v-card>
					<v-card-title>
						<v-row>
							<v-col v-if="selectedScriptId != -1" cols="6">{{$t('scriptList.title')}} #{{ selectedScriptId }}</v-col>
							<v-col v-else cols="6">{{ $t('scriptList.newScript') }}</v-col>
							<v-col cols="4">
								<v-btn
									v-if="selectedScriptId != -1"
									block
									elevation="1"
									@click="saveAndRunScript(selectedScript.xid)"
								>
									<v-icon>mdi-cog</v-icon>
									{{ $t('scriptList.saveRun') }}
								</v-btn>
							</v-col>
							<v-col cols="2">
								<v-btn
									v-if="selectedScriptId != -1"
									@click="onScriptDelete(selectedScriptId)"
									block
									elevation="1"
									color="error"
								>
									<v-icon>mdi-delete</v-icon>
									{{ $t('common.delete') }}
								</v-btn>
							</v-col>
						</v-row>
					</v-card-title>
					<v-card-text>
						<v-form ref="editForm">
							<v-row>
								<v-col cols="6">
									<v-text-field :label="$t('common.name')" v-model="scriptForm.name" :rules="[ruleNotNull, ruleMaxLen40 ]"></v-text-field>
								</v-col>
								<v-col cols="6">
									<v-text-field ref="xidInput" :label="$t('common.xid')" @input="validateXid" v-model="scriptForm.xid" :rules="[ruleNotNull, ruleXidUnique, ruleMaxLen50 ]"></v-text-field>
								</v-col>
								<v-col cols="6">
									<v-select
										item-value="id"
										:placeholder="$t('scriptList.selectDatapoint')"
										item-text="name"
										v-model="selectedDatapointId"
										@change="addDatapoint"
										:items="filteredDatapoints"
									></v-select>
								</v-col>
								<v-col cols="6">
									<table style="width: 100%">
										<tr v-for="p in scriptForm.pointsOnContext">
											<td style="width: 20%">{{ p.dataPointXid }}</td>
											<td style="width: 70%">
												<v-text-field style="width: 100%" v-model="p.varName" />
											</td>
											<td style="width: 10%">
												<v-icon
													color="red"
													style="cursor: pointer; border: 0"
													@click="removeDatapoint(p.dataPointXid)"
													>mdi-close</v-icon
												>
											</td>
										</tr>
									</table>
								</v-col>
							</v-row>
							<v-row>
								<v-col cols="6">
									<v-text-field
										v-model="scriptForm.datapointContext"
										:label="$t('scriptList.dialog.command.datapoints')"
									></v-text-field>
								</v-col>
								<v-col cols="6">
									<v-text-field
										v-model="scriptForm.datasourceContext"
										:label="$t('scriptList.dialog.command.datasources')"
									></v-text-field>
								</v-col>
							</v-row>
							<v-textarea :rules="[ruleNotNull]"
								style="width: 100%; font-family: monospace"
								:label="$t('scriptList.script')"
								v-model="scriptForm.script"
								ref="scriptBodyTextarea"
							></v-textarea>
						</v-form>
					</v-card-text>
					<v-card-actions>
						<v-spacer></v-spacer>
						<v-btn  text @click="dialog = false"> Close </v-btn>
						<v-btn elevation="1" color="primary" @click="saveScript()">
							<v-icon>mdi-content-save</v-icon>
							{{ $t('common.save') }}
						</v-btn>
					</v-card-actions>
				</v-card>
			</v-dialog>
		</v-row>

		<ConfirmationDialog
			:btnvisible="false"
			ref="deleteScriptDialog"
			@result="onScriptDeleteConfirm"
			:title="$t('scriptList.dialog.conf.header')"
			:message="$t('scriptList.dialog.conf.message')"
		></ConfirmationDialog>

		<div class="text-center ma-2">
			<v-snackbar v-model="snackbar">
				{{ snackbarMessage }}
				<template v-slot:action="{ attrs }">
					<v-btn color="pink" text v-bind="attrs" @click="snackbar = false">{{
						$t('common.close')
					}}</v-btn>
				</template>
			</v-snackbar>
		</div>
	</div>
</template>
<style scoped>
.datapoints {
	width: 100%;
}
.datapoints th {
	background: darkslategray;
	color: white;
}
.datapoints th,
.datapoints td {
	padding: 4px;
}

.historical-alarms {
	z-index: -1;
}
</style>

<script>
/**
 * @author sselvaggi
 */
import ConfirmationDialog from '@/layout/dialogs/ConfirmationDialog.vue';

export default {
	name: 'scriptList',
	components: { ConfirmationDialog },
	async mounted() {
		this.fetchScriptList();
		this.datapoints = await this.$store.dispatch('getAllDatapoints');
	},
	watch: {
		options(data) {
			this.searchFilters.page = data.page;
			this.searchFilters.itemsPerPage = data.itemsPerPage;
			this.searchFilters.sortBy = data.sortBy;
			this.searchFilters.sortDesc = data.sortDesc;
			this.fetchScriptList();
		},
	},
	data() {
		return {
			xidUnique: true,
			ruleNotNull: (v) => !!v || this.$t('validation.rule.notNull'),
			ruleMaxLen40: (v) => !!(v.length < 40) || this.$t('validation.rule.maxLen')+40,
			ruleMaxLen50: (v) => !!(v.length < 50) || this.$t('validation.rule.maxLen')+50,
			ruleXidUnique: (v) => !!this.xidUnique || this.$t('validation.rule.xid.notUnique'),
			ruleValidScriptBody: (v) => !this.scriptBodyErrors || this.$t('validation.rule.xid.notUnique'),
			snackbarMessage: '',
			snackbar: false,
			dialog: false,
			search: '',
			scriptListFiltered: [],
			datapointToSave: null,
			selectedDatapointId: null,
			datapoints: [],
			options: {},
			selectedScriptId: -1,
			selectedScript: null,
			scriptForm: {
				id: null,
				name: '',
				xid: '',
				pointsOnContext: [],
				datasourceContext: '',
				datapointContext: '',
				script: '',
				type: 'CONTEXTUALIZED_SCRIPT',
				typeKey: 'event.audit.scripts',
				new: false,
			},
			searchFilters: {
				keywordfs: '',
				page: 1,
				itemsPerPage: 10,
				sortBy: [],
				sortDesc: [],
			},
			totalScripts: 100,
			scriptList: [],
			loading: false,
			headers: [
				{
					text: this.$t('scriptList.id'),
					sortable: true,
					align: 'center',
					value: 'id',
				},
				{
					text: this.$t('scriptList.name'),
					align: 'center',
					sortable: true,
					value: 'name',
				},
				{
					text: this.$t('scriptList.xid'),
					align: 'center',
					sortable: true,
					value: 'xid',
				},
				{
					text: this.$t('scriptList.actions'),
					align: 'end',
					sortable: false,
					value: 'actions',
				},
			],
			sortModeOptions: [
				{ label: 'Ascending', value: 'ASC' },
				{ label: 'Descending', value: 'DEC' },
			],
		};
	},
	computed: {
		filteredDatapoints() {
			const p = this.datapoints.find((x) => x.id === this.selectedDatapointId);
			return this.datapoints
			.filter(dp => this.scriptForm.pointsOnContext
				.filter(sp => dp.xid === sp.dataPointXid)
			.length === 0)
		},
	},

	methods: {
		async validateXid() {
			const response = await this.$store.dispatch('validateXid', this.scriptForm)
			this.xidUnique = response.xidRepeated === 'false' ? true: false;
			this.$refs.xidInput.validate();
		},

		async validateScriptBody() {
			const response = await this.$store.dispatch('validateScriptBody', this.scriptForm)

			alert(JSON.stringify(response))
			this.scriptBodyErrors = response.scriptBodyErrors;
			this.$refs.scriptBodyTextarea.validate();
		},
		async createNewScript() {
			this.selectedScriptId = -1;

			this.selectedScript = null;
			this.scriptForm.id = -1;
			this.scriptForm.name = '';
			this.scriptForm.pointsOnContext = [];
			this.scriptForm.script = '';
			this.scriptForm.datasourceContext = 'ds';
			this.scriptForm.datapointContext = 'dp';

			try {
				this.scriptForm.xid = await this.$store.dispatch('getScriptsUniqueXid');
			} catch (e) {
				this.scriptForm.xid = ''
			}

			if(!!this.$refs.editForm) this.$refs.editForm.resetValidation();
			this.dialog = true;
		},
		removeDatapoint(dataPointXid) {
			this.scriptForm.pointsOnContext = this.scriptForm.pointsOnContext.filter(
				(p) => p.dataPointXid != dataPointXid,
			);
		},

		selectScript(id) {
			this.selectedScriptId = id;
			this.dialog = true;
			this.selectedScript = this.scriptList.find((x) => x.id === this.selectedScriptId);
			this.scriptForm.id = this.selectedScript.id;
			this.scriptForm.xid = this.selectedScript.xid;
			this.scriptForm.name = this.selectedScript.name;
			this.scriptForm.pointsOnContext = this.selectedScript.pointsOnContext.map((x) => {
				const dp = this.datapoints.find((dp) => dp.id === x.key);
				return {
					varName: x.value,
					dataPointXid: dp.xid,
				};
			});

			const oc = this.selectedScript.objectsOnContext;
			if (oc && oc.length) {
				const o1 = oc.find((x) => x.key == 1);
				const o2 = oc.find((x) => x.key == 2);
				if (o1 && o1.value) {
					this.scriptForm.datasourceContext = o1.value;
				} else {
					this.scriptForm.datasourceContext = '';
				}

				if (o2 && o2.value) {
					this.scriptForm.datapointContext = o2.value;
				} else {
					this.scriptForm.datapointContext = '';
				}

				this.scriptForm.datapointContext = o2.value;
			} else {
				this.scriptForm.datasourceContext = '';
				this.scriptForm.datapointContext = '';
			}

			this.scriptForm.script = this.selectedScript.script;
		},
		addDatapoint() {
			const p = this.datapoints.find((x) => x.id === this.selectedDatapointId);
			if (!this.scriptForm.pointsOnContext.find((x) => p.xid === x.dataPointXid)) {
				this.scriptForm.pointsOnContext.push({
					dataPointXid: p.xid,
					varName: `p${p.id}`,
				});
				this.selectedDatapointId = null;
			}
		},
		async fetchScriptList() {
			this.loading = true;
			this.scriptList = await this.$store.dispatch('searchScripts', this.searchFilters);
			if (!this.search) {
				this.scriptListFiltered = this.scriptList;
			} else {
				const keywords = this.search.split(' ');
				this.scriptListFiltered = this.scriptList.filter((x) =>
					`${x.id} ${x.xid} ${x.name} ${x.script}`
						.toLowerCase()
						.includes(keywords[0].toLowerCase()),
				);
			}
			this.totalScripts = this.scriptList.total;
		},
		async runScript(xid) {
			try {
				await this.$store.dispatch('runScript', xid);
				this.$store.dispatch('showSuccessNotification', this.$t('scriptList.successfulScriptExecution'));
			} catch (e) {
				this.$store.dispatch('showErrorNotification', this.$t('scriptList.failedScriptExecution'));
			}
		},
		async saveAndRunScript(xid) {
			await this.saveScript(false);
			this.runScript(xid);
		},

		async saveScript(closeOnSaveConfirmation = true) {
			if (this.$refs.editForm.validate()) {
				let method = this.selectedScriptId != -1 ? 'updateScript' : 'createScript';
				await this.$store.dispatch(method, this.scriptForm);
				this.fetchScriptList();
				this.$store.dispatch('showSuccessNotification', this.$t('scriptList.scriptSaved'));
				if (closeOnSaveConfirmation) this.dialog = false
			}

		},
		async deleteScript(id) {
			this.scriptList = await this.$store.dispatch('deleteScript', id);
			this.fetchScriptList();
			this.dialog = false;
            this.snackbar = true;
            this.snackbarMessage = `${this.$t('scriptList.deletedScript')} #${id}`;
			this.$store.dispatch('showSuccessNotification', `${this.$t('scriptList.deletedScript')} #${id}`);
		},

		onScriptDelete(id) {
			this.$refs.deleteScriptDialog.showDialog();
			this.operationQueue = id;
		},

		onScriptDeleteConfirm(e) {
			if(e) { this.deleteScript(this.operationQueue); }
		}
	},
};
</script>
