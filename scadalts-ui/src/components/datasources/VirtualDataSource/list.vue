<template>
	<div>
		<v-row v-if="datapoint.type === 'Binary'">
			<v-col cols="6"> Test </v-col>
			<v-col cols="6"> Test3 </v-col>
		</v-row>
		<v-row v-if="datapoint.type === 'Multistate'">
			<v-col cols="12"> Teerere </v-col>
		</v-row>
	</div>
</template>
<script>
export default {
	props: ['datapoint'],
};
</script>
<style scoped></style>
