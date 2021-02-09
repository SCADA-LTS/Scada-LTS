<template>
	<v-row>
		<v-col cols="12">
			<v-row justify="space-between" align="center">
				<v-col>
					<h3>Event Detectors</h3>
				</v-col>
				<v-col class="row justify-end">
					<CreateEventDetectorDialog>
					</CreateEventDetectorDialog>
				</v-col>
			</v-row>
		</v-col>
		<v-col cols="12">
			<v-row v-for="e in data.eventDetectors" :key="e.id">
				<hr class="v-divider theme--light ped-divider" />

				<v-col cols="11">
					<h4>{{ e.detectorType | detectorType }} - {{ e.xid }}</h4>
				</v-col>
				<v-col cols="1">
					<v-btn icon> <v-icon>mdi-minus-circle</v-icon></v-btn>
				</v-col>

				<v-col cols="1">
					<img
						v-if="e.alarmLevel === 1"
						src="images/flag_blue.png"
						title="Information"
						alt="Information"
					/>
					<img
						v-if="e.alarmLevel === 2"
						src="images/flag_yellow.png"
						title="Urgent"
						alt="Urgent"
					/>
					<img
						v-if="e.alarmLevel === 3"
						src="images/flag_orange.png"
						title="Critical"
						alt="Critical"
					/>
					<img
						v-if="e.alarmLevel === 4"
						src="images/flag_red.png"
						title="Life Safety"
						alt="Life Safety"
					/>
				</v-col>

				<v-col cols="4">
					<v-select
						v-model="e.alarmLevel"
						:items="alarmLevels"
						item-value="value"
						item-text="text"
						label="Alarm Level"
						dense
					></v-select>
				</v-col>

				<v-col cols="7">
					<v-text-field v-model="e.alias" label="Alias" dense> </v-text-field>
				</v-col>

				<v-col cols="12">
					<!-- High Limit Detector -->
					<v-row v-if="e.detectorType === 1">
						<v-col cols="1"></v-col>
						<v-col cols="4">
							<v-text-field v-model="e.limit" label="High limit" dense></v-text-field>
						</v-col>
						<v-col cols="3">
							<v-text-field label="Duration" v-model="e.duration" dense></v-text-field>
						</v-col>
						<v-col cols="4">
							<v-select
								v-model="e.durationType"
								:items="timePeriods"
								item-value="id"
								item-text="label"
								dense
							>
							</v-select>
						</v-col>
					</v-row>

					<!-- Low Limit Detector -->
					<v-row v-if="e.detectorType === 2">
						<v-col cols="1"></v-col>
						<v-col cols="4">
							<v-text-field v-model="e.limit" label="Low limit" dense></v-text-field>
						</v-col>
						<v-col cols="3">
							<v-text-field label="Duration" v-model="e.duration" dense></v-text-field>
						</v-col>
						<v-col cols="4">
							<v-select
								v-model="e.durationType"
								:items="timePeriods"
								item-value="id"
								item-text="label"
								dense
							>
							</v-select>
						</v-col>
					</v-row>

					<!-- State Detector -->
					<v-row
						v-if="e.detectorType === 3 || e.detectorType === 4 || e.detectorType === 9"
					>
						<v-col cols="1"></v-col>
						<v-col cols="4" v-if="data.pointLocator.dataTypeId === 1">
							<v-select
								v-model="e.binaryState"
								:items="binaryState"
								item-value="value"
								item-text="text"
								dense
							>
							</v-select>
						</v-col>
						<v-col cols="4" v-if="data.pointLocator.dataTypeId === 2">
							<v-text-field
								label="State"
								v-model="e.multistateState"
								dense
							></v-text-field>
						</v-col>
						<v-col cols="4" v-if="data.pointLocator.dataTypeId === 4">
							<v-text-field
								label="State"
								v-model="e.alphanumericState"
								dense
							></v-text-field>
						</v-col>
						<v-col cols="3">
							<v-text-field label="Duration" v-model="e.duration" dense></v-text-field>
						</v-col>
						<v-col cols="4">
							<v-select
								v-model="e.durationType"
								:items="timePeriods"
								item-value="id"
								item-text="label"
								dense
							>
							</v-select>
						</v-col>
					</v-row>

					<!-- State Change Detector -->
					<v-row v-if="e.detectorType === 6">
						<v-col cols="1"></v-col>
						<v-col cols="4">
							<v-text-field
								v-model="e.changeCount"
								label="State change count"
								dense
							></v-text-field>
						</v-col>
						<v-col cols="3">
							<v-text-field label="Duration" v-model="e.duration" dense></v-text-field>
						</v-col>
						<v-col cols="4">
							<v-select
								v-model="e.durationType"
								:items="timePeriods"
								item-value="id"
								item-text="label"
								dense
							>
							</v-select>
						</v-col>
					</v-row>

					<!-- NoChange Detector -->
					<v-row v-if="e.detectorType === 7">
						<v-col cols="1"></v-col>
						<v-col cols="5">
							<v-text-field label="Duration" v-model="e.duration" dense></v-text-field>
						</v-col>
						<v-col cols="6">
							<v-select
								v-model="e.durationType"
								:items="timePeriods"
								item-value="id"
								item-text="label"
								dense
							>
							</v-select>
						</v-col>
					</v-row>

					<!-- NoUpdate Detector -->
					<v-row v-if="e.detectorType === 8">
						<v-col cols="1"></v-col>
						<v-col cols="5">
							<v-text-field label="Duration" v-model="e.duration" dense></v-text-field>
						</v-col>
						<v-col cols="6">
							<v-select
								v-model="e.durationType"
								:items="timePeriods"
								item-value="id"
								item-text="label"
								dense
							>
							</v-select>
						</v-col>
					</v-row>
				</v-col>
			</v-row>
		</v-col>
	</v-row>
</template>
<script>
import CreateEventDetectorDialog from '@/layout/dialogs/CreateEventDetectorDialog';

export default {
	name: 'PointPropEventDetectors',

	components: {
		CreateEventDetectorDialog,
	},

	filters: {
		detectorType: (value) => {
			console.log(value);
			let detectorsList = [
				'High Limit',
				'Low Limit',
				'Binary State Detector',
				'Multistate State Detector',
				'Point Change Detector',
				'State Change Detector',
				'No Change Detector',
				'No Update Detector',
				'Alphanumeric State Detector',
			];
			return detectorsList[value - 1];
		},
	},

	props: ['data'],

	data() {
		return {
			binaryState: [
				{ text: 'Zero', value: false },
				{ text: 'One', value: true },
			],
			alarmLevels: [
				{ text: this.$t('alarmlevels.none'), value: 0 },
				{ text: this.$t('alarmlevels.information'), value: 1 },
				{ text: this.$t('alarmlevels.urgent'), value: 2 },
				{ text: this.$t('alarmlevels.critical'), value: 3 },
				{ text: this.$t('alarmlevels.lifesafety'), value: 4 },
			],
		};
	},

	computed: {
		timePeriods() {
			return this.$store.state.timePeriods.filter((e) => {
				return e.id === 1 || e.id === 2 || e.id === 3;
			});
		},
	},
};
</script>
<style scoped>
.ped-divider {
	margin: 7px 0;
}
</style>
