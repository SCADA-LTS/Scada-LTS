<template>
	<BaseViewComponent
		:component="component"
		@update="$emit('update')"
		@click="$emit('click', $event)"
		@mousedown="$emit('mousedown', $event)"
	>
		<template v-slot:default>
			<LineChartComponent
				v-if="chartLoaded"
				:pointIds="pointList"
				:useXid="true"
				:startDate="`${component.durationPeriods}-${convertToStringTimePeriod(component.durationType)}`"
				:width="`${component.width}`"
				:height="`${component.height}`"
			>
			</LineChartComponent>
		</template>

		<template v-slot:layout>
			<v-col cols="6">
				<v-text-field label="Width" v-model="component.width"></v-text-field>
			</v-col>
			<v-col cols="6">
				<v-text-field label="Height" v-model="component.height"></v-text-field>
			</v-col>
		</template>
		<template v-slot:renderer>
			<v-row>
				<v-col cols="8">
					<v-text-field
						label="Start time"
						v-model="component.durationPeriods"
					></v-text-field>
				</v-col>
				<v-col cols="4">
					<v-select
						label="Period"
						v-model="component.durationType"
						:items="durationTypes"
					></v-select>
				</v-col>
				<v-col cols="12">
					<DataPointSerachComponent
						@change="onPointChange($event, 1)"
					></DataPointSerachComponent>
				</v-col>
				<v-col cols="12">
					<DataPointSerachComponent
						@change="onPointChange($event, 2)"
					></DataPointSerachComponent>
				</v-col>
				<v-col cols="12">
					<DataPointSerachComponent
						@change="onPointChange($event, 3)"
					></DataPointSerachComponent>
				</v-col>
				<v-col cols="12">
					<DataPointSerachComponent
						@change="onPointChange($event, 4)"
					></DataPointSerachComponent>
				</v-col>
				<v-col cols="12">
					<DataPointSerachComponent
						@change="onPointChange($event, 5)"
					></DataPointSerachComponent>
				</v-col>
				<!-- <v-col cols="12">
					<DataPointSerachComponent @change="onPointChange(2)"></DataPointSerachComponent>
				</v-col> -->
			</v-row>
		</template>
	</BaseViewComponent>
</template>
<script>
import BaseViewComponent from '../BaseViewComponent.vue';
import LineChartComponent from '@/components/amcharts/LineChartComponent.vue';
import DataPointSerachComponent from '@/layout/buttons/DataPointSearchComponent';
export default {
	components: {
		BaseViewComponent,
		LineChartComponent,
		DataPointSerachComponent,
	},

	data() {
		return {
			pointList: '',
			chartLoaded: false,
			durationTypes: [
				{
					value: 1,
					text: 'Seconds',
				},
				{
					value: 2,
					text: 'Minutes',
				},
				{
					value: 3,
					text: 'Hours',
				},
				{
					value: 4,
					text: 'Days',
				},
				{
					value: 5,
					text: 'Weeks',
				},
				{
					value: 6,
					text: 'Months',
				},
				{
					value: 7,
					text: 'Years',
				},
			],
		};
	},

	mounted() {
		this.pointList = Object.values(this.component.children)
			.filter((x) => !!x)
			.join(',');
		this.chartLoaded = true;
	},

	props: {
		component: {
			type: Object,
			required: true,
		},
	},

	methods: {
		onPointChange(event, index) {
			this.chartLoaded = false;
			this.component.children[`point${index}`] = event.xid;
			this.pointList = Object.values(this.component.children)
				.filter((x) => !!x)
				.join(',');
			this.chartLoaded = true;
		},

		convertToStringTimePeriod(timePeriod) {
		switch (timePeriod) {
			case 1:
				return 'second';
			case 2:
				return 'minute';
			case 3:
				return 'hour';
			case 4:
				return 'day';
			case 5:
				return 'week';
			case 6:
				return 'month';
			case 7:
				return 'year';
			default:
				return 'day';
		}
	}
	},
	
};
</script>
<style></style>
