<template>
	<div v-if="dataPointDetails">
        <v-container fluid>
            <v-row>
                <v-col cols="8" xs="12">
                    <h1>Data Point Details {{dataPointDetails.id}}
                        <v-badge
                            overlap
                            color="error"
                            content="6"
                            >
                            <v-btn icon fab dark color="primary">
                                <v-icon>mdi-message-alert</v-icon>
                            </v-btn>
                        </v-badge>
                    </h1>
                </v-col>
                <v-col cols="2" xs="12" class="row justify-end">
                    <PointProperties :data="dataPointDetails"></PointProperties>
                </v-col>
                <v-col cols="2">
                    <DataPointSearchComponent @change="reload"></DataPointSearchComponent>
                </v-col>
            </v-row>
        </v-container>
        <v-container>
            <v-card class="pointDetailsCards">
                <v-card-title>
                    Details and previlages
                </v-card-title>
                <v-card-text>
                    <v-row>
                        <v-col cols="6">
                            Description:
                        </v-col>
                        <v-col cols="6">
                            {{dataPointDetails.extendedName}}
                        </v-col>
                        <v-col cols="6">
                            Value:
                        </v-col>
                        <v-col cols="6">
                            2231 {{dataPointDetails.intervalLoggingType}}
                        </v-col>
                        <v-col cols="6">
                            Time:
                        </v-col>
                        <v-col cols="6">
                            14:14.00
                        </v-col>
                        <v-col cols="6">
                            Export Id:
                        </v-col>
                        <v-col cols="6">
                            {{dataPointDetails.xid}}
                        </v-col>
                    </v-row>
                </v-card-text>
            </v-card>
            <v-card class="pointDetailsCards">
                <v-card-title>
                    Events
                </v-card-title>
                <v-card-text>
                    Description and so on...
                </v-card-text>
            </v-card>
            <v-card class="pointDetailsCards">
                <v-card-title>
                    Views
                </v-card-title>
                <v-card-text>
                    Description and so on...
                </v-card-text>
            </v-card>
            <v-card class="pointDetailsCards">
                <v-card-title>
                    Statistics
                </v-card-title>
                <v-card-text>
                    <v-row>
                        <v-col cols="6">
                            Start:
                        </v-col>
                        <v-col cols="6">
                            ...
                        </v-col>
                        <v-col cols="6">
                            End:
                        </v-col>
                        <v-col cols="6">
                            ...
                        </v-col>
                        <v-col cols="6">
                            Minimum:
                        </v-col>
                        <v-col cols="6">
                            ...
                        </v-col>
                        <v-col cols="6">
                            Maximum:
                        </v-col>
                        <v-col cols="6">
                            ...
                        </v-col>
                    </v-row>
                </v-card-text>
            </v-card>
        </v-container>
        <v-container fluid>
            <LineChartComponent
                :pointId="this.$route.params.id"
            >
            </LineChartComponent>
        </v-container>
	</div>
</template>
<script>
import DataPointSearchComponent from '@/layout/buttons/DataPointSearchComponent';
import PointProperties from './PointProperties';
import LineChartComponent from '@/components/amcharts/LineChartComponent';
/**
 * Data Point Details page
 *
 * @author Radoslaw Jajko <rjajko@softq.pl>
 * @version 1.0
 *
 */
export default {
	name: 'DataPointDetails',

	components: { 
        DataPointSearchComponent,
        PointProperties,
        LineChartComponent
    },

	data() {
		return { 
            dataPointDetails: undefined,
            
        };
	},

	mounted() { 
        console.log(this.$route.params.id);
        this.fetchDataPointDetails(this.$route.params.id);
    },

	methods: { 
        reload(dataPoint) {
            this.$router.push(`${dataPoint.id}`);
            this.$router.go();
            // console.log(dataPoint.name, dataPoint.xid);
            // console.log(dataPoint);
            // this.fetchDataPointDetails(dataPoint.id);
        },

        async fetchDataPointDetails(datapointId) {
            this.dataPointDetails = await this.$store.dispatch("getDataPointDetails", datapointId);
        }
    }
		
};
</script>
<style scoped>
.pointDetailsCards {
    width: 25%;
    float: left;
}

</style>
