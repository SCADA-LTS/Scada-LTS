<template>
	<v-app>
		<NavigationBar/>

		<v-app-bar id="topbar" app dark color="primary">
			<v-list-item>
				<v-list-item-content>
					<v-list-item-title class="title"> Scada-LTS </v-list-item-title>
					<v-list-item-subtitle>
						version {{ $store.getters.appMilestone }}
					</v-list-item-subtitle>
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
import NavigationBar from '../layout/NavigationBar.vue'
export default {
	name: 'app',

	components: {
		NavigationBar
	},

	data() {
		return {};
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

	mounted() {
		if(!this.user) {
			this.$store.dispatch('getUserInfo');
		}
		this.$store.dispatch('getLocaleInfo');
		
	},

	methods: {
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
</style>
