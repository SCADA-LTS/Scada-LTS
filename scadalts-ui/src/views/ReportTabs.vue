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
	watch: { 
		$route(to) {
			switch (to.name) {
				case 'template': this.tab = 0
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
		tab: 0,
		items: [
			{ tab: 'template', content: 'Template' },
			{ tab: 'data', content: 'Data' },
		],
      }
    },
	methods: {
	
		changeTab(index) {
			this.$router.push({ path: `/reports/${this.items[index].tab}` });
		},
	},
};
</script>

<style lang="scss" scoped>

</style>