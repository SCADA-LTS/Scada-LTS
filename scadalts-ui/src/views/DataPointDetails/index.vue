<template>
	<div v-if="dataPointDetails">
		<v-container fluid>
			<v-row>
				<v-col cols="8" xs="12">
					<h1>
						{{ dataPointDetails.name }}
						<DataPointComment :data="dataPointDetails"></DataPointComment>
					</h1>
				</v-col>
				<v-col cols="2" xs="12" class="row justify-end">
					<PointProperties
						:data="dataPointDetails"
						@saved="saveDataPointDetails"
					></PointProperties>
				</v-col>
				<v-col cols="2">
					<DataPointSearchComponent @change="reload"></DataPointSearchComponent>
				</v-col>
			</v-row>
		</v-container>
		<v-container fluid>
			<v-card class="pointDetailsCards">
				<v-card-title>
					<v-btn
						x-small
						fab
						elevation="1"
						@click="toggleDataPoint"
						:color="dataPointDetails.enabled ? 'primary' : 'error'"
					>
						<v-icon v-show="dataPointDetails.enabled">mdi-decagram</v-icon>
						<v-icon v-show="!dataPointDetails.enabled">mdi-decagram-outline</v-icon>
					</v-btn>
					<span> Details and previlages </span></v-card-title
				>
				<v-card-text>
					<v-row>
						<v-col cols="6">
							<v-text-field
								v-model="pointValue"
								label="Value:"
								append-icon="mdi-send"
								@click:append="sendValue"
								:disabled="!dataPointDetails.pointLocator.settable"
								dense
							></v-text-field>
						</v-col>
						<v-col cols="6">
							<v-text-field
								v-model="pointValueTime"
								label="Time:"
								dense
								disabled
							></v-text-field>
						</v-col>
						<v-col cols="6"> Export Id: </v-col>
						<v-col cols="6">
							{{ dataPointDetails.xid }}
						</v-col>
					</v-row>
				</v-card-text>
			</v-card>
			<DataPointEventList :datapointId="dataPointDetails.id" class="pointDetailsCards"></DataPointEventList>
			<v-card class="pointDetailsCards">
				<v-card-title> Views </v-card-title>
				<v-card-text> Description and so on... </v-card-text>
			</v-card>
			<v-card class="pointDetailsCards">
				<v-card-title> Statistics </v-card-title>
				<v-card-text>
					<v-row>
						<v-col cols="6"> Start: </v-col>
						<v-col cols="6"> ... </v-col>
						<v-col cols="6"> End: </v-col>
						<v-col cols="6"> ... </v-col>
						<v-col cols="6"> Minimum: </v-col>
						<v-col cols="6"> ... </v-col>
						<v-col cols="6"> Maximum: </v-col>
						<v-col cols="6"> ... </v-col>
					</v-row>
				</v-card-text>
			</v-card>
		</v-container>
		<v-container fluid>
			<LineChartComponent :pointId="this.$route.params.id"> </LineChartComponent>
		</v-container>
	</div>
</template>
<script>
import DataPointSearchComponent from '@/layout/buttons/DataPointSearchComponent';
import DataPointComment from './DataPointComment';
import PointProperties from './PointProperties';
import DataPointEventList from './DataPointEventList';
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
		DataPointComment,
		PointProperties,
		DataPointEventList,
		LineChartComponent,
	},

	data() {
		return {
			pointValue: 0.0,
			pointValueTime: 'N/A',
			newComment: '',
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
		},

		async fetchDataPointDetails(datapointId) {
			this.dataPointDetails = await this.$store.dispatch(
				'getDataPointDetails',
				datapointId,
			);
			this.getDataPointValue(datapointId);
		},

		async toggleDataPoint() {
			let resp = await this.$store.dispatch('toggleDataPoint', this.dataPointDetails.id);
			if (!!resp) {
				this.dataPointDetails.enabled = resp.enabled;
			}
		},

		sendValue() {
			let request = {
				xid: this.dataPointDetails.xid,
				type: this.dataPointDetails.pointLocator.dataTypeId,
				value: this.pointValue,
			};

			if (request.type === 1) {
				if (request.value === true || request.value === 'true') {
					request.value = 1;
				} else if (request.value === false || request.value === 'false') {
					request.value = 0;
				}
			}

			this.$store.dispatch('setDataPointValue', request).then((resp) => {
				this.getDataPointValue(this.dataPointDetails.id);
			});
		},

		async getDataPointValue(datapointId) {
			let value = await this.$store.dispatch('getDataPointValue', datapointId);
			this.pointValue = value.value;
			this.pointValueTime =
				Number(value.ts).toString() !== 'NaN'
					? new Date(value.ts).toLocaleString()
					: 'Not valid date!';
		},

		saveDataPointDetails() {
			this.$store.dispatch('saveDataPointDetails', this.dataPointDetails).then((resp) => {
				alert(resp);
			});
		},
	},
};
</script>
<style scoped>
.pointDetailsCards {
	width: 25%;
	float: left;
}
</style>
