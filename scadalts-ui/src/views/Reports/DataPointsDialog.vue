<template>
	<v-dialog max-width="500" persistent v-model="dialog">
		<template v-slot:activator="{ on, attrs }">
			<v-btn color="primary" v-bind="attrs" v-on="on">
				{{ $t('reports.chooseDataPoints') }}
			</v-btn>
		</template>

		<v-card>
			<v-card-title>
                <span>
                    {{ $t('reports.chooseDataPoints') }}
                </span>
                <v-spacer></v-spacer>
                <v-btn icon @click="dialog=false">
                    <v-icon>mdi-close</v-icon>
                </v-btn>
            </v-card-title>

			<v-card-text>
				<v-row>
                    <v-col cols="12">
                        <DataPointSearchComponent @change="selectedPoint"/>
                    </v-col>
                    <v-col cols="12">
                        <v-list v-if="report.points">
                            <v-list-item v-for="p in report.points" :key="p.pointId">
                                
                                <v-list-item-title>
                                    {{ p.name }}
                                </v-list-item-title>

                                <v-list-item-action class="datapoint--actions">
                                    <v-menu offset-y :close-on-content-click="false">
										<template v-slot:activator="{ on }">
											<v-btn class="no-padding" :color="p.colour" v-on="on"> </v-btn>
										</template>
										<v-color-picker
											v-model="p.colour"
											:close-on-content-click="false"
										></v-color-picker>
									</v-menu>

                                    <v-btn icon @click="toggleConsolidatedChart(p)">
                                        <v-icon v-show="p.consolidatedChart">
                                            mdi-chart-areaspline
                                        </v-icon>
                                        <v-icon v-show="!p.consolidatedChart">
                                            mdi-chart-line-variant
                                        </v-icon>
                                    </v-btn>

                                    <v-btn icon @click="removePointFromReport(p)">
                                        <v-icon>mdi-close</v-icon>
                                    </v-btn>    
                                </v-list-item-action>
                            </v-list-item>
                        </v-list>
                    </v-col>
				</v-row>
			</v-card-text>
		</v-card>
	</v-dialog>
</template>
<script>
import DataPointSearchComponent from '@/layout/buttons/DataPointSearchComponent.vue';
export default {
    components: {
        DataPointSearchComponent
    },
	props: {
		report: {
			type: Object,
			required: true,
		},
	},

	data() {
		return {
			dialog: false,
            
		};
	},
    methods: {
        selectedPoint(point) {
            if(!this.report.points.find(x => x.pointId === point.id)) {
                let p = {
                    pointId: point.id,
                    pointXid: point.xid,
                    name: point.name,
                    colour: '#000000',
                    consolidatedChart: true,
                }
                this.report.points.push(p);
            }
        },

        toggleConsolidatedChart(point) {
            point.consolidatedChart = !point.consolidatedChart;
        },
        removePointFromReport(point) {
            this.report.points = this.report.points.filter(x => x.pointId !== point.pointId);
        }
    }
};
</script>
<style>
.datapoint--actions {
    flex-direction: row;
    flex-shrink: 0;
    flex-basis: 25%;
}
.no-padding {
    padding: 0 2px !important;
    min-width: 36px !important;
    margin: 0 8px;
}
</style>
