<template>
	<div>
		<DataPointCreation
			title="Virtual Data Point"
			:creator="createMode"
			@cancel="cancel()"
			@accept="save()"
		>
			<template v-slot:selector>
				<v-select v-model="selectedType" :items="datapointTypes"></v-select>
			</template>

			<v-row>
				<v-col>
					<v-text-field label="Data Point Name"></v-text-field>
				</v-col>
				<v-col>
					<v-text-field label="Data Point Export ID"></v-text-field>
				</v-col>
                <v-col>
                    <v-checkbox label="Settable"></v-checkbox>
                </v-col>
			</v-row>

            <!-- Binary -->
			<v-row v-if="selectedType === 'Binary'">
				<v-col>
					<v-text-field label="Type"></v-text-field>
				</v-col>
			</v-row>

            <!-- Multistate -->
			<v-row v-if="selectedType === 'Multistate'">
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
	},

    data() {
        return {
            selectedType: '',
            datapointTypes: ['Binary', 'Multistate', 'Numeric', 'Alphanumeric'],
        }
    },

	methods: {
		cancel() {
			this.$emit('canceled');
		},

		save() {
			this.$emit('saved', this.datasource);
		},
	},
};
</script>
<style></style>
