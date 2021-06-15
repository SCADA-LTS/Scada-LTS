<template>
	<v-col cols="12">
		<v-list-item v-for="dp in datasource.datapoints" :key="dp.xid">
			<v-list-item-content>
				<v-list-item-title>
					<v-row>
						
						<v-col cols="1" class="datapoint-status">
							<v-btn x-small icon fab elevation="0" :color="dp.enabled ? 'primary' : 'error'" @click="toggleDataPoint(dp)">
								<v-icon v-show="dp.enabled">mdi-decagram</v-icon>
								<v-icon v-show="!dp.enabled">mdi-decagram-outline</v-icon>
							</v-btn>
						</v-col>

						<v-col cols="11" :sm="9" :md="7" :lg="5" xl="3">
							<v-tooltip bottom>
								<template v-slot:activator="{ on, attrs }">
									<span v-bind="attrs" v-on="on">
										{{ dp.name }}
									</span>
								</template>
								<span>
									<span>{{ dp.xid }}</span
									><br />
									<span>{{ dp.desc }}</span>
								</span>
							</v-tooltip>
						</v-col>

						<v-col cols="12" :sm="2" :md="4" lg="1" xl="1">
							{{ dp.type }}
						</v-col>

						<v-col v-show="!$vuetify.breakpoint.lg && !$vuetify.breakpoint.xl" cols="1">
						</v-col>

						<v-col cols="11" lg="4" xl="7">
							<component :is="`${datasourceType}pointlist`" :datapoint="dp"></component>
						</v-col>
					</v-row>
				</v-list-item-title>
				<v-list-item-subtitle v-show="$vuetify.breakpoint.lg || $vuetify.breakpoint.xl">
					<v-row>
						<v-col cols="1"> </v-col>
						<v-col cols="1">
							{{ dp.xid }}
						</v-col>
						<v-col>
							{{ dp.desc }}
						</v-col>
					</v-row>
				</v-list-item-subtitle>
			</v-list-item-content>
			<v-list-item-action>
				<v-menu top :offset-y="true">
					<template v-slot:activator="{ on, attrs }">
						<v-btn icon v-bind="attrs" v-on="on">
							<v-icon> mdi-dots-vertical </v-icon>
						</v-btn>
					</template>
					<v-list>
						<v-list-item @click="showDataPoint(datasource, dp)">
							<v-list-item-icon>
								<v-icon>mdi-information</v-icon>
							</v-list-item-icon>
							<v-list-item-title> Details </v-list-item-title>
						</v-list-item>
						<v-list-item @click="createDataPoint(datasource, dp)">
							<v-list-item-icon>
								<v-icon>mdi-pencil</v-icon>
							</v-list-item-icon>
							<v-list-item-title> Edit </v-list-item-title>
						</v-list-item>
						<v-list-item @click="copyDataPoint(datasource, dp)" disabled>
							<v-list-item-icon>
								<v-icon>mdi-content-copy</v-icon>
							</v-list-item-icon>
							<v-list-item-title> Copy </v-list-item-title>
						</v-list-item>
						<v-list-item @click="deleteDataPoint(datasource, dp)">
							<v-list-item-icon>
								<v-icon>mdi-delete</v-icon>
							</v-list-item-icon>
							<v-list-item-title> Delete </v-list-item-title>
						</v-list-item>
					</v-list>
				</v-menu>
			</v-list-item-action>
		</v-list-item>
		<v-list-item v-if="!datasource.datapoints">
			<v-list-item-content>
				<v-list-item-title>
					This Data Source does not contain any DataPoints. Create the first one!
				</v-list-item-title>
			</v-list-item-content>
		</v-list-item>
		<v-list-item>
			<v-list-item-content>
				<v-list-item-title>
					<v-row align="center" justify="center">
						<v-col cols="1">
							<v-btn
								color="primary"
								dark
								fab
								x-small
								elevation="0"
								@click="createDataPoint(datasource)"
							>
								<v-icon> mdi-plus</v-icon>
							</v-btn>
						</v-col>
					</v-row>
				</v-list-item-title>
				<v-list-item-subtitle> </v-list-item-subtitle>
			</v-list-item-content>
		</v-list-item>
	</v-col>
</template>
<script>
import dataSourceMixin from '../../components/datasources/DataSourcesMixin.js';

export default {
	props: ['datasource', 'datasourceType'],

	mixins: [dataSourceMixin],

	methods: {
		createDataPoint(item, datapoint = null) {
			this.$emit('create', { item, datapoint });
		},
		deleteDataPoint(item, datapoint) {
			this.$emit('delete', { item, datapoint });
		},
		toggleDataPoint(dp) {
			dp.enabled = !dp.enabled;
			console.log(dp);
		},
	},
};
</script>
<style>
.datapoint-status {
	justify-content: flex-start;
	display: flex;
}
@media (min-width: 1264px) {
	.datapoint-status {
		justify-content: flex-end;
	}
}
</style>
