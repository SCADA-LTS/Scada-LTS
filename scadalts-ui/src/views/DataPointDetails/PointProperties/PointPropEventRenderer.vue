<template>
	<v-row>
		<v-col cols="12">
			<h3>Event renderer properties</h3>
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
			<!-- BINARY EVENT RENDERER -->
			<v-row v-if="selected === 1" dense>
				<v-col cols="1">
					<v-menu offset-y>
						<template v-slot:activator="{ on }">
							<v-btn :color="data.eventTextRenderer.zeroColour" v-on="on" block> </v-btn>
						</template>
						<v-color-picker v-model="data.eventTextRenderer.zeroColour"> </v-color-picker>
					</v-menu>
				</v-col>
				<v-col cols="4">
					<v-text-field
						v-model="data.eventTextRenderer.zeroShortLabel"
						label="Zero Short Label"
						dense
					>
					</v-text-field>
				</v-col>
				<v-col cols="7">
					<v-text-field
						v-model="data.eventTextRenderer.zeroLongLabel"
						label="Zero Long Label"
						dense
					>
					</v-text-field>
				</v-col>
				<v-col cols="1">
					<v-menu offset-y>
						<template v-slot:activator="{ on }">
							<v-btn :color="data.eventTextRenderer.oneColour" v-on="on" block> </v-btn>
						</template>
						<v-color-picker v-model="data.eventTextRenderer.oneColour"> </v-color-picker>
					</v-menu>
				</v-col>
				<v-col cols="4">
					<v-text-field
						v-model="data.eventTextRenderer.oneShortLabel"
						label="One Short Label"
						dense
					>
					</v-text-field>
				</v-col>
				<v-col cols="7">
					<v-text-field
						v-model="data.eventTextRenderer.oneLongLabel"
						label="One Long Label"
						dense
					>
					</v-text-field>
				</v-col>
			</v-row>

			<!-- MULTISTATE RENDERER PROPERTIES -->
			<v-row v-if="selected === 2" dense>
				<v-col cols="1">
					<v-menu offset-y>
						<template v-slot:activator="{ on }">
							<v-btn :color="multistateRenderer.colour" v-on="on" block> </v-btn>
						</template>
						<v-color-picker v-model="multistateRenderer.colour"> </v-color-picker>
					</v-menu>
				</v-col>
				<v-col cols="2">
					<v-text-field v-model="multistateRenderer.key" label="Key" dense>
					</v-text-field>
				</v-col>
				<v-col cols="3">
					<v-text-field v-model="multistateRenderer.shortText" label="Short Text" dense>
					</v-text-field>
				</v-col>
				<v-col cols="6">
					<v-text-field
						v-model="multistateRenderer.longText"
						label="Long Text"
						dense
						append-outer-icon="mdi-plus-circle"
						@click:append-outer="addMultistateValue"
					>
					</v-text-field>
				</v-col>

				<v-divider></v-divider>

				<v-col cols="12" v-if="data.eventTextRenderer.multistateEventValues">
					<v-row v-for="e in data.eventTextRenderer.multistateEventValues" :key="e" dense>
						<v-col cols="1">
							<v-btn :color="e.colour" block> </v-btn>
						</v-col>

						<v-col cols="2">
							<v-text-field v-model="e.key" label="Key" dense> </v-text-field>
						</v-col>

						<v-col cols="3">
							<v-text-field v-model="e.shortText" label="Short Text" dense>
							</v-text-field>
						</v-col>

						<v-col cols="6">
							<v-text-field
								v-model="e.longText"
								label="Long Text"
								dense
								append-outer-icon="mdi-close-circle-outline"
								@click:append-outer="delMultistateValue(e)"
							>
							</v-text-field>
						</v-col>
					</v-row>
				</v-col>
			</v-row>

			<!-- RANGE RENDERER PROPERTIES -->
			<v-row v-if="selected === 4" dense>
				<v-col cols="1">
					<v-menu offset-y>
						<template v-slot:activator="{ on }">
							<v-btn :color="rangeRenderer.colour" v-on="on" block> </v-btn>
						</template>
						<v-color-picker v-model="rangeRenderer.colour"> </v-color-picker>
					</v-menu>
				</v-col>
				<v-col cols="2">
					<v-text-field v-model="rangeRenderer.from" label="From" dense> </v-text-field>
				</v-col>
				<v-col cols="2">
					<v-text-field v-model="rangeRenderer.to" label="To" dense> </v-text-field>
				</v-col>
				<v-col cols="3">
					<v-text-field v-model="rangeRenderer.shortText" label="Short Text" dense>
					</v-text-field>
				</v-col>
				<v-col cols="4">
					<v-text-field
						v-model="rangeRenderer.longText"
						label="Long Text"
						dense
						append-outer-icon="mdi-plus-circle"
						@click:append-outer="addRangeValue"
					>
					</v-text-field>
				</v-col>

				<v-divider></v-divider>

				<v-col cols="12">
					<v-row v-for="e in data.eventTextRenderer.rangeEventValues" :key="e" dense>
						<v-col cols="1">
							<v-btn :color="e.colour" block></v-btn>
						</v-col>

						<v-col cols="2">
							<v-text-field v-model="e.from" label="From" dense> </v-text-field>
						</v-col>
						<v-col cols="2">
							<v-text-field v-model="e.to" label="To" dense> </v-text-field>
						</v-col>
						<v-col cols="3">
							<v-text-field v-model="e.shortText" label="Short Text" dense>
							</v-text-field>
						</v-col>
						<v-col cols="4">
							<v-text-field
								v-model="e.longText"
								label="Long Text"
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
export default {
	name: 'PointPropEventRenderer',

	props: ['data'],

	data() {
		return {
			selected: undefined,
			rangeRenderer: { from: 0, to: 0, shortText: '', longText: '', colour: '#458e23' },
			multistateRenderer: { key: 0, shortText: '', longText: '', colour: '#458e23' },
		};
	},

	computed: {
		eventRenderersList() {
			if (!!this.data) {
				let renderers = [{ id: -1, label: 'None' }];
				if (this.data.pointLocator.dataTypeId === 1) {
					// Binary datapoint
					renderers.push(
						this.$store.state.dataPoint.textRenderesList.filter((e) => {
							return e.id === 1;
						})[0],
					);
				} else if (this.data.pointLocator.dataTypeId === 2) {
					// Multistate datapoint
					renderers.push(
						this.$store.state.dataPoint.textRenderesList.filter((e) => {
							return e.id === 2;
						})[0],
					);
				} else if (this.data.pointLocator.dataTypeId === 3) {
					// Numeric datapoint
					renderers.push(
						this.$store.state.dataPoint.textRenderesList.filter((e) => {
							return e.id === 4;
						})[0],
					);
				}
				return renderers;
			}
		},
	},

	mounted() {
		if (!!this.data.eventTextRenderer) {
			switch (this.data.eventTextRenderer.def.exportName) {
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
				this.data.eventTextRenderer = null;
			} else {
				let template = JSON.parse(
					JSON.stringify(this.$store.state.dataPoint.eventRenderersTemplates[val]),
				);
				if (!!this.data.eventTextRenderer) {
					if (this.data.eventTextRenderer.def.exportName === template.def.exportName) {
						return;
					}
				}
				this.data.eventTextRenderer = template;
			}
		},

		addRangeValue() {
			this.data.eventTextRenderer.rangeEventValues.push(
				Object.assign({}, this.rangeRenderer),
			);
			this.rangeRenderer = {
				from: 0,
				to: 0,
				shortText: '',
				longText: '',
				colour: '#458e23',
			};
		},
		delRangeValue(val) {
			this.data.eventTextRenderer.rangeEventValues = this.data.eventTextRenderer.rangeEventValues.filter(
				(e) => {
					return e.longText !== val.longText;
				},
			);
		},

		addMultistateValue() {
			this.data.eventTextRenderer.multistateEventValues.push(
				Object.assign({}, this.multistateRenderer),
			);
			this.rangeRenderer = { key: 0, shortText: '', longText: '', colour: '#458e23' };
		},
		delMultistateValue(val) {
			this.data.eventTextRenderer.multistateEventValues = this.data.eventTextRenderer.multistateEventValues.filter(
				(e) => {
					return e.key !== val.key;
				},
			);
		},
	},
};
</script>
<style scoped></style>
