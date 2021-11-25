<template>
	<v-dialog max-width="600" persistent v-model="dialog">
		<v-card>
			<v-card-title> Background settings </v-card-title>
			<v-card-text>
				<!-- IMAGE GALLERY -->
				<v-row> </v-row>

				<v-row align="center">
					<v-col cols="8">
						<v-file-input 
                            accept="image/*"
                            label="File input" 
                            show-size
                        ></v-file-input>
					</v-col>
                    <v-col cols="4">
                        <v-btn block>
                            Upload
                        </v-btn>
                    </v-col>
				</v-row>
			</v-card-text>
			<v-card-actions>
				<v-spacer></v-spacer>
				<v-btn text @click="closeDialog" color="success">
					{{ $t('common.close') }}
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
		};
	},

	methods: {

		openDialog() {
			this.dialog = true;
		},
		closeDialog() {
			this.dialog = false;
		},
	},
};
</script>
<style scoped>
</style>
