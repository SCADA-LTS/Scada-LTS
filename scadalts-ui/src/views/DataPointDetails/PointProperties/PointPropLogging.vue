<template>
	<v-row id="point-prop-logging">
		<v-col cols="12">
			<h3>{{ $t('datapointDetails.pointProperties.logging.title') }}</h3>
		</v-col>
		<v-col cols="12">
			<v-select
				v-model="data.loggingType"
				:items="loggingTypeList"
				item-value="id"
				item-text="label"
				dense
			></v-select>
		</v-col>
		<v-col cols="12">
			<v-row v-show="data.loggingType === 4" dense>
				<v-col cols="6">
					{{ $t('datapointDetails.pointProperties.logging.interval.label') }}
				</v-col>
				<v-col cols="3">
					<v-text-field v-model="data.intervalLoggingPeriod" dense></v-text-field>
				</v-col>
				<v-col cols="3">
					<v-select
						v-model="data.intervalLoggingPeriodType"
						:items="intervalLoggingPeriodTypeList"
						item-value="id"
						item-text="label"
						dense
					></v-select>
				</v-col>
				<v-col cols="12" v-if="data.pointLocator.dataTypeId === 3" dense>
					<v-select
						v-model="data.intervalLoggingType"
						:items="valueTypeList"
						item-value="id"
						item-text="label"
						:label="$t('datapointDetails.pointProperties.logging.valueType')"
						dense
					></v-select>
				</v-col>
			</v-row>

			<v-row v-if="data.pointLocator.dataTypeId === 3" dense>
				<v-col cols="6">
					{{ $t('datapointDetails.pointProperties.logging.tolerance') }}
				</v-col>
				<v-col cols="6">
					<v-text-field v-model="data.tolerance" dense></v-text-field>
				</v-col>
				<v-col cols="12">
					<v-switch
						v-model="data.discardExtremeValues"
						:label="$t('datapointDetails.pointProperties.logging.extreme')"
					></v-switch>
				</v-col>

				<v-col cols="6">
					<v-text-field
						v-model="data.discardLowLimit"
						:label="$t('datapointDetails.pointProperties.logging.discard.low')"
						dense
					></v-text-field>
				</v-col>
				<v-col cols="6">
					<v-text-field
						v-model="data.discardHighLimit"
						:label="$t('datapointDetails.pointProperties.logging.discard.high')"
						dense
					></v-text-field>
				</v-col>
			</v-row>
		</v-col>

		<v-col cols="6"> {{ $t('datapointDetails.pointProperties.logging.purge') }} </v-col>
		<v-col cols="3">
			<v-text-field v-model="data.purgePeriod" dense></v-text-field>
		</v-col>
		<v-col cols="3">
			<v-select
				v-model="data.purgeType"
				:items="purgePeriodTypeList"
				item-value="id"
				item-text="label"
				dense
			></v-select>
		</v-col>
		<v-col cols="6"> {{ $t('datapointDetails.pointProperties.logging.cache') }} </v-col>
		<v-col cols="6">
			<v-text-field v-model="data.defaultCacheSize" dense>
				<template v-slot:append-outer>
					<v-btn text block @click="clearCache" disabled>
						<v-icon>mdi-delete-sweep</v-icon>
						<span> {{ $t('datapointDetails.pointProperties.logging.cache.clear') }}</span>
					</v-btn>
				</template>
			</v-text-field>
		</v-col>
		<v-snackbar v-model="response.status">
			{{ response.message }}
		</v-snackbar>
	</v-row>
</template>
<script>
/**
 * Logging for Point Properties
 *
 * ScadaLTS supports four types of point value logging
 * - When point value changes is the default logging setting.
 * - The All data setting causes every point update to be saved to the database.
 * - The Do not log setting prevents any historical data for the point
 * from being stored in the database.
 * - The Interval setting allows the collection of data via the data source
 * to be separated from its logging.
 * - The When point timestamp changes setting. This is similar in behaviour
 * to the on value change setting, but the timestamp of the sample
 * is compared instead of the value.
 *
 * @param {Object} data - Point Details object with data.
 *
 * @author Radoslaw Jajko <rjajko@softq.pl>
 * @version 1.0
 */
export default {
	name: 'PointPropLogging',

	props: ['data'],

	data() {
		return {
			response: {
				status: false,
				message: '',
			},
		};
	},

	computed: {
		intervalLoggingPeriodTypeList() {
			return this.$store.state.timePeriods.filter((e) => {
				return e.id === 1 || e.id === 2 || e.id === 3;
			});
		},
		purgePeriodTypeList() {
			return this.$store.state.timePeriods.filter((e) => {
				return e.id === 4 || e.id === 5 || e.id === 6 || e.id === 7;
			});
		},
		loggingTypeList() {
			return this.$store.state.dataPoint.loggingTypeList;
		},
		valueTypeList() {
			return this.$store.state.dataPoint.valueTypeList;
		},
	},

	methods: {
		clearCache() {
			this.$store.dispatch('clearDataPointCache', this.data.id).then((resp) => {
				this.response.status = true;
				this.response.message = this.$t('common.snackbar.delete.success');
			});
		},
	},
};
</script>
<style scoped></style>
