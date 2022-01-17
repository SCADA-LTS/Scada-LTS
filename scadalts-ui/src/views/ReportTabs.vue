<template>
	<div class="reports">

		
		<v-card>
			<v-tabs
			v-model="tab"
			@change="changeTab"
			>
				<v-tab
					v-for="item in items"
					:key="item.tab"
					:class="{'blink':(item.tab === 'data' && newInstances)}"
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
			this.fetchIntances();
		}, 1000);
	},
	watch: { 
		$route(to) {
			switch (to.name) {
				case 'template': this.tab = 0
				 break
				case 'data': this.tab = 1 
				break
			} 
			if (this.tab === 1 ) this.newInstances = false
		},
	},
	data () {
      return {
		tab: 0,
		instances:-1,
	newInstances: false,
		items: [
			{ tab: 'template', content: 'Template' },
			{ tab: 'data', content: 'Data' },
		],
      }
    },
	methods: {
		async fetchIntances() {
			this.$store.dispatch('fetchReportInstances')
			.then((ret) => {
				if (this.instances!=-1 && this.instances < ret.length) {
					this.newInstances = true;
				}
				this.instances = ret.length
			});
		},
		changeTab(index) {
			this.$router.push({ path: `/reports/${this.items[index].tab}` });
		},
	},
};
</script>

<style lang="scss" scoped>

</style>