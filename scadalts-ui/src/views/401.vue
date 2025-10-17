<template>
	<v-container fluid class="error-page">
		<h1>Access denied!</h1>
		<p>You are not authorized to access this page.</p>
	</v-container>
</template>

<script>
export default {
	name: 'UnauthorizedPage',
};
</script>
<style>
.error-page {
    min-height: 50vh;
    display: flex;
    flex-direction: column;
    align-content: center;
    justify-content: center;
    align-items: center;
}
</style>
