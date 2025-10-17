<template>
	<div class="alertsBox">
		<v-alert
			v-for="(notif, index) in scadaAlerts"
			:key="index"
			v-model="notif.value"
			dismissible
            elevation="4"
			:type="notif.type"
			:icon="notif.icon"
			:color="notif.color"
			transition="slide-x-reverse-transition"
		>
			{{ notif.text }}
		</v-alert>
	</div>
</template>
<script>
export default {
	computed: {
		scadaAlerts() {
			return this.$store.state.notificationModule.scadaNotifications;
		},
	},
};
</script>
<style scoped>
.alertsBox {
	z-index: 1000;
	position: fixed;
	right: 1vw;
	top: 80px;
	width: 30vw;
}
</style>
