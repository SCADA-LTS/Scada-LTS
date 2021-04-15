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
				<p>My IP: {{ myip }} </p>
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
 * 	  pxid-entry-point='DP12312'
 *    pport-number=1234/>
 * 
 * plabel - describe
 * ptime-warning - time of non-response when a warning is shown
 * ptime-error - time of non-response when a error is shown
 * pxid-entry-point - xid of point to record of last amendment
 * pport-number - port on the localhost for sending information
 * 
 */

const DEFAULT_REFRESH_IS_ALIVE = 2000; // 2 [s]
const REFRESH_NO_LESS_THEN_IS_ALIVE = 1000; // 1 [s]
const MINIMAL_DATE = 1; // 1 [s] unix time
const DEFAULT_PORT_NUMBER = 1234;


export default {
	name: 'is-alive',
	props: ['plabel', 'ptimeWarning', 'ptimeError', 'ptimeRefresh', 'pxidEntryPoint', 'pportNumber' ],
	data() {
		return {
			label: '',
			timeInWebEpoch: -1,
			timeFromServerEpoch: -1,

			timeRefresh: DEFAULT_REFRESH_IS_ALIVE, // default refresh 2 [s]
			timeWarningEpoch: -1,
			timeErrorEpoch: -1,

			xidEntryPoint: -1,
			portNumber: -1,

			danger: false,
			warning: false,
			success: true,

			myip: '',

			error: ''
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
		},
		getIPs(callback){
			// https://github.com/diafygi/webrtc-ips   
    		let ip_dups = {};
			
			//console.log('step 0')

    		//compatibility for firefox and chrome
    		let RTCPeerConnection = window.RTCPeerConnection
        		|| window.mozRTCPeerConnection
        		|| window.webkitRTCPeerConnection;
    		let useWebKit = !!window.webkitRTCPeerConnection;

			//console.log('step 1')

    		//bypass naive webrtc blocking using an iframe
    		if(!RTCPeerConnection){
        	//NOTE: you need to have an iframe in the page right above the script tag
        	//
        	//<iframe id="iframe" sandbox="allow-same-origin" style="display: none"></iframe>
        	//<script>...getIPs called in here...
        	//
        		let win = iframe.contentWindow;
        			RTCPeerConnection = win.RTCPeerConnection
            		|| win.mozRTCPeerConnection
            		|| win.webkitRTCPeerConnection;
        			useWebKit = !!win.webkitRTCPeerConnection;
    		}

			//console.log('step 2')

    		//minimal requirements for data connection
    		let mediaConstraints = {
        		optional: [{RtpDataChannels: true}]
    		};

			//console.log('step 3')

    		let servers = {iceServers: [{urls: "stun:stun.services.mozilla.com"}]};

			//console.log('step 4')

    		//construct a new RTCPeerConnection
    		let pc = new RTCPeerConnection(servers, mediaConstraints);

			//console.log('step 5')

    		function handleCandidate(candidate){
        		//match just the IP address
        		let ip_regex = /([0-9]{1,3}(\.[0-9]{1,3}){3}|[a-f0-9]{1,4}(:[a-f0-9]{1,4}){7})/
        		let ip_addr = ip_regex.exec(candidate)[1];

        		//remove duplicates
        		if(ip_dups[ip_addr] === undefined)
            		callback(ip_addr);

        		ip_dups[ip_addr] = true;
    		}

			//console.log('step 6')

    		//listen for candidate events
    		pc.onicecandidate = function(ice){
        		//skip non-candidate events
        		if(ice.candidate)
            		handleCandidate(ice.candidate.candidate);
    		}

			//console.log('step 7')

    		//create a bogus data channel
    		pc.createDataChannel("");

			//console.log('step 8')

    		//create an offer sdp
    		pc.createOffer(function(result){
        	//trigger the stun server request
        		pc.setLocalDescription(result, function(){}, function(){});

    		}, function(){});

			//console.log('step 9')

    		//wait for a while to let everything done
    		setTimeout(function(){
        	//read candidate info from local description
        		let lines = pc.localDescription.sdp.split('\n');

        		lines.forEach(function(line){
            		if(line.indexOf('a=candidate:') === 0)
                		handleCandidate(line);
        		});
    		}, 1000);
	}

      //Test: Print the IP addresses into the console
     //getIPs(function(ip){console.log(ip);});
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


		this.$store.dispatch('setInitIsAlive', {
			tw: this.timeWarningEpoch,
			te: this.timeErrorEpoch,
			tr: this.timeRefresh,
		});

		this.getIPs(
			function(myip) { 
				this.myip = myip; 
				console.log('myip'+ myip);
			}
		);
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

<style></style>
