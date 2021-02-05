<template>
	<v-dialog v-model="dialog" width="800">
		<template v-slot:activator="{ on, attrs }">
			<v-btn elevation="2" fab v-bind="attrs" v-on="on">
				<v-icon>mdi-pencil</v-icon>
			</v-btn>
		</template>

		<v-card>
			<v-card-title> </v-card-title>
			<v-card-text> 
                <v-row>
                    <v-col cols="8">
                        <v-row>
                            <v-col cols="12">
                                <h3>Point properties</h3>
                            </v-col>
                            <v-col cols="6">
                                Data Source
                            </v-col>
                            <v-col cols="6">
                                {{data.dataSourceName}}
                            </v-col>
                            <v-col cols="6">
                                Point Name
                            </v-col>
                            <v-col cols="6">
                                {{data.name}}
                            </v-col>
                        </v-row>
                        
                        <PointPropLogging :data="data"></PointPropLogging>
                        <PointPropTextRenderer :data="data"></PointPropTextRenderer>
                        
                        <v-row>
                            <v-col cols="12">
                                <h3>Chart renderer</h3>
                            </v-col>
                            <v-col cols="6">
                                Type
                            </v-col>
                            <v-col cols="6">
                                Select box
                            </v-col>
                            <v-col cols="6">
                                Suffix
                            </v-col>
                            <v-col cols="6">
                                1 year
                            </v-col>
                        </v-row>
                    </v-col>
                    <v-col cols="4">
                        <h3>Event Detectors</h3>
                    </v-col>
                </v-row>
            </v-card-text>
			<v-divider></v-divider>
			<v-card-actions>
				<v-spacer> </v-spacer>
				<v-btn color="primary" text @click="dialog = false">{{
					$t('uiv.modal.ok')
				}}</v-btn>
			</v-card-actions>
		</v-card>
	</v-dialog>
</template>
<script>
import PointPropLogging from './PointPropLogging';
import PointPropTextRenderer from './PointPropTextRenderer';

export default {
	name: 'PointProperties',

    components: {
        PointPropLogging,
        PointPropTextRenderer
    },

    props: [
        'data',
    ],

	data() {
		return {
            dialog: false,
            defaultLoggingType: undefined,
            intervalLoggingPeriodTypeList: [
                {id: 1, label:'Seconds'},
                {id: 2, label:'Hours'},
                {id: 3, label:'Days'},
            ]
        };
	},

    computed: {
        loggingTypeList() {
            return this.$store.state.dataPoint.loggingTypeList;
        }
    },

    mounted() {
        console.log(this.list);
    },

	methods: {},
};
</script>
<style scoped></style>
