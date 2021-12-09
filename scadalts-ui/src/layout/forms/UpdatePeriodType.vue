<template>
	<v-select
		v-model="selectedType"
		:items="timePeriods"
		item-value="id"
		item-text="label"
		:dense="dense"
        :label="label"
		@change="updateValue()"
	>
	</v-select>
</template>
<script>
import i18n from '@/i18n';
export default {
    data() {
        return {
            selectedType: 2,
        }
    },

	props: {
		dense: {
			type: Boolean,
			default: false,
		},
		types: {
			type: String,
			default: '1,2,3,4,5,6,7,8',
		},
		value: {
			type: Number,
			default: 2,
		},
        label: {
            type: String,
            default: i18n.t('common.timeperiod.title'),
        }
	},

    mounted() {
        this.selectedType = this.value;
    },

	computed: {
		timePeriods() {
			return this.$store.state.timePeriods.filter((e) => {
				return this.types.split(',').includes(`${e.id}`);
			});
		},
	},

	methods: {
		updateValue() {
			this.$emit('update', this.selectedType);
		},
	},
};
</script>