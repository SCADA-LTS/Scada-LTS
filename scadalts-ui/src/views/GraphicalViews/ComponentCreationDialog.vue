<template>
    <v-dialog max-width="600" persistent v-model="dialog">
        <v-card>
            <v-card-title>
            </v-card-title>
            <v-card-text>
            </v-card-text>
            <v-card-actions>
                <v-spacer></v-spacer>
                <v-btn text @click="cancel">
                    {{ $t('common.cancel') }}
                </v-btn>
                <v-btn text @click="accept" color="success">
                    {{ $t('common.ok') }}
                </v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>
    
</template>
<script>
export default {
    data() {
        return {
            dialog: false,
        }
    },

    methods: {
        openDialog() {
            this.dialog = true;
        },
        closeDialog() {
            this.dialog = false;
        },
        cancel() {
            this.closeDialog();
        },
        accept() {
            this.closeDialog();
        },
    },


}
</script>
<style scoped>

</style>