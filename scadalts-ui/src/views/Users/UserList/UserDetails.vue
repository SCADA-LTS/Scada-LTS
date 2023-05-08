<template>
	<v-row v-if="!!userDetails" id="user-details">
		
		<v-col md="12" sm="12" xs="12" id="user-details-section">
			<v-row>
				<v-col v-if="!edit" cols="12" class="heading-action-buttons">
					<h2>{{ $t('userDetails.title') }}</h2>
					<v-spacer></v-spacer>
					
					
					<ChangeUserPassword 
						:userId="userDetails.id"
						@changed="onPasswordChanged"
					></ChangeUserPassword>


					<v-btn class="small-margin" fab elevation="2" color="primary" small @click="save()" :disabled="!valid">
						<v-icon>mdi-content-save</v-icon>
					</v-btn>
					
				</v-col>

				
				<v-col cols="12">
					<v-form v-model="valid" ref="form" id="user-details--form">
					<v-row>
						<v-col md="6" sm="12" xs="12">
							<v-text-field 
								id="user-form--username"
								:label="$t('userDetails.field.username')" 
								:disabled="!edit"
								@input="checkUsernameUnique"
								:rules="[ruleRequired, ruleUsernameUnique]"
								v-model="userDetails.username"
							></v-text-field>
						</v-col>

						<v-col md="6" sm="12" xs="12">
							<v-select 
								id="user-form--userprofiles"
								v-if="isAdmin"
								:label="$t('userDetails.field.userprofile')"
								:disabled="userDetails.admin"
								v-model="userDetails.userProfile"
								:items="userProfiles"
								item-text="name"
								item-value="id"
								prepend-icon="mdi-account-multiple-plus"
								@click:prepend="openUserProfileDialog"
							></v-select>

							<v-checkbox 
								v-else
								id="user-form--audit-events"
								:label="$t('userDetails.field.receiveOwnAuditEvents')"
								v-model="userDetails.receiveOwnAuditEvents"
							></v-checkbox>

						</v-col>


						<v-col v-if="edit"  md="6" sm="12" xs="12">
							<v-text-field  
								id="user-form--password"
								type="password" 
								:label="$t('userDetails.field.password')"
								v-model="userPassword"
								:rules="[ruleRequired]"
								@input="emitPassword"
							></v-text-field>	
						</v-col>
						<v-col v-if="edit"  md="6" sm="12" xs="12">
							<v-text-field  
								id="user-form--password-repeat"
								type="password" 
								:label="$t('userDetails.field.password.repeat')"
								v-model="passwordRepeat"
								:rules="[ruleRequired, rulePasswordEqual]"
								@input="emitPassword"
							></v-text-field>	
						</v-col>

						<v-col md="6" sm="12" xs="12">
							<v-text-field 
								id="user-form--first-name"
								:label="$t('userDetails.field.firstName')"
								v-model="userDetails.firstName"
							></v-text-field>
						</v-col>
						<v-col md="6" sm="12" xs="12">
							<v-text-field 
								id="user-form--last-name"
								:label="$t('userDetails.field.lastName')"
								v-model="userDetails.lastName"
							></v-text-field>
						</v-col>
						<v-col md="6" sm="12" xs="12">
							<v-text-field 
								id="user-form--email"
								:label="$t('userDetails.field.email')" 
								v-model="userDetails.email"
								:rules="emailRules"
								:disabled="!edit"
							></v-text-field>
						</v-col>
						<v-col md="6" sm="12" xs="12">
							<v-text-field 
								id="user-form--phone"
								:label="$t('userDetails.field.phone')"
								v-model="userDetails.phone"
							></v-text-field>
						</v-col>
					</v-row>
					<v-row>	
						<v-col md="2" sm="6" xs="12" v-if="isAdmin">
							<v-checkbox 
								id="user-form--admin"
								:label="$t('userDetails.field.admin')"
								:disabled="$store.state.loggedUser.username === userDetails.username"
								v-model="userDetails.admin"
							></v-checkbox>
						</v-col>
						<v-col md="2" sm="6" xs="12" v-if="isAdmin">
							<v-checkbox 
								id="user-form--disabled"
								:label="$t('userDetails.field.disabled')"
								:disabled="$store.state.loggedUser.username === userDetails.username"
								v-model="userDetails.disabled"
							></v-checkbox>
						</v-col>
						<v-col md="2" sm="6" xs="12" v-if="isAdmin">
							<v-checkbox 
								id="user-form--audit-events"
								:label="$t('userDetails.field.receiveOwnAuditEvents')"
								v-model="userDetails.receiveOwnAuditEvents"
							></v-checkbox>
						</v-col>
						<v-col md="2" sm="6" xs="12" v-if="isAdmin">
							<v-checkbox 
								id="user-form--hide-menu"
								:label="$t('userDetails.field.hideMenu')"
								v-model="userDetails.hideMenu"
								:disabled="userDetails.admin"
							></v-checkbox>
						</v-col>
						<v-col md="2" sm="6" xs="12">
							<v-checkbox
								id="user-form--enableFullScreen"
								:label="$t('userDetails.view.enableFullScreen')"
								:disabled="userDetails.forceFullScreenMode"
								v-model="userDetails.enableFullScreen"
							></v-checkbox>
						</v-col>
						<v-col md="2" sm="6" xs="12">
							<v-checkbox
								id="user-form--hideShortcutDisableFullScreen"
								:label="$t('userDetails.view.hideShortcutDisableFullScreen')"
								:disabled="userDetails.forceHideShortcutDisableFulLScreen"
								v-model="userDetails.hideShortcutDisableFullScreen"
							></v-checkbox>
						</v-col>
						<v-col cols="12" v-if="isAdmin">
							<v-text-field
								id="user-form--homeurl"
								:label="$t('userDetails.field.homeurl')"
								v-model="userDetails.homeUrl"
							></v-text-field>
						</v-col>
					</v-row>
					<v-row>
						<v-col md="6" sm="12" xs="12">
							<v-select 
								id="user-form--alarm-events"
								:label="$t('userDetails.field.receiveAlarmEmails')"
								v-model="userDetails.receiveAlarmEmails"
								:items="alarmLevels"
								item-text="label"
								item-value="id"
							></v-select>
						</v-col>
						<v-col md="6" sm="12" xs="12">
							<v-select 
								id="user-form--theme"
								:label="$t('userDetails.field.theme')"
								v-model="userDetails.theme"
								:items="themes"
							></v-select>
						</v-col>
					</v-row>
					</v-form>
				</v-col>
			</v-row>
		</v-col>

		<v-dialog v-model="userProfileDialogVisible" max-width="1000">
			<v-card>
				<v-card-title>
					{{ $t('userprofile.dialog.create.title') }}
				</v-card-title>
				<v-card-text>
					<UserProfileDetails
						ref="userProfileDialog"
						@create="onUserProfileCreation"
					></UserProfileDetails>
				</v-card-text>
				<v-card-actions>
					<v-spacer></v-spacer>
					<v-btn text @click="userProfileDialogVisible = false">
						{{$t('common.cancel')}}
					</v-btn>
					<v-btn text color="success" @click="addUserProfile">
						{{$t('common.ok')}}
					</v-btn>
				</v-card-actions>
			</v-card>
		</v-dialog>
	</v-row>
</template>
<script>
import ChangeUserPassword from './ChangeUserPassword'
import UserProfileDetails from '../UserProfiles/UserProfileDetails'
/**
 * User Details
 *
 *
 * @param userDetails Object - User detailed object
 * @param edit boolean - Enabled "edit" mode
 *
 * @author Sergio Selvaggi <sselvaggi@softq.pl>
 * @author Radoslaw Jajko <rjajko@softq.pl>
 * @version 1.0.1
 */
export default {
	name: 'UserDetails',

	components: {
		ChangeUserPassword,
		UserProfileDetails
	},

	props: {
		userDetails: {
			type: Object,
		},
		userProfiles: {
			type: Array,
			default: [],
		},
		edit: {
			type: Boolean,
			default: false,
		},
	},

	data() {
		return {
			valid: false,
			userPassword: '',
			passwordRepeat: '',
			userProfileDialogVisible: false,
			usernameUnique: true,
			ruleRequired: (v) => !!v || this.$t('form.validation.required'),
			rulePasswordEqual: (v) => v === this.userPassword || this.$t('form.validation.passwordNotMatch'),
			ruleUsernameUnique: () => this.usernameUnique || this.$t('form.validation.usernameNotUnique'),
			emailRules: [
				(v) =>
					/.+@.+\..+/.test(v) ||
					this.$t('recipientlistDetails.dialog.recipient.valid.mail'),
			],
			

			themes: [
				{
					text: 'Default',
					value: 'DEFAULT',
				},
				{
					text: 'Modern',
					value: 'MODERN',
				},
				{
					text: 'Default - Dark',
					value: 'DARK',
				},

			]
		};
	},

	computed: {
		alarmLevels() {
			return this.$store.state.alarmLevels;
		},

		isAdmin() {
            try {
                return this.$store.state.loggedUser.admin;
            } catch (e) {
                return false;
            }
		}
	},

	methods: {
		save() {
			if(this.isFormValid()) {
				this.$emit('saved');
			}
		},

		isFormValid() {
			return this.$refs.form.validate()
		},

		onPasswordChanged(result) {
			this.$emit('passwordChanged', result);
		},

		openUserProfileDialog() {
			this.userProfileDialogVisible = true;
		},

		addUserProfile() {
			this.userProfileDialogVisible = false;
			this.$refs.userProfileDialog.createUserProfile();
        },

		onUserProfileCreation(result) {
			this.$emit('userProfileCreated', result);
		},

		async checkUsernameUnique() {
			try {
				if(this.edit) {
					let resp = await this.$store.dispatch(
						'requestGet', `/users/validate?username=${this.userDetails.username}`
					);
					this.usernameUnique = resp.unique;
					this.$refs.form.validate();
				}
			} catch (e) {
				console.error("Failed to check unique of username!");
			}
		},

		emitPassword() {
			if(this.userPassword === this.passwordRepeat) {
				this.$emit('passwordInput', this.userPassword);
			}

		}

	},
};
</script>

<style scoped>

.small-margin {
	margin: 0 5px;
}
</style>
