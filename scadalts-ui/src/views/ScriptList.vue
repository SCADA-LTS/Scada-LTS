<template>
	<div>
		<h1>{{ $t('scriptList.title') }}</h1>	
		<v-container fluid v-if="!!scriptList">	
			<v-row>
				<v-col cols="12">
					<v-form ref="form" id="user-details--form">	
						<v-row>
							<v-col cols="3">
								<v-select
									:label="$t('scriptList.searchFilter.alarmLevel')"
									v-model="searchFilters.alarmLevel"
									:items="alarmLevelOptions"
									item-text="name"
									item-value="id"
									@change="fetchScriptList"
								></v-select>
							</v-col>
						
							<v-col cols="3">
								<v-select 
									:label="$t('scriptList.searchFilter.status')"
									v-model="searchFilters.status"
									:items="statusOptions"
									item-text="label"
									item-value="value"
									@change="fetchScriptList"
								></v-select>
							</v-col>
							
							<v-col cols="3">
								<v-select 
									:label="$t('scriptList.searchFilter.eventSourceType')"
									v-model="searchFilters.eventSourceType"
									:items="eventSourceTypeOptions"
									item-text="name"
									item-value="id"
									@change="fetchScriptList"
									
								></v-select>
							</v-col>
							
							<v-col cols="3" v-if="searchFilters.eventSourceType === 0 || searchFilters.eventSourceType === 1">
								<v-text-field 
									:label="$t('scriptList.searchFilter.datapoint')" 
									v-model="searchFilters.datapoint"
									@change="fetchScriptList"
								></v-text-field>
							</v-col>
						</v-row>

						<v-text-field
							v-model="searchFilters.keywords"
							append-icon="mdi-magnify"
							:label="$t('common.search')"
							single-line
							hide-details
						></v-text-field>
						
						<template v-slot:append-outer>
							<v-btn
								icon
								fab
								x-small
								@click="fetchScriptList"
								:loading="loading"
								:disabled="loading"
							>
								<v-icon>mdi-autorenew</v-icon>
							</v-btn>
						</template>
					</v-form>
				</v-col>
			</v-row>

			<v-data-table
				:headers="headers"
				:items="scriptList"
				:options.sync="options"
				:loading="loading"
				:server-items-length="totalScripts"
				multi-sort
				class="elevation-1"
				@click:row="open"
				></v-data-table>	
		</v-container>
		<v-progress-circular v-else indeterminate color="primary"></v-progress-circular>
	</div>
</template>
<style scoped>
.historical-alarms {
	z-index: -1;
}
</style>

<script>
export default {
	name: 'scriptList',
	components: {},
	mounted() {
		this.fetchScriptList();
	},
	watch: {
    	options (data) {
			this.searchFilters.page = data.page;
			this.searchFilters.itemsPerPage = data.itemsPerPage;
			this.searchFilters.sortBy = data.sortBy;
			this.searchFilters.sortDesc = data.sortDesc;
			this.fetchScriptList()	
      	},
    },
	data() {
		return {	
			searchFilters: {
				keywords: '',
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
	methods: {
		async fetchScriptList() {
			this.loading = true;
			
			const result = await this.$store.dispatch('searchEvents', this.searchFilters);
			this.scriptList = result.rows;
			this.totalScripts = result.total;
			this.loading = false;
		},
	  	open(item, item2) {
			this.$router.push({ name: 'event-details', params: { id: item.id } });
			this.$router.go();
	  	}
	},
};
</script>
