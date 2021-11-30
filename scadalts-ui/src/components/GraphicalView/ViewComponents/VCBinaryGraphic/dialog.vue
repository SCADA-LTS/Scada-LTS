<template>
	<v-dialog max-width="300" persistent v-model="dialog">
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
                    <v-col cols="6">
                        <v-btn @click="setImage(0)">
                            Zero image
                        </v-btn>
                    </v-col>
                    <v-col cols="6">
                        <v-btn @click="setImage(1)">
                            One image
                        </v-btn>
                    </v-col>
                </v-row>
            </v-card-text>
		</v-card>
	</v-dialog>
</template>
<script>
export default {

    data() {
		return {
			dialog: false,
            image: null,
		};
	},

    methods: {
		openDialog(image) {
            this.image = image;
			this.dialog = true;
		},
		closeDialog() {
			this.dialog = false;
		},

        setImage(index) {
            this.$emit('result', {
                image: this.image,
                index: index,
            });
            this.closeDialog();
        },

        cancel() {
            this.closeDialog();
        },
	},
    
    
};
</script>
<style></style>
