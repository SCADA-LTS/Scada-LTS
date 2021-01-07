<template>
	<div class="panel-body">
		<div>
			<input
				type="text"
				name="url"
				id="url"
				placeholder="example.com"
				pattern=".*"
				size="30"
				required
				v-model="url"
			/>
		</div>
		<div>
			<btn v-if="enable_save" size="xs" type="success" v-on:click="save()">Save</btn>
			<btn v-if="!enable_save" id="dis_btn_sms_dom" size="xs" type="light">Save</btn>
			<tooltip
				v-if="!enable_save"
				text="I have nothing to write down"
				target="#dis_btn_sms_dom"
			/>
		</div>
	</div>
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
			const api = `./api/systemSettings/saveSMSDomain/${this.url}`;
			console.log(`test ${this.url}`);
			console.log(`api:${api}`);
			axios
				.post(api)
				.then((response) => {
					//TODO change color or disable button
					console.log(`post request ${this.url}`);
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
					console.log(`response url:${this.url}, url_save:${this.url_save}`);
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
			console.log(`watch ${val}, url_sava:${this.url_save}`);
			//do something when the data changes.
			if (val != this.url_save) {
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
</style>
