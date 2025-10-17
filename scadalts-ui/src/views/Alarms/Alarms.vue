<template>
	<div class="alarms">
		<AlarmsComponent></AlarmsComponent>
	</div>
</template>

<script>
import Components from '@min-gb/vuejs-components';
import AlarmsComponent from '../../components/graphical_views/AlarmsComponent';
import EventList from './EventList.vue';

export default {
	el: '#alarms',
	name: 'alarms',
	components: {
		AlarmsComponent,
		...Components,
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
@import '../../../node_modules/@min-gb/vuejs-components/dist/min-gb.css';

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
