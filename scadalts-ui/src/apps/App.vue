<template>
	<v-app>
		<v-navigation-drawer app dark permanent expand-on-hover color="primary">
			<v-list nav dense>
				<v-list-item link href="#/alarms">
					<v-list-item-icon>
						<v-icon>mdi-bell</v-icon>
					</v-list-item-icon>
					<v-list-item-title>Alarms</v-list-item-title>
				</v-list-item>
				<v-list-item link href="#/historical-alarms">
					<v-list-item-icon>
						<v-icon>mdi-bell-sleep</v-icon>
					</v-list-item-icon>
					<v-list-item-title>Historical Alarms</v-list-item-title>
				</v-list-item>
				<v-list-item link href="#/alarm-notifications">
					<v-list-item-icon>
						<v-icon>mdi-alert-decagram</v-icon>
					</v-list-item-icon>
					<v-list-item-title>{{ $t('plcalarms.notification') }}</v-list-item-title>
				</v-list-item>
				<v-list-item link href="#/system-settings">
					<v-list-item-icon>
						<v-icon>mdi-tune</v-icon>
					</v-list-item-icon>
					<v-list-item-title>{{ $t('systemsettings.title') }}</v-list-item-title>
				</v-list-item>
				<v-list-item link href="#/about">
					<v-list-item-icon>
						<v-icon>mdi-information</v-icon>
					</v-list-item-icon>
					<v-list-item-title>About</v-list-item-title>
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
					<v-list-item-title class="title"> Scada-LTS </v-list-item-title>
					<v-list-item-subtitle> version 2.5.0 </v-list-item-subtitle>
				</v-list-item-content>
			</v-list-item>

			<v-spacer></v-spacer>
			<v-menu bottom rounded max-width="250" offset-y>
				<template v-slot:activator="{ on }">
					<v-btn icon v-on="on">
						<v-icon>mdi-account</v-icon>
					</v-btn>
				</template>
				<v-card>
					<v-list-item-content class="justify-center text-center">
						<v-icon>mdi-account</v-icon>
						<h3>Admin</h3>
						<p>admin@user.com</p>
						<v-divider></v-divider>
						<v-btn block text link href="./users.shtm">
							<span>Edit profile</span>
							<v-icon>mdi-account-box</v-icon>
						</v-btn>
						<v-btn block text link href="./logout.htm">
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

		<v-footer app> </v-footer>
	</v-app>
</template>

<script>
import store from '../store';

export default {
	name: 'app',
	components: {},
	data() {
		return {
			isUserRoleAdmin: false,
		};
	},
	mounted() {
		this.getUserRole();
	},
	methods: {
		async getUserRole() {
			this.isUserRoleAdmin = await store.dispatch('getUserRole');
		},
	},
};
</script>

<style scoped>
a:hover {
	text-decoration-line: none;
}
</style>
