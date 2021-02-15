<template>
	<v-card>
		<v-card-title>
			History
            <v-spacer>
            </v-spacer>
			<v-dialog v-model="dialog" width="800">
				<template v-slot:activator="{ on, attrs }">
					<v-btn icon fab small v-bind="attrs" v-on="on">
						<v-icon>mdi-arrow-expand-all</v-icon>
					</v-btn>
				</template>

				<v-card>
                    <v-card-title>
                        Hisotry
                    </v-card-title>
					<v-card-text>
						<v-row>
							<v-col cols="6">
								<v-text-field v-model="timePeriod" label="Time period" dense>
								</v-text-field>
							</v-col>
							<v-col cols="6">
								<v-select
									v-model="timePeriodType"
									:items="timePeriods"
									item-value="id"
									item-text="label"
									append-outer-icon="mdi-autorenew"
									@click:append-outer="fetchData"
									dense
								></v-select>
							</v-col>
						</v-row>
						<v-simple-table dense fixed-header height="500px">
							<template v-slot:default>
								<thead>
									<tr>
										<th>Value</th>
										<th>Date</th>
									</tr>
								</thead>
								<tbody>
									<tr v-for="e in valueList" :key="e">
										<td>{{ e.value }}</td>
										<td>{{ new Date(e.ts).toLocaleString() }}</td>
									</tr>
								</tbody>
							</template>
						</v-simple-table>
					</v-card-text>
				</v-card>
			</v-dialog>
		</v-card-title>

		<v-card-text>
			<v-row>
				<v-col cols="6">
					<v-text-field v-model="timePeriod" label="Time period" dense> </v-text-field>
				</v-col>
				<v-col cols="6">
					<v-select
						v-model="timePeriodType"
						:items="timePeriods"
						item-value="id"
						item-text="label"
						append-outer-icon="mdi-autorenew"
						@click:append-outer="fetchData"
						dense
					></v-select>
				</v-col>
			</v-row>
			<v-simple-table dense fixed-header height="150px">
				<template v-slot:default>
					<thead>
						<tr>
							<th>Value</th>
							<th>Date</th>
						</tr>
					</thead>
					<tbody>
						<tr v-for="e in valueList" :key="e">
							<td>{{ e.value }}</td>
							<td>{{ new Date(e.ts).toLocaleString() }}</td>
						</tr>
					</tbody>
				</template>
			</v-simple-table>
		</v-card-text>
	</v-card>
</template>
<script>
export default {
	name: 'DataPointValueHistory',

	props: ['datapointId'],

	data() {
		return {
			timePeriod: 1,
			timePeriodType: 3,
			valueList: null,
		};
	},

	computed: {
		timePeriods() {
			return this.$store.state.timePeriods.filter((e) => {
				return !(e.id === 0 || e.id === 8 || e.id === 7);
			});
		},
	},

	mounted() {
		this.fetchData();
	},

	methods: {
		async fetchData() {
			let from = await this.$store.dispatch('convertSinceTimePeriodToTimestamp', {
				period: this.timePeriod,
				type: this.timePeriodType,
			});

			console.log(from.getTime());

			let response = await this.$store.dispatch('getDataPointValueFromTimeperiod', {
				datapointId: this.datapointId,
				startTs: from.getTime(),
				endTs: new Date().getTime(),
			});
			this.valueList = response.values.reverse();
			// console.log("FETCH HISTORY DATA", response);
		},
	},
};
</script>
<style scoped></style>
