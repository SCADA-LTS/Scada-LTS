<template>
    <v-row justify="center">
        <v-dialog v-model="dialog" persistent max-width="300">
            <template v-slot:activator="{on}">
                <v-btn fab small dark color="secondary" v-on="on">
                    <v-icon dark>mdi-delete</v-icon>
                </v-btn>
            </template>
            <v-card>
                <v-card-title class="headline">
                    Confirm delete?
                </v-card-title>
                <v-card-actions>
                    <div class="flex-grow-1"></div>
                    <v-btn color="green darken-1" text @click="dialog = false">Disagree</v-btn>
                    <v-btn color="green darken-1" text @click="accept">Agree</v-btn>
                </v-card-actions>
            </v-card>
        </v-dialog>
    </v-row>
</template>
<script>
    export default {
        data() {
            return {
                dialog: false,
            }
        },
        methods: {
            accept() {
                this.dialog = false;
                this.$emit('accepted');
            }
        }
    }
</script>
