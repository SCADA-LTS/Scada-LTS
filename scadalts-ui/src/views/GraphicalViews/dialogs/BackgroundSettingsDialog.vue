<template>
	<v-dialog max-width="600" persistent v-model="dialog">
		<v-card>
			<v-card-title> Background settings </v-card-title>
			<v-card-text>
				<!-- IMAGE GALLERY -->
				<v-row>
					<v-col v-for="img in images" cols="6">
						<v-card @click="selectImage(img)">
							<v-img :src="`./${img.imgUrl}`" class="gv-background--thumbnail">
							</v-img>
						</v-card>
					</v-col>

				</v-row>

				<v-row align="center">
					<v-col cols="8">
						<v-file-input 
                            accept="image/*"
                            label="File input" 
                            show-size
							ref="file"
							@change="handleFileInput"
                        ></v-file-input>
					</v-col>
                    <v-col cols="4">
                        <v-btn block @click="uploadImage">
                            Upload
                        </v-btn>
                    </v-col>
				</v-row>
			</v-card-text>
			<v-card-actions>
				<v-spacer></v-spacer>
				<v-btn text @click="reset">
					Reset
				</v-btn>
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
			file: null,
			images: [],
		};
	},

	methods: {

		openDialog() {
			this.dialog = true;
			this.fetchBackroundImages();
		},
		closeDialog() {
			this.dialog = false;
		},

		async fetchBackroundImages() {
			try {
				this.images = await this.$store.dispatch('getUploadedBackgrounds');
			} catch (e) {
				console.error(e);
			}
		},

		selectImage(img) {
			this.$emit('image-update', img);
			this.closeDialog();
		},

		handleFileInput(file){
    		this.file = file
			console.log(this.file);
  		},

		uploadImage() {
			let formData = new FormData();
			formData.append('file', this.file);
			this.$store.dispatch('uploadBackground', formData)
				.then(() => {
					this.fetchBackroundImages();
				})
				.catch(error => {
					console.error(error);
				});
		},

		reset() {
			this.$store.commit('RESET_GRAPHICAL_PAGE_BACKGROUND');
			this.closeDialog();
		}
	},
};
</script>
<style scoped>
.gv-background--thumbnail {
	width: 100%;
	object-fit: scale-down;
}
</style>
