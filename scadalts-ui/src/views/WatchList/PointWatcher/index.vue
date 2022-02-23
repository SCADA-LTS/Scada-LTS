<template>
	<div class="datapointList">
		<v-list dense>
			<draggable
				:list="pointList"
				handle=".dragHandle"
				@end="checkMove"
				@start="drag = true"
				v-bind="{ animation: 200 }"
			>
				<transition-group type="transition" :name="!drag ? 'flip-list' : null">
					<v-list-item
						class="list-item--container"
						v-for="point in pointList"
						:key="point.id"
						:class="{ disabled: !point.enabled || !point.dataSourceEnabled }"
					>
						<div class="list-item--content">
							<div class="list-item--prefix">
								<v-icon class="dragHandle">mdi-drag-vertical</v-icon>
								<v-btn
									x-small
									icon
									fab
									v-if="point.settable && permissions === 2"
									@click="showPointValueSetDialog(point)"
								>
									<v-icon>mdi-circle-edit-outline</v-icon>
								</v-btn>
							</div>
							<span class="list-item-content--dsname" v-if="!hideDataSourceName">
								{{ point.dataSourceName }}
							</span>
							<span class="list-item-content--name">
								{{ point.name }}
							</span>
							<span
								v-if="!!point.description"
								class="list-item-content--desc text--description"
							>
								({{ point.description }})
							</span>
						</div>
						<div class="list-item--value">
							<span
								v-if="point.enabled && point.dataSourceEnabled"
								class="list-item-content--value"
							>
								<transition name="slide-fade" mode="out-in">
									<PointValueRenderer
										class="list-item-content--value-number"
										:key="point.value"
										:pointRawValue="point.value"
										:textRenderer="point.textRenderer"
									>
									</PointValueRenderer>
								</transition>
								<transition name="slide-fade-2" mode="out-in">
									<span class="list-item-content--value-ts" :key="point.timestamp">
										{{ point.timestamp }}
									</span>
								</transition>
							</span>
							<span v-if="!point.enabled" class="list-item-content--value">
								{{ $t('watchlist.datapoint.disabled') }}
							</span>
							<span v-if="!point.dataSourceEnabled" class="list-item-content--value">
								{{ $t('watchlist.datapoint.ds.disabled') }}
							</span>
						</div>
						<div class="list-item--action-buttons">
							<v-menu :close-on-content-click="false" offset-y>
								<template v-slot:activator="{ on, attrs }">
									<v-badge
										v-if="!!point.events && point.events.length > 0"
										bordered
										overlap
										:color="countHighestEvent(point.events)"
										:content="countActiveEvents(point.events)"
									>
										<v-btn
											small
											icon
											fab
											v-bind="attrs"
											v-on="on"
											v-if="point.events.length > 0"
										>
											<v-icon>mdi-flag</v-icon>
										</v-btn>
									</v-badge>
								</template>
								<v-card>
									<v-card-text>
										<v-list class="event-menu-list">
											<EventScadaItem
												v-for="e in point.events"
												:key="e.id"
												:event="e"
												:pointId="point.id"
												@acknowledge="acknowledgeEvent(e, point.id)"
											></EventScadaItem>
										</v-list>
									</v-card-text>
								</v-card>
							</v-menu>
							<v-btn small icon fab @click="togglePointVisiblityOnChart(point)">
								<v-icon v-if="!point.onChart">mdi-chart-box-outline</v-icon>
								<v-icon v-else>mdi-chart-box</v-icon>
							</v-btn>
							<v-btn small icon fab :href="`#/datapoint-details/${point.id}`" v-if="isAdmin">
								<v-icon>mdi-information-outline</v-icon>
							</v-btn>
							<v-btn
								small
								icon
								fab
								@click="deletePointFromList(point)"
								v-if="permissions === 2"
							>
								<v-icon>mdi-close</v-icon>
							</v-btn>
						</div>
					</v-list-item>
				</transition-group>
			</draggable>
		</v-list>

		<PointValueSet
			v-if="activeDataPoint"
			@closed="hidePointValueSetDialog"
			:dialogVisible="showDialog"
			:pointDetails="activeDataPoint"
			:key="activeDataPoint.id"
		>
		</PointValueSet>
	</div>
</template>
<script>
import draggable from 'vuedraggable';

import PointValueSet from './PointValueSet';
import PointValueRenderer from './PointValueRenderer';

import EventScadaItem from '@layout/lists/events/EventScadaItem';
import WatchListPoint from '@models/WatchListPoint';

/**
 *
 *
 * @author Radoslaw Jajko <rjajko@softq.pl>
 * @version 1.0.0
 */
export default {
	name: 'PointWatcher',

	components: {
		draggable,
		PointValueRenderer,
		EventScadaItem,
		PointValueSet,
	},

	props: {
		dataPointList: {
			type: Array,
			required: true,
		},

		permissions: {
			type: Number,
			default: 0,
		},
		hideDataSourceName: {
			type: Boolean,
			default: false,
		},
	},

	data() {
		return {
			drag: false,
			showDialog: false,
			activeDataPoint: null,
		};
	},

	computed: {
		isAdmin() {
			try {
				return !!this.$store.state.loggedUser.admin
			} catch(e) {
				return false;
			}
			
		},
		pointList: {
			get() {
				return this.$store.state.watchListModule.pointWatcher;
			},
			set(value) {
				this.$store.commit('SET_POINT_WATCHER', value);
			}
		},
	},

	mounted() {
		this.fetchDataPointDetails();
		this.$store.state.webSocketModule.webSocket.subscribe(
			`/topic/alarm`,
			this.wsOnEventRaised
		);
	},

	watch: {
		pointList(oldValue, newValue) {
			if (oldValue.length !== newValue.length) {
				this.pointList.forEach((p) => {
					if (!p.wsEnable) {
						p.wsEnable = this.$store.state.webSocketModule.webSocket.subscribe(
							`/topic/datapoint/${p.id}/enabled`,
							this.updatePointEnabled
						);
					}

					if (p.enabled) {
						if (!p.wsValue) {
							p.wsValue = this.$store.state.webSocketModule.webSocket.subscribe(
								`/topic/datapoint/${p.id}/value`,
								this.updatePointValue
							);
						}
					}
				});
			}
		},
	},

	methods: {
		updatePointEnabled(data) {
			this.$store.commit('UPDATE_POINT_STATE', data);
		},

		wsOnEventRaised(data) {
			if (data.body === 'Event Raised') {
				this.$store.dispatch('updateWatchListEventList');
			}
		},

		updatePointValue(data) {
			this.$store.commit('UPDATE_POINT_VALUE', data);
		},

		fetchDataPointDetails() {
			let reuqests = [];
			this.dataPointList.forEach((dataPoint) => {
				reuqests.push(this.getDataPointDetails(dataPoint));
			});
			Promise.all(reuqests).then((r) => {
				r.sort((a, b) => a.order - b.order);
				this.$store.commit('SET_POINT_WATCHER', r);
			});
		},

		getDataPointDetails(dataPoint) {
			return new Promise(async (resolve, reject) => {
				try {
					const pointId = dataPoint.identifier.id;
					let point = await this.$store.dispatch('getDataPointDetails', pointId);
					console.log(this.$store);
					let pv = await this.$store.dispatch('getDataPointValue', pointId);
					let pointEvents = await this.$store.dispatch('fetchDataPointEvents', {
						datapointId: pointId,
						limit: 10,
					});
					let ds = await this.$store.dispatch('getDatasourceByXid', point.dataSourceXid);
					let map = this.$store.getters.getWatchListPointOrder;
					let pointData2 = new WatchListPoint().createWatchListPoint(
						point,
						pv,
						pointEvents,
						ds,
						map.get(String(pointId)),
						dataPoint.accessType
					);

					resolve(pointData2);
				} catch (e) {
					reject(e);
				}
			});
		},

		checkMove: function (e) {
			this.drag = false;
			console.log("moved");
			this.$store.commit('SET_POINT_MOVED', this.pointList);
		},

		countActiveEvents(eventTable) {
			if (!!eventTable && eventTable.length > 0) {
				let count = 0;
				for (let i = 0; i < eventTable.length; i++) {
					if (eventTable[i].ackTs === 0) {
						count++;
					}
				}
				return count;
			}
			return 0;
		},

		countHighestEvent(eventTable) {
			if (!!eventTable && eventTable.length > 0) {
				let level = 0;
				for (let i = 0; i < eventTable.length; i++) {
					if (eventTable[i].alarmLevel > level) {
						level = eventTable[i].alarmLevel;
					}
				}
				switch (level) {
					case 2:
						return 'yellow';
					case 3:
						return 'orange';
					case 4:
						return 'red';
					default:
						return 'blue';
				}
			}
			return 'blue';
		},

		togglePointVisiblityOnChart(point) {
			this.$store.commit('TOGGLE_POINT_VISIBILITY_ON_CHART', point);
		},

		deletePointFromList(point) {
			this.$store.commit('REMOVE_POINT_FROM_WATCHLIST', point);
			this.pointList = this.pointList.filter((p) => p.id !== point.id);
			this.$store.dispatch('updateWatchList');
		},

		showPointValueSetDialog(point) {
			this.activeDataPoint = point;
			this.showDialog = true;
		},

		hidePointValueSetDialog() {
			this.showDialog = false;
		},

		acknowledgeEvent(e, pointId) {
			this.$store.dispatch('updateWatchListEvent', pointId);
		},
	},
};
</script>
<style scoped>
.list-item--container {
	display: flex;
	justify-content: space-between;
}
.list-item--container:nth-of-type(2n + 1) {
	background-color: #458e2324;
}
.list-item--prefix {
	width: 60px;
	float: left;
}
.list-item--content {
	width: 45%;
	display: flex;
	align-items: center;
}

.list-item--value {
	width: 40%;
}
.list-item--action-buttons {
	display: flex;
	justify-content: flex-end;
	width: 15%;
	min-width: 120px;
}
.list-item--event-text {
	width: 250px;
	white-space: nowrap;
	overflow: hidden;
	text-overflow: ellipsis;
}
.list-item-content--desc {
	width: 50%;
	overflow: hidden;
	white-space: nowrap;
	text-overflow: ellipsis;
}
.list-item-content--name {
	/* width: calc(50% - 60px); */
	overflow: hidden;
	white-space: nowrap;
	text-overflow: ellipsis;
}
.list-item-content--dsname {
	color: #0000008f;
	margin-right: 5px;
}
.text--description {
	font-style: italic;
	color: #0000008f;
	margin-left: 10px;
}
.slide-fade-enter-active,
.slide-fade-2-enter-active {
	background-color: var(--v-accent-darken1);
	color: var(--v-secondary-lighten5);
	border-radius: 7px;
	transition: all 0.8s ease;
}

.list-item-content--value {
	padding: 3px;
	display: flex;
	justify-content: space-between;
	align-items: flex-end;
}
.list-item-content--value-number {
	display: block;
	width: 50%;
	text-overflow: ellipsis;
	white-space: nowrap;
	overflow: hidden;
	float: left;
}
.list-item-content--value-ts {
	font-style: italic;
	color: gray;
	display: block;
	float: left;
	padding-left: 5px;
	font-size: 11px;
}

a.v-btn:hover {
	text-decoration: none;
}

.flip-list-move {
	transition: transform 0.5s;
}
.no-move {
	transition: transform 0s;
}
.dragHandle {
	cursor: move;
}
.event-menu-list {
	max-height: 70vh;
	overflow: auto;
}

div.list-item--container.v-list-item.theme--light.disabled {
	color: gray !important;
}
</style>
