<template>
	<div class="datapointList">
		<v-dialog max-width="700" v-model="dialog">
			<template v-slot:activator="{ on, attrs }">
				<v-badge
					v-bind="attrs"
					v-on="on"
					bordered
					overlap
					color="warning"
					icon="mdi-content-save"
					:value="!create && $store.getters.watchListConfigChanged"
				>
					<v-btn fab elevation="1" @click="showDialog">
						<v-icon v-if="create">mdi-plus</v-icon>
						<v-icon v-else>mdi-pencil</v-icon>
					</v-btn>
				</v-badge>
			</template>
			<v-card v-if="!!watchListDetails">
				<v-card-title>
					<h2>{{ $t(`watchlist.settings.title`) }}</h2>
				</v-card-title>
				<v-card-text>
					<v-form v-model="valid" ref="form">
						<section>
							<v-row>
								<v-col cols="8">
									<v-text-field
										dense
										:label="$t(`watchlist.settings.name`)"
										v-model="watchListDetails.name"
										:rules="[ruleRequired]"
									></v-text-field>
								</v-col>
								<v-col cols="4">
									<v-text-field
										dense
										:label="$t(`common.xid`)"
										:disabled="!create"
										v-model="watchListDetails.xid"
										@input="checkXidUnique"
										:rules="[ruleRequired, ruleXidUnique]"
									></v-text-field>
								</v-col>
							</v-row>
						</section>

						<section class="watchlist-section--layout">
							<v-row dense>
								<v-col cols="8" class="watchlist-settings--layout">
									<p>{{ $t(`watchlist.settings.layout`) }}</p>
									<v-btn-toggle v-model="watchListDetails.horizontal" group>
										<v-btn :value="true">
											{{ $t(`watchlist.settings.layout.horizontal`) }}
											<v-icon> mdi-view-sequential </v-icon>
										</v-btn>
										<v-btn :value="false">
											{{ $t(`watchlist.settings.layout.vertical`) }}
											<v-icon> mdi-view-parallel </v-icon>
										</v-btn>
									</v-btn-toggle>
								</v-col>
								<v-col cols="4" v-if="!watchListDetails.horizontal">
									<v-checkbox
										:label="$t(`watchlist.settings.chartBigger`)"
										v-model="watchListDetails.biggerChart"
									>
									</v-checkbox>
								</v-col>
							</v-row>
						</section>
						<section class="watchlist-section--points">
							<v-autocomplete
								v-model="searchDataPoint"
								:label="$t(`common.search`)"
								auto-select-first
								clearable
								:loading="isDPListLoading"
								:items="datapointsList"
								prepend-icon="mdi-magnify"
								item-text="name"
								return-object
							>
								<template v-slot:item="{ item }">
									{{ item.name }}
									<span class="ph--datasource-name ph--datapoint-xid"
										>({{ item.xid }})</span
									>
								</template>

								<template v-slot:append-outer>
									<v-icon @click="addDataPointSearch">mdi-check-outline</v-icon>
								</template>
							</v-autocomplete>
							<v-treeview
								v-if="datapointHierarchy"
								:items="datapointHierarchy"
								:load-children="fetchDataPointList"
								dense
								activatable
							>
								<template v-slot:label="{ item }">
									<span v-if="!!item.dataSource" class="ph--datasource-name">
										{{ item.dataSource }}
									</span>
									<span>
										{{ item.name }}
									</span>
								</template>
								<template v-slot:prepend="{ item, open }">
									<v-icon v-if="item.folder">
										{{ open ? 'mdi-folder-open' : 'mdi-folder' }}
									</v-icon>
									<v-icon v-else>mdi-cube-outline</v-icon>
								</template>
								<template v-slot:append="{ item }">
									<v-checkbox
										v-if="!item.folder"
										dense
										v-model="item.selected"
										@change="datapointSelected(item)"
									>
									</v-checkbox>
								</template>
							</v-treeview>
						</section>
					</v-form>
				</v-card-text>
				<v-card-actions>
					<v-spacer></v-spacer>
					<v-btn text @click="closeDialog">
						{{ $t(`common.cancel`) }}
					</v-btn>
					<v-btn
						text
						v-if="create"
						color="primary"
						@click="createWatchList"
						:disabled="!valid"
					>
						{{ $t(`common.create`) }}
					</v-btn>
					<v-btn text v-else color="primary" @click="updateWatchList">
						{{ $t(`common.update`) }}
					</v-btn>
				</v-card-actions>
			</v-card>
		</v-dialog>
	</div>
</template>
<script>
/**
 *
 *
 * @author Radoslaw Jajko <rjajko@softq.pl>
 * @version 1.0.0
 */
export default {
	name: 'WatchListConfig',

	props: {
		create: {
			type: Boolean,
			default: false,
		},
	},

	data() {
		return {
			dialog: false,
			isDPListLoading: false,
			searchDataPoint: null,
			datapointsList: [],
			xidUnique: true,
			valid: false,
			ruleRequired: (v) => !!v || this.$t('form.validation.required'),
			ruleXidUnique: () => this.xidUnique || this.$t('common.snackbar.xid.not.unique'),
		};
	},

	mounted() {
		this.loadWatchListData();
	},

	computed: {
		datapointHierarchy() {
			return this.$store.state.watchListModule.datapointHierarchy;
		},

		watchListDetails: {
			get() {
				return this.$store.state.watchListModule.activeWatchList;
			},
			set(newValue) {
				return this.$store.dispatch('updateActiveWatchList', newValue);
			},
		},
	},

	methods: {
		async loadWatchListData() {
			this.isDPListLoading = true;
			this.$store.dispatch('loadWatchListPointHierarchyNode', { id: 0 });
			this.datapointsList = await this.$store.dispatch('getAllDatapoints');
			this.isDPListLoading = false;
		},

		async fetchDataPointList(item) {
			await this.$store.dispatch('loadWatchListPointHierarchyNode', {
				id: item.id.slice(1),
				parentNode: item.children,
			});
		},

		datapointSelected(item) {
			let data = {
				id: Number(item.id.slice(1)),
				xid: item.xid,
				name: item.name,
			};

			if (item.selected) {
				this.$store.commit('ADD_POINT_TO_WATCHLIST', data);
				if (!this.create) {
					this.$store.dispatch('loadWatchListDataPointDetails', data.id);
				}
			} else {
				this.$store.commit('REMOVE_POINT_FROM_WATCHLIST', data);
			}
		},

		addDataPointSearch() {
			if (!!this.searchDataPoint) {
				let data = {
					id: this.searchDataPoint.id,
					xid: this.searchDataPoint.xid,
					name: this.searchDataPoint.name,
				};
				this.$store.commit('ADD_POINT_TO_WATCHLIST', data);
				if (!this.create) {
					this.$store.dispatch('loadWatchListDataPointDetails', data.id);
				}
				this.$store.commit('CHECK_POINT_IN_PH', data.id);
				this.searchDataPoint = null;
			}
		},

		async showDialog() {
			if (this.create) {
				this.$emit("createStarted", true);
				let xid;
				try {
					xid = await this.$store.dispatch('getWatchListUniqueXid');
				} catch (e) {
					console.error('Failed to get unique WatchList XID');
				}
				this.loadWatchListData();
				this.$store.commit('SET_BLANK_ACTIVE_WATCHLIST', xid);
			}
			this.dialog = true;
		},

		closeDialog() {
			this.dialog = false;
		},

		createWatchList() {
			if (this.isFormValid()) {
				this.$emit('create');
				this.closeDialog();
			}
		},

		updateWatchList() {
			this.$emit('update');
			this.closeDialog();
		},

		isFormValid() {
			return this.$refs.form.validate();
		},

		async checkXidUnique() {
			try {
				if (this.create) {
					let resp = await this.$store.dispatch(
						'requestGet',
						`/watch-lists/validate?xid=${this.watchListDetails.xid}&id=${this.watchListDetails.id}`
					);
					this.xidUnique = resp.unique;
					this.$refs.form.validate();
				}
			} catch (e) {
				console.error('Failed to check unique of xid!');
			}
		},
	},
};
</script>
<style scoped>
.watchlist-settings--layout {
	display: flex;
	justify-content: space-between;
	align-items: baseline;
}
.watchlist-section--points {
	max-height: 50vh;
	overflow-y: auto;
	overflow-x: clip;
}
.ph--datasource-name {
	color: #0000008f;
}
.ph--datapoint-xid {
	margin-left: 5px;
}
</style>
