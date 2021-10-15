<template>
	<div>
		<v-dialog max-width="500" v-model="dialogVisible" persistent>
			<v-card>
				<v-card-title>
					<h2>{{ $t('watchlist.datapoint.pointvalue.set') }} {{ pointDetails.name }}</h2>
				</v-card-title>
				<v-card-text>
					<div
						v-if="
							pointDetails.textRenderer.type === 'textRendererPlain' ||
							pointDetails.textRenderer.type === 'textRendererAnalog' ||
							pointDetails.textRenderer.type === 'textRendererRange'
						"
					>
						<v-text-field
							:label="$t('watchlist.datapoint.pointvalue.new')"
							v-model="newPointValue"
							@input="changeValue"
							:suffix="pointDetails.textRenderer.suffix"
						>
							<template v-slot:append-outer>
								<v-btn
									fab
									small
									elevation="1"
									icon
									@click="sendValue"
									color="primary"
									class="small-margin--left"
									:disabled="!changedValue"
								>
									<v-icon> mdi-send </v-icon>
								</v-btn>
							</template>
						</v-text-field>
					</div>
					<div v-else-if="pointDetails.textRenderer.type === 'textRendererBinary'">
						<v-row class="ai-center">
							<v-col cols="10">
								<v-btn-toggle v-model="newPointValue" @change="changeValue">
									<v-btn :value="false" block>
										{{ pointDetails.textRenderer.zeroLabel || 'Disabled' }}
									</v-btn>
									<v-btn :value="true" block>
										{{ pointDetails.textRenderer.oneLabel || 'Enabled' }}
									</v-btn>
								</v-btn-toggle>
							</v-col>
							<v-col cols="2">
								<v-btn
									fab
									small
									elevation="1"
									icon
									@click="sendValue"
									color="primary"
									class="small-margin--left"
									:disabled="!changedValue"
								>
									<v-icon> mdi-send </v-icon>
								</v-btn>
							</v-col>
						</v-row>
					</div>
					<div v-else-if="pointDetails.textRenderer.type === 'textRendererMultistate'">
						<v-select
							:label="$t('watchlist.datapoint.pointvalue.new')"
							v-model="newPointValue"
							:items="multistateValues"
							append-outer-icon="mdi-send"
							@click:append-outer="sendValue"
						></v-select>
					</div>
				</v-card-text>
				<v-card-actions>
					<v-btn text @click="closeDialog">
						{{ $t('common.close') }}
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
	name: 'PointValueSet',

	props: {
		pointDetails: {
			type: Object,
			required: true,
		},
		dialogVisible: {
			type: Boolean,
			default: false,
		},
	},

	data() {
		return {
			changedValue: false,
			newPointValue: null,
			oldPointValue: null,
		};
	},

	mounted() {
		if (this.pointDetails.type === 1) {
			const v = this.pointDetails.value;
			this.newPointValue = v === 'true' || v === 1 || v === true;
		} else {
			this.newPointValue = this.pointDetails.value;
			if (this.pointDetails.type !== 4) {
				this.newPointValue = Number(this.newPointValue);
			}
		}
		this.oldPointValue = JSON.parse(JSON.stringify(this.newPointValue));
	},

	computed: {
		multistateValues() {
			let items = [];
			const msv = this.pointDetails.textRenderer.multistateValues;
			if (!!msv) {
				msv.forEach((item) => {
					items.push({
						text: item.text,
						value: item.key,
					});
				});
			}
			return items;
		},
	},

	methods: {
		sendValue() {
			let request = {
				xid: this.pointDetails.xid,
				type: this.pointDetails.type,
				value: this.newPointValue,
			};

			if (request.type === 1) {
				if (request.value === true || request.value === 'true') {
					request.value = 1;
				} else if (request.value === false || request.value === 'false') {
					request.value = 0;
				}
			}

			this.$store
				.dispatch('setDataPointValue', request)
				.then(() => {
					this.newPointValue = null;
					this.oldPointValue = null;
					this.$emit('closed');
				})
				.catch((e) => {
					console.error(e);
				});
		},

		changeValue() {
			this.changedValue = this.newPointValue !== this.oldPointValue;
		},

		closeDialog() {
			this.$emit('closed');
		},
	},
};
</script>
<style scoped>
.ai-center {
	align-items: center;
}
.small-margin--left {
	margin-left: 8px;
}
</style>
