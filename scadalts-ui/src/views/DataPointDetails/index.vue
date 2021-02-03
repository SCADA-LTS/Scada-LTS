<template>
	<div>
        <v-container fluid>
            <v-row>
                <v-col cols="8" xs="12">
                    <h1>Data Point Details {{$route.params.id}}
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
                    <v-btn
                        elevation="2"
                        fab dark color="primary"
                    >
                        <v-icon>mdi-pencil</v-icon>
                    </v-btn>
                </v-col>
                <v-col cols="2">
                    <v-autocomplete
                    :items="datapoints"
                    item-text="name"
                    prepend-icon="mdi-magnify"></v-autocomplete>
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
                            Some point
                        </v-col>
                        <v-col cols="6">
                            Value:
                        </v-col>
                        <v-col cols="6">
                            2231
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
                            EX_DP_12312
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
        LineChartComponent
    },

	data() {
		return { 
            datapoints: [
                {name: '1 minute Load', id: 1, xid: 'DP_250372'},
                {name: '5 minutes Load', id: 2, xid: 'DP_335775'},
                {name: 'Point', id: 5, xid: 'DP_712779'},
                {name: 'Numeric', id: 6, xid: 'DP_954927'},
            ]   
        };
	},

	mounted() { },

	methods: { }
		
};
</script>
<style scoped>
.pointDetailsCards {
    width: 25%;
    float: left;
}

</style>
