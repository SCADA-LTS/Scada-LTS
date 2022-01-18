<script>
/**
 * Synoptic Panel Base  Component.
 * 
 * Abstract component that should not be created but must be 
 * inheritated by child components. It is providing basic methods
 * to manipulate SVG object. Use that methods or try to override them.
 * 
 * @author Radoslaw Jajko <rjajko@softq.pl>
 * @version 1.0.0
 */
export default {
	props: ['componentData', 'componentId', 'componentEditor'],
	
    data() {
		return {};
	},

	methods: {

        /**
         * Get Point Value by Export ID
         * 
         * @param {string} xid - Export ID
         */
		getDataPointValueXid(xid) {
            if(xid !== undefined) {
                return this.$store.dispatch('getDataPointValueByXid', xid);
            }
		},

        /**
         * Change SVG component color
         * 
         * @param {string} component - Component unique Id
         * @param {string} color - Hex color value
         */
		changeComponentColor(component, color) {
			let x = this.$svg.get(component).style().split(';');
			for (let i = 0; i < x.length; i++) {
				if (x[i].includes('fill:')) {
					x[i] = `fill:${color}`;
				}
			}
			this.$svg.get(component).style(x.join(';'));
		},

        /**
         * Change SVG component inner text
         * 
         * @param {string} component - Component unique Id
         * @param {string} text - Text to be provided
         */
		changeComponentText(component, text) {
			if (this.$svg.get(component).node.textContent)
				this.$svg.get(component).node.textContent = text;
		},

        /**
         * Rotate SVG component using anchor point
         * 
         * @param {string} component - Component unique Id
         * @param {number} duration - (Optional) Time in ms to perform a 360 angle rotation
         * @param {number} angle - (Optional) Angle to rotate specific component
         * @param {boolean} loop - (Optional) Is that animation looped?
         * 
         */
		rotateComponent(component, duration = 3000, angle = 360, loop = true) {
			let element = this.$svg.get(component);
			if (element.fx) {
				if (element.fx.play()) {
					element.finish();
				}
			}
			if (loop) {
				element.animate(duration).rotate(angle).loop();
			} else {
				element.animate(duration).rotate(angle);
			}
		},

        /**
         * Pause SVG component animation
         * After pausing it can be restored.
         * 
         * @param {string} component - Component unique Id
         */
		pauseComponentAnimation(component) {
			if (this.$svg.get(component).fx) this.$svg.get(component).fx.pause();
		},

        /**
         * Finish SVG component animation
         * 
         * @param {string} component - Component unique Id
         */
		finishComponentAnimation(component) {
			if (this.$svg.get(component).fx) this.$svg.get(component).fx.finish();
		},
	},
};
</script>
