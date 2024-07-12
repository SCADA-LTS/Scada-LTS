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
import {getAppLocation} from '../../../utils/common';

export default {

	data() {
		return {
			title: 'Maintenance Events',
			url: 'maintenance_events.shtm',			
		}
	},

	computed: {
		location() {
			return getAppLocation() + this.url;
		}
	}    
}
</script>