<template>
	<v-container>
        <v-alert type="error" dismissible v-if="!!errorMessage" transition="scale-transition" dense>
			{{ errorMessage }}
		</v-alert>
		<v-card class="login-form">
			<v-card-title>
				<h1>Welcome to Scada-LTS!</h1>
			</v-card-title>
			<v-form ref="loginForm" v-model="valid">
				<v-card-text>
					<v-row>
						<v-col cols="12">
							<v-text-field
								v-model="user.username"
								label="Username"
								:rules="[(v) => !!v || 'Username is required']"
								required
							></v-text-field>
						</v-col>
						<v-col cols="12">
							<v-text-field
								v-model="user.password"
								:rules="[(v) => !!v || 'Password is required']"
								:append-icon="passwordVisible ? 'mdi-eye' : 'mdi-eye-off'"
								:type="passwordVisible ? 'text' : 'password'"
								@click:append="passwordVisible = !passwordVisible"
								label="Password"
								required
							></v-text-field>
						</v-col>
					</v-row>
				</v-card-text>
				<v-card-actions>
					<v-spacer></v-spacer>
					<v-btn :disabled="!valid" block color="primary" @click="login()">Login</v-btn>
				</v-card-actions>
			</v-form>
		</v-card>

	</v-container>
</template>
<script>
export default {
	name: 'LoginPage',

	data() {
		return {
			valid: true,
			errorMessage: '',
			passwordVisible: false,
			user: {
				username: '',
				password: '',
			},
		};
	},

	methods: {
		login() {
            this.errorMessage = '';
			if (this.valid) {
				this.$store.dispatch('loginUser', this.user).then((r) => {
					if (r) {
						this.$router.push({ name: 'datapoint-list' });
					} else {
						this.errorMessage = 'User credentials are not valid!';
					}
				}).catch(()=> {
                    this.errorMessage = 'User credentials are not valid!';
                });
			}
		},
	},
};
</script>
<style scoped>
.login-form {
	margin: 0 25%;
	margin-top: 10%;
}
.login-form h1 {
	text-align: center;
}

@media (max-width: 1024px) {
	.login-form {
		margin: 0 20%;
		margin-top: 10%;
	}
	.login-form .v-card__title {
		justify-content: center;
	}
}

@media (max-width: 650px) {
	.login-form {
		margin: 0 10%;
		margin-top: 10%;
	}
}

@media (max-width: 425px) {
	.login-form {
		margin: 0;
		margin-top: 10%;
	}
	.login-form h1 {
		font-size: 26px;
	}
}
</style>
