<template>
	<v-dialog max-width="400" persistent v-model="dialog">
		<v-card>
			<v-card-title> 
                <span>
                    Set image 
                </span>
                <v-spacer>
                </v-spacer>
                <v-btn icon @click="closeDialog">
                    <v-icon>mdi-close</v-icon>
                </v-btn>
            </v-card-title>
			<v-card-text>
                <v-row>
                    <v-col cols="12">
                        <v-text-field
                            label="Set state"
                            v-model="state"
                        ></v-text-field>
                    </v-col>
                </v-row>
            </v-card-text>
            <v-card-text v-if="errors">
                <v-row>
                    <v-col cols="12">
                        <p id="errors">{{errors}}</p>
                    </v-col>
                </v-row>
            </v-card-text>
            <v-card-actions>
                <v-spacer></v-spacer>
                <v-btn text @click="reset">
                    Reset
                </v-btn>
                <v-btn text @click="setDefault">
                    Set as default
                </v-btn>
                <v-btn text color="primary" @click="accept">
                    Set state
                </v-btn>
            </v-card-actions>
		</v-card>
	</v-dialog>
</template>
<script>
import {isInt32} from '../../../../utils/common';

export default {

    data() {
		return {
			dialog: false,
            image: null,
            state: 0,
            errors: ''
		};
	},

    methods: {
		openDialog(image) {
            this.image = image;
			this.dialog = true;
            this.state = 0;
		},
		closeDialog() {
			this.dialog = false;
			this.errors = '';
		},

        accept() {
            if(!isInt32(this.state)) {
                this.errors = 'Invalid state. State must be an integer.';
                return;
            }
            this.$emit('result', {
                image: this.image,
                state: Number(this.state),
            });
            this.closeDialog();
        },

        setDefault() {
            this.$emit('default', this.image);
            this.closeDialog();
        },

        reset() {
            this.$emit('reset', this.image);
            this.closeDialog();
        },

        cancel() {
            this.closeDialog();
        }
	},


};
</script>
<style>
    #errors {
        color: red
    }
</style>
