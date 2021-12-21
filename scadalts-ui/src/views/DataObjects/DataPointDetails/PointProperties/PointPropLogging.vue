<template>
	<v-row id="point-prop-logging">
		<v-col cols="12">
			<h3>{{ $t('datapointDetails.pointProperties.logging.title') }}</h3>
		</v-col>
		<v-col cols="12" id="point-prop-logging--type">
			<v-select
				v-model="data.loggingType"
				:items="loggingTypeList"
				item-value="id"
				item-text="label"
				:label="$t('datapointDetails.pointProperties.logging.type.title')"
				dense
			></v-select>
		</v-col>
		<v-col cols="12" v-show="data.loggingType === consts.loggingTypes.INTERVAL" id="log-type--interval">
			<v-row dense>
				<v-col cols="6">
					<v-text-field
						v-model="data.intervalLoggingPeriod"
						:label="$t('datapointDetails.pointProperties.logging.interval.label')"
						dense
					></v-text-field>
				</v-col>
				<v-col cols="6">
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
		</v-col>
		<v-col cols="12" v-if="data.pointLocator.dataTypeId === 3" id="log-type--numeric">
			<v-row dense>
				<v-col cols="12">
					<v-text-field
						v-model="data.tolerance"
						dense
						:label="$t('datapointDetails.pointProperties.logging.tolerance')"
						:disabled="data.loggingType !== consts.loggingTypes.ON_CHANGE"
					></v-text-field>
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
						:disabled="!data.discardExtremeValues"
						dense
					></v-text-field>
				</v-col>
				<v-col cols="6">
					<v-text-field
						v-model="data.discardHighLimit"
						:label="$t('datapointDetails.pointProperties.logging.discard.high')"
						:disabled="!data.discardExtremeValues"
						dense
					></v-text-field>
				</v-col>
			</v-row>
		</v-col>

		<v-col cols="12" id="purge--select">
			<v-select
				v-model="data.purgeStrategy"
				:label="$t('datapointDetails.pointProperties.logging.purge.title')"
				:items="purgeStrategyList"
				item-value="id"
				item-text="label"
				:disabled="data.loggingType === consts.loggingTypes.NONE"
				dense
			></v-select>
		</v-col>

		<v-col cols="12" v-show="data.purgeStrategy === 1" id="purge--period">
			<v-row dense>
				<v-col cols="6">
					<v-text-field
						:label="$t('datapointDetails.pointProperties.logging.purge')"
						v-model="data.purgePeriod"
						:disabled="data.loggingType === consts.loggingTypes.NONE"
						dense
					></v-text-field>
				</v-col>
				<v-col cols="6">
					<v-select
						v-model="data.purgeType"
						:items="purgePeriodTypeList"
						item-value="id"
						item-text="label"
						:disabled="data.loggingType === consts.loggingTypes.NONE"
						dense
					></v-select>
				</v-col>
			</v-row>
		</v-col>

		<v-col cols="12" v-show="data.purgeStrategy === 2" id="purge--values">
			<v-row dense>
				<v-col cols="12">
					<v-text-field
						v-model="data.purgeValuesLimit"
						:label="$t('datapointDetails.pointProperties.logging.purgeLimitValues')"
						:disabled="data.loggingType === consts.loggingTypes.NONE"
						:rules="ruleMinPurgeValues"
						dense
					></v-text-field>
				</v-col>
			</v-row>
		</v-col>

		<v-col cols="12" id="cache">
			<v-text-field
				v-model="data.defaultCacheSize"
				:label="$t('datapointDetails.pointProperties.logging.cache')"
				dense
			>
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
			consts: {
				loggingTypes: {
					ON_CHANGE: 1,
					ALL: 2,
					NONE: 3,
					INTERVAL: 4,
					ON_TS_CHANGE: 5,
				},
			},
			ruleMinPurgeValues: [
				v => !!v || 'Value is required.',
				v => v > 1 || 'Value must be greater than 1.',
			],
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
		purgeStrategyList() {
			return this.$store.state.dataPoint.purgeStrategyList;
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
