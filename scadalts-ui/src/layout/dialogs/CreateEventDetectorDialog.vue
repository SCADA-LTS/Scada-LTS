<template>
	<v-dialog v-model="dialog" width="600">
		<template v-slot:activator="{ on, attrs }">
			<v-btn icon v-bind="attrs" v-on="on">
				<v-icon>mdi-bell-plus</v-icon>
			</v-btn>
		</template>

		<v-card>
            <v-card-title>
                Create Event Detector
            </v-card-title>
            <v-card-text>
                <v-row>
                    <v-col cols="12">
                        <v-select>
                        </v-select>
                    </v-col>
                </v-row>				
            </v-card-text>
            <v-divider></v-divider>
            <v-card-actions>
                <v-spacer></v-spacer>
                <v-btn color="primary" text @click="add">
                    {{$t('uiv.modal.ok')}}
                </v-btn>
            </v-card-actions>
			
		</v-card>
	</v-dialog>
</template>
<script>
export default {
    name: 'CreateEventDetectorDialog',

    data() {
        return {
            dialog: false,
        }
    },

    methods: {
        add() {
            console.log("added");
            this.dialog = false;
        }
    }
};
</script>
<style scoped></style>
