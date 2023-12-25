<template>
	<v-col cols="12">
		<v-list-item v-for="dp in datasource.datapoints" :key="dp.xid" class="datapoint-item">
			<v-list-item-icon>
				<v-btn
					x-small
					icon
					fab
					elevation="0"
					:color="dp.enabled ? 'primary' : 'error'"
					@click="toggleDataPoint(dp)"
				>
					<v-icon v-show="dp.enabled">mdi-decagram</v-icon>
					<v-icon v-show="!dp.enabled">mdi-decagram-outline</v-icon>
				</v-btn>
			</v-list-item-icon>

			<v-list-item-content>
				<v-list-item-title>
					<v-row>
						<v-col cols="12" sm="12" md="5" lg="6">
							<v-tooltip bottom>
								<template v-slot:activator="{ on, attrs }">
									<v-row v-bind="attrs" v-on="on">
										<v-col cols="12" sm="9" lg="9" xl="6" class="text-main-primary">
											{{ dp.name.length > 45 ? dp.name.substring(0, 45) + '...' : dp.name }}
										</v-col>
										<v-col cols="2" sm="3" xl="2" v-show="$vuetify.breakpoint.sm || $vuetify.breakpoint.lg || $vuetify.breakpoint.xl" class="text-secondary">
											{{ dp.xid }}
										</v-col>
										<v-col cols="4" xl="4" v-show="$vuetify.breakpoint.xl" class="text-secondary">
											{{ dp.description }}
										</v-col>
									</v-row>
								</template>
								<span>
									<span v-if="dp.name.length > 45">
										{{ dp.name }}
									</span>
									<br>
									<span>
										{{ dp.xid }}
									</span>
									<br>
									<span>
										{{ dp.description }}
									</span>
								</span>
							</v-tooltip>
						</v-col>

						<v-col cols="12" sm="12" md="7" lg="6">
							<component :is="`${datasourceType}pointlist`" :datapoint="dp"></component>
						</v-col>
					</v-row>
				</v-list-item-title>
			</v-list-item-content>

			<v-list-item-action>
				<v-menu top :offset-y="true">
					<template v-slot:activator="{ on, attrs }">
						<v-btn icon v-bind="attrs" v-on="on">
							<v-icon> mdi-dots-vertical </v-icon>
						</v-btn>
					</template>
					<v-list>
						<v-list-item @click="showDataPoint(dp)">
							<v-list-item-icon>
								<v-icon>mdi-information</v-icon>
							</v-list-item-icon>
							<v-list-item-title> Details </v-list-item-title>
						</v-list-item>
						<v-list-item @click="editDataPoint(datasource, dp)">
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
		<v-list-item v-if="!datasource.datapoints || (!!datasource.datapoints && datasource.datapoints.length === 0)">
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
							<v-tooltip bottom>
								<template v-slot:activator="{ on, attrs }">
									<v-btn
										v-bind="attrs"
										v-on="on"
										color="primary"
										dark
										fab
										x-small
										elevation="0"
										@click="createDataPoint(datasource)"
									>
										<v-icon> mdi-plus</v-icon>
									</v-btn>
								</template>
								<span>Create new data point</span>
							</v-tooltip>
						</v-col>
					</v-row>
				</v-list-item-title>
				<v-list-item-subtitle> </v-list-item-subtitle>
			</v-list-item-content>
		</v-list-item>
	</v-col>
</template>

<script>
import dataSourceMixin from '../../../components/datasources/DataSourcesMixin.js';

export default {
	props: ['datasource', 'datasourceType'],

	mixins: [dataSourceMixin],

	methods: {
		editDataPoint(item, datapoint = null) {
			this.$emit('edit', { item, datapoint });
		},
		createDataPoint(item) {
			this.$emit('create', item);
		},
		deleteDataPoint(item, datapoint) {
			this.$emit('delete', { item, datapoint });
		},
		toggleDataPoint(dp) {
			this.$store.dispatch('toggleDataPoint', dp.id).then(() => {
				dp.enabled = !dp.enabled;
			});
		},
		showDataPoint(datapoint) {
			this.$router.push({ name: 'datapoint-details', params: { id: datapoint.id } });
			this.$router.go();
		},
	},
};
</script>
<style>
.datapoint-status {
	justify-content: flex-start;
	display: flex;
}
.datapoint-item {
	border-bottom: solid 0.1px rgba(0, 0, 0, 0.06);
}
.datapoint-item:hover {
	background-color: #eee;
}
.text-secondary {
	color: var(--v-secondary-lighten3);
	font-style: italic;
}
@media (min-width: 1264px) {
	.datapoint-status {
		justify-content: flex-end;
	}
}
</style>
