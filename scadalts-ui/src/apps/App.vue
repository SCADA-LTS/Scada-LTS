<template>
	<v-app>
		<v-navigation-drawer v-if="user" app dark permanent expand-on-hover color="primary">
			<v-list nav dense>
				<v-list-item link href="#/alarms/scada">
					<v-list-item-icon>
						<v-icon>mdi-bell-ring</v-icon>
					</v-list-item-icon>
					<v-list-item-title>Alarms</v-list-item-title>
				</v-list-item>
				<v-list-item link href="#/alarm-notifications">
					<v-list-item-icon>
						<v-icon>mdi-bell-circle</v-icon>
					</v-list-item-icon>
					<v-list-item-title>{{ $t('plcalarms.notification') }}</v-list-item-title>
				</v-list-item>
				<v-list-item link href="#/watch-list">
					<v-list-item-icon>
						<v-icon>mdi-chart-line</v-icon>
					</v-list-item-icon>
					<v-list-item-title>{{ $t('watchlist.title')}}
					</v-list-item-title>
				</v-list-item>
				<v-list-item link href="#/synoptic-panel" v-if="isUserRoleAdmin">
					<v-list-item-icon>
						<v-icon>mdi-view-dashboard</v-icon>
					</v-list-item-icon>
					<v-list-item-title>{{$t('synopticpanels.titile')}}</v-list-item-title>
				</v-list-item>
				<v-list-item link href="#/datasources" v-if="isUserRoleAdmin">
					<v-list-item-icon>
						<v-icon>mdi-database</v-icon>
					</v-list-item-icon>
					<v-list-item-title>
						Data Sources
					</v-list-item-title>
				</v-list-item>
				<v-list-item link href="#/datapoint-list" v-if="isUserRoleAdmin">
					<v-list-item-icon>
						<v-icon>mdi-format-list-checkbox</v-icon>
					</v-list-item-icon>
					<v-list-item-title>{{
						$t('datapointDetails.pointList.title')
					}}</v-list-item-title>
				</v-list-item>
				<v-list-item link href="#/recipient-list" v-if="isUserRoleAdmin">
					<v-list-item-icon>
						<v-icon>mdi-book-account</v-icon>
					</v-list-item-icon>
					<v-list-item-title>
						{{ $t('recipientlist.title') }}
					</v-list-item-title>
				</v-list-item>
				<v-list-item link href="#/users">
					<v-list-item-icon>
						<v-icon>mdi-account</v-icon>
					</v-list-item-icon>
					<v-list-item-title>
						{{ $t('userList.title') }}
					</v-list-item-title>
				</v-list-item>
				<v-list-item link href="#/reports">
					<v-list-item-icon>
						<v-icon>mdi-book-multiple</v-icon>
					</v-list-item-icon>
					<v-list-item-title>
						{{ $t('reports.title') }}
					</v-list-item-title>
				</v-list-item>
				<v-list-item link href="#/user-profiles" v-if="isUserRoleAdmin">
					<v-list-item-icon>
						<v-icon>mdi-account-group</v-icon>
					</v-list-item-icon>
					<v-list-item-title>
						{{ $t('userprofiles.title') }}
					</v-list-item-title>
				</v-list-item>
				<v-list-item link href="#/system-settings" v-if="isUserRoleAdmin">
					<v-list-item-icon>
						<v-icon>mdi-tune</v-icon>
					</v-list-item-icon>
					<v-list-item-title>{{ $t('systemsettings.title') }}</v-list-item-title>
				</v-list-item>
				<v-list-item link href="./watch_list.shtm">
					<v-list-item-icon>
						<v-icon>mdi-bank</v-icon>
					</v-list-item-icon>
					<v-list-item-title>Old UI</v-list-item-title>
				</v-list-item>
			</v-list>
		</v-navigation-drawer>

		<v-app-bar app dark color="primary">
			<v-list-item>
				<v-list-item-content>
					<v-list-item-title class="title"> Scada-LTS
						<v-icon  v-if="!wsLive" title="Offline">mdi-access-point-network-off</v-icon></v-list-item-title>
					<v-list-item-subtitle>
						version {{ $store.getters.appMilestone }}
					</v-list-item-subtitle>
				</v-list-item-content>
			</v-list-item>
			<v-list-item max-width="50">
				<v-list-item-content>
					<a @click="goToEvents" :style="{cursor: (this.$route.name==='scada')? 'auto':'pointer'}">
						<img v-if="highestUnsilencedAlarmLevel != -1" :src="alarmFlags[highestUnsilencedAlarmLevel].image"/>
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

		<v-main>
			<v-container fluid>
				<router-view></router-view>
			</v-container>
		</v-main>
	</v-app>
</template>

<script>
import webSocketMixin from '@/utils/web-socket-utils';
import internetMixin from '@/utils/connection-status-utils';

export default {
	name: 'app',
	mixins: [webSocketMixin, internetMixin],

	data() {
		return {
			onAppOnline: () => {
				this.wsLive = true;
			},
			onAppOffline() {
				this.wsLive = false;
			},
			wsCallback: () => {
				this.wsLive = true;
				this.wsSubscribeTopic(`alarm`, async(x) => {
					await this.$store.dispatch('getHighestUnsilencedAlarmLevel');
				});
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
		}
	},

	async mounted() {
	    if(!this.user) {
    			this.$store.dispatch('getUserInfo');
    	}
		this.$store.dispatch('getLocaleInfo');
		await this.$store.dispatch('getHighestUnsilencedAlarmLevel');
	},

	methods: {
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
a:hover {
	text-decoration-line: none;
}
</style>
<style>
td > button,
td > input,
td > select,
td > textarea {
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
