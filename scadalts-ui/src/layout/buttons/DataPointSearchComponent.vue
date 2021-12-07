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
		item-value="xid"
		:label="`${$t('datapoint.search.label')}: ${value}`"
		:placeholder="`${$t('datapoint.search.placeholder')}`"
		return-object
		prepend-icon="mdi-magnify"
		@change="emit()"
	>
	</v-autocomplete>
</template>
<script>
export default {
	name: 'DataPointSearchComponent',

	props: ['value', 'dataTypes'],

	data() {
		return {
			isLoading: false,
			datapoints: [],
			search: null,
			select: this.value,
		};
	},

	async mounted() {
		await this.$store.dispatch('fetchDataPointSimpleList', this.dataTypes);
		this.datapoints = this.$store.state.dataPoint.datapointSimpleList;
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
				this.$emit('input', this.select.xid);
				this.$emit('change', this.select);
			}
		},
	},
};
</script>
<style scoped></style>
