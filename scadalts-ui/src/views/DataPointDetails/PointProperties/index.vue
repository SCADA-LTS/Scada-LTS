<template>
	<v-dialog v-model="dialog" width="1200">
		<template v-slot:activator="{ on, attrs }">
			<v-btn elevation="2" fab v-bind="attrs" v-on="on">
				<v-icon>mdi-pencil</v-icon>
			</v-btn>
		</template>

		<v-card>
			<v-card-title> </v-card-title>
			<v-card-text class="point-properties-box">
				<v-row>
					<v-col cols="6" xs="12">
						<v-row>
							<v-col cols="12">
								<h3>
									<v-btn x-small fab elevation="1" :color="data.enabled ? 'primary' : 'error'">
										<v-icon v-show="data.enabled">mdi-decagram</v-icon>
										<v-icon v-show="!data.enabled">mdi-decagram-outline</v-icon>
									</v-btn>
									Point properties
								</h3>
							</v-col>
							
							<v-col md="6" cols="12">
								<v-text-field v-model="data.name" label="Point Name" dense></v-text-field>
							</v-col>

                            <v-col md="6" cols="12">
                                <v-icon>mdi-database
                                </v-icon>
								{{ data.dataSourceName }}
							</v-col>
						</v-row>

						<PointPropLogging :data="data"></PointPropLogging>
						<PointPropTextRenderer :data="data"></PointPropTextRenderer>
						<PointPropChartRenderer :data="data"></PointPropChartRenderer>
					</v-col>

                    
                    <v-divider vertical class="point-properties-horizontal"></v-divider>
                    
                
					<v-col cols="5" xs="12">
                        <PointPropEventDetectors :data="data"></PointPropEventDetectors>
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
import PointPropChartRenderer from './PointPropChartRenderer';
import PointPropEventDetectors from './PointPropEventDetectors';

export default {
	name: 'PointProperties',

	components: {
		PointPropLogging,
		PointPropTextRenderer,
		PointPropChartRenderer,
        PointPropEventDetectors,
	},

	props: ['data'],

	data() {
		return {
			dialog: false,
		};
	},

	methods: {},
};
</script>
<style scoped>
.point-properties-box {
    max-height: 70vh;
    overflow: auto;
}
.point-properties-horizontal {
    margin: 0 3.5%;
}
</style>
