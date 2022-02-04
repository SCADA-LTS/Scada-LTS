<template>
	<v-app>
		<v-card elevation="1" class="state-container">
			<v-card-text>
				<v-row
					v-if="!!lastMessage"
					class="state-block"
					v-bind:class="`is-state-${lastMessage.state.toLowerCase()}`"
				>
					<v-col xs="3" sm="2" class="flex-al-center" v-if="checkingConditions">
						<v-icon class="is-loading">mdi-refresh</v-icon>
					</v-col>
					<v-col xs="3" sm="2" class="flex-al-center" v-else>
						<v-icon v-if="lastMessage.state === 'OK'">mdi-check</v-icon>
						<v-icon v-else-if="lastMessage.state === 'INFO'">mdi-information</v-icon>
						<v-icon v-else-if="lastMessage.state === 'WARN'">mdi-alert</v-icon>
						<v-icon v-else>mdi-close</v-icon>
					</v-col>
					<v-col cols="8" class="flex-column flex-al-fs">
						<v-row class="flex-column">
							<v-col class="flex-al-fs flex-column no-pad--bottom">
								<span class="state-title"> {{ lastMessage.message }} </span>
								<span v-if="!!lastMessage.description" class="state-description">
									- {{ lastMessage.description }}
								</span>
							</v-col>
							<v-col xs="12" v-if="!!lastServerTime" class="flex-al-fs state-value-container no-pad--top">
								<span class="state-value-label">Last message: {{ lastServerTime }}</span>
							</v-col>
						</v-row>
					</v-col>
					<v-col cols="2" class="flex-al-center">
						<v-menu offset-y offset-x left min-width="400" attach>
							<template v-slot:activator="{ on, attrs }">
								<v-btn icon x-small v-bind="attrs" v-on="on">
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
												<v-icon v-if="i.state === 'OK' || i.state === 'INFO'">mdi-check</v-icon>
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
const MSG_TYPES = {
	OK: 'OK',
	INFO: 'INFO',
	WARN: 'WARN',
	ERROR: 'FAILED',
}

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
		dpBreak: {
			type: Boolean,
			default: false,
		},
		dpWarnAsFail: {
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
			failedConditions: [],
			lastServerTime: null,
			clock: null,
			clockInterval: null,
		};
	},

	computed: {
		lastMessage() {
			if (this.failedConditions.length > 0) {
				return this.failedConditions[this.failedConditions.length - 1];
			} else if (this.conditionsResult.length > 0) {
				return this.conditionsResult[this.conditionsResult.length - 1];
			}
			return null;
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
				this.failedConditions = [];
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
			this.addConditionResult('Network connection', this.networkConnection ? MSG_TYPES.OK : MSG_TYPES.ERROR);
			if (!this.networkConnection) {
				throw new Error('Network connection is not available');
			}
		},

		async isServerConnection() {
			try {
				let resp = await Axios.get(WATCHDOG_API_TIME);
				this.addConditionResult('Server connection', MSG_TYPES.OK);
				this.lastServerTime = new Date(resp.data).toLocaleString();
			} catch (error) {
				this.addConditionResult('Unable to connect to Server', MSG_TYPES.ERROR, error.message);
				throw new Error('Unable to connect to Server');
			}
		},

		async areDataPointsValid() {
			if (!!this.dpValidation) {
				for (let i = 0; i < this.dpValidation.length; i++) {
					try {
						await this.validateDataPoint(this.dpValidation[i]);
					} catch (e) {
						if (e instanceof CheckError && !this.dpWarnAsFail) {
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
				const resp = await Axios.get(`./api/point_value/getValue/${datapoint.xid}`);
				const checkResult = this.checkPointCondition(datapoint, resp.data);
				const description = this.generateCheckMessage(datapoint.check, checkResult, resp.data.value, datapoint.value);

				if (checkResult) {
					this.addConditionResult(
						`${resp.data.name} (${datapoint.xid}) - Check OK`,
						MSG_TYPES.INFO,
						description,
					);
				} else {
					this.addConditionResult(
						`${resp.data.name} (${datapoint.xid}) check failed`,
						this.dpWarnAsFail ? MSG_TYPES.ERROR : MSG_TYPES.WARN,
						description,
					);
					if (!!this.dpBreak) {
						throw new CheckError();
					}
				}
			} catch (error) {
				if (error instanceof CheckError) {
					console.warn('Stopping further checks');
				} else {
					this.addConditionResult(
						`DataPoint ${datapoint.xid} fetching failed`,
						MSG_TYPES.ERROR,
						error.message,
					);
				}
				throw error;
			}
		},

		async notifyWatchdog() {
			if (!!this.wdHost && !!this.wdPort) {
				try {
					if (this.lastMessage.state !== MSG_TYPES.ERROR) {
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

		addConditionResult(message, state, description = null) {
			if(state === MSG_TYPES.WARN || state === MSG_TYPES.ERROR) {
				this.failedConditions.push({ message, state, description });
			}
			this.conditionsResult.push({ message, state, description });
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
			let checkValue = datapoint.value;
			let binary = false;
			if (response.type === 'BinaryValue') {
				binary = true;
				checkValue = Number(datapoint.value);
				respValue = response.value == 'true' ? 1 : 0;
			} else if (response.type === 'AlphanumericValue') {
				respValue = response.value;
			} else {
				respValue = Number(response.value);
			}

			if (datapoint.check === 'equal') {
				return checkValue === respValue;
			} else if (datapoint.check === 'not_equal') {
				return checkValue !== respValue;
			} else if (datapoint.check === 'greater' && !binary) {
				return checkValue < respValue;
			} else if (datapoint.check === 'less' && !binary) {
				return checkValue > respValue;
			} else if (datapoint.check === 'greater_equal' && !binary) {
				return checkValue <= respValue;
			} else if (datapoint.check === 'less_equal' && !binary) {
				return checkValue >= respValue;
			} else {
				return false;
			}
		},

		/**
		 * Generate description message.
		 * @param {string} check - check type
		 * @param {boolean} state - check result
		 * @param {any} val - received value
		 * @param {any} ref - reference value
		 */
		generateCheckMessage(check, state, val='', ref='') {
			switch (check) {
				case 'equal':
					return state 
						? `Value ${val} is equal to ${ref}` 
						: `Value ${val} is not equal to ${ref}`;
				case 'less':
					return state
						? `Value ${val} is lesser than ${ref}` 
						: `Value ${val} is greater than ${ref}`;
				case 'greater':
					return state
						? `Value ${val} is greater than ${ref}` 
						: `Value ${val} is lesser than ${ref}`;
				case 'less_equal':
					return state
						? `Value ${val} is lesser or equal to ${ref}` 
						: `Value ${val} is not lesser or equal to ${ref}`;
				case 'greater_equal':
					return state
						? `Value ${val} is greater or equal to ${ref}` 
						: `Value ${val} is not greater or equal to ${ref}`;
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
.flex-al-fs {
	display: flex;
	align-items: flex-start;
}
.flex-al-fs.col {
	padding: 8px;
}
.flex-column {
	display: flex;
	flex-direction: column;
}
.is-loading {
	animation: rotate 1s linear infinite;
}
.state-container {
	max-width: 250px;
}
.state-block {
	border-radius: 5px;
}
.state-value-container {
	padding: 6px;
	font-size: 0.8em;
	flex-basis: unset !important;
}
.is-state-ok {
	background-color: #98e171;
}
.is-state-info {
	background-color: #00bcd4;
}
.is-state-warn {
	background-color: #ffff74;
}
.is-state-failed {
	background-color: #ff6f6a;
}
.state-title {
	font-size: 0.8em;
    line-height: initial;
    font-weight: bold;
}
.state-description {
	font-style: italic;
	font-size: 0.8em;
}
.state-value-label {
	font-size: 0.8em;
	font-style: italic;
}
.watchdog-note {
	margin: 0 !important;
	font-style: italic;
}
.no-pad--bottom {
	padding-bottom: 0 !important;
}
.no-pad--top {
	padding-top: 0 !important;
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
