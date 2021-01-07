<template>
	<div class="col-md-6">
		<div class="row align-items-center">
			<h2 class="col-xs-12" id="title-scada-config">
				{{ $t('systemsettings.scadaconf.title') }}
			</h2>
			<tooltip
				:text="$t('systemsettings.tooltip.scadaconf')"
				target="#title-scada-config"
			/>
			<btn
				v-if="!showScadaConfig"
				type="link"
				class="col-xs-12"
				block
				@click="showScadaConfig = true"
				><i class="glyphicon glyphicon-menu-down"></i
			></btn>
			<btn
				v-if="showScadaConfig"
				type="link"
				class="col-xs-12"
				block
				@click="showScadaConfig = false"
				><i class="glyphicon glyphicon-menu-up"></i
			></btn>
		</div>
		<collapse v-model="showScadaConfig">
			<div class="row" v-for="key in Object.keys(scadaConfig)" v-bind:key="key">
				<div class="col-xs-8">
					<p>{{ $t(`systemsettings.scadaconf.${key}`) }}</p>
				</div>
				<div class="col-xs-4">
					<p>{{ scadaConfig[key] | convert }}</p>
				</div>
			</div>
		</collapse>
	</div>
</template>
<script>
import { object } from '@amcharts/amcharts4/core';
import i18n from '../../i18n';

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
