<template>
	<div>
		<v-container fluid v-if="!!datapointList">
			<h1>{{ $t('datapointDetails.pointList.title') }}</h1>
			<v-text-field
				v-model="search"
				append-icon="mdi-magnify"
				:label="$t('common.search')"
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
				dense
			>
				<template v-slot:item.enabled="{ item }">
					<v-icon :color="item.enabled ? 'primary' : 'error'" v-show="item.enabled"
						>mdi-decagram</v-icon
					>
					<v-icon :color="item.enabled ? 'primary' : 'error'" v-show="!item.enabled"
						>mdi-decagram-outline</v-icon
					>
				</template>
				<template v-slot:item.typeId="{ item }">
					{{ $t(`datapoint.type.${item.typeId}`) }}
				</template>
                <template v-slot:item.name="{ item }">
                    <span v-html="item.name"></span>
                </template>
                <template v-slot:item.datasourceName="{ item }">
                    <span v-html="item.datasourceName"></span>
                </template>
                <template v-slot:item.description="{ item }">
                    <span v-html="item.description"></span>
                </template>
                <template v-slot:item.xid="{ item }">
                    <span v-html="item.xid"></span>
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
				{
					text: this.$t('datapointDetails.pointList.table.header.status'),
					align: 'center',
					value: 'enabled',
				},
				{
					text: this.$t('datapointDetails.pointList.table.header.datasource'),
					align: 'center',
					value: 'datasourceName',
				},
				{
					text: this.$t('datapointDetails.pointList.table.header.name'),
					align: 'center',
					value: 'name',
				},
				{
					text: this.$t('datapointDetails.pointList.table.header.description'),
					sortable: false,
					filterable: false,
					align: 'center',
					value: 'description',
				},
				{
					text: this.$t('datapointDetails.pointList.table.header.type'),
					filterable: false,
					align: 'center',
					value: 'typeId',
				},
				{
					text: this.$t('datapointDetails.pointList.table.header.xid'),
					align: 'center',
					value: 'xid',
				},
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
			this.$router.push({ name: 'datapoint-details', params: { id: item.id } });
			this.$router.go();
		},
	},
};
</script>
<style scoped></style>
