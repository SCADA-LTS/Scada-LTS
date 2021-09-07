<template>
	<v-row v-if="!!userDetails" id="recipient-list-details">
		
		<v-col md="12" sm="12" xs="12" id="rl-section-details">
			<v-row>
				<v-col v-if="!edit" cols="12" class="heading-action-buttons">
					<h2>{{ $t('userDetails.title') }}</h2>
					<v-spacer></v-spacer>
					
					
					<ChangeUserPassword 
						:userId="userDetails.id"
						@changed="onPasswordChanged"
					></ChangeUserPassword>


					<v-btn fab elevation="2" color="primary" small @click="save()">
						<v-icon>mdi-content-save</v-icon>
					</v-btn>
					
				</v-col>
			
				<v-col md="6" sm="12" xs="12">
					<v-text-field 
						:label="$t('userDetails.field.username')" 
						:disabled="!edit"
						:rules="[ruleRequired]"
						v-model="userDetails.username"
					></v-text-field>
				</v-col>

				<v-col md="6" sm="12" xs="12">
					<v-select 
						v-if="isAdmin"
						:label="$t('userDetails.field.userprofile')"
						:disabled="userDetails.admin"
						v-model="userDetails.userProfile"
						:items="userProfiles"
						item-text="name"
						item-value="id"
					></v-select>
					
					<v-checkbox 
						v-else
						:label="$t('userDetails.field.receiveOwnAuditEvents')"
						v-model="userDetails.receiveOwnAuditEvents"
					></v-checkbox>

				</v-col>
				
				
				<v-col v-if="edit"  md="6" sm="12" xs="12">
					<v-text-field  
						type="password" 
						:label="$t('userDetails.field.password')"
						v-model="password"
					></v-text-field>	
				</v-col>
				<v-col v-if="edit"  md="6" sm="12" xs="12">
					<v-text-field  
						type="password" 
						:label="$t('userDetails.field.password.repeat')"
						v-model="passwordRepeat"
					></v-text-field>	
				</v-col>

				<v-col md="6" sm="12" xs="12">
					<v-text-field 
						:label="$t('userDetails.field.firstName')"
						v-model="userDetails.firstName"
					></v-text-field>
				</v-col>
				<v-col md="6" sm="12" xs="12">
					<v-text-field 
						:label="$t('userDetails.field.lastName')"
						v-model="userDetails.lastName"
					></v-text-field>
				</v-col>
				<v-col md="6" sm="12" xs="12">
					<v-text-field 
						:label="$t('userDetails.field.email')" 
						v-model="userDetails.email"
						:rules="emailRules"
						:disabled="!edit"
					></v-text-field>
				</v-col>
				<v-col md="6" sm="12" xs="12">
					<v-text-field 
						:label="$t('userDetails.field.phone')"
						:rules="phoneRules"
						v-model="userDetails.phone"
					></v-text-field>
				</v-col>
			</v-row>
			<v-row>	
				<v-col md="3" sm="6" xs="12" v-if="isAdmin">
					<v-checkbox 
						:label="$t('userDetails.field.admin')"
						v-model="userDetails.admin"
					></v-checkbox>
				</v-col>
				<v-col md="3" sm="6" xs="12" v-if="isAdmin">
					<v-checkbox 
						:label="$t('userDetails.field.disabled')"
						v-model="userDetails.disabled"
					></v-checkbox>
				</v-col>
				<v-col md="3" sm="6" xs="12" v-if="isAdmin">
					<v-checkbox 
						:label="$t('userDetails.field.receiveOwnAuditEvents')"
						v-model="userDetails.receiveOwnAuditEvents"
					></v-checkbox>
				</v-col>
				<v-col md="3" sm="6" xs="12" v-if="isAdmin">
					<v-checkbox 
						:label="$t('userDetails.field.hideMenu')"
						v-model="userDetails.hideMenu"
					></v-checkbox>
				</v-col>
			</v-row>
			<v-row>
				<v-col md="6" sm="12" xs="12">
					<v-select 
						:label="$t('userDetails.field.receiveAlarmEmails')"
						v-model="userDetails.receiveAlarmEmails"
						:items="alarmLevels"
						item-text="label"
						item-value="id"
					></v-select>
				</v-col>
				<v-col md="6" sm="12" xs="12">
					<v-select 
						:label="$t('userDetails.field.theme')"
						v-model="userDetails.theme"
						:items="themes"
					></v-select>
				</v-col>
				
			</v-row>
		</v-col>
	</v-row>
</template>
<script>
import ChangeUserPassword from './ChangeUserPassword'
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
			valid: true,
			ruleRequired: (v) => !!v || this.$t('form.validation.required'),
			emailRules: [
				(v) =>
					/.+@.+\..+/.test(v) ||
					this.$t('recipientlistDetails.dialog.recipient.valid.mail'),
			],
			phoneRules: [
				(v) =>
					/\+?\d{1,4}?[-.\s]?\(?\d{1,3}?\)?[-.\s]?\d{1,4}[-.\s]?\d{1,4}[-.\s]?\d{1,9}/.test(
						v,
					) || this.$t('recipientlistDetails.dialog.recipient.valid.phone'),
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
		
		

		/**
		 * Save Recipient List
		 *
		 * After saving emit the event for parent component to handle
		 * the result of that operation
		 */
		async save() {
			this.preSave();
			let resp = await this.$store.dispatch('updateMailingList', this.recipientList);
			this.$emit('saved', resp);
			// console.log(this.convertInactiveIntervals(this.inactiveTime));
		},

		onPasswordChanged(result) {
			this.$emit('passwordChanged', result);
		}
	},
};
</script>

<style scoped>

.small-margin {
	margin: 0 5px;
}
</style>
