<template>
  <v-card> 
    <v-card-title>
      <v-text-field
        v-model="search"
        append-icon="mdi-magnify"
        label="Search"
        single-line
        hide-details
      ></v-text-field>
    </v-card-title>
    <v-data-table
		v-model="selectedItems"
		show-select
		:headers="headers"
		:items="scriptList"
		:loading="loading"
		:server-items-length="totalItems"
		:footer-props="{
			'items-per-page-options': [10, 20, 50, 100]
		}"
		multi-sort
		class="elevation-1"
		@click:row="openDetails"
		>
			<template v-slot:item.actions="{ item }">
				<v-icon border="0" @click.stop="$router.push({ name: 'datapoint-details', params: { id: item.typeRef1 } });$router.go();" v-if="item.typeId===1" title="point details">
					mdi-cog
				</v-icon>
			</template>
	</v-data-table>
	<v-row justify="center" class="mt-6">
		<v-dialog
		v-model="selectedItemId"
		scrollable
		max-width="90%"
		>
		<v-card v-if="selectedItemId && datapoints">
			<v-card-title>
				<v-row>
					<v-col cols="6">Script #{{selectedItem.id}}</v-col>
					<v-col cols="12">
						<v-form v-model="valid">
							<v-container>
								<v-row>
									<v-col cols="12" md="6">
										<v-text-field
										v-model="firstname"
										:rules="nameRules"
										label="XID"
										required></v-text-field>
        							</v-col>
									<v-col cols="12" md="6">
										<v-text-field
										v-model="lastname"
										:rules="nameRules"
										label="Name"
										required
										></v-text-field>
        							</v-col>
									<v-col cols="12">
										<h4>Context points</h4>
										<v-select v-model="selectedDatapoint" :items="datapoints"></v-select>
        							</v-col>
									<!-- <v-col cols="12">
										<h4>Context object</h4>
										<table>
											<thead>
												<tr>
													<th>key</th>
													<th>value</th>
												</tr>
											</thead>
											<tbody v-for="pointContext in selectedItem.pointsOnContext" key="key">
												<tr>
													<td>
													<input type="text" style="width:100%;border:0" :value="pointContext.key"/>
													</td>
													<td>
													<input type="text" style="width:100%;border:0" :value="pointContext.value"/>
													</td>
												</tr>
											</tbody>
											<tfoot>
												<tr>
													<td colspan="2">add new </td>
												</tr>
												<tr>
													<td>
													<input type="text" label="key" value="" style="width:100%;border:0" />
													</td>
													<td>
													<input type="text" label="value" value="" style="width:100%;border:0" />
													</td>
												</tr>
											</tfoot>
										</table>	
									</v-col> -->
								</v-row>
							</v-container>
         					<v-textarea
								counter
								label="Script"
								:value="selectedItem.script"
							></v-textarea>
						</v-form>
					</v-col>
				</v-row>
				<v-row>
					<v-col>
						<v-btn class="mr-2" color="blue">
							<v-icon>mdi-cog</v-icon>
							{{$t('scriptList.run')}}
						</v-btn>
					</v-col>	
				</v-row>
			</v-card-title>
			
		</v-card>
		</v-dialog>
	</v-row>
  </v-card>
</template>

<script>
import Components from '@min-gb/vuejs-components';

export default {
	name: 'scripts',
	components: {
		...Components,
	},
	async mounted() {
		await this.fetchScripts()
		this.datapoints = await this.$store.dispatch('fetchDataPointSimpleList');
	},
	methods: {
		openDetails(item) {
			this.selectedItemId = item.id
		},
		async fetchScripts() {
			this.loading = true;
			const result = await this.$store.dispatch('fetchScripts', { ...this.searchFilters, itemsPerPage: this.options.itemsPerPage });
			this.scriptList = result;
			this.totalItems = result.lenght;
			this.loading = false;
		},
	},
	watch: {
    	options (data) {
			this.searchFilters.page = data.page;
			this.searchFilters.itemsPerPage = data.itemsPerPage;
			this.searchFilters.sortBy = data.sortBy;
			this.searchFilters.sortDesc = data.sortDesc;
			this.fetchScripts();
      	},
    },
	data() {
		return {
			datapoints:[],
			options: {},
			searchFilters: {
				keywords: '',
				datapoint: null,
				page: 1,
				itemsPerPage: 10,
				sortBy: [],
				sortDesc: [],
			},
			showDatails: false,
			search: '',
			get selectedItem() {
				return this.scriptList.find(item => item.id === this.selectedItemId)
			},
			detailsDialog: false,
			selectedItemId: null,
			totalItems: 100,
        	scriptList: [],
			loading: false,
			selectedItems:[],
			headers: [
				{
					text: this.$t('#'),
					sortable: true,
					align: 'center',
					value: 'id',
				},
				{
					text: this.$t('scriptList.xid'),
					sortable: true,
					align: 'center',
					value: 'xid',
				},
				{
					text: this.$t('scriptList.name'),
					align: 'center',
					sortable: true,
					value: 'name',
				},
				
			],
		};
	},
};
</script>

<style lang="scss" scoped>
@import '../../node_modules/@min-gb/vuejs-components/dist/min-gb.css';

.action {
	margin-top: 50px;
	margin-left: 20px;
	padding-top: 10px;
}
.action_bottom {
	padding-top: 10px;
	margin-left: 20px;
}

table {
	font-family: arial, sans-serif;
	border-collapse: collapse;
	width: 100%;
}

td,
th {
	border: 1px solid #dddddd;
	text-align: left;
	padding: 8px;
}

.activation_alarm {
	background: yellow;
}
.activation {
	color: red;
}
.inactivation {
	color: green;
}
</style>
