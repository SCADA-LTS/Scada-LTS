<template>
	<div>
		<iframe
			class="scada-classic-frame"
			width="100%"
			height="100%"
			frameborder="0"
			:src="location"
		>
		</iframe>
	</div>
</template>
<script>
import browserMixin from '../../../utils/browser-utils';

export default {

	mixins: [browserMixin],

	data() {
		return {
			title: 'Compound Events',
			url: 'compound_events.shtm',			
		}
	},

	computed: {
		location() {
			return this.convertBrowserPath(this.url);
		}
	}    
}
</script>