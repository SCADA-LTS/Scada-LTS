<template>
	<v-autocomplete
		v-model="select"
		:items="datapoints"
		:loading="isLoading"
		:search-input.sync="search"
		cache-items
		hide-no-data
		hide-details
		item-text="name"
		item-value="id"
		:label="$t('datapoint.search.label')"
		:placeholder="$t('datapoint.search.placeholder')"
		return-object
		prepend-icon="mdi-magnify"
		@change="emit()"
	>
	</v-autocomplete>
</template>
<script>
export default {
	name: 'DataPointSearchComponent',

	data() {
		return {
			isLoading: false,
			datapoints: [],
			search: null,
			select: null,
		};
	},

	mounted() {
		this.$store.dispatch('fetchDataPointSimpleList');
	},

	watch: {
		search(val) {
			val && this.quesrySelect(val);
		},
	},

	methods: {
		async quesrySelect(v) {
			this.isLoading = true;
			this.datapoints = await this.$store.dispatch('getDataPointSimpleFilteredList', v);
			this.isLoading = false;
		},
		emit() {
			if (!!this.select) {
				this.$emit('change', this.select);
			}
		},
	},
};
</script>
<style scoped></style>
