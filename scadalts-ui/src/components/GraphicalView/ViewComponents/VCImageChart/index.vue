<template>
	<BaseViewComponent :component="component" @update="$emit('update')">
		<template v-slot:default>
			<LineChartComponent
				v-if="chartLoaded"
				:pointIds="pointList"
				:useXid="true"
				:startDate="`${component.durationPeriods}-${component.durationType
					.slice(0, -1)
					.toLowerCase()}`"
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
					value: 'SECONDS',
					text: 'Seconds',
				},
				{
					value: 'MINUTEST',
					text: 'Minutes',
				},
				{
					value: 'HOURS',
					text: 'Hours',
				},
				{
					value: 'DAYS',
					text: 'Days',
				},
				{
					value: 'WEEKS',
					text: 'Weeks',
				},
				{
					value: 'MONTHS',
					text: 'Months',
				},
				{
					value: 'YEARS',
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
	},
};
</script>
<style></style>
