<template>
	<div class="alarms">
		<v-card>
			<v-tabs
			v-model="tab"
			@change="changeTab"
			>
				<v-tab
					v-for="item in items"
					:key="item.tab"
					:class="{'blink':(item.tab === 'plc' && newAlarms)}"
				>
					{{ item.content }}
				</v-tab>
			</v-tabs>
		</v-card>				
        <router-view></router-view>

	</div>
</template>

<script>
import Components from '@min-gb/vuejs-components';

export default {
	name: 'alarmTabs',
	components: {
		...Components,
	},
	mounted() {
		this.tab = 0
		this.changeTab(this.tab)
		this.interval = setInterval(() => {
			this.getAlarms();
		}, 1000);
	},
	beforeDestroy() {
		clearInterval(this.interval)
	},
	watch: { 
		$route(to) {
			switch (to.name) {
				case 'scada': this.tab = 0
				 break
				case 'plc': this.tab = 1 
				break
				case 'plc-history': this.tab = 2 
				break 
			} 
		} 
	},
	data () {
      return {
		interval: null,
		newAlarms: 0,
		tab: 0,
		items: [
			{ tab: 'scada', content: 'Scada' },
			{ tab: 'plc', content: 'PLC' },
			{ tab: 'plc-history', content: 'PLC History' },
		],
      }
    },
	methods: {
		getAlarms() {
			this.$store.dispatch('getLiveAlarms', { offset: 0, limit: 1 })
			.then((ret) => {
				this.newAlarms = ret.length
			});
		},
		changeTab(index) {
			this.$router.push({ path: `/alarms/${this.items[index].tab}` });
		},
	},
};
</script>

<style lang="scss" scoped>

</style>