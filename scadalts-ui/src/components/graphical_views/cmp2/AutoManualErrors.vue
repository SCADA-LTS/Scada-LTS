<template>
	<div class="auto-manual--errors">
		<v-tabs v-model="tab" show-arrows background-color="primary" vertical dark class="my-tabs">
			<v-tab v-for="(list, key) in errorHandler" :index="key" :key="key" left>
				<v-badge
					color="error"
					:value="list[1].messages.length > 0"
					:content="list[1].messages.length"
				>
                    <v-icon>
					    {{ list[1].connection ? 'mdi-cloud-check' : 'mdi-cloud-off-outline' }}
					</v-icon>
					{{ list[0] }}
					
				</v-badge>
			</v-tab>
			<v-tabs-items v-model="tab">
				<v-tab-item v-for="(list, key) in errorHandler" :index="key" :key="key">
					<div v-if="list[1].messages.length">
						<v-list class="auto-manual--error-list">
							<v-list-item v-for="entry in list[1].messages" :key="entry.time.getTime()">
								{{ entry.time.toLocaleString() }} - {{ entry.message }}
							</v-list-item>
						</v-list>
					</div>
					<div v-else>
						<v-list class="auto-manual--error-list">
							<v-list-item> {{ $t('component.automanual.errors.empty') }} </v-list-item>
						</v-list>
					</div>
				</v-tab-item>
			</v-tabs-items>
		</v-tabs>
	</div>
</template>
<script>
/**
 * Auto Manual Errors component
 * 
 * @version 2.0.0
 * @author Radek Jajko <rjajko@softq.pl>
 */
export default {
	props: ['errorHandler'],

	data() {
		return {
			tab: null,
		};
	},
};
</script>
<style scoped>
.auto-manual--errors {
    min-width: 750px;
}
.auto-manual--error-list {
	max-height: 50vh;
	overflow: auto;
}
div.my-tabs [role="tab"] {
  justify-content: flex-start;
}
</style>
