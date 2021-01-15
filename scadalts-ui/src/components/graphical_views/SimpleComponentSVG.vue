/* eslint-disable no-console */
<template>
	<div>
		<p>{{ label }}</p>
		<svg width="100" height="20">
			<rect
				width="100"
				height="20"
				style="fill: rgb(255, 255, 255); stroke-width: 2; stroke: rgb(0, 0, 0)"
			/>
			<text x="5" y="16" fill="red">{{ value }}</text>
		</svg>
	</div>
</template>
<script>
import axios from 'axios';
export default {
	name: 'simple-component-svg',
	props: ['pxidPoint', 'ptimeRefresh', 'plabel', 'pvalue'],
	data() {
		return {
			xidPoint: this.pxidPoint,
			timeRefresh: this.ptimeRefresh,
			label: this.plabel,
			value: this.pvalue,
			count: 0,
		};
	},
	methods: {
		getData() {
			const apiGetValuePoint = `./api/point_value/getValue/${this.xidPoint}`;
			axios
				.get(apiGetValuePoint)
				.then((response) => {
					this.value = response.data.value;
					this.count++;
				})
				.catch((error) => {
					console.log(error);
				});
		},
	},
	created() {
		if (this.timeRefresh != undefined) {
			setInterval(
				function () {
					this.getData();
				}.bind(this),
				this.timeRefresh,
			);
		}
	},
};
</script>

<style></style>
