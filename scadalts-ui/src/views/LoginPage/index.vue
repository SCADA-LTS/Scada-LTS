<template>
	<v-container>
        <v-alert type="error" dismissible v-if="!!errorMessage" transition="scale-transition" dense>
			{{ errorMessage }}
		</v-alert>
		<v-card class="login-form">
			<v-card-title>
				<img src="@/assets/slts_logo.png" alt="Scada-LTS logo text"/>
			</v-card-title>
			<v-form ref="loginForm" v-model="valid">
				<v-card-text>
					<v-row>
						<v-col cols="12">
							<v-text-field
								autofocus
								v-model="user.username"
								:label="$t('loginPage.username')"
								:rules="[(v) => !!v || $t('loginPage.validation.username')]"
								required
							></v-text-field>
						</v-col>
						<v-col cols="12">
							<v-text-field
								v-model="user.password"
								:label="$t('loginPage.password')"
								:rules="[(v) => !!v || $t('loginPage.validation.password')]"
								:append-icon="passwordVisible ? 'mdi-eye' : 'mdi-eye-off'"
								:type="passwordVisible ? 'text' : 'password'"
								@click:append="passwordVisible = !passwordVisible"
								required
							></v-text-field>
						</v-col>
					</v-row>
				</v-card-text>
				<v-card-actions>
					<v-spacer></v-spacer>
					<v-btn @click="login" :disabled="!valid" block color="primary">{{$t('loginPage.login')}}</v-btn>
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

	created() {
		window.addEventListener('keyup', this.getEnterKey);
	},

	beforeDestroy() {
		window.removeEventListener('keyup', this.getEnterKey);
	},

	methods: {
		async login() {
            this.errorMessage = '';
			if (this.valid) {
				try {
					let logged = await this.$store.dispatch('loginUser', this.user);
					if (logged) {
						this.$router.push({ name: 'datapoint-list' });
						return;
					}
					this.errorMessage = this.$t('loginPage.validation.not.valid');
				} catch (e) {
					this.errorMessage = e.message;
				}
			}
		},

		getEnterKey(e) {
			if(e.keyCode === 13) {
				this.$refs.loginForm.validate();
				this.login();
			}
		}
	},
};
</script>
<style scoped>
.login-form {
	margin: 0 35%;
	margin-top: 10%;
}
.login-form .v-card__title {
	display: flex;
	justify-content: center;
}
.login-form .v-card__title img {
	max-width: 80%;
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
}
</style>
