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
	},
	data () {
      return {
		tab: 0,
		items: [
			{ tab: 'scada', content: 'Scada' },
			{ tab: 'plc', content: 'PLC' },
			{ tab: 'plc-history', content: 'PLC History' },
		],
      }
    },
	methods: {
		changeTab(index) {
			this.$router.push({ path: `/alarms/${this.items[index].tab}` });
		},
		onRouteChanged(id) {
				alert(id)
		}
	},
};
</script>

<style lang="scss" scoped>

</style>