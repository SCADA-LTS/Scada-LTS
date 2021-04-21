<template>
	<div>
		<v-container fluid>
			<header>
				<v-row align="center">
					<v-col cols="6">
						<h1>Data Sources</h1>
					</v-col>
					<v-col cols="6" class="row justify-end" v-if="tableLoaded">
						<v-btn color="primary" dark fab small>
							<v-icon> mdi-plus </v-icon>
						</v-btn>
					</v-col>
				</v-row>
			</header>
			<v-data-table
                v-if="tableLoaded"
				:headers="headers"
				:items="dataSourceList"
				:single-expand="false"
				:expanded.sync="expanded"
				:sort-by="[]"
				:sort-desc="[]"
				item-key="name"
				multi-sort
				show-expand
				@item-expanded="fetchDataPointList"
			>
				<template v-slot:item.enabled="{ item }">
					<v-icon :color="item.enabled ? 'primary' : 'error'" v-show="item.enabled"
						>mdi-decagram</v-icon
					>
					<v-icon :color="item.enabled ? 'primary' : 'error'" v-show="!item.enabled"
						>mdi-decagram-outline</v-icon
					>
				</template>
				<template v-slot:expanded-item="{ headers, item }">
					<td :colspan="headers.length" class="small-margin-top">
						<v-row>
							<v-col cols="12" class="flex">
								<v-spacer> </v-spacer>
								<v-btn icon elevation="0">
									<v-icon> mdi-alert-decagram </v-icon>
								</v-btn>
								<v-btn icon elevation="0">
									<v-icon> mdi-pencil </v-icon>
								</v-btn>
								<v-btn icon elevation="0">
									<v-icon> mdi-delete </v-icon>
								</v-btn>
							</v-col>
						</v-row>
						<v-divider></v-divider>
						<v-row class="datapoint-list" v-if="item.loaded">
							<v-col cols="12">
								<v-list-item v-for="dp in item.datapoints" :key="dp.xid">
									<v-list-item-content>
										<v-list-item-title>
											<v-row>
												<v-col cols="1">
													<v-icon
														:color="dp.enabled ? 'primary' : 'error'"
														v-show="dp.enabled"
														>mdi-decagram</v-icon
													>
													<v-icon
														:color="dp.enabled ? 'primary' : 'error'"
														v-show="!dp.enabled"
														>mdi-decagram-outline</v-icon
													>
												</v-col>
												<v-col cols="3">
													{{ dp.name }}
												</v-col>
												<v-col cols="3">
													{{ dp.type }}
												</v-col>
												<v-col cols="2">
													{{ dp.xid }}
												</v-col>
												<v-col cols="3">
													{{ dp.desc }}
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
												<v-list-item>
													<v-list-item-icon>
														<v-icon>mdi-pencil</v-icon>
													</v-list-item-icon>
													<v-list-item-title> Edit </v-list-item-title>
												</v-list-item>
												<v-list-item>
													<v-list-item-icon>
														<v-icon>mdi-delete</v-icon>
													</v-list-item-icon>
													<v-list-item-title> Delete </v-list-item-title>
												</v-list-item>
											</v-list>
										</v-menu>
									</v-list-item-action>
								</v-list-item>
								<v-list-item v-if="!item.datapoints">
									<v-list-item-content>
										<v-list-item-title>
											This Data Source does not contain any DataPoints. Create the first
											one!
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
														@click="createDataPoint(item)"
													>
														<v-icon> mdi-plus</v-icon>
													</v-btn>
												</v-col>
											</v-row>
										</v-list-item-title>
									</v-list-item-content>
								</v-list-item>
								<v-list-item> </v-list-item>
							</v-col>
						</v-row>
                        <v-skeleton-loader v-else type="article">
                        </v-skeleton-loader>
					</td>
				</template>
			</v-data-table>
            <v-skeleton-loader v-else type="article"> </v-skeleton-loader>
		</v-container>
	</div>
</template>
<script>
export default {
	data() {
		return {
			tableLoaded: false,
			expanded: [],
			headers: [
				{
					text: 'Status',
					align: 'center',
					value: 'enabled',
				},
				{
					text: 'Name',
					align: 'center',
					value: 'name',
				},
				{
					text: 'Type',
					align: 'center',
					value: 'type',
				},
				{
					text: 'Connection',
					align: 'center',
					value: 'conn',
				},
				{
					text: 'Status Description',
					align: 'center',
					value: 'descr',
				},
				{ text: '', value: 'data-table-expand' },
			],
			dataSourceList: [],
		};
	},

    mounted() {
        this.fetchDataSources();
    },

	methods: {
        async fetchDataSources() {
            try {
                this.tableLoaded = false;
                this.dataSourceList = await this.$store.dispatch('getDataSources');
                this.tableLoaded = true;
            } catch (e) {
                console.error(e);
                this.tableLoaded = true;
                this.dataSourceList = [];
            }
        },

		async fetchDataPointList({item, value}) {
            if(value) {
                // Load data from REST API if threre is no datapoints.
                if(!!item.datapoints && item.datapoints.length === 0) {
                    item.loaded = false;
                    item.datapoints = await this.$store.dispatch('fetchDataPointsForDS', item.name);
                    item.loaded = true;
                }
            }
		},

		createDataPoint(item) {
			console.log(item);
		},
	},
};
</script>
<style scoped>
.flex {
	display: flex;
}
.datapoint-list {
	padding: 0 5%;
}
.small-margin-top > .row {
	margin-top: 8px;
}
</style>
