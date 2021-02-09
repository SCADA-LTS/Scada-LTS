<template>
	<v-col cols="12" md="6" class="align-stretch">
		<v-card>
			<v-card-title>
				{{ $t('systemsettings.audit.title') }}
				<span v-if="isAuditEventEdited">*</span>
			</v-card-title>
			<v-card-text>
				<v-row v-for="event in auditEventTypes" v-bind:key="event.i1" dense>
					<v-col cols="10">
						<v-select
							@change="watchDataChange()"
							v-model="event.i2"
							:items="alarmLevels"
							item-value="value"
							item-text="text"
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
	</v-col>
</template>
<script>
import { object } from '@amcharts/amcharts4/core';
import i18n from '../../i18n';

export default {
	name: 'AuditEventTypesComponent',

	data() {
		return {
			auditEventTypes: undefined,
			auditEventTypesStore: undefined,
			isAuditEventEdited: false,
			alarmLevels: [
				{ text: this.$t('alarmlevels.none'), value: 0 },
				{ text: this.$t('alarmlevels.information'), value: 1 },
				{ text: this.$t('alarmlevels.urgent'), value: 2 },
				{ text: this.$t('alarmlevels.critical'), value: 3 },
				{ text: this.$t('alarmlevels.lifesafety'), value: 4 },
			],
		};
	},

	mounted() {
		this.fetchData();
	},

	methods: {
		async fetchData() {
			this.auditEventTypes = await this.$store.dispatch('getAuditEventTypes');
			this.auditEventTypesStore = this.copyDataFromStore();
		},

		saveData() {
			console.log('Saved Audit!');
			this.$store.commit('setAuditEventTypes', this.auditEventTypes);
			this.auditEventTypesStore = this.copyDataFromStore();
			this.$store
				.dispatch('saveAuditEventTypes')
				.then((resp) => {
					if (resp) {
						this.restoreData();
						this.$notify({
							placement: 'top-right',
							type: 'success',
							content: i18n.t('systemsettings.notification.save.auditevent'),
						});
					}
				})
				.catch(() => {
					this.$notify({
						placement: 'top-right',
						type: 'danger',
						content: i18n.t('systemsettings.notification.fail'),
					});
				});
		},

		restoreData() {
			console.log('Restored Audit!');
			this.fetchData();
			this.isAuditEventEdited = false;
		},

		copyDataFromStore() {
			return JSON.parse(JSON.stringify(this.$store.state.systemSettings.auditEventTypes));
		},

		async watchDataChange() {
			this.isAuditEventEdited = await this.isDataChanged();
			this.emitData(this.isAuditEventEdited);
		},

		async isDataChanged() {
			for (let i = 0; i < this.auditEventTypes.length; i++) {
				if (
					!(await this.$store.dispatch('configurationEqual', {
						object1: this.auditEventTypes[i],
						object2: this.auditEventTypesStore[i],
					}))
				)
					return true;
			}
			return false;
		},

		emitData(changed) {
			this.$emit('changed', {
				component: 'auditEventTypesComponent',
				title: 'systemsettings.audit.title',
				changed: changed,
				data: this.sumarizeDataChanges(),
			});
		},

		sumarizeDataChanges() {
			let data = [];
			for (let i = 0; i < this.auditEventTypes.length; i++) {
				if (this.auditEventTypes[i].i2 !== this.auditEventTypesStore[i].i2) {
					data.push({
						label: this.auditEventTypes[i].translation,
						originalData: this.convertInfoLevel(this.auditEventTypesStore[i].i2),
						changedData: this.convertInfoLevel(this.auditEventTypes[i].i2),
					});
				}
			}
			return data;
		},

		convertInfoLevel(value) {
			switch (Number(value)) {
				case 0:
					return i18n.t('alarmlevels.none');
					break;
				case 1:
					return i18n.t('alarmlevels.information');
					break;
				case 2:
					return i18n.t('alarmlevels.urgent');
					break;
				case 3:
					return i18n.t('alarmlevels.critical');
					break;
				case 4:
					return i18n.t('alarmlevels.lifesafety');
					break;
			}
			return value;
		},
	},
};
</script>
<style></style>
