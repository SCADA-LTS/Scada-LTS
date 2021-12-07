<template>
	<BaseViewComponent
		:component="component"
		@update="update"
		@click="$emit('click', $event)"
		@mousedown="$emit('mousedown', $event)"
	>
		<template v-slot:default>
			<v-simple-table dense :style="{ width: component.width + 'px' }">
				<template v-slot:default>
					<thead>
						<tr>
							<th>Alarm Level</th>
							<th>Time</th>
							<th>Description</th>
						</tr>
					</thead>
					<tbody>
						<tr v-for="al in alarmList" :key="al.id">
							<td><img :src="alarmFlags[al.alarmLevel].image" /></td>
							<td>{{ $date(al.activeTs).format('YYYY-MM-DD hh:mm:ss') }}</td>
							<td>
								{{ $t(`${al.message.split('|')[0]}`, al.message.split('|')) }}
							</td>
						</tr>
					</tbody>
				</template>
			</v-simple-table>
		</template>
		<template v-slot:layout>
			<v-col cols="12">
				<v-text-field label="Width" v-model="component.width"></v-text-field>
			</v-col>
		</template>

		<template v-slot:renderer>
			<v-row>
				<v-col cols="12">
					<v-text-field
						label="Max List size"
						v-model="component.maxListSize"
					></v-text-field>
				</v-col>
				<v-col cols="12">
					<v-select
						label="Min Alarm Level"
						v-model="component.minAlarmLevel"
						:items="alarmLevels"
					></v-select>
				</v-col>
			</v-row>
		</template>
	</BaseViewComponent>
</template>
<script>
import BaseViewComponent from '../BaseViewComponent.vue';
export default {
	components: {
		BaseViewComponent,
	},

	props: {
		component: {
			type: Object,
			required: true,
		},
	},

	mounted() {
		this.fetchAlarms();
	},

	computed: {
		alarmFlags() {
			return this.$store.state.staticResources.alarmFlags;
		},
	},

	data() {
		return {
			alarmList: [],
			alarmUpdateInterval: null,
			alarmLevels: [
				{
					value: 0,
					text: 'None',
				},
				{
					value: 1,
					text: 'Information',
				},
				{
					value: 2,
					text: 'Urgent',
				},
				{
					value: 3,
					text: 'Critical',
				},
				{
					value: 4,
					text: 'Life safety',
				},
			],
		};
	},

	destroyed() {
		clearInterval(this.alarmUpdateInterval);
	},

	methods: {
		update() {
			this.fetchAlarms();
			this.$emit('update');
		},
		async fetchAlarms() {
			try {
				const response = await this.$store.dispatch('searchEvents', {
					alarmLevel: this.component.minAlarmLevel,
					datapoint: null,
					endDate: '',
					endTime: '',
					eventSourceType: 0,
					keywords: '',
					limit: this.component.maxListSize,
					offset: 0,
					sortBy: ['activeTs'],
					sortDesc: [true],
					startDate: '',
					startTime: '00:00',
					status: '*',
				});
				this.alarmList = response.rows.slice(0, this.component.maxListSize);
				if (!this.alarmUpdateInterval) {
					this.alarmUpdateInterval = setInterval(() => {
						this.fetchAlarms();
					}, 5000);
				}
			} catch (e) {
				clearInterval(this.alarmUpdateInterval);
				console.error(e);
			}
		},
	},
};
</script>
<style></style>
