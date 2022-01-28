<template>
	<v-app>
		<v-card elevation="1">
			<v-card-text>
				<v-row
					v-if="!!lastMessage"
					class="state-block"
					v-bind:class="`is-state-${lastMessage.state.toLowerCase()}`"
				>
					<v-col xs="2" sm="1" class="flex-al-center" v-if="checkingConditions">
						<v-icon class="is-loading">mdi-refresh</v-icon>
					</v-col>
					<v-col xs="2" sm="1" class="flex-al-center" v-else>
						<v-icon v-if="lastMessage.state === 'OK'">mdi-check</v-icon>
						<v-icon v-else-if="lastMessage.state === 'WARN'">mdi-alert</v-icon>
						<v-icon v-else>mdi-close</v-icon>
					</v-col>
					<v-col cols="9" class="flex-al-center">
						<v-row>
							<v-col class="flex-al-center">
								{{ lastMessage.message }}
							</v-col>
							<v-col xs="12" md="6" v-if="!!lastServerTime">
								<p class="state-value-label">Last message</p>
								{{ lastServerTime }}
							</v-col>
						</v-row>
					</v-col>
					<v-col cols="2" class="flex-al-center">
						<v-menu offset-y offset-x left min-width="400">
							<template v-slot:activator="{ on, attrs }">
								<v-btn icon v-bind="attrs" v-on="on">
									<v-icon>mdi-information-outline</v-icon>
								</v-btn>
							</template>
							<v-card>
								<v-card-title>
									<span>
										{{ name }}
									</span>
									<v-spacer> </v-spacer>
									<span>
										{{ clock }}
									</span>
								</v-card-title>
								<v-card-text>
									<v-divider></v-divider>
									<v-list>
										<v-list-item
											two-line
											v-for="(i, index) in conditionsResult"
											:key="index"
										>
											<v-list-item-icon>
												<v-icon v-if="i.state === 'OK'">mdi-check</v-icon>
												<v-icon v-else-if="i.state === 'WARN'">mdi-alert</v-icon>
												<v-icon v-else>mdi-close</v-icon>
											</v-list-item-icon>
											<v-list-item-content>
												<v-list-item-title>
													{{ i.message }}
												</v-list-item-title>
												<v-list-item-subtitle v-if="!!i.description">
													{{ i.description }}
												</v-list-item-subtitle>
											</v-list-item-content>
										</v-list-item>
									</v-list>
								</v-card-text>
							</v-card>
						</v-menu>
					</v-col>
				</v-row>
				<v-row v-if="!watchdogState.running">
					<v-col cols="1"></v-col>
					<v-col>
						<p class="watchdog-note">
							{{ $t(watchdogState.errorMessage) }}
						</p>
					</v-col>
				</v-row>
			</v-card-text>
		</v-card>
	</v-app>
</template>
<script>
import Axios from 'axios';
import CheckError from './CheckError';
const WATCHDOG_API_TIME = './api/is_alive/time2';
const WATCHDOG_API_RUNNER = './api/is_alive/watchdog';

/**
 *
 * Watchdog Component
 *
 * @author radek2s <rjajko@softq.pl>
 * @version 1.0.01
 */
export default {
	props: {
		name: {
			type: String,
			default: 'IsAlive2',
		},
		interval: {
			type: Number,
			default: 10000,
		},
		wdHost: {
			type: String,
			default: null,
		},
		wdPort: {
			type: Number,
			default: null,
		},
		wdMessage: {
			type: String,
			default: null,
		},
		dpValidation: {
			type: Array,
			default: null,
		},
		dpFailure: {
			type: Boolean,
			default: false,
		},
	},

	data() {
		return {
			isAliveInterval: null,
			networkConnection: false,
			watchdogState: {
				running: true,
				errorMessage: '',
			},
			checkingConditions: false,
			conditionsResult: [],
			lastServerTime: null,
			clock: null,
			clockInterval: null,
		};
	},

	computed: {
		lastMessage() {
			return this.conditionsResult.length > 0
				? this.conditionsResult[this.conditionsResult.length - 1]
				: null;
		},
	},

	mounted() {
		this.initNetworkMonitor();
		this.isAlive();
		this.initIsAliveLoop();
		this.runClock();
	},

	destroyed() {
		this.disableIsAliveLoop();
		this.disableNetworkMonitor();
		this.stopClock();
	},

	methods: {
		//Main component loop//
		initIsAliveLoop() {
			if (!this.isAliveInterval) {
				this.isAliveInterval = setInterval(() => {
					this.isAlive();
				}, this.interval);
			}
		},

		disableIsAliveLoop() {
			clearInterval(this.isAliveInterval);
			this.isAliveInterval = null;
		},

		async isAlive() {
			try {
				this.checkingConditions = true;
				this.conditionsResult = [];
				this.isNetworkConnection();
				await this.isServerConnection();
				await this.areDataPointsValid();
				await this.notifyWatchdog();
			} catch (e) {
				console.warn(e.message);
			} finally {
				this.checkingConditions = false;
			}
		},

		// Validations and conditions //
		isNetworkConnection() {
			this.addConditionResult('Network connection', this.networkConnection);
			if (!this.networkConnection) {
				throw new Error('Network connection is not available');
			}
		},

		async isServerConnection() {
			try {
				let resp = await Axios.get(WATCHDOG_API_TIME);
				this.addConditionResult('Server connection', true);
				this.lastServerTime = new Date(resp.data).toLocaleString();
			} catch (error) {
				this.addConditionResult('Unable to connect to Server', false, error.message);
				throw new Error('Unable to connect to Server');
			}
		},

		async areDataPointsValid() {
			if (!!this.dpValidation) {
				for(let i = 0; i < this.dpValidation.length; i++) {
					try { 
						await this.validateDataPoint(this.dpValidation[i]);
					} catch (e) {
						if(e instanceof CheckError && !this.dpFailure) {
							return;
						} else {
							throw e;
						}
					}
				}
			}
		},

		// NETWORK:: Network monitor methods //
		initNetworkMonitor() {
			this.networkConnection = navigator.onLine;
			window.addEventListener('online', this.onNetworkStatusChange);
			window.addEventListener('offline', this.onNetworkStatusChange);
		},

		disableNetworkMonitor() {
			window.removeEventListener('online', this.onNetworkStatusChange);
			window.removeEventListener('offline', this.onNetworkStatusChange);
		},

		onNetworkStatusChange() {
			this.networkConnection = navigator.onLine;
			this.isAlive();
			if (this.networkConnection) {
				this.initIsAliveLoop();
			} else {
				this.disableIsAliveLoop();
			}
		},

		// DATAPOINTS:: DataPoint monitor methods //
		async validateDataPoint(datapoint) {
			try {
				let resp = await Axios.get(`./api/point_value/getValue/${datapoint.xid}`);
				if (this.checkPointCondition(datapoint, resp.data)) {
					this.addConditionResult(`${resp.data.name} (${datapoint.xid}) pass`, true);
				} else {
					this.addConditionResult(
						`${resp.data.name} (${datapoint.xid}) failed`,
						this.dpFailure ? false : 'WARN',
					);
					throw new CheckError();
				}
			} catch (error) {
				if(error instanceof CheckError) {
					console.warn('Stopping further checks');
				} else {
					this.addConditionResult(
						`DataPoint ${datapoint.xid} fetching failed`,
						false,
						error.message,
					);
				}
				throw error;
			}
		},

		async notifyWatchdog() {
			if (!!this.wdHost && !!this.wdPort) {
				try {
					if (this.lastMessage.state !== 'FAILED') {
						await Axios.post(WATCHDOG_API_RUNNER, {
							host: this.wdHost,
							port: this.wdPort,
							message: this.wdMessage || 'ping',
						});
						this.watchdogState.running = true;
						this.watchdogState.errorMessage = '';
					}
				} catch (error) {
					this.watchdogState.running = false;
					this.watchdogState.errorMessage = error.response.data;
				}
			}
		},

		addConditionResult(message, result, description = null) {
			if (typeof result === 'boolean') {
				result = result ? 'OK' : 'FAILED';
			}
			this.conditionsResult.push({
				message: message,
				state: result,
				description: description,
			});
		},

		runClock() {
			if (!this.clockInterval) {
				this.clockInterval = setInterval(() => {
					this.clock = new Date().toLocaleString();
				}, 1000);
			}
		},

		stopClock() {
			clearInterval(this.clockInterval);
			this.clockInterval = null;
		},

		/**
		 * Validate DataPoint condition
		 *
		 * @private
		 */
		checkPointCondition(datapoint, response) {
			let respValue = null;
			let binary = false;
			if (response.type === 'BinaryValue') {
				binary = true;
				respValue = response.value == 'true' ? 1 : 0;
			} else if (response.type === 'AlphanumericValue') {
				respValue = response.value;
			} else {
				respValue = Number(response.value);
			}

			if (datapoint.check === 'equal') {
				return datapoint.value === respValue;
			} else if (datapoint.check === 'not_equal') {
				return datapoint.value !== respValue;
			} else if (datapoint.check === 'greater' && !binary) {
				return datapoint.value < respValue;
			} else if (datapoint.check === 'less' && !binary) {
				return datapoint.value > respValue;
			} else if (datapoint.check === 'greater_equal' && !binary) {
				return datapoint.value <= respValue;
			} else if (datapoint.check === 'less_equal' && !binary) {
				return datapoint.value >= respValue;
			} else {
				return false;
			}
		},
	},
};
</script>
<style>
.flex-al-center {
	display: flex;
	align-items: center;
}
.is-loading {
	animation: rotate 1s linear infinite;
}
.state-block {
	border-radius: 5px;
}
.is-state-ok {
	background-color: #98e171;
}
.is-state-warn {
	background-color: #ffff74;
}
.is-state-failed {
	background-color: #ff6f6a;
}
.state-value-label {
	margin: -7px 0 !important;
	font-size: 0.8em;
	font-style: italic;
}
.watchdog-note {
	margin: 0 !important;
	font-style: italic;
}
@keyframes rotate {
	from {
		transform: rotate(0deg);
	}
	to {
		transform: rotate(360deg);
	}
}
</style>
