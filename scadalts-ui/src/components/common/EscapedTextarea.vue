<template>
	<v-textarea
		ref="inner"
		:value="decoded"
		v-bind="forwardAttrs"
		v-on="listenersWithoutInput"
		@input="onInput"
	/>
</template>

<script>
import { unescapeVueHtml } from '@/utils/common';

const decodeHtml = (s = '') => unescapeVueHtml(s);

export default {
	name: 'EscapedTextarea',
	inheritAttrs: false,
	props: {
		value: { type: String, default: '' },
	},
	computed: {
		decoded() {
			const raw = this.value == null ? '' : this.value;
			return decodeHtml(raw);
		},
		forwardAttrs() {
			const { value, modelValue, ...rest } = this.$attrs;
			return rest;
		},
		listenersWithoutInput() {
			const { input, ...others } = this.$listeners || {};
			return others;
		},
	},
	methods: {
		onInput(v) {
			this.$emit('input', v == null ? '' : String(v));
		},
		validate() {
			if (this.$refs.inner && this.$refs.inner.validate) {
				return this.$refs.inner.validate();
			}
		},
	},
};
</script>
