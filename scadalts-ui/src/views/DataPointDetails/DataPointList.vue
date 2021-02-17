<template>
	<div>
		<v-container fluid v-if="!!datapointList">
			<h1>Data Point List</h1>
			<v-text-field
				v-model="search"
        		append-icon="mdi-magnify"
        		label="Search"
        		single-line
        		hide-details
			></v-text-field>
			<v-data-table
				:headers="headers"
				:items="datapointList"
				:sort-by="[]"
				:sort-desc="[]"
				multi-sort
				:loading="loading"
				:search="search"
				class="elevation-1"
				@click:row="open"
				dense>
				<template v-slot:item.enabled="{ item }">
					<v-icon :color="item.enabled ? 'primary' : 'error'" v-show="item.enabled">mdi-decagram</v-icon>
					<v-icon :color="item.enabled ? 'primary' : 'error'" v-show="!item.enabled">mdi-decagram-outline</v-icon>
				</template>
				<template v-slot:item.typeId="{ item }">
					{{$t(`datapoint.type.${item.typeId}`)}}
				</template>
			</v-data-table>
			
		</v-container>
		<v-progress-circular v-else indeterminate color="primary"></v-progress-circular>
	</div>
</template>
<script>
export default {
	name: 'DataPointList',

	data() {
		return {
			datapointList: null,
			loading: true,
			search: '',
			headers: [
				{text: 'Status', align: 'center', value: 'enabled'},
				{text: 'DataSource', align: 'center', value: 'datasourceName'},
				{text: 'Name', align: 'center', value: 'name'},
				{text: 'Description', sortable: false, filterable: false, align: 'center', value: 'description'},
				{text: 'Type', filterable: false, align: 'center', value: 'typeId'},
				{text: 'XID', align: 'center', value: 'xid'},
			],
		};
	},

	mounted() {
		this.fetchDataPointList();
	},

	methods: {
		async fetchDataPointList() {
			this.datapointList = null;
			this.loading = true;
			this.datapointList = await this.$store.dispatch('getAllDataPointsTable');
			this.loading = false;
		},

		

		open(item, item2) {
			console.log(item, item2);
			this.$router.push({ name: 'datapoint-details', params: { id: item.id } });
			this.$router.go();
		},
	},
};
</script>
<style scoped></style>
