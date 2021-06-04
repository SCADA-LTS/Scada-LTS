<template>
	<div>
		<DataPointCreation
			title="Virtual Data Point"
			:creator="createMode"
			:datapoint="datapoint"
			@cancel="cancel()"
			@accept="save()"
		>
			<template v-slot:selector>
				<v-select v-model="datapoint.type" :items="datapointTypes"></v-select>
			</template>

            <!-- Binary -->
			<v-row v-if="datapoint.type === 'Binary'">
				<v-col>
					<v-text-field label="Type"></v-text-field>
				</v-col>
			</v-row>

            <!-- Multistate -->
			<v-row v-if="datapoint.type === 'Multistate'">
				<v-col>
					<v-text-field label="Type MS"></v-text-field>
				</v-col>
			</v-row>
		</DataPointCreation>
	</div>
</template>
<script>
import DataPointCreation from '../DataPointCreation';
export default {
	components: {
		DataPointCreation,
	},

	props: {
		createMode: {
			type: Boolean,
			default: true,
		},
		datapoint: {
			required: true,
			type: Object,
		},
	},

    data() {
        return {
            datapointTypes: ['Binary', 'Multistate', 'Numeric', 'Alphanumeric'],
        }
    },

	methods: {
		cancel() {
			this.$emit('canceled');
		},

		save() {
			this.$emit('saved', this.datapoint);
		},
	},
};
</script>
<style></style>
