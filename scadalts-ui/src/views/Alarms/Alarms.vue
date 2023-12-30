<template>
	<div class="alarms">
		<AlarmsComponent></AlarmsComponent>
	</div>
</template>

<script>
import AlarmsComponent from '@/components/graphical_views/AlarmsComponent.vue';
import EventList from './EventList.vue';

export default {
	el: '#alarms',
	name: 'alarms',
	components: {
		AlarmsComponent
	},
	mounted() {
		this.tab = 1
	},
	data () {
      return {
      }
    },
	methods: {
	},
};
</script>

<style lang="scss" scoped>
.alarms {
	//position: absolute;
	z-index: -1;
}

.action {
	margin-top: 50px;
	margin-left: 20px;
	padding-top: 10px;
}
.action_bottom {
	padding-top: 10px;
	margin-left: 20px;
}

table {
	font-family: arial, sans-serif;
	border-collapse: collapse;
	width: 100%;
}

td,
th {
	border: 1px solid #dddddd;
	text-align: left;
	padding: 8px;
}

.activation_alarm {
	background: yellow;
}
.activation {
	color: red;
}
.inactivation {
	color: green;
}
</style>
