<template>
	<v-row id="point-prop-text-renderer">
		<v-col cols="12">
			<h3>{{ $t('datapointDetails.pointProperties.textRenderer.title') }}</h3>
		</v-col>
		<v-col cols="12" v-if="textRenderesList" id="text-renderer-selector">
			<v-select
				v-model="selected"
				:items="textRenderesList"
				item-value="id"
				item-text="label"
				@change="watchTextRendererChange"
				dense
			></v-select>
		</v-col>

		<v-col cols="12">
			<v-row v-if="selected === 0" dense id="renderer-analog">
				<v-col cols="6">
					<v-text-field
						v-model="data.textRenderer.format"
						:label="$t('datapointDetails.pointProperties.textRenderer.label.format')"
						dense
					>
					</v-text-field>
				</v-col>
				<v-col cols="6">
					<v-text-field
						v-model="data.textRenderer.suffix"
						:label="$t('datapointDetails.pointProperties.textRenderer.label.suffix')"
						dense
					>
					</v-text-field>
				</v-col>
			</v-row>

			<v-row v-if="selected === 1" dense id="renderer-binary">
				<v-col cols="1">
					<v-menu offset-y :close-on-content-click="false">
						<template v-slot:activator="{ on }">
							<v-btn :color="data.textRenderer.zeroColour" v-on="on" block> </v-btn>
						</template>
						<v-color-picker v-model="data.textRenderer.zeroColour"> </v-color-picker>
					</v-menu>
				</v-col>
				<v-col cols="11">
					<v-text-field
						v-model="data.textRenderer.zeroLabel"
						:label="$t('datapointDetails.pointProperties.textRenderer.label.zero')"
						dense
					>
					</v-text-field>
				</v-col>
				<v-col cols="1">
					<v-menu offset-y :close-on-content-click="false">
						<template v-slot:activator="{ on }">
							<v-btn :color="data.textRenderer.oneColour" v-on="on" block> </v-btn>
						</template>
						<v-color-picker v-model="data.textRenderer.oneColour"> </v-color-picker>
					</v-menu>
				</v-col>
				<v-col cols="11">
					<v-text-field
						v-model="data.textRenderer.oneLabel"
						:label="$t('datapointDetails.pointProperties.textRenderer.label.one')"
						dense
					>
					</v-text-field>
				</v-col>
			</v-row>

			<v-row v-if="selected === 2" dense id="renderer-multistate">
				<v-col cols="1">
					<v-menu offset-y :close-on-content-click="false">
						<template v-slot:activator="{ on }">
							<v-btn :color="multistateRenderer.colour" v-on="on" block> </v-btn>
						</template>
						<v-color-picker v-model="multistateRenderer.colour"> </v-color-picker>
					</v-menu>
				</v-col>
				<v-col cols="3">
					<v-text-field
						v-model="multistateRenderer.key"
						:label="$t('datapointDetails.pointProperties.eventRenderer.label.key')"
						dense
					>
					</v-text-field>
				</v-col>
				<v-col cols="8">
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

				<v-row v-for="e in data.textRenderer.multistateValues" :key="e.key" dense>
					<v-col cols="1">
						<v-btn :color="e.colour" block> </v-btn>
					</v-col>

					<v-col cols="3">
						<v-text-field
							v-model="e.key"
							:label="$t('datapointDetails.pointProperties.eventRenderer.label.key')"
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
							@click:append-outer="delMultistateValue(e)"
						>
						</v-text-field>
					</v-col>
				</v-row>
			</v-row>

			<v-row v-if="selected === 3" dense id="renderer-plain">
				<v-col cols="12">
					<v-text-field
						v-model="data.textRenderer.format"
						:label="$t('datapointDetails.pointProperties.textRenderer.label.suffix')"
						dense
					></v-text-field>
				</v-col>
			</v-row>

			<v-row v-if="selected === 4" dense id="renderer-range">
				<v-col cols="12">
					<v-text-field
						v-model="data.textRenderer.format"
						:label="$t('datapointDetails.pointProperties.textRenderer.label.format')"
						dense
					>
					</v-text-field>
				</v-col>
				<v-col cols="1">
					<v-menu offset-y :close-on-content-click="false">
						<template v-slot:activator="{ on }">
							<v-btn :color="rangeRenderer.colour" v-on="on" block> </v-btn>
						</template>
						<v-color-picker v-model="rangeRenderer.colour"> </v-color-picker>
					</v-menu>
				</v-col>
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
				<v-col cols="7">
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

				<v-row v-for="e in data.textRenderer.rangeValues" :key="e.text" dense>
					<v-col cols="1">
						<v-btn :color="e.colour" block></v-btn>
					</v-col>

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
					<v-col cols="7">
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
			</v-row>

			<v-row v-if="selected === 5" dense id="renderer-time">
				<v-col cols="12">
					<v-text-field
						v-model="data.textRenderer.format"
						:label="$t('datapointDetails.pointProperties.textRenderer.label.format')"
						dense
					>
					</v-text-field>
				</v-col>
				<v-col cols="12">
					<v-text-field
						v-model="data.textRenderer.conversionExponent"
						:label="$t('datapointDetails.pointProperties.textRenderer.label.exponent')"
						dense
					>
					</v-text-field>
				</v-col>
			</v-row>
		</v-col>
	</v-row>
</template>
<script>
/**
 * Text Renderer for Point Properties
 *
 * A text renderer's purpose is to convert a raw value into a form
 * that is more recognizable and informative to a human user.
 * The ways to render raw values depend upon the data type
 * of value to be rendered.
 *
 * @param {Object} data - Point Details object with data.
 *
 * @author Radoslaw Jajko <rjajko@softq.pl>
 * @version 1.0
 */
export default {
	name: 'PointPropTextRenderer',

	props: ['data'],

	data() {
		return {
			selected: undefined,
			rangeRenderer: { from: 0, to: 0, text: '', colour: '#458e23' },
			multistateRenderer: { key: 0, text: '', colour: '#458e23' },
		};
	},

	computed: {
		textRenderesList() {
			if (!!this.data) {
				if (this.data.pointLocator.dataTypeId === 1) {
					//Binary datapoint
					return this.$store.state.dataPoint.textRenderesList.filter((e) => {
						return e.id === 1 || e.id === 3;
					});
				} else if (this.data.pointLocator.dataTypeId === 2) {
					//Multistate datapoint
					return this.$store.state.dataPoint.textRenderesList.filter((e) => {
						return e.id === 2 || e.id === 3;
					});
				} else if (this.data.pointLocator.dataTypeId === 4) {
					//Alphanumeric datapoint
					return this.$store.state.dataPoint.textRenderesList.filter((e) => {
						return e.id === 3;
					});
				} else {
					//Numeric datapoint
					return this.$store.state.dataPoint.textRenderesList.filter((e) => {
						return !(e.id === 1 || e.id === 2);
					});
				}
			}
			return null;
		},
	},

	mounted() {
		if (!!this.data.textRenderer) {
			switch (this.data.textRenderer.def.exportName) {
				case 'ANALOG':
					this.selected = 0;
					break;
				case 'BINARY':
					this.selected = 1;
					break;
				case 'MULTISTATE':
					this.selected = 2;
					break;
				case 'PLAIN':
					this.selected = 3;
					break;
				case 'RANGE':
					this.selected = 4;
					break;
				case 'TIME':
					this.selected = 5;
					break;
				default:
					console.error('Not found suitable Text Renderer!');
			}
		}
	},

	methods: {
		watchTextRendererChange(val) {
			let template = JSON.parse(
				JSON.stringify(this.$store.state.dataPoint.textRenderesTemplates[val]),
			);
			if (this.data.textRenderer.def.exportName !== template.def.exportName) {
				this.data.textRenderer = template;
			}
		},
		addRangeValue() {
			this.data.textRenderer.rangeValues.push(Object.assign({}, this.rangeRenderer));
			this.rangeRenderer = { from: 0, to: 0, text: '', colour: '#458e23' };
		},
		delRangeValue(val) {
			this.data.textRenderer.rangeValues = this.data.textRenderer.rangeValues.filter(
				(e) => {
					return e.text !== val.text;
				},
			);
		},

		addMultistateValue() {
			this.data.textRenderer.multistateValues.push(
				Object.assign({}, this.multistateRenderer),
			);
			this.multistateRenderer = { key: 0, text: '', colour: '#458e23' };
		},
		delMultistateValue(val) {
			this.data.textRenderer.multistateValues = this.data.textRenderer.multistateValues.filter(
				(e) => {
					return e.key !== val.key;
				},
			);
		},
	},
};
</script>
<style scoped></style>
