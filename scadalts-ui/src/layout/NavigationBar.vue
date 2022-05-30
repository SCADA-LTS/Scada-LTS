<template>
	<div @mouseleave="closeNavigationDrawer()">
		<v-navigation-drawer
			class="navigation-bar"
			v-if="user"
			app
			dark
			permanent
			expand-on-hover
			color="primary"
		>
			<v-list nav dense>
				<v-list-group v-model="isOpenedAlarms" prepend-icon="mdi-bell">
					<template v-slot:activator>
						<v-list-item-title>Alarms</v-list-item-title>
					</template>

					<v-list-item link href="#/alarms/scada">
						<v-list-item-icon>
							<v-icon>mdi-bell-ring</v-icon>
						</v-list-item-icon>
						<v-list-item-title>Active Alarms</v-list-item-title>
					</v-list-item>
					<v-list-item link href="#/alarm-notifications" v-if="isUserRoleAdmin">
						<v-list-item-icon>
							<v-icon>mdi-bell-circle</v-icon>
						</v-list-item-icon>
						<v-list-item-title>{{ $t('plcalarms.notification') }}</v-list-item-title>
					</v-list-item>
				</v-list-group>

				<v-list-item link href="#/watch-list">
					<v-list-item-icon>
						<v-icon>mdi-chart-line</v-icon>
					</v-list-item-icon>
					<v-list-item-title>{{ $t('watchlist.title') }} </v-list-item-title>
				</v-list-item>
				<v-list-item link href="#/graphical-view" disabled>
					<v-list-item-icon>
						<v-icon>mdi-image</v-icon>
					</v-list-item-icon>
					<v-list-item-title> Graphical Views </v-list-item-title>
					<v-chip class="v-chip-display-flex-justify-center" color="primary" x-small> +2.7.2 </v-chip>
				</v-list-item>
				<v-list-item link href="#/synoptic-panel" v-if="isUserRoleAdmin">
					<v-list-item-icon>
						<v-icon>mdi-view-dashboard</v-icon>
					</v-list-item-icon>
					<v-list-item-title>{{ $t('synopticpanels.titile') }}</v-list-item-title>
				</v-list-item>

				<v-list-item link href="#/reports">
					<v-list-item-icon>
						<v-icon>mdi-book</v-icon>
					</v-list-item-icon>
					<v-list-item-title> Reports </v-list-item-title>
				</v-list-item>

				<v-list-group v-model="isOpenedDataSources" prepend-icon="mdi-database-settings" v-if="isUserRoleAdmin">
					<template v-slot:activator>
						<v-list-item-title>Data Sources</v-list-item-title>
					</template>

					<v-list-item link href="#/datasources" v-if="isUserRoleAdmin">
						<v-list-item-icon>
							<v-icon>mdi-database</v-icon>
						</v-list-item-icon>
						<v-list-item-title>Data Sources</v-list-item-title>
					</v-list-item>

					<v-list-item link href="#/datapoint-list" v-if="isUserRoleAdmin">
						<v-list-item-icon>
							<v-icon>mdi-format-list-checkbox</v-icon>
						</v-list-item-icon>
						<v-list-item-title>{{
							$t('datapointDetails.pointList.title')
						}}</v-list-item-title>
					</v-list-item>

					<v-list-item link href="#/point-hierarchy">
						<v-list-item-icon>
							<v-icon>mdi-folder-multiple</v-icon>
						</v-list-item-icon>
						<v-list-item-title> {{ $t('pointHierarchy.title') }} </v-list-item-title>
					</v-list-item>

					<v-list-item link href="#/point-links">
						<v-list-item-icon>
							<v-icon>mdi-ray-start-vertex-end</v-icon>
						</v-list-item-icon>
						<v-list-item-title> Point Links </v-list-item-title>
					</v-list-item>
					<v-list-item link href="#/scripts">
						<v-list-item-icon>
							<v-icon>mdi-code-json</v-icon>
						</v-list-item-icon>
						<v-list-item-title> Scripting </v-list-item-title>
					</v-list-item>
				</v-list-group>

				<v-list-group v-model="isOpenedEvents" prepend-icon="mdi-cog" v-if="isUserRoleAdmin">
					<template v-slot:activator>
						<v-list-item-title>Events</v-list-item-title>
					</template>

					<v-list-item link href="#/event-handlers">
						<v-list-item-icon>
							<v-icon>mdi-cog-transfer</v-icon>
						</v-list-item-icon>
						<v-list-item-title> Event Handlers </v-list-item-title>
					</v-list-item>

					<v-list-item link href="#/scheduled-events">
						<v-list-item-icon>
							<v-icon>mdi-calendar-clock</v-icon>
						</v-list-item-icon>
						<v-list-item-title> Scheduled Events </v-list-item-title>
					</v-list-item>
					<v-list-item link href="#/compound-event-detectors">
						<v-list-item-icon>
							<v-icon>mdi-cogs</v-icon>
						</v-list-item-icon>
						<v-list-item-title> Compound Event Detectors </v-list-item-title>
					</v-list-item>
					<v-list-item link href="#/maitenance-events">
						<v-list-item-icon>
							<v-icon>mdi-wrench</v-icon>
						</v-list-item-icon>
						<v-list-item-title> Maitenance Events </v-list-item-title>
					</v-list-item>
				</v-list-group>

				<v-list-group v-model="isOpenedUsers" prepend-icon="mdi-account-box-outline" v-if="isUserRoleAdmin">
					<template v-slot:activator>
						<v-list-item-title>Users</v-list-item-title>
					</template>

					<v-list-item link href="#/users">
						<v-list-item-icon>
							<v-icon>mdi-account</v-icon>
						</v-list-item-icon>
						<v-list-item-title>
							{{ $t('userList.title') }}
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

					<v-list-item link href="#/recipient-list" v-if="isUserRoleAdmin">
						<v-list-item-icon>
							<v-icon>mdi-email-multiple</v-icon>
						</v-list-item-icon>
						<v-list-item-title>
							{{ $t('recipientlist.title') }}
						</v-list-item-title>
					</v-list-item>
				</v-list-group>

				<v-list-group v-model="isOpenedSystem" prepend-icon="mdi-monitor-dashboard" v-if="isUserRoleAdmin">
					<template v-slot:activator>
						<v-list-item-title>System</v-list-item-title>
					</template>

					<v-list-item link href="#/system-settings" v-if="isUserRoleAdmin">
						<v-list-item-icon>
							<v-icon>mdi-tune</v-icon>
						</v-list-item-icon>
						<v-list-item-title>{{ $t('systemsettings.title') }}</v-list-item-title>
					</v-list-item>

					<v-list-item link href="#/import-export">
						<v-list-item-icon>
							<v-icon>mdi-swap-vertical-circle</v-icon>
						</v-list-item-icon>
						<v-list-item-title> Import/Export </v-list-item-title>
					</v-list-item>

					<v-list-item link href="#/sql">
						<v-list-item-icon>
							<v-icon>mdi-database-edit</v-icon>
						</v-list-item-icon>
						<v-list-item-title> SQL </v-list-item-title>
					</v-list-item>

					<v-list-item link href="#/publishers">
						<v-list-item-icon>
							<v-icon>mdi-broadcast</v-icon>
						</v-list-item-icon>
						<v-list-item-title> Publishers </v-list-item-title>
					</v-list-item>
				</v-list-group>

				<v-list-item link href="./watch_list.shtm">
					<v-list-item-icon>
						<v-icon>mdi-bank</v-icon>
					</v-list-item-icon>
					<v-list-item-title>Old UI</v-list-item-title>
				</v-list-item>
			</v-list>
		</v-navigation-drawer>
	</div>
</template>

<script>
export default {
	name: 'NavigationBar',

	data() {
		return {
            isOpenedAlarms: false,
            isOpenedDataSources: false,
            isOpenedEvents: false,
            isOpenedUsers: false,
            isOpenedSystem: false,
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
	},

	methods: {
		closeNavigationDrawer() {
            this.isOpenedAlarms = false;
            this.isOpenedDataSources = false;
            this.isOpenedEvents = false;
            this.isOpenedUsers = false;
            this.isOpenedSystem = false;
		},
	},
};
</script>

<style scoped>
.navigation-bar {
	padding-top: 66px;
}
a:hover {
	text-decoration-line: none;
}
.v-application .primary--text {
	color: white !important;
}

</style>

<style>
.v-list-group--active  .v-list-group__items {
    margin-left: 10px;
}
.v-list-item--disabled {
    background-color:#8FBC8F
}
.v-chip-display-flex-justify-center span {
    display: flex;
    justify-content: center;
}
</style>
