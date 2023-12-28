<template>
	<div>
		<v-simple-table
			class="slts-cmp--table"
			fixed-header
			dense
			:style="{ maxWidth: maxWidth + 'px' }"
			:height="maxHeight + 'px'"
			v-if="wsConnection && retires < 5"
		>
			<template v-slot:default>
				<thead>
					<tr>
						<th>DataPoint Name</th>
						<th>Current Value</th>
						<th>Last timestamp</th>
						<th v-if="showTotal">Sum (Changes for binary)</th>
						<th v-if="showAverage">Average</th>
						<th v-if="showMax">Max</th>
						<th v-if="showMin">Min</th>
					</tr>
				</thead>
				<tbody>
					<tr v-for="dp in pointState" :key="dp.name">
						<td>{{ dp.name }}</td>
						<td>{{ dp.current | fixed(roundValue, dp.type) }}</td>
						<td>{{ dp.lastTimestamp | localeDate }}</td>
						<td v-if="showTotal">{{ dp.sum | fixed(roundValue, 'numeric') }}</td>
						<td v-if="showAverage">{{ dp.avg | fixed(roundValue, 'numeric') }}</td>
						<td v-if="showMax">{{ dp.max }}</td>
						<td v-if="showMin">{{ dp.min }}</td>
					</tr>
				</tbody>
			</template>
		</v-simple-table>
		<div v-else>
			<v-alert type="warning" dense color="orange">
				Simple Point Table: Failed to establish connection!
			</v-alert>
		</div>
	</div>
</template>
<script>
import { getValidDate } from '@/../../utils.js';
import BinaryDataPointEntry from './models/BinaryDataPointEntry.js';
import NumericDataPointEntry from '@/models/NumericDataPointEntry.js';
/**
 * Simple Point Table Graphical View Component
 *
 * Graphical View component that can display a table of data points
 * with their current values. It also provides the statistics for
 * the data points from the specific time range. Connection
 * between the server and the client is established by the
 * websocket connection. If the component looses the connection
 * it will inform the user about that.
 *
 * @author Raodslaw Jajko
 * @version 1.0.0
 */
export default {
	props: {
		pointIds: { type: String, required: true },
		startDate: { type: String },
		showTotal: { type: Boolean, default: false },
		showAverage: { type: Boolean, default: false },
		showMax: { type: Boolean, default: false },
		showMin: { type: Boolean, default: false },
		roundValue: { type: Number, default: 2 },
		maxWidth: { type: Number, default: 600 },
		maxHeight: { type: Number, default: 400 },
	},

	data() {
		return {
			startTimestamp: null,
			endTimestamp: null,
			pointState: [],
			retires: 0,
		};
	},

	filters: {
		localeDate(value) {
			return new Date(value).toLocaleString();
		},
		fixed: (value, precision, type) => {
			if(value !== null && value !== undefined) {
				if(type === 'numeric' && !!precision) {
					return value.toFixed(precision);
				} 
				return value;
			} else {
				return '-';
			}
		}
	},

	computed: {
		wsConnection() {
			return this.$store.state.webSocketModule.webSocketConnection;
		},
	},

	mounted() {
		this.initTableData();
		this.fix = this.roundValue;
	},

	destroyed() {
		this.pointState.forEach((p) => {
			p.unsubscribe();
		});
	},

	methods: {
		initTableData() {
			if (this.wsConnection) {
				this.retires = 0;
				this.initTimeRange();
				this.initDataPointValues();
			} else {
				if (this.retires < 5) {
					this.retires++;
					setTimeout(() => {
						this.initTableData();
					}, 1000);
				}
			}
		},

		/**
		 * Initialize the time range for the data points
		 */
		initTimeRange() {
			this.endTimestamp = new Date().getTime();
			if (!this.startDate) {
				this.startTimestamp = this.endTimestamp - 3600000;
			} else {
				this.startTimestamp = getValidDate(this.startDate);
			}
		},

		/**
		 * Initialize the TableDataPoint entries
		 *
		 * Depending on the type of the datapoint,
		 * a different model is used. This datapoints
		 * use WebSocket connection to get the current value.
		 */
		initDataPointValues() {
			const pointIds = this.pointIds.split(',');
			const ws = this.$store.state.webSocketModule.webSocket;
			pointIds.forEach(async (pointId) => {
				try {
					const { name, values, type } = await this.getDataPointHistoricValues(pointId);
					switch (type) {
						case 'Binary':
							this.pointState.push(new BinaryDataPointEntry(pointId, name, values, ws));
							break;
						case 'Numeric':
						case 'Multistate':
							this.pointState.push(new NumericDataPointEntry(pointId, name, values, ws, type));
							break;
						default:
							console.error(`Unsupported datapoint type: ${type}`);
					}
				} catch (e) {
					console.error(`Failed to load DataPoint id=${pointId} values`, e);
				}
			});
		},

		/**
		 * Get Data Point Values from time-range
		 *
		 * Fetches values from the server and returns an object with
		 * data point details and values.
		 * @private
		 */
		getDataPointHistoricValues(datapointId) {
			return this.$store.dispatch('getDataPointValueFromTimeperiod', {
				datapointId: datapointId,
				startTs: this.startTimestamp,
				endTs: this.endTimestamp,
			});
		},
	},
};
</script>
<style scoped>
.slts-cmp--table {
	overflow: auto;
}
</style>
