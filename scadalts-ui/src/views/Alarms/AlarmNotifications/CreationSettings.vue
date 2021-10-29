<template>
	<v-dialog v-model="dialog" width="600">
		<template v-slot:activator="{ on, attrs }">
			<v-btn elevation="2" fab v-bind="attrs" v-on="on">
				<v-icon>mdi-pencil</v-icon>
			</v-btn>
		</template>

		<v-card>
			<v-card-title class="headline gray">
				{{ $t('plcalarms.notification.settings.title') }}
			</v-card-title>
			<v-card-text>
				<div v-if="pedTemplate">
					<h3>{{ $t('plcalarms.notification.settings.ped.title') }}</h3>
					<v-text-field
						:label="$t('plcalarms.notification.settings.ped.alias')"
						v-model="pedTemplate.alias"
					></v-text-field>
					<v-select
						:items="itemsAlarm"
						:label="$t('plcalarms.notification.settings.ped.alarmLevel')"
						v-model="pedTemplate.alarmLevel"
						item-value="id"
						item-text="label"
					></v-select>
				</div>
				<div v-if="ehTemplate">
					<h3>{{ $t('plcalarms.notification.settings.eh.title') }}</h3>
					<v-text-field
						:label="$t('plcalarms.notification.settings.eh.xid')"
						v-model="ehTemplate.xid"
					></v-text-field>
					<v-text-field
						:label="$t('plcalarms.notification.settings.eh.alias')"
						v-model="ehTemplate.alias"
					></v-text-field>
					<v-checkbox
						:label="$t('plcalarms.notification.settings.eh.disable')"
						v-model="ehTemplate.disabled"
					></v-checkbox>
				</div>
			</v-card-text>
			<v-divider></v-divider>
			<v-card-actions>
				<v-spacer></v-spacer>
				<v-btn color="primary" text @click="dialog = false">{{
					$t('uiv.modal.ok')
				}}</v-btn>
			</v-card-actions>
		</v-card>
	</v-dialog>
</template>
<script>
export default {
	name: 'CreationSettingsDialog',

	data() {
		return {
			dialog: false,
			itemsAlarm: [
				{ id: 0, label: 'None' },
				{ id: 1, label: 'Information' },
				{ id: 2, label: 'Urgent' },
				{ id: 3, label: 'Critical' },
				{ id: 4, label: 'Life safety' },
			],
			itemsDuration: [
				{ id: 1, label: 'Second(s)' },
				{ id: 2, label: 'Minute(s)' },
				{ id: 3, label: 'Hour(s)' },
			],
			pedTemplate: undefined,
			ehTemplate: undefined,
		};
	},

	mounted() {
		this.pedTemplate = this.$store.state.storeAlarmsNotifications.pedTemplate;
		this.ehTemplate = this.$store.state.storeAlarmsNotifications.ehTemplate;
	},
};
</script>
<style></style>
