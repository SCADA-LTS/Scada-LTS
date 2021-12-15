<template>
	<v-col cols="12" md="6" class="align-stretch">
		<v-card>
			<v-card-title>
				{{ $t('systemsettings.event.title') }}
				<span v-if="isSystemEventEdited">*</span>
			</v-card-title>
			<v-card-text>
				<v-row v-for="event in systemEventTypes" v-bind:key="event.i1" dense>
					<v-col cols="10">
						<v-select
							@change="watchDataChange()"
							v-model="event.i2"
							:items="alarmLevels"
							item-value="id"
							item-text="label"
							:label="$t(`${event.translation}`)"
							dense
						></v-select>
					</v-col>
					<v-col cols="2">
						<img
							v-if="event.i2 === 1"
							src="images/flag_blue.png"
							title="Information"
							alt="Information"
						/>
						<img
							v-if="event.i2 === 2"
							src="images/flag_yellow.png"
							title="Urgent"
							alt="Urgent"
						/>
						<img
							v-if="event.i2 === 3"
							src="images/flag_orange.png"
							title="Critical"
							alt="Critical"
						/>
						<img
							v-if="event.i2 === 4"
							src="images/flag_red.png"
							title="Life Safety"
							alt="Life Safety"
						/>
					</v-col>
				</v-row>
			</v-card-text>
		</v-card>

		<v-snackbar v-model="response.status" :color="response.color">
			{{ response.message }}
		</v-snackbar>
	</v-col>
</template>
<script>
import { object } from '@amcharts/amcharts4/core';

export default {
	name: 'SystemEventTypesComponent',

	data() {
		return {
			systemEventTypes: undefined,
			systemEventTypesStore: undefined,
			isSystemEventEdited: false,
			response: {
				color: 'success',
				status: false,
				message: '',
			},
		};
	},

	computed: {
		alarmLevels() {
			return this.$store.state.alarmLevels;
		},
	},

	mounted() {
		this.fetchData();
	},

	methods: {
		async fetchData() {
			this.systemEventTypes = await this.$store.dispatch('getSystemEventTypes');
			this.systemEventTypesStore = this.copyDataFromStore();
		},

		saveData() {
			console.log('Saved System!');
			this.$store.commit('setSystemEventTypes', this.systemEventTypes);
			this.systemEventTypesStore = this.copyDataFromStore();
			this.$store
				.dispatch('saveSystemEventTypes')
				.then((resp) => {
					if (resp) {
						this.restoreData();
						this.response = {
							status: true,
							message: this.$t('systemsettings.notification.save.systemevent'),
							color: 'success',
						};
					}
				})
				.catch(() => {
					this.response = {
						status: true,
						message: this.$t('systemsettings.notification.fail'),
						color: 'danger',
					};
				});
		},

		restoreData() {
			console.log('Restored System!');
			this.fetchData();
			this.isSystemEventEdited = false;
		},

		copyDataFromStore() {
			return JSON.parse(
				JSON.stringify(this.$store.state.systemSettings.systemEventTypes),
			);
		},

		async watchDataChange() {
			this.isSystemEventEdited = await this.isDataChanged();
			this.emitData(this.isSystemEventEdited);
		},

		async isDataChanged() {
			for (let i = 0; i < this.systemEventTypes.length; i++) {
				if (
					!(await this.$store.dispatch('configurationEqual', {
						object1: this.systemEventTypes[i],
						object2: this.systemEventTypesStore[i],
					}))
				)
					return true;
			}
			return false;
		},

		emitData(changed) {
			this.$emit('changed', {
				component: 'systemEventTypesComponent',
				title: 'systemsettings.event.title',
				changed: changed,
				data: this.sumarizeDataChanges(),
			});
		},

		sumarizeDataChanges() {
			let data = [];
			for (let i = 0; i < this.systemEventTypes.length; i++) {
				if (this.systemEventTypes[i].i2 !== this.systemEventTypesStore[i].i2) {
					data.push({
						label: this.systemEventTypes[i].translation,
						originalData: this.convertInfoLevel(this.systemEventTypesStore[i].i2),
						changedData: this.convertInfoLevel(this.systemEventTypes[i].i2),
					});
				}
			}
			return data;
		},

		convertInfoLevel(value) {
			switch (Number(value)) {
				case 0:
					return this.$t('alarmlevels.none');
				case 1:
					return this.$t('alarmlevels.information');
				case 2:
					return this.$t('alarmlevels.urgent');
				case 3:
					return this.$t('alarmlevels.critical');
				case 4:
					return this.$t('alarmlevels.lifesafety');
			}
			return value;
		},
	},
};
</script>
<style></style>
