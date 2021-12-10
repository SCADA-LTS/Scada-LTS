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
						label="Search"
						class="mr-2"
						single-line
						hide-details
					></v-text-field>

					<v-btn
						color="primary"
						@click="createNewScript"
					>New Script</v-btn>
				</v-card-title>
				<v-data-table
					:headers="headers"
					:items="scriptListFiltered"
					:options.sync="options"
					:loading="loading"
					:server-items-length="totalScripts"
					multi-sort
					class="elevation-1"
					@click:row="selectScript($event.id)"
					>
					<template v-slot:item.actions="{ item }">	
						<v-icon class="mr-2" border="0" @click.stop="runScript(item.xid)" title="run">
							mdi-cog
						</v-icon>
						<v-icon border="0" @click.stop="deleteScript(item.id)" title="delete">
							mdi-delete
						</v-icon>
					</template>
				</v-data-table>	
			</v-card>
		</v-container>






		<v-row justify="center">
			<v-dialog
			v-model="dialog"
			v-show="selectedScriptId !== null"
			@change="selectedScriptId = null"
			
			>
			
			<v-card>
				<v-card-title>
					<v-row>
						<v-col v-if="selectedScriptId != -1" cols="6">Script #{{selectedScriptId}}</v-col>
						<v-col v-else cols="6">New script </v-col>
						<v-col cols="12">
							<v-btn v-if="selectedScriptId" class="mr-2" color="blue" @click="runScript(selectedScript.xid)" >
								<v-icon>mdi-cog</v-icon>
								{{$t('scriptList.run')}}
							</v-btn>
							
							<v-btn v-if="selectedScriptId"  @click="deleteScript(selectedScriptId)" class="mr-2" color="red">
								<v-icon>mdi-delete</v-icon>
								{{$t('scriptList.delete')}}
							</v-btn>
						</v-col>	
					</v-row>
				</v-card-title>
				<v-card-text>
					<form>
					<v-row>
						<v-col cols="6">
							<v-text-field label="xid" v-model="scriptForm.xid"></v-text-field>
						</v-col>
						<v-col cols="6">
							<v-text-field label="name" v-model="scriptForm.name"></v-text-field>
						</v-col>
						<v-col cols="6">
							<v-btn :disabled="selectedDatapointId==null" @click="addDatapoint()" class="mr-2" color="red">
								<v-icon>mdi-plus</v-icon>
								{{$t('scriptList.addDatapoint')}}
							</v-btn>
							<v-select 
							item-value="id"
							placeholder="select datapoint"
							item-text="name"
							v-model="selectedDatapointId"
							:items="filteredDatapoints"></v-select>
						</v-col>
						
						<v-col cols="6">
							<table class='datapoints'>
								<thead>
									<tr>
										<th>Point name</th>
										<th>Var</th>
										<th>Actions</th>
									</tr>
								</thead>
								<tbody>
									<tr v-for="p in scriptForm.pointsOnContext" style="list-style: none;">
										<td>{{p.dataPointXid}}</td>
										<td> <v-text-field v-model="p.varName"/> </td>
										<td> 
											<v-icon color="red" style="cursor:pointer" @click="removeDatapoint(p.key)">mdi-close</v-icon>
										</td>
									</tr>
								</tbody>
							</table>
						</v-col>
					</v-row>
					<v-row>
						<v-col cols="6">
							<v-text-field v-model="scriptForm.datapointContext" label="Datapoints commands"></v-text-field>
						</v-col>
						<v-col cols="6">
							<v-text-field v-model="scriptForm.datasourceContext" label="Datasources commands"></v-text-field>
						</v-col>
					</v-row>
					<v-textarea style="width:100%; font-family: monospace" :label="$t('scriptList.script')" :value="scriptForm.script"></v-textarea>
				</form>
				</v-card-text>
		

			
				<v-card-actions>
				<v-spacer></v-spacer>
				<v-btn
					color="blue darken-1"
					text
					@click="dialog = false"
				>
					Close
				</v-btn>
				<v-btn class="mr-2" color="blue" @click="saveScript()" >
					<v-icon>mdi-content-save</v-icon>
						{{$t('scriptList.save')}}
				</v-btn>
				</v-card-actions>
			</v-card>
			</v-dialog>
		</v-row> 
		
		
					
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
.datapoints th, .datapoints td {
	padding: 4px;
}

.historical-alarms {
	z-index: -1;
}
</style>

<script>
export default {
	name: 'scriptList',
	components: {},
	async mounted() {
		this.fetchScriptList();
		this.datapoints = await this.$store.dispatch('getAllDatapoints');
	},
	watch: {
    	options (data) {
			this.searchFilters.page = data.page;
			this.searchFilters.itemsPerPage = data.itemsPerPage;
			this.searchFilters.sortBy = data.sortBy;
			this.searchFilters.sortDesc = data.sortDesc;
			this.fetchScriptList()	
      	},
		// search() {
		// 	this.filterScriptsBySearch()
		// },
		// scriptList() {
		// 	this.filterScriptsBySearch()
		// }
    },
	data() {
		return {
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
				typeKey:"event.audit.scripts",
				new:false
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
					text: this.$t('scriptList.xid'),
					align: 'center',
					sortable: true,
					value: 'xid',
				},
				{
					text: this.$t('scriptList.name'),
					align: 'center',
					sortable: true,
					value: 'name',
				},
				{
					text: this.$t('scriptList.actions'),
					align: 'center',
					sortable: true,
					value: 'actions',
				},
			],
			sortModeOptions: [
				{label:"Ascending", value:'ASC'},
				{label:"Descending", value:'DEC'}
			],
		};
	},
	computed: {
		filteredDatapoints() {
			return this.datapoints
			.filter(x => this.scriptForm.pointsOnContext
				.filter(y => y.key === x.id)
			.length === 0)
		},
	},

	methods: {
		createNewScript() {

			this.selectedScriptId = -1
			
			this.selectedScript = null
			this.scriptForm.id = -1;
			this.scriptForm.xid = '';
			this.scriptForm.name = '';
			this.scriptForm.pointsOnContext = [];
			this.scriptForm.script = ''
			this.scriptForm.datasourceContext = ''
			this.scriptForm.datapointContext = 'dp'

			this.dialog = true
		},
		// filterScriptsBySearch() {
		// 	if (!this.search) {
		// 		this.scriptListFiltered = this.scriptList
		// 	} else {
		// 		const keywords = this.search.split(' ')
		// 		this.scriptListFiltered = this.scriptList.filter(x => `${x.id} ${x.xid} ${x.name} ${x.script}`.toLowerCase().includes(keywords[0].toLowerCase()))
		// 	}
		// },
		removeDatapoint(key) {
			this.scriptForm.pointsOnContext = this.scriptForm.pointsOnContext
				.filter(p => p.key != key)
		},
		selectScript(id) {
			this.selectedScriptId = id
			this.dialog = true
			this.selectedScript = this.scriptList.find(x => x.id === this.selectedScriptId)
			this.scriptForm.id = this.selectedScript.id;
			this.scriptForm.xid = this.selectedScript.xid;
			this.scriptForm.name = this.selectedScript.name;
			this.scriptForm.pointsOnContext = this.selectedScript.pointsOnContext.map( x => { return {
					varName: x.value ,
					dataPointXid: (this.datapoints.find(dp => dp.id === x.id)).xid
				}
			}	
			);

			const oc = this.selectedScript.objectsOnContext
			if (oc && oc.length) {
				const o1 = oc.find(x => x.key == 1)
				const o2 = oc.find(x => x.key == 2)
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
			const p = this.datapoints.find(x => x.id === this.selectedDatapointId)
			this.scriptForm.pointsOnContext
				.push({dataPointXid: p.xid, varName: `p${p.id}`})
			this.selectedDatapointId = null
		},
		async fetchScriptList() {
			this.loading = true;
			this.scriptList = await this.$store.dispatch('searchScripts', this.searchFilters);
			if (!this.search) {
				this.scriptListFiltered = this.scriptList
			} else {
				const keywords = this.search.split(' ')
				this.scriptListFiltered = this.scriptList.filter(x => `${x.id} ${x.xid} ${x.name} ${x.script}`.toLowerCase().includes(keywords[0].toLowerCase()))
			}
			this.totalScripts = this.scriptList.total;
		},
		runScript(xid) {
			this.$store.dispatch('runScript', xid);
		},
		saveScript() {
			if (this.selectedScriptId != -1) {
				this.$store.dispatch('updateScript', this.scriptForm);
			} else {
				this.$store.dispatch('createScript', this.scriptForm);
			}
		},
		async deleteScript(id) {
			this.scriptList = await this.$store.dispatch('deleteScript', id);
			this.fetchScriptList()
			this.dialog = false
		}
	},
};
</script>
