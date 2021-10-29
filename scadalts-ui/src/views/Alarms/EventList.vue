<template>
	<div>


		<h1>{{ $t('eventList.title') }}</h1>
		
			
		<v-container fluid v-if="!!eventList">	
			<v-row>
				<v-col cols="12">
					<v-form ref="form" id="user-details--form">	
						<v-row>
							<v-col cols="3">
								<v-select
									:label="$t('eventList.searchFilter.alarmLevel')"
									v-model="searchFilters.alarmLevel"
									:items="alarmLevelOptions"
									item-text="name"
									item-value="id"
									@change="fetchEventList"
								></v-select>
							</v-col>
						
							<v-col cols="3">
								<v-select 
									:label="$t('eventList.searchFilter.status')"
									v-model="searchFilters.status"
									:items="statusOptions"
									item-text="label"
									item-value="value"
									@change="fetchEventList"
								></v-select>
							</v-col>
							
							<v-col cols="3">
								<v-select 
									:label="$t('eventList.searchFilter.eventSourceType')"
									v-model="searchFilters.eventSourceType"
									:items="eventSourceTypeOptions"
									item-text="name"
									item-value="id"
									@change="fetchEventList"
									
								></v-select>
							</v-col>
							
							<v-col cols="3" v-if="searchFilters.eventSourceType === 0 || searchFilters.eventSourceType === 1">
								<v-text-field 
									:label="$t('eventList.searchFilter.datapoint')" 
									v-model="searchFilters.datapoint"
									@change="fetchEventList"
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
								@click="fetchEventList"
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
				:items="eventList"
				:options.sync="options"
				:loading="loading"
				:server-items-length="totalEvents"
				multi-sort
				class="elevation-1"
				@click:row="open"
				></v-data-table>
				
		</v-container>
		<v-progress-circular v-else indeterminate color="primary"></v-progress-circular>
		<pre>{{searchFilters}} {{eventList}}</pre>
	</div>

	
</template>




<style scoped>
.historical-alarms {
	z-index: -1;
}
</style>

<script>
export default {
	name: 'EventList',
	components: {},
	mounted() {
		this.fetchEventList();
	},
	watch: {
    	options (data) {
			this.searchFilters.page = data.page;
			this.searchFilters.itemsPerPage = data.itemsPerPage;
			this.searchFilters.sortBy = data.sortBy;
			this.searchFilters.sortDesc = data.sortDesc;
			this.fetchEventList()	
      	},
    },
	data() {
		return {	
			searchFilters: {
				alarmLevel: 1,
				keywords: '',
				status: "*",
				eventSourceType: 0,
				datapoint: null,
				page: 1,
				itemsPerPage: 10,
				sortBy: ['alarmLevel'],
				sortDesc: [true],
			},
			totalEvents: 100,
        	eventList: [],
        	loading: false,
        	options: {},
			headers: [
				{	
					text: this.$t('eventList.id'),
					sortable: true,
					align: 'center',
					value: 'id',
				},
				{
					text: this.$t('eventList.alarmLevel'),
					sortable: true,
					align: 'center',
					value: 'alarmLevel',
				},
				{
					text: this.$t('eventList.datetime'),
					align: 'center',
					sortable: true,
					value: 'activeTs',
				},
				{
					text: this.$t('eventList.eventSourceType'),
					sortable: true,
					align: 'center',
					value: 'typeId',
				},
				{
					text: this.$t('eventList.datapoint'),
					sortable: true,
					align: 'center',
					value: 'datapoint',
				},
				{
					text: this.$t('eventList.message'),
					align: 'center',
					value: 'message',
					sortable: true,
				},
				{
					text: this.$t('eventList.status'),
					align: 'center',
					sortable: true,
					value: 'status',
				},
			],
			sortModeOptions: [
				{label:"Ascending", value:'ASC'},
				{label:"Descending", value:'DEC'}
			],
			alarmLevelOptions: [
				{ name:"Information", id: 1, color: 'blue' },
				{ name:"Urgent", id: 2, color: 'yellow' },
				{ name:"Critical", id: 3, color: 'orange' },			
				{ name:"Life Safety", id: 4, color: 'red' },		
			],
			eventSourceTypeOptions: [
				{ name: "All", id: 0 },
				{ name: "Point event detectors", id: 1 },
				{ name: "Data source events", id: 3 },
				{ name: "System events", id: 4 },
				{ name: "Compound event detectors", id: 5 },
				{ name: "Scheduled events", id: 6 },
				{ name: "Publisher events", id: 7 },
				{ name: "Audit events", id: 8 },
				{ name: "Maintenance events", id: 9 },
			],
			statusOptions: [
				{ label:"All", value: "*" },
				{ label:"Active", value: "A" },
				{ label:"RTN", value: "R" },
				{ label:"No RTN", value: "N" },
			]
		};
	},
	
	methods: {
		async fetchEventList() {
			this.loading = true;
			
			const result = await this.$store.dispatch('searchEvents', this.searchFilters);
			this.eventList = result.rows;
			this.totalEvents = result.total;
			this.loading = false;
		},
	  	open(item, item2) {
			this.$router.push({ name: 'event-details', params: { id: item.id } });
			this.$router.go();
	  	}
	},
};
</script>
