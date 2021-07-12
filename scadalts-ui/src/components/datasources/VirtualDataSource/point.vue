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

			<v-row>
				<v-col>
					<v-select 
						label="Change Type"
						v-model="selectedChangeType"
						:items="changeTypes"
					></v-select>
				</v-col>
				
			</v-row>

            <!-- Binary -->
			<v-row v-if="datapoint.type === 0">
				<v-col v-if="datapoint.changeTypeId === 0 || datapoint.changeTypeId === 4">
					<v-select label="Start Value">
						<v-option value="0">False</v-option>
						<v-option value="1">True</v-option>
					</v-select>
				</v-col>

				
			</v-row>

            <!-- Multistate -->
			<v-row v-if="datapoint.type === 1">
				<v-col v-if="selectedChangeType === 2 || selectedChangeType === 4">
					<v-row>
						<v-col>
							<v-text-field label="Values"></v-text-field>
						</v-col>
					</v-row>
					<v-row>
						<v-col v-for="v in msValues" :key="v">
							<span>{{v}}</span>
						</v-col>
					</v-row>
					<v-row>
						<v-checkbox label="Roll"></v-checkbox>
					</v-row>
					<v-row>
						<v-select label="Initail Value"></v-select>
					</v-row>
				</v-col>

				<v-col v-if="selectedChangeType === 3">
					<v-text-field label="Initial Value"></v-text-field>
				</v-col>
			</v-row>

			<!-- Numeric -->
			<v-row v-if="datapoint.type === 2">
				<v-col v-if="selectedChangeType !== 3">
					<v-row>
						<v-col>
							<v-text-field label="Minimum"></v-text-field>
						</v-col>
						<v-col>
							<v-text-field label="Maximum"></v-text-field>
						</v-col>
					</v-row>
					<v-row v-if="selectedChangeType === 1">
						<v-col>
							<v-text-field label="Maximum Change"></v-text-field>
						</v-col>
					</v-row>

					<v-row v-if="selectedChangeType === 2">
						<v-col>
							<v-text-field label="Change Change"></v-text-field>
							<!--  roll -->
						</v-col>
					</v-row>
				</v-col>
			</v-row>

			<v-row>
				<v-col>
					<v-text-field label="Initial Value"></v-text-field>
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
			selectedChangeType:'',
			msValues: ['1', '2']
        }
    },

	computed: {
		changeTypes() {
			console.log(this.datapoint)
			if(!!this.datapoint) {
				return this.$store.getters.getVirtualDatapointChangeType(this.datapoint.type);
			} else {
				return 1;
			}
			
		},

		datapointTypes() {
			return this.$store.state.dataSourceState.datapointTypes;	
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
