<template>
	<span v-if="!!textRenderer">
		<span v-if="textRenderer.type === 'textRendererPlain'">
			{{ pointRawValue }}
			<span v-if="!!textRenderer.suffix">{{ textRenderer.suffix }}</span>
		</span>
		<span v-if="textRenderer.type === 'textRendererAnalog'">
			{{ analogValue }}
		</span>
		<span
			v-if="textRenderer.type === 'textRendererRange'"
			:style="{ color: activeColor }"
		>
			{{ rangeValue }}
		</span>
		<span
			v-if="textRenderer.type === 'textRendererBinary'"
			:style="{ color: activeColor }"
		>
			{{ binaryValue }}
		</span>
		<span
			v-if="textRenderer.type === 'textRendererMultistate'"
			:style="{ color: activeColor }"
		>
			{{ multistateValue }}
		</span>
	</span>
</template>
<script>
import DecimalFormat from 'decimal-format';

export default {
	props: {
		textRenderer: {
			type: Object,
			required: true,
		},
		pointRawValue: {
			required: true,
		},
	},

	data() {
		return {
			activeColor: 'black',
		};
	},

	computed: {
		analogValue() {
			if (!!this.textRenderer.format) {
				const df = new DecimalFormat(this.textRenderer.format);
				return df.format(this.pointRawValue);
			} else {
				return this.pointRawValue;
			}
		},

		rangeValue() {
			const v = this.pointRawValue;
			const rv = this.textRenderer.rangeValues;
			for (let i = 0; i < rv.length; i++) {
				if (v >= rv[i].from && v <= rv[i].to) {
					if (!!rv[i].colour) {
						this.activeColor = rv[i].colour;
					}
					return rv[i].text;
				}
			}
			this.activeColor = 'black';
			return this.analogValue;
		},

		binaryValue() {
			const v =
				this.pointRawValue === 'true' ||
				this.pointRawValue === 1 ||
				this.pointRawValue === true;
			if (v) {
				this.activeColor = this.textRenderer.oneColour || 'black';
				return this.textRenderer.oneLabel || 1;
			} else {
				this.activeColor = this.textRenderer.zeroColour || 'black';
				return this.textRenderer.zeroLabel || 1;
			}
		},

		multistateValue() {
			const v = Number(this.pointRawValue);
			const mv = this.textRenderer.multistateValues;
			for (let i = 0; i < mv.length; i++) {
				if (v === mv[i].key) {
					this.activeColor = mv[i].colour || 'black';
					return mv[i].text;
				}
			}
			this.activeColor = 'black';
			return this.pointRawValue;
		},
	},
};
</script>
<style></style>
