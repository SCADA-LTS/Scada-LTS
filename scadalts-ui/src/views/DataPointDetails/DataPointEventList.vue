<template>
	<v-card>
		<v-card-title> Events </v-card-title>
		<v-card-text>
			<v-list>
				<v-list-item v-for="e in events" :key="e.id">
                    <v-list-item-icon>
                        <img
								v-if="e.alarmLevel === 1"
								src="images/flag_blue.png"
								title="Information"
								alt="Information"
							/>
							<img
								v-if="e.alarmLevel === 2"
								src="images/flag_yellow.png"
								title="Urgent"
								alt="Urgent"
							/>
							<img
								v-if="e.alarmLevel === 3"
								src="images/flag_orange.png"
								title="Critical"
								alt="Critical"
							/>
							<img
								v-if="e.alarmLevel === 4"
								src="images/flag_red.png"
								title="Life Safety"
								alt="Life Safety"
							/>
                    </v-list-item-icon>
					<v-list-item-content>
						<v-list-item-title>
							
							<span>
								{{ e.message }}
							</span>
						</v-list-item-title>
                        <v-list-item-subtitle>
                            {{new Date(e.activeTs).toLocaleString()}}
                        </v-list-item-subtitle>
					</v-list-item-content>
                    <v-list-item-action>
                        <v-icon>
                            mdi-comment-plus
                        </v-icon>
                        <v-icon>
                            mdi-check
                        </v-icon>
                    </v-list-item-action>
				</v-list-item>
			</v-list>
		</v-card-text>
	</v-card>
</template>
<script>
export default {
	name: 'DataPointEventList',

	props: ['datapointId'],

	data() {
		return {
			events: undefined,
		};
	},

	mounted() {
		this.fetchDataPointEvents(this.datapointId);
	},

	methods: {
		async fetchDataPointEvents(datapointId) {
			if (!!datapointId) {
				this.events = await this.$store.dispatch('fetchDataPointEvents', datapointId);
			}
		},
	},
};
</script>
<style scoped></style>
