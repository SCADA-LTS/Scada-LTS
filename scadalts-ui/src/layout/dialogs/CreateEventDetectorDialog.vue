<template>
	<v-dialog v-model="dialog" width="600">
		<template v-slot:activator="{ on, attrs }">
			<v-btn icon v-bind="attrs" v-on="on" @click="openDialog">
				<v-icon>mdi-bell-plus</v-icon>
			</v-btn>
		</template>

		<v-card id="dialog-create-event-detector">
			<v-card-title> {{ $t('eventDetector.dialog.create.title') }} </v-card-title>
			<v-card-text>
				<v-row>
					<v-col cols="12">
						<v-select
							v-model="selected"
							:items="eventDetectorList"
							item-value="id"
							item-text="label"
							:label="$t('eventDetector.dialog.create.select')"
							@change="watchEventDectectorChange"
							dense
						></v-select>
					</v-col>
				</v-row>
				<v-row v-if="eventDetector">
					<v-col cols="12">
						<v-text-field
							v-model="eventDetector.xid"
							:label="$t('eventDetector.dialog.create.xid')"
							dense
						></v-text-field>
					</v-col>
					<v-col cols="1">
						<img
							v-if="eventDetector.alarmLevel === 1"
							:src="images/flag_blue.png"
							title="Information"
							alt="Information"
						/>
						<img
							v-if="eventDetector.alarmLevel === 2"
							:src="images/flag_yellow.png"
							title="Urgent"
							alt="Urgent"
						/>
						<img
							v-if="eventDetector.alarmLevel === 3"
							:src="images/flag_orange.png"
							title="Critical"
							alt="Critical"
						/>
						<img
							v-if="eventDetector.alarmLevel === 4"
							:src="images/flag_red.png"
							title="Life Safety"
							alt="Life Safety"
						/>
					</v-col>
					<v-col cols="4">
						<v-select
							v-model="eventDetector.alarmLevel"
							:items="alarmLevels"
							item-value="id"
							item-text="label"
							:label="$t('common.alarmlevels.title')"
							dense
						></v-select>
					</v-col>
					<v-col cols="7">
						<v-text-field
							v-model="eventDetector.alias"
							:label="$t('datapointDetails.pointProperties.eventDetectors.alias')"
							dense
						></v-text-field>
					</v-col>

					<v-col cols="12">
						<!-- High Limit Detector -->
						<v-row v-if="selected === 1">
							<v-col cols="1"></v-col>
							<v-col cols="4">
								<v-text-field
									v-model="eventDetector.limit"
									:label="
										$t('datapointDetails.pointProperties.eventDetectors.limit.high')
									"
									dense
								></v-text-field>
							</v-col>
							<v-col cols="3">
								<v-text-field
									:label="$t('datapointDetails.pointProperties.eventDetectors.duration')"
									v-model="eventDetector.duration"
									dense
								></v-text-field>
							</v-col>
							<v-col cols="4">
								<v-select
									v-model="eventDetector.durationType"
									:items="timePeriods"
									item-value="id"
									item-text="label"
									dense
								>
								</v-select>
							</v-col>
						</v-row>

						<!-- Low Limit Detector -->
						<v-row v-if="selected === 2">
							<v-col cols="1"></v-col>
							<v-col cols="4">
								<v-text-field
									v-model="eventDetector.limit"
									:label="$t('datapointDetails.pointProperties.eventDetectors.limit.low')"
									dense
								></v-text-field>
							</v-col>
							<v-col cols="3">
								<v-text-field
									:label="$t('datapointDetails.pointProperties.eventDetectors.duration')"
									v-model="eventDetector.duration"
									dense
								></v-text-field>
							</v-col>
							<v-col cols="4">
								<v-select
									v-model="eventDetector.durationType"
									:items="timePeriods"
									item-value="id"
									item-text="label"
									dense
								>
								</v-select>
							</v-col>
						</v-row>

						<!-- State Detector -->
						<v-row v-if="selected === 3 || selected === 4 || selected === 9">
							<v-col cols="1"></v-col>
							<v-col cols="4" v-if="data.pointLocator.dataTypeId === 1">
								<v-select
									v-model="eventDetector.binaryState"
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
									v-model="eventDetector.multistateState"
									dense
								></v-text-field>
							</v-col>
							<v-col cols="4" v-if="data.pointLocator.dataTypeId === 4">
								<v-text-field
									:label="$t('datapointDetails.pointProperties.eventDetectors.state')"
									v-model="eventDetector.alphanumericState"
									dense
								></v-text-field>
							</v-col>
							<v-col cols="3">
								<v-text-field
									:label="$t('datapointDetails.pointProperties.eventDetectors.duration')"
									v-model="eventDetector.duration"
									dense
								></v-text-field>
							</v-col>
							<v-col cols="4">
								<v-select
									v-model="eventDetector.durationType"
									:items="timePeriods"
									item-value="id"
									item-text="label"
									dense
								>
								</v-select>
							</v-col>
						</v-row>

						<!-- State Change Detector -->
						<v-row v-if="selected === 6">
							<v-col cols="1"></v-col>
							<v-col cols="4">
								<v-text-field
									v-model="eventDetector.changeCount"
									:label="$t('datapointDetails.pointProperties.eventDetectors.change')"
									dense
								></v-text-field>
							</v-col>
							<v-col cols="3">
								<v-text-field
									:label="$t('datapointDetails.pointProperties.eventDetectors.duration')"
									v-model="eventDetector.duration"
									dense
								></v-text-field>
							</v-col>
							<v-col cols="4">
								<v-select
									v-model="eventDetector.durationType"
									:items="timePeriods"
									item-value="id"
									item-text="label"
									dense
								>
								</v-select>
							</v-col>
						</v-row>

						<!-- NoChange Detector -->
						<v-row v-if="selected === 7">
							<v-col cols="1"></v-col>
							<v-col cols="5">
								<v-text-field
									:label="$t('datapointDetails.pointProperties.eventDetectors.duration')"
									v-model="eventDetector.duration"
									dense
								></v-text-field>
							</v-col>
							<v-col cols="6">
								<v-select
									v-model="eventDetector.durationType"
									:items="timePeriods"
									item-value="id"
									item-text="label"
									dense
								>
								</v-select>
							</v-col>
						</v-row>

						<!-- NoUpdate Detector -->
						<v-row v-if="selected === 8">
							<v-col cols="1"></v-col>
							<v-col cols="5">
								<v-text-field
									:label="$t('datapointDetails.pointProperties.eventDetectors.duration')"
									v-model="eventDetector.duration"
									dense
								></v-text-field>
							</v-col>
							<v-col cols="6">
								<v-select
									v-model="eventDetector.durationType"
									:items="timePeriods"
									item-value="id"
									item-text="label"
									dense
								>
								</v-select>
							</v-col>
						</v-row>

						<v-row v-if="selected === 10" id="ped-cusum-positive-settings">
							<v-col cols="1"></v-col>
							<v-col cols="3">
								<v-text-field
									:label="
										$t('datapointDetails.pointProperties.eventDetectors.positiveLimit')
									"
									v-model="eventDetector.limit"
									dense
								></v-text-field>
							</v-col>
							<v-col cols="3">
								<v-text-field
									:label="$t('datapointDetails.pointProperties.eventDetectors.weight')"
									v-model="eventDetector.weight"
									dense
								></v-text-field>
							</v-col>
							<v-col cols="2">
								<v-text-field
									:label="$t('datapointDetails.pointProperties.eventDetectors.duration')"
									v-model="eventDetector.duration"
									dense
								></v-text-field>
							</v-col>
							<v-col cols="3">
								<v-select
									v-model="eventDetector.durationType"
									:items="timePeriods"
									item-value="id"
									item-text="label"
									dense
								>
								</v-select>
							</v-col>
						</v-row>

						<v-row v-if="selected === 11" id="ped-cusum-negative-settings">
							<v-col cols="1"></v-col>
							<v-col cols="3">
								<v-text-field
									:label="
										$t('datapointDetails.pointProperties.eventDetectors.negativeLimit')
									"
									v-model="eventDetector.limit"
									dense
								></v-text-field>
							</v-col>
							<v-col cols="3">
								<v-text-field
									:label="$t('datapointDetails.pointProperties.eventDetectors.weight')"
									v-model="eventDetector.weight"
									dense
								></v-text-field>
							</v-col>
							<v-col cols="2">
								<v-text-field
									:label="$t('datapointDetails.pointProperties.eventDetectors.duration')"
									v-model="eventDetector.duration"
									dense
								></v-text-field>
							</v-col>
							<v-col cols="3">
								<v-select
									v-model="eventDetector.durationType"
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
			</v-card-text>
			<v-divider></v-divider>
			<v-card-actions>
				<v-spacer></v-spacer>
				<v-btn color="primary" text @click="cancel">
					{{ $t('common.cancel') }}
				</v-btn>
				<v-btn color="primary" text @click="add">
					{{ $t('common.save') }}
				</v-btn>
			</v-card-actions>
		</v-card>
	</v-dialog>
</template>
<script>
export default {
	name: 'CreateEventDetectorDialog',

	props: ['data'],

	data() {
		return {
			dialog: false,
			select: 0,
			eventDetector: null,
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

		eventDetectorList() {
			if (!!this.data) {
				if (this.data.pointLocator.dataTypeId === 1) {
					// Binary Datapoint
					return this.$store.state.eventDetectorModule.eventDetectorList.filter((e) => {
						return e.id === 3 || e.id === 5 || e.id === 6 || e.id === 7 || e.id === 8;
					});
				} else if (this.data.pointLocator.dataTypeId === 2) {
					// Multistate Datapoint
					return this.$store.state.eventDetectorModule.eventDetectorList.filter((e) => {
						return e.id === 4 || e.id === 5 || e.id === 6 || e.id === 7 || e.id === 8;
					});
				} else if (this.data.pointLocator.dataTypeId === 3) {
					// Numeric Datapoint
					return this.$store.state.eventDetectorModule.eventDetectorList.filter((e) => {
						return !(e.id === 3 || e.id === 4 || e.id === 6 || e.id === 9);
					});
				} else if (this.data.pointLocator.dataTypeId === 4) {
					// Alphanumeric Datapoint
					return this.$store.state.eventDetectorModule.eventDetectorList.filter((e) => {
						return e.id === 5 || e.id === 6 || e.id === 7 || e.id === 8 || e.id === 9;
					});
				}
				return null;
			}
		},
	},

	methods: {
		openDialog() {
			this.dialog = true;
			if(!!this.eventDetector) {
				this.eventDetector.xid = this.generateRandomXid();
			}
		},

		add() {
			this.$store
				.dispatch('createEventDetector', {
					datapointId: this.data.id,
					requestData: this.eventDetector,
				})
				.then((resp) => {
					this.$emit('saved', resp);
				})
				.catch((err) => {
					this.$emit('savedfailed', err);
				});
			this.dialog = false;
		},

		cancel() {
			this.dialog = false;
		},

		watchEventDectectorChange(val) {
			this.eventDetector = Object.assign({}, this.$store.state.eventDetectorModule.eventDetectorTemplate);
			this.eventDetector.detectorType = val;
			if(val === 6 || val === 7 || val === 8) {
				this.eventDetector.duration = 1;
			}
			this.eventDetector.xid = this.generateRandomXid();
		},

		generateRandomXid() {
			return `PED_${Math.round(Math.random() * 100000)}`;
		}
	},
};
</script>
<style scoped></style>
