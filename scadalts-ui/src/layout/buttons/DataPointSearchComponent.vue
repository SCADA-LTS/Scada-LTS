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

	mounted() {
		this.$store.dispatch('fetchDataPointSimpleList', this.dataTypes);
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
				this.$emit('input', this.select.id);
				this.$emit('change', this.select);
			}
		},
	},
};
</script>
<style scoped></style>
