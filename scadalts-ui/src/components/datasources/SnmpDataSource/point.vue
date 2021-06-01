<template>
	<div>
		<DataPointCreation
			title="SNPM Data Point"
			:creator="createMode"
			@cancel="cancel()"
			@accept="save()"
		>
			<template v-slot:selector>
				<v-select v-model="datapoint.type" :items="datapointTypes"></v-select>
			</template>

			<v-row>
				<v-col>
					<v-text-field v-model="datapoint.name" label="Data Point Name"></v-text-field>
				</v-col>
				<v-col>
					<v-text-field v-model="datapoint.xid" label="Data Point Export ID"></v-text-field>
				</v-col>
                <v-col>
                    <v-checkbox v-model="datapoint.settable" label="Settable"></v-checkbox>
                </v-col>
			</v-row>

            <!-- Binary -->
			<v-row v-if="datapoint.type === 'Binary'">
				<v-col>
					<v-text-field label="SNMP Bin"></v-text-field>
				</v-col>
			</v-row>

            <!-- Multistate -->
			<v-row v-if="datapoint.type === 'Multistate'">
				<v-col>
					<v-text-field label="Type MS SNMP"></v-text-field>
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
