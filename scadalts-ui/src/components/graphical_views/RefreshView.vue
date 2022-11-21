/* eslint-disable no-console */
<template>
	<div></div>
</template>

<script>
/**
 * @author grzegorz.bylica@abilit.eu
 */

export default {
	name: 'refresh-view',
	props: ['ptimeToCheckRefresh', 'pviewId'],
	data() {
		return {
			timeToCheckRefresh: this.ptimeToCheckRefresh,
			viewId: this.pviewId,
			lastTModification: 0,
		};
	},
	methods: {
		check() {
			this.$store
				.dispatch('checkViewModificationTime', this.viewId)
				.then((ret) => {
					if (this.lastTModification == 0) {
						this.lastTModification = ret.data.mtime;
					} else if (this.lastTModification < ret.data.mtime) {
						location.reload();
					}
				})
				.catch((err) => {
					console.log(err);
				});
		},
	},
	created() {},
	mounted() {
		if (this.timeToCheckRefresh == undefined || this.timeToCheckRefresh > 5000) {
			this.timeToCheckRefresh = 5000;
		}
		if (this.viewId != undefined && this.viewId > 0) {
			setInterval(
				function () {
					this.check(this.id);
				}.bind(this),
				this.timeToCheckRefresh,
			);
		} else {
			console.log('Err get viewId');
		}

		setInterval(
			function () {
				this.check(this.id);
			}.bind(this),
			this.timeToCheckRefresh,
		);
	},
};
</script>

<style>
td > select, div[id*='Content'] select, div[id*='Content'] textarea, #viewContent select {
	background-color: rgb(221, 221, 221);
	border: 1px solid #39B54A;
	appearance: auto;
}
</style>
