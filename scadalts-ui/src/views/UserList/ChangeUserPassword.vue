<template>
<v-dialog max-width="500" persistent v-model="dialog">
    <template v-slot:activator="{on, attrs}">
        <v-btn v-bind="attrs" v-on="on" text elevation="1" color="primary" @click="dialog=true">
            {{ $t('userDetails.changePassword') }}
        </v-btn>
    </template>

    <v-card>
        <v-card-title>
            Change password
        </v-card-title>
        <v-card-text>
            <v-form v-model="valid" ref="form">
                <v-row>
                <v-col cols="12" v-if="!isAdmin">
                    <v-text-field  
						type="password" 
						:label="$t('userDetails.field.password.old')"
                        :rules="[ruleRequired]"
						v-model="passwordOld"
                        required
					></v-text-field>	
                </v-col>
                <v-col cols="12" v-show="showErrorMessage">
                    <v-alert
                        dense
                        dismissible
                        type="error"
                        v-model="showErrorMessage"
                    >{{$t(errorMessage)}}</v-alert>
                </v-col>
                <v-col cols="12">
                    <v-text-field  
						type="password" 
						:label="$t('userDetails.field.password')"
                        :rules="[ruleRequired]"
						v-model="password"
                        required
					></v-text-field>	
                </v-col>
                <v-col cols="12">
                    <v-text-field  
						type="password" 
						:label="$t('userDetails.field.password.repeat')"
                        :rules="[ruleRequired, rulePasswordEqual]"
						v-model="passwordRepeat"
                        required
					></v-text-field>	
                </v-col>
            </v-row>
            </v-form>            

        </v-card-text>
        <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn text @click="cancel">
				{{ $t('common.cancel') }}
			</v-btn>
			<v-btn text color="success" @click="accept" :disabled="!valid">
				{{ $t('common.ok') }}
			</v-btn>
        </v-card-actions>
    </v-card>


</v-dialog>

    
</template>
<script>
export default {

    props: ['userId'],

    data() {
		return {
            valid: false,
            dialog: false,
            password: '',
            passwordOld: '',
            passwordRepeat: '',
            errorMessage: '',
            showErrorMessage: false,
            ruleRequired: (v) => !!v || this.$t('form.validation.required'),
            rulePasswordEqual: (v) => v === this.password || this.$t('form.validation.passwordNotMatch'),
        };
	},

    computed: {
		isAdmin() {
            try {
                return this.$store.state.loggedUser.admin;
            } catch (e) {
                return false;
            }
		}
	},

    methods: {
        cancel() {
			this.dialog = false;
		},

		async accept() {
            if(this.password === this.passwordRepeat) {
                try {
                    await this.updatePassword();
                    this.dialog = false;
			        this.$emit('changed', true);
                } catch (e) {
                    if(e.status === 400) {
                        this.showErrorMessage = true;
                        this.errorMessage = e.data.description;
                    } else {
                        this.$emit('changed', false);
                    }
                }
            } else {
                this.$refs.form.validate();
            }			
		},

        updatePassword() {
            let requestData = {
                userId : this.userId,
                password: this.password,
                current: this.passwordOld,
            };
            return this.$store.dispatch('updatePassword', requestData);
        }
    }
}
</script>
<style scoped>

</style>