<template>
	<v-row>
		<v-col cols="12">
			<h3>{{$t('datapointDetails.pointProperties.logging.title')}}</h3>
		</v-col>
		<v-col cols="12">
			<v-select
				v-model="data.intervalLoggingType"
				:items="loggingTypeList"
				item-value="id"
				item-text="label"
				dense
			></v-select>
		</v-col>
		<v-col cols="12">
			<v-row v-show="data.intervalLoggingType === 4" dense>
				<v-col cols="6"> {{$t('datapointDetails.pointProperties.logging.interval.label')}} </v-col>
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
				<v-row v-if="data.pointLocator.dataTypeId === 3" dense>
					<v-col cols="12">
						<v-select v-model="data.loggingType" dense></v-select>
					</v-col>
				</v-row>
			</v-row>

			<v-row v-if="data.pointLocator.dataTypeId === 3" dense>
				<v-col cols="6"> {{$t('datapointDetails.pointProperties.logging.tolerance')}} </v-col>
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

		<v-col cols="6"> {{$t('datapointDetails.pointProperties.logging.purge')}} </v-col>
		<v-col cols="3">
			<v-text-field v-model="data.purgePeriod" dense></v-text-field>
		</v-col>
		<v-col cols="3">
			<v-select
				v-model="data.purgeType"
				:items="intervalLoggingPeriodTypeList"
				item-value="id"
				item-text="label"
				dense
			></v-select>
		</v-col>
		<v-col cols="6"> {{$t('datapointDetails.pointProperties.logging.cache')}} </v-col>
		<v-col cols="6">
			<v-text-field v-model="data.defaultCacheSize" dense>
				<template v-slot:append-outer>
					<v-btn text block @click="clearCache">
						<v-icon>mdi-delete-sweep</v-icon>
						<span> {{$t('datapointDetails.pointProperties.logging.cache.clear')}}</span>
					</v-btn>
				</template>
			</v-text-field>
		</v-col>
	</v-row>
</template>
<script>
export default {
	name: 'PointPropLogging',

	props: ['data'],

	data() {
		return {};
	},

	computed: {
		intervalLoggingPeriodTypeList() {
			return this.$store.state.timePeriods.filter((e) => {
				return e.id === 1 || e.id === 2 || e.id === 3;
			});
		},
		loggingTypeList() {
			return this.$store.state.dataPoint.loggingTypeList;
		},
	},

	methods: {
		clearCache() {
			this.$store.dispatch('clearDataPointCache', this.data.id);
		},
	},
};
</script>
<style scoped></style>
