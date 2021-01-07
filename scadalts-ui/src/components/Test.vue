<template>
	<div>
		<p>TEST!!!! {{ label }}</p>
	</div>
</template>

<script>
export default {
	name: 'test',
	props: ['plabel'],
	data() {
		return {
			label: this.plabel,
		};
	},
};
</script>
