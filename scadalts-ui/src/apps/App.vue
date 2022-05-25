<template>
	<v-app>
		<NavigationBar/>

		<v-app-bar id="topbar" app dark color="primary">
			<v-list-item>
				<v-list-item-content>
					<v-list-item-title class="title"> Scada-LTS
						<v-icon  v-if="!wsLive" title="Offline">mdi-access-point-network-off</v-icon></v-list-item-title>
					<v-list-item-subtitle>
						version: {{ $store.getters.appMilestone }} - ui: {{ $store.getters.appVersion }}
					</v-list-item-subtitle>
				</v-list-item-content>
			</v-list-item>
			<v-list-item max-width="50" v-if="!!highestUnsilencedAlarmLevel">
				<v-list-item-content>
					<a @click="goToEvents" :style="{cursor: (this.$route.name==='scada')? 'auto':'pointer'}">
						<img v-if="highestUnsilencedAlarmLevel !== -1" :src="alarmFlags[highestUnsilencedAlarmLevel].image"/>
					</a>
				</v-list-item-content>
			</v-list-item>

			<v-spacer></v-spacer>
			<v-menu bottom rounded max-width="250" offset-y v-if="user">
				<template v-slot:activator="{ on }">
					<v-btn icon v-on="on">
						<v-icon v-show="!user.admin">mdi-account</v-icon>
						<v-icon v-show="user.admin">mdi-account-tie</v-icon>
					</v-btn>
				</template>
				<v-card>
					<v-list-item-content class="justify-center text-center">
						<v-icon v-show="!user.admin">mdi-account</v-icon>
						<v-icon v-show="user.admin">mdi-account-tie</v-icon>
						<h3>
							<span v-if="!!user.firstName && !!user.lastName">
								{{user.firstName}} {{user.lastName}}
							</span>
							<span v-else>
								{{ user.username }}
							</span>
						</h3>
						<p>{{ user.email }}</p>
						<v-divider></v-divider>
						<v-btn block text link href="#/users">
							<span>Edit profile</span>
							<v-icon>mdi-account-box</v-icon>
						</v-btn>
						<v-btn block text link @click="logout()">
							<span>Logout</span>
							<v-icon>mdi-logout</v-icon>
						</v-btn>
					</v-list-item-content>
				</v-card>
			</v-menu>
		</v-app-bar>

		<NotificationAlert/>

		<v-main>
			<v-container fluid v-if="webSocketConnected || isLoginPage">
				<router-view></router-view>
			</v-container>
			<v-skeleton-loader v-else type="article">
			</v-skeleton-loader>
		</v-main>
	</v-app>
</template>

<script>
import NavigationBar from '../layout/NavigationBar.vue'
import webSocketMixin from '@/utils/web-socket-utils';
import internetMixin from '@/utils/connection-status-utils';
import NotificationAlert from '../layout/snackbars/NotificationAlert.vue';

export default {
	name: 'app',
	mixins: [internetMixin],

	components: {
		NavigationBar,
		NotificationAlert
	},

	data() {
		return {
			wsConnectionRetires: 5,
			onAppOnline: () => {
				this.wsLive = true;
			},
			onAppOffline() {
				this.wsLive = false;
			},
			wsLive: false,
			alarmFlags: {
				1: {
					image: "images/flag_blue.png"
				},
				2: {
					image: "images/flag_yellow.png"
				},
				3: {
					image: "images/flag_orange.png"
				},
				4: {
					image: "images/flag_red.png"
				}
			},
		};
	},

	computed: {
		user() {
			return this.$store.state.loggedUser;
		},
		isUserRoleAdmin() {
			if (!!this.$store.state.loggedUser) {
				return this.$store.state.loggedUser.admin;
			} else {
				return false;
			}
		},
		highestUnsilencedAlarmLevel() {
            return this.$store.state.storeEvents.highestUnsilencedAlarmLevel;
		},
		webSocketConnected() {
			return this.$store.state.webSocketModule.webSocketConnection;
		},
		isLoginPage() {
			return this.$route.name === 'login';
		},
	},

	mounted() {
	    if(!this.user) {
    			this.$store.dispatch('getUserInfo');
    	}
		this.$store.dispatch('getLocaleInfo');
		this.$store.dispatch('getHighestUnsilencedAlarmLevel');
		this.connectToWebSocket();
	},

	destroyed() {
		this.unSubscribeAlarms();
	},

	methods: {
		subscribeForAlarms() {
			this.wsConnectionRetires = 5;
			this.alarmSubscription = this.$store.state.webSocketModule.webSocket.subscribe(`/topic/alarm`, this.getHighestAlarmLevel);
		},

		unSubscribeAlarms() {
			this.alarmSubscription.unsubscribe();
		},

		connectToWebSocket() {
			if(!!this.webSocketConnected) {
				this.subscribeForAlarms();
			} else if (!this.webSocketConnected && this.wsConnectionRetires > 0){
				this.wsConnectionRetires--;
				console.debug("Failed to connect to websocket. Remaining retries: " + this.wsConnectionRetires);
				setTimeout(this.connectToWebSocket, 1000);
			}
		},

		getHighestAlarmLevel() {
			this.$store.dispatch('getHighestUnsilencedAlarmLevel');
		},

		goToEvents() {
			if (this.$route.name !== 'scada') {
				this.$router.push({ name: 'scada' });
			}

		},
		logout() {
			this.$store.dispatch('logoutUser');
			this.$router.push({ name: 'login' });
		},
	},
};
</script>

<style scoped>
#topbar {
	left: 0 !important;
	z-index: 10;
}
</style>
<style>
#sltsContent td > button,
#sltsContent td > input,
#sltsContent td > select,
#sltsContent td > textarea {
	border-style: solid;
}
td > select,
div[id*='Content'] select,
div[id*='Content'] textarea,
#viewContent select {
	background-color: rgb(221, 221, 221);
	border: 1px solid #39b54a;
	appearance: auto;
}

.blink {
	color: white;
  animation: blinker .8s linear infinite;
}

@keyframes blinker {
  50% {
	 color: yellow;
  }
   0% {
	 color: red;
	 /* text-shadow: yellow 0px 0px 5px; */
  }
   100% {
	 color: red;
	 /* text-shadow: transparent 2px 2px 0px; */
  }
}

</style>
