<template>
	<div>
		<v-container fluid v-if="!!datapointList">
			<h1>Data Point List</h1>
			<v-simple-table>
				<template v-slot:default>
					<thead>
						<tr>
							<th>Id</th>
							<th>Name</th>
						</tr>
					</thead>
					<tbody>
						<tr v-for="item in datapointList" :key="item.id" @click="open(item)">
							<td>
								{{ item.id }}
							</td>
							<td>
								{{ item.name }}
							</td>
						</tr>
					</tbody>
				</template>
			</v-simple-table>
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
		};
	},

	mounted() {
		this.fetchDataPointList();
	},

	methods: {
		async fetchDataPointList() {
			this.datapointList = null;
			this.datapointList = await this.$store.dispatch('getAllDatapoints');
		},

		open(item) {
			this.$router.push({ name: 'datapoint-details', params: { id: item.id } });
			this.$router.go();
		},
	},
};
</script>
<style scoped></style>
