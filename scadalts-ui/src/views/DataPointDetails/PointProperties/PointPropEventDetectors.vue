<template>
	<v-row id="point-prop-event-detecotrs">
		<v-col cols="12">
			<v-row justify="space-between" align="center">
				<v-col>
					<h3>
						Event Detectors
						<ConfirmationDialog
							:btnvisible="false"
							:dialog="confirmDeleteDialog"
							@result="deleteEventDetector"
							:title="
								$t('datapointDetails.pointProperties.eventDetectors.delete.dialog.title')
							"
							:message="
								$t('datapointDetails.pointProperties.eventDetectors.delete.dialog.text')
							"
						></ConfirmationDialog>
					</h3>
				</v-col>
				<v-col class="row justify-end">
					<CreateEventDetectorDialog :data="data" @saved="addEventDetector" @savedfailed="addEventDetectorFail">
					</CreateEventDetectorDialog>
				</v-col>
			</v-row>
		</v-col>
		<v-col cols="12" id="point-prop-event-detecotrs-list">
			<v-row v-for="e in data.eventDetectors" :key="e.id">
				<hr class="v-divider theme--light ped-divider" />

				<v-col cols="10">
					<h4>{{ e.detectorType | detectorType }} - {{ e.xid }}</h4>
				</v-col>

				<v-col cols="1">
					<v-btn icon @click="updateEventDetector(e)">
						<v-icon>mdi-content-save</v-icon></v-btn
					>
				</v-col>

				<v-col cols="1">
					<v-btn icon @click="openConfirmDialog(e)">
						<v-icon>mdi-minus-circle</v-icon></v-btn
					>
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
						item-value="id"
						item-text="label"
						:label="$t('common.alarmlevels.title')"
						dense
					></v-select>
				</v-col>

				<v-col cols="7">
					<v-text-field
						v-model="e.alias"
						:label="$t('datapointDetails.pointProperties.eventDetectors.alias')"
						dense
					>
					</v-text-field>
				</v-col>

				<v-col cols="12">
					<!-- High Limit Detector -->
					<v-row v-if="e.detectorType === 1">
						<v-col cols="1"></v-col>
						<v-col cols="4">
							<v-text-field
								v-model="e.limit"
								:label="$t('datapointDetails.pointProperties.eventDetectors.limit.high')"
								dense
							></v-text-field>
						</v-col>
						<v-col cols="3">
							<v-text-field
								:label="$t('datapointDetails.pointProperties.eventDetectors.duration')"
								v-model="e.duration"
								dense
							></v-text-field>
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
							<v-text-field
								v-model="e.limit"
								:label="$t('datapointDetails.pointProperties.eventDetectors.limit.low')"
								dense
							></v-text-field>
						</v-col>
						<v-col cols="3">
							<v-text-field
								:label="$t('datapointDetails.pointProperties.eventDetectors.duartion')"
								v-model="e.duration"
								dense
							></v-text-field>
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
								:label="$t('datapointDetails.pointProperties.eventDetectors.state')"
								v-model="e.multistateState"
								dense
							></v-text-field>
						</v-col>
						<v-col cols="4" v-if="data.pointLocator.dataTypeId === 4">
							<v-text-field
								:label="$t('datapointDetails.pointProperties.eventDetectors.state')"
								v-model="e.alphanumericState"
								dense
							></v-text-field>
						</v-col>
						<v-col cols="3">
							<v-text-field
								:label="$t('datapointDetails.pointProperties.eventDetectors.duration')"
								v-model="e.duration"
								dense
							></v-text-field>
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
								:label="$t('datapointDetails.pointProperties.eventDetectors.change')"
								dense
							></v-text-field>
						</v-col>
						<v-col cols="3">
							<v-text-field
								:label="$t('datapointDetails.pointProperties.eventDetectors.duration')"
								v-model="e.duration"
								dense
							></v-text-field>
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
							<v-text-field
								:label="$t('datapointDetails.pointProperties.eventDetectors.duration')"
								v-model="e.duration"
								dense
							></v-text-field>
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
							<v-text-field
								:label="$t('datapointDetails.pointProperties.eventDetectors.duration')"
								v-model="e.duration"
								dense
							></v-text-field>
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

					<v-row v-if="e.detectorType === 10" id="ped-cusum-positive-settings">
						<v-col cols="1"></v-col>
						<v-col cols="3">
							<v-text-field
								:label="
									$t('datapointDetails.pointProperties.eventDetectors.positiveLimit')
								"
								v-model="e.limit"
								dense
							></v-text-field>
						</v-col>
						<v-col cols="2">
							<v-text-field
								:label="$t('datapointDetails.pointProperties.eventDetectors.weight')"
								v-model="e.weight"
								dense
							></v-text-field>
						</v-col>
						<v-col cols="2">
							<v-text-field
								:label="$t('datapointDetails.pointProperties.eventDetectors.duration')"
								v-model="e.duration"
								dense
							></v-text-field>
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

					<v-row v-if="e.detectorType === 11" id="ped-cusum-negative-settings">
						<v-col cols="1"></v-col>
						<v-col cols="3">
							<v-text-field
								:label="
									$t('datapointDetails.pointProperties.eventDetectors.negativeLimit')
								"
								v-model="e.limit"
								dense
							></v-text-field>
						</v-col>
						<v-col cols="2">
							<v-text-field
								:label="$t('datapointDetails.pointProperties.eventDetectors.weight')"
								v-model="e.weight"
								dense
							></v-text-field>
						</v-col>
						<v-col cols="2">
							<v-text-field
								:label="$t('datapointDetails.pointProperties.eventDetectors.duration')"
								v-model="e.duration"
								dense
							></v-text-field>
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
				</v-col>
			</v-row>
			<v-snackbar v-model="response.status">
				{{ response.message }}
			</v-snackbar>
		</v-col>
	</v-row>
</template>
<script>
import CreateEventDetectorDialog from '@/layout/dialogs/CreateEventDetectorDialog';
import ConfirmationDialog from '@/layout/dialogs/ConfirmationDialog';

/**
 * Event Detectors for Point Properties
 *
 * An event detector's purpose is to determine if the value of a point satisfies
 * one or more related conditions, and if so, to become "active" and raise an
 * event that can be appropriately handled.
 *
 * @param {Object} data - Point Details object with data.
 *
 * @author Radoslaw Jajko <rjajko@softq.pl>
 * @version 1.0
 */
export default {
	name: 'PointPropEventDetectors',

	components: {
		CreateEventDetectorDialog,
		ConfirmationDialog,
	},

	filters: {
		detectorType: (value) => {
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
				'Positive CUSUM',
				'Negative CUSUM'
			];
			return detectorsList[value - 1];
		},
	},

	props: ['data'],

	data() {
		return {
			confirmDeleteDialog: false,
			confirmDeleteDetector: null,
			binaryState: [
				{
					text: this.$t('datapointDetails.pointProperties.eventDetectors.binary.zero'),
					value: false,
				},
				{
					text: this.$t('datapointDetails.pointProperties.eventDetectors.binary.one'),
					value: true,
				},
			],
			response: {
				status: false,
				message: '',
			},
		};
	},

	computed: {
		alarmLevels() {
			return this.$store.state.alarmLevels;
		},

		timePeriods() {
			return this.$store.state.timePeriods.filter((e) => {
				return e.id === 1 || e.id === 2 || e.id === 3;
			});
		},
	},

	methods: {
		addEventDetector(value) {
			this.data.eventDetectors.push(value);
			this.response.status = true;
			this.response.message = this.$t('common.snackbar.add.success');
		},
		addEventDetectorFail(error) {
			this.response.status = true;
			if(error.status === 409) {
				this.response.message = this.$t('common.snackbar.xid.not.unique');
			} else {
				this.response.message = this.$t('common.snackbar.add.fail');
			}
			
		},

		openConfirmDialog(e) {
			this.confirmDeleteDialog = true;
			this.confirmDeleteDetector = e;
		},

		updateEventDetector(e) {
			this.$store
				.dispatch('updateEventDetector', {
					datapointId: this.data.id,
					pointEventDetectorId: e.id,
					requestData: e,
				})
				.then((resp) => {
					this.response.status = true;
					this.response.message = this.$t('common.snackbar.update.success');
				})
				.catch((err) => {
					this.response.status = true;
					this.response.message = this.$t('common.snackbar.update.fail');
				});
		},

		deleteEventDetector(e) {
			this.confirmDeleteDialog = false;
			if (e) {
				this.$store
					.dispatch('deleteEventDetector', {
						datapointId: this.data.id,
						pointEventDetectorId: this.confirmDeleteDetector.id,
					})
					.then((resp) => {
						if (resp.status === 'deleted') {
							this.data.eventDetectors = this.data.eventDetectors.filter((el) => {
								return el.id !== this.confirmDeleteDetector.id;
							});
							this.response.status = true;
							this.response.message = this.$t('common.snackbar.delete.success');
						} else {
							this.response.status = true;
							this.response.message = this.$t('common.snackbar.delete.fail');
						}
					}).catch(() => {
						this.response.status = true;
						this.response.message = this.$t('common.snackbar.delete.fail');
					})
			}
		},
	},
};
</script>
<style scoped>
.ped-divider {
	margin: 7px 0;
}
</style>
