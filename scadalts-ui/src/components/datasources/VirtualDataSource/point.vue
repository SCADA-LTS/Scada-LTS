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
						v-model="datapoint.changeTypeId"
						:items="changeTypes"
					></v-select>
				</v-col>
				
			</v-row>

            <!-- Binary -->
			<v-row v-if="datapoint.type === DataTypes.BINARY">
				<v-col v-if="datapoint.changeTypeId === DataChangeTypes.ALTERNATE_BOOLEAN 
					|| datapoint.changeTypeId === DataChangeTypes.RANDOM_BOOLEAN">
					<v-select label="Start Value" v-model="pointLocator.startValue">
						<v-option value="0">False</v-option>
						<v-option value="1">True</v-option>
					</v-select>
				</v-col>

				<v-col v-else>
					<v-text-field label="Start Value" v-model="pointLocator.startValue"></v-text-field>
				</v-col>

				
			</v-row>

            <!-- Multistate -->
			<v-row v-if="datapoint.type === DataTypes.MULTISTATE">
				<v-col v-if="datapoint.changeTypeId === DataChangeTypes.INCREMENT_MULTISTATE 
					|| datapoint.changeTypeId === DataChangeTypes.RANDOM_MULTISTATE">
					<v-row>
						<v-col>
							<v-text-field type="Number" label="Values" v-model="multistateValue">
								<v-icon slot="append" @click="addMsValue">mdi-plus</v-icon>
							</v-text-field>
						</v-col>
					</v-row>
					<v-row>
						<v-col v-for="(v, index) in pointLocator.values"  :key="index">
							<span @click="removeMsValue(v)">{{v}}</span>
						</v-col>
					</v-row>
					<v-row>
						<v-checkbox label="Roll" v-model="pointLocator.roll"></v-checkbox>
					</v-row>
					<v-row>
						<v-select label="Initail Value" v-model="pointLocator.startValue"></v-select>
					</v-row>
				</v-col>

				<v-col v-else>
					<v-text-field label="Initial Value" v-model="pointLocator.startValue"></v-text-field>
				</v-col>
			</v-row>

			<!-- Numeric -->
			<v-row v-if="datapoint.type === DataTypes.NUMERIC">
				<v-col v-if="datapoint.changeTypeId !== DataChangeTypes.NO_CHANGE">
					<v-row>
						<v-col>
							<v-text-field label="Minimum" v-model="pointLocator.min"></v-text-field>
						</v-col>
						<v-col>
							<v-text-field label="Maximum" v-model="pointLocator.max"></v-text-field>
						</v-col>
					</v-row>
					<v-row v-if="datapoint.changeTypeId === DataChangeTypes.BROWNIAN">
						<v-col>
							<v-text-field label="Maximum Change" v-model="pointLocator.maxChange"></v-text-field>
						</v-col>
					</v-row>

					<v-row v-if="datapoint.changeTypeId === DataChangeTypes.INCREMENT_ANALOG">
						<v-col>
							<v-text-field label="Change Change" v-model="pointLocator.change"></v-text-field>
							<v-checkbox label="Roll" v-model="pointLocator.roll"></v-checkbox>
							<!--  roll -->
						</v-col>
					</v-row>
				</v-col>

				<v-col>
					<v-text-field label="Initial Value" v-model="pointLocator.startValue"></v-text-field>
				</v-col>

			</v-row>

			<!-- Alphanumeric -->
			<v-row v-if="datapoint.type === DataTypes.APLHANUMERIC">
				<v-col>
					<v-text-field label="Initial Value" v-model="pointLocator.startValue"></v-text-field>
				</v-col>
			</v-row>

			


		</DataPointCreation>
	</div>
</template>
<script>
import DataPointCreation from '../DataPointCreation';
import { DataTypes, DataChangeTypes } from '@/store/dataSource/constants';
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
			msValues: ['1', '2'],
			DataTypes: DataTypes,
			DataChangeTypes: DataChangeTypes,
			pointLocator: {
				startValue: '',
        		min: 0,
        		max: 100,
        		maxChange: 10,
        		change: '',
        		roll: false,
        		values: [],
        		volatility: 0,
        		attractionPointId: 0
			},
			multistateValue: 0,
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

		addMsValue() {
			this.pointLocator.values.push(this.multistateValue);
			this.multistateValue = Number(this.multistateValue) + 1;
		},

		removeMsValue(index) {
			this.pointLocator.values = this.pointLocator.values.filter(t => t !== index);
		}
	},
};
</script>
<style></style>
