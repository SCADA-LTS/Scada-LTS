<template>
	<v-row>
		<v-col cols="12">
			<h3>{{ $t('datapointDetails.pointProperties.chartRenderer') }}</h3>
		</v-col>
		<v-col cols="12">
			<v-select
				v-model="selected"
				:items="chartRenderersList"
				item-value="id"
				item-text="label"
				@change="watchChartRendererChange"
				dense
			>
			</v-select>
		</v-col>
		<v-col cols="12">
			<v-row v-if="selected === 0">
				<v-col cols="12">
					<v-text-field
						v-model="data.chartRenderer.limit"
						:label="$t('datapointDetails.pointProperties.chartRenderer.limit')"
						dense
					></v-text-field>
				</v-col>
			</v-row>

			<v-row v-if="selected === 1 || selected === 2">
				<v-col cols="6">
					<v-text-field
						v-model="data.chartRenderer.numberOfPeriods"
						:label="$t('common.timeperiod.title')"
						dense
					>
					</v-text-field>
				</v-col>
				<v-col cols="6">
					<v-select
						v-model="data.chartRenderer.timePeriod"
						:items="timePeriods"
						item-value="id"
						item-text="label"
						dense
					>
					</v-select>
				</v-col>
				<v-col cols="12" v-if="selected === 2">
					<v-switch
						v-model="data.chartRenderer.includeSum"
						:label="$t('datapointDetails.pointProperties.chartRenderer.sum')"
					></v-switch>
				</v-col>
			</v-row>
		</v-col>
	</v-row>
</template>
<script>
export default {
	name: 'PointPropChartRenderer',

	props: ['data'],

	data() {
		return {
			selected: undefined,
			chartRenderersList: [
				{
					id: -1,
					label: this.$t('datapointDetails.pointProperties.chartRenderer.type.none'),
				},
				{
					id: 0,
					label: this.$t('datapointDetails.pointProperties.chartRenderer.type.table'),
				},
				{
					id: 1,
					label: this.$t('datapointDetails.pointProperties.chartRenderer.type.image'),
				},
				{
					id: 2,
					label: this.$t('datapointDetails.pointProperties.chartRenderer.type.stats'),
				},
			],
		};
	},

	computed: {
		timePeriods() {
			return this.$store.state.timePeriods.filter((e) => {
				return !(e.id === 0 || e.id === 8 || e.id === 7);
			});
		},
	},

	mounted() {
		if (!!this.data.chartRenderer) {
			switch (this.data.chartRenderer.def.exportName) {
				case 'TABLE':
					this.selected = 0;
					break;
				case 'IMAGE':
					this.selected = 1;
					break;
				case 'STATS':
					this.selected = 2;
					break;
				default:
					console.error('Not found suitable Text Renderer!');
			}
		} else {
			this.selected = -1;
		}
	},

	methods: {
		watchChartRendererChange(val) {
			let template = JSON.parse(
				JSON.stringify(this.$store.state.dataPoint.chartRenderersTemplates[val]),
			);
			if (!!template) {
				if (!!this.data.chartRenderer) {
					if (this.data.chartRenderer.def.exportName !== template.def.exportName) {
						this.data.chartRenderer = template;
						if (this.data.chartRenderer.hasOwnProperty('startTime')) {
							this.data.chartRenderer.startTime = new Date().getTime();
						}
					}
				} else {
					this.data.chartRenderer = template;
					if (this.data.chartRenderer.hasOwnProperty('startTime')) {
						this.data.chartRenderer.startTime = new Date().getTime();
					}
				}
			} else {
				this.data.chartRenderer = null;
			}
		},
	},
};
</script>
<style scoped></style>
