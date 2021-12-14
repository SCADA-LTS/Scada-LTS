<template>
	<div class="flex">
		<component
			:is="`${datasourceType}`"
			:id="datasource.id"
			:datasource="datasource"
			ref="component"
			@saved="onSaved"
		/>

		<v-spacer> </v-spacer>
		<!-- <DataSourceEvents>
		</DataSourceEvents> -->
		<v-tooltip bottom>
			<template v-slot:activator="{ on, attrs }">
				<v-btn
					icon
					elevation="0"
					@click="enableAllPoints(datasource.id)"
					v-bind="attrs"
					v-on="on"
				>
					<v-icon> mdi-alert-decagram </v-icon>
				</v-btn>
			</template>
			<span>Enable all points</span>
		</v-tooltip>

		<v-tooltip bottom>
			<template v-slot:activator="{ on, attrs }">
				<v-btn icon @click="openEditor()" elevation="0" v-bind="attrs" v-on="on">
					<v-icon> mdi-pencil </v-icon>
				</v-btn>
			</template>
			<span>Edit</span>
		</v-tooltip>

		<v-tooltip bottom>
			<template v-slot:activator="{ on, attrs }">
				<v-btn icon @click="deleteDataSource()" elevation="0" v-bind="attrs" v-on="on">
					<v-icon> mdi-delete </v-icon>
				</v-btn>
			</template>
			<span>Delete</span>
		</v-tooltip>
	</div>
</template>
<script>
import dataSourceMixin from '../../../components/datasources/DataSourcesMixin.js';
import DataSourceEvents from './DataSourceEvents';

export default {
	props: ['datasource', 'datasourceType'],

	components: {
		DataSourceEvents,
	},

	mixins: [dataSourceMixin],

	methods: {
		openEditor() {
			this.$refs.component.openEditor();
		},

		onSaved(event) {
			console.debug('DataSourceDetails.vue::onSaved()');
			this.$emit('saved', event);
		},

		deleteDataSource() {
			console.debug('DataSourceDetails.vue::deleteDataSource()');
			this.$emit('deleted', this.datasource.id);
		},

		enableAllPoints(datasourceId) {
			this.$store.dispatch('enableAllDataPoints', datasourceId);
		},
	},
};
</script>
<style></style>
