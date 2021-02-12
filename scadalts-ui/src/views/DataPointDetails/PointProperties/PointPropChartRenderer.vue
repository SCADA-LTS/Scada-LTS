<template>
	<v-row>
		<v-col cols="12">
			<h3>Chart renderer</h3>
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
						label="Limit"
						dense
					></v-text-field>
				</v-col>
			</v-row>

			<v-row v-if="selected === 1 || selected === 2">
				<v-col cols="6">
					<v-text-field
						v-model="data.chartRenderer.numberOfPeriods"
						label="Time period"
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
						label="Include sum"
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
				{ id: -1, label: 'None' },
				{ id: 0, label: 'Table' },
				{ id: 1, label: 'Image' },
				{ id: 2, label: 'Statistics' },
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
		}
	},

	methods: {
		watchChartRendererChange(val) {
			console.log(val);
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
