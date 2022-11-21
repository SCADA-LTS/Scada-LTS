<template>
	<v-app class="small-panel">
		<v-text-field v-model="url" placeholder="example.com" class="margin-small">
			<template v-slot:append>
				<v-btn icon @click="save()" color="success" :disabled="!enable_save" >
					<v-icon>mdi-content-save</v-icon>
				</v-btn>
			</template>
		</v-text-field>
	</v-app>
</template>

<script>
import axios from 'axios';

/**
 * @author grzegorz.bylica@gmail.com
 *
 */

export default {
	data() {
		return {
			url: '',
			url_save: '',
			enable_save: false,
		};
	},
	methods: {
		save() {
			const parts_of_domain = this.url.split('.');
			const api = `./api/systemSettings/saveSMSDomain/${parts_of_domain}`;
			axios
				.post(api)
				.then((response) => {
					this.url_save = this.url;
				})
				.catch((error) => {
					console.error(error);
				});
		},
		load() {
			const api = `./api/systemSettings/getSMSDomain`;
			axios
				.get(api)
				.then((response) => {
					this.url = response.data;
					this.url_save = response.data;
				})
				.catch((error) => {
					console.error(error);
				});
		},
	},
	mounted() {
		this.load();
	},
	watch: {
		url: function (val) {
			if (val != this.url_save) {
				this.enable_save = true;
			} else {
				this.enable_save = false;
			}
		},
		url_save: function (val) {
			if (val != this.url) {
				this.enable_save = true;
			} else {
				this.enable_save = false;
			}
		},
	},
};
</script>

<style scoped>
.number-width {
	width: 70px;
}
.move-top {
	top: -170px;
}
.format_font {
	font-size: 12px;
}
.margin-small {
	margin: 3px;
}

</style>
<style>
.small-panel .v-application--wrap {
	min-height: unset;
}
</style>