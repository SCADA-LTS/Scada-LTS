<template>
	<v-row id="point-prop-event-renderer">
		<v-col cols="12">
			<h3>{{ $t('datapointDetails.pointProperties.eventRenderer.title') }}</h3>
		</v-col>
		<v-col cols="12">
			<v-select
				v-model="selected"
				:items="eventRenderersList"
				item-value="id"
				item-text="label"
				@change="watchEventRendererChange"
				dense
			></v-select>
		</v-col>

		<v-col cols="12">
			<v-row v-if="selected === 1" dense id="renderer-binary">
				<v-col cols="6">
					<v-text-field
						v-model="data.eventTextRenderer.zeroLabel"
						:label="$t('datapointDetails.pointProperties.textRenderer.label.zero')"
						dense
					>
					</v-text-field>
				</v-col>

				<v-col cols="6">
					<v-text-field
						v-model="data.eventTextRenderer.oneLabel"
						:label="$t('datapointDetails.pointProperties.textRenderer.label.one')"
						dense
					>
					</v-text-field>
				</v-col>
			</v-row>

			<v-row v-if="selected === 2" dense id="renderer-multistate">
				<v-col cols="2">
					<v-text-field
						v-model="multistateRenderer.key"
						:label="$t('datapointDetails.pointProperties.eventRenderer.label.key')"
						dense
					>
					</v-text-field>
				</v-col>

				<v-col cols="10">
					<v-text-field
						v-model="multistateRenderer.text"
						:label="$t('datapointDetails.pointProperties.textRenderer.label.text')"
						dense
						append-outer-icon="mdi-plus-circle"
						@click:append-outer="addMultistateValue"
					>
					</v-text-field>
				</v-col>

				<v-divider></v-divider>

				<v-col cols="12" v-if="data.eventTextRenderer.multistateEventValues">
					<v-row
						v-for="e in data.eventTextRenderer.multistateEventValues"
						:key="e.key"
						dense
					>
						<v-col cols="2">
							<v-text-field
								v-model="e.key"
								:label="$t('datapointDetails.pointProperties.eventRenderer.label.key')"
								dense
							>
							</v-text-field>
						</v-col>

						<v-col cols="10">
							<v-text-field
								v-model="e.text"
								:label="$t('datapointDetails.pointProperties.textRenderer.label.text')"
								dense
								append-outer-icon="mdi-close-circle-outline"
								@click:append-outer="delMultistateValue(e)"
							>
							</v-text-field>
						</v-col>
					</v-row>
				</v-col>
			</v-row>

			<v-row v-if="selected === 4" dense id="renderer-range">
				<v-col cols="2">
					<v-text-field
						v-model="rangeRenderer.from"
						:label="$t('datapointDetails.pointProperties.eventRenderer.label.from')"
						dense
					>
					</v-text-field>
				</v-col>
				<v-col cols="2">
					<v-text-field
						v-model="rangeRenderer.to"
						:label="$t('datapointDetails.pointProperties.eventRenderer.label.to')"
						dense
					>
					</v-text-field>
				</v-col>

				<v-col cols="8">
					<v-text-field
						v-model="rangeRenderer.text"
						:label="$t('datapointDetails.pointProperties.textRenderer.label.text')"
						dense
						append-outer-icon="mdi-plus-circle"
						@click:append-outer="addRangeValue"
					>
					</v-text-field>
				</v-col>

				<v-divider></v-divider>

				<v-col cols="12">
					<v-row v-for="e in data.eventTextRenderer.rangeEventValues" :key="e.from" dense>
						<v-col cols="2">
							<v-text-field
								v-model="e.from"
								:label="$t('datapointDetails.pointProperties.eventRenderer.label.from')"
								dense
							>
							</v-text-field>
						</v-col>
						<v-col cols="2">
							<v-text-field
								v-model="e.to"
								:label="$t('datapointDetails.pointProperties.eventRenderer.label.to')"
								dense
							>
							</v-text-field>
						</v-col>

						<v-col cols="8">
							<v-text-field
								v-model="e.text"
								:label="$t('datapointDetails.pointProperties.textRenderer.label.text')"
								dense
								append-outer-icon="mdi-close-circle-outline"
								@click:append-outer="delRangeValue(e)"
							>
							</v-text-field>
						</v-col>
					</v-row>
				</v-col>
			</v-row>
		</v-col>
	</v-row>
</template>
<script>
/**
 * Event Text Renderer for Point Properties
 *
 * An event text renderer's purpose is to display a specific
 * message related to the raw value of data point in several places.
 *
 * @param {Object} data - Point Details object with data.
 *
 * @author Radoslaw Jajko <rjajko@softq.pl>
 * @version 1.0
 */
export default {
	name: 'PointPropEventRenderer',

	props: ['data'],

	data() {
		return {
			selected: undefined,
			rangeRenderer: { from: 0, to: 0, text: '' },
			multistateRenderer: { key: 0, text: '' },
		};
	},

	computed: {
		eventRenderersList() {
			let renderers = [{ id: -1, label: 'None' }];
			if (!!this.data) {
				if (this.data.pointLocator.dataTypeId === 1) {
					// Binary datapoint
					renderers.push(
						this.$store.state.dataPoint.textRenderesList.filter((e) => {
							return e.id === 1;
						})[0]
					);
				} else if (this.data.pointLocator.dataTypeId === 2) {
					// Multistate datapoint
					renderers.push(
						this.$store.state.dataPoint.textRenderesList.filter((e) => {
							return e.id === 2;
						})[0]
					);
				} else if (this.data.pointLocator.dataTypeId === 3) {
					// Numeric datapoint
					renderers.push(
						this.$store.state.dataPoint.textRenderesList.filter((e) => {
							return e.id === 4;
						})[0]
					);
				}
			}
			return renderers;
		},
	},

	mounted() {
		if (!!this.data.eventTextRenderer) {
			switch (this.data.eventTextRenderer.def.exportName) {
				case 'EVENT_NONE':
					this.selected = -1;
					break;
				case 'EVENT_BINARY':
					this.selected = 1;
					break;
				case 'EVENT_MULTISTATE':
					this.selected = 2;
					break;
				case 'EVENT_RANGE':
					this.selected = 4;
					break;
				default:
					console.error('Not found suitable Text Renderer!');
			}
		} else {
			this.selected = -1;
		}
	},

	methods: {
		watchEventRendererChange(val) {
			if (val === -1) {
				val = 0;
			}
			let template = JSON.parse(
				JSON.stringify(this.$store.state.dataPoint.eventRenderersTemplates[val])
			);
			if (!!this.data.eventTextRenderer) {
				if (this.data.eventTextRenderer.def.exportName === template.def.exportName) {
					return;
				}
			}
			this.data.eventTextRenderer = template;
		},

		addRangeValue() {
			this.data.eventTextRenderer.rangeEventValues.push(
				Object.assign({}, this.rangeRenderer)
			);
			this.rangeRenderer = {
				from: 0,
				to: 0,
				text: '',
			};
		},
		delRangeValue(val) {
			this.data.eventTextRenderer.rangeEventValues = this.data.eventTextRenderer.rangeEventValues.filter(
				(e) => {
					return e.text !== val.text;
				}
			);
		},

		addMultistateValue() {
			this.data.eventTextRenderer.multistateEventValues.push(
				Object.assign({}, this.multistateRenderer)
			);
			this.rangeRenderer = { key: 0, text: '' };
		},
		delMultistateValue(val) {
			this.data.eventTextRenderer.multistateEventValues = this.data.eventTextRenderer.multistateEventValues.filter(
				(e) => {
					return e.key !== val.key;
				}
			);
		},
	},
};
</script>
<style scoped></style>
