/* eslint-disable no-console */
<template>
	<div>
		<alert v-if="danger" id="p_is_alive_bdanger" type="danger"
			><b>Error</b> - time of the latest server response:
			{{ timeFromServerEpoch | moment }}
		</alert>
		<alert v-if="warning" id="p_is_alive_bwarning" type="warning"
			><b>Warning</b> - time of the latest server response:
			{{ timeFromServerEpoch | moment }}
		</alert>
		<alert v-if="success" id="p_is_alive" type="success">{{ label }}</alert>
		<popover title="Is Alive" target="#p_is_alive" trigger="hover">
			<template slot="popover">
				<p class="p_is_now">
					Time from web browser: <b>{{ timeInWebEpoch | moment }}</b>
				</p>
				<p class="p_is_last_time_modyfication">
					Time of the latest server response:
					<b>{{ timeFromServerEpoch | moment }}</b>
				</p>
				<p>Refresh time: {{ timeRefresh }} [ms]</p>
				<p>Time before warning: {{ timeWarningEpoch }} [ms]</p>
				<p>Time before error: {{ timeErrorEpoch }} [ms]</p>
				<p>Feedback url: {{ feedbackUrl }}</p>
			</template>
		</popover>
	</div>
</template>

<script>
import moment from 'moment';

/**
 * @author grzegorz.bylica@abilit.eu
 *
 * <div
 *    id='app-isalive'
 *    plabel='Is Alive'
 *    ptime-warning=7000
 *    ptime-refresh=3000
 *    ptime-error=11000
 *    feedback-url=''
 * />
 *
 */

const DEFAULT_REFRESH_IS_ALIVE = 2000; // 2 [s]
const REFRESH_NO_LESS_THEN_IS_ALIVE = 1000; // 1 [s]
const MINIMAL_DATE = 1; // 1 [s] unix time

export default {
	name: 'is-alive',
	props: ['plabel', 'ptimeWarning', 'ptimeError', 'ptimeRefresh', 'feedbackUrl'],
	data() {
		return {
			label: '',
			timeInWebEpoch: -1,
			timeFromServerEpoch: -1,

			timeRefresh: DEFAULT_REFRESH_IS_ALIVE, // default refresh 2 [s]
			timeWarningEpoch: -1,
			timeErrorEpoch: -1,
			feedbackUrl: '',

			danger: false,
			warning: false,
			success: true,
		};
	},
	methods: {
		setData() {
			this.timeInWebEpoch = this.$store.getters.timeInWebIsAlive;
			this.timeFromServerEpoch = this.$store.getters.timeFromServerIsAlive;

			this.danger = this.$store.getters.dangerIsAlive;
			this.warning = this.$store.getters.warningIsAlive;
			this.success = this.$store.getters.successIsAlive;
		},
		check() {
			this.$store
				.dispatch('isAlive')
				.then(() => {
					this.setData();
				})
				.catch(() => {
					this.setData();
				});
			if (this.danger) { 
			   Swal.fire( 'Server is not responding')
			} else {
				Swal.close();
			}
		},
	},
	created() {
		this.label = this.plabel;
		if (this.ptimeUpdate < REFRESH_NO_LESS_THEN_IS_ALIVE) {
			this.timeRefresh = DEFAULT_REFRESH_IS_ALIVE;
		} else {
			this.timeRefresh = this.ptimeRefresh;
		}
		this.timeWarningEpoch = this.ptimeWarning;
		this.timeErrorEpoch = this.ptimeError;

		if (this.timeRefresh == undefined) {
			this.timeRefresh = DEFAULT_UPDATE_IS_ALIVE;
		}

		this.feedbackUrl = this.feedbackUrl || '';
		this.$store.dispatch('setInitIsAlive', {
			tw: this.timeWarningEpoch,
			te: this.timeErrorEpoch,
			tr: this.timeRefresh,
			wh: this.feedbackUrl,
		});
	},
	mounted() {
		setInterval(
			function () {
				this.check();
			}.bind(this),
			this.timeRefresh,
		);
	},
	filters: {
		moment: function (epoch) {
			if (epoch > MINIMAL_DATE) {
				let date = new Date(epoch * 1000);
				return moment(date).format(' hh:mm:ss');
			} else {
				return 'waiting for data';
			}
		},
	},
};
</script>

<style>

</style>
