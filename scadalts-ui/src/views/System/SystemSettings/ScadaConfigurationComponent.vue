<template>
	<v-col cols="12" md="6">
		<v-card>
			<v-card-title>
				<v-tooltip bottom>
					<template v-slot:activator="{ on, attrs }">
						<span v-bind="attrs" v-on="on">{{
							$t('systemsettings.scadaconf.title')
						}}</span>
					</template>
					<span>{{ $t('systemsettings.tooltip.scadaconf') }}</span>
				</v-tooltip>
			</v-card-title>
			<v-card-text>
				<v-expansion-panels accordion>
					<v-expansion-panel>
						<v-expansion-panel-header>env.properties</v-expansion-panel-header>
						<v-expansion-panel-content>
							<v-row v-for="key in Object.keys(scadaConfig)" v-bind:key="key">
								<v-col cols="8">
									<p>{{ $t(`systemsettings.scadaconf.${key}`) }}</p>
								</v-col>
								<v-col cols="4">
									<p>{{ scadaConfig[key] | convert }}</p>
								</v-col>
							</v-row>
						</v-expansion-panel-content>
					</v-expansion-panel>
				</v-expansion-panels>
			</v-card-text>
		</v-card>
	</v-col>
</template>
<script>
import { object } from '@amcharts/amcharts4/core';
import i18n from '../../../i18n';

export default {
	name: 'ScadaConfigurationComponent',

	data() {
		return {
			scadaConfig: undefined,
			showScadaConfig: false,
		};
	},

	mounted() {
		this.fetchData();
	},

	methods: {
		async fetchData() {
			this.scadaConfig = await this.$store.dispatch('getScadaConfiguration');
		},
	},
};
</script>
<style></style>
