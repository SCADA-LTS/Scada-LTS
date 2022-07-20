/**
 * @mixin
 * Utils mixin for modifing the SVG graphic.
 * 
 * @author Radoslaw Jajko <rjajko@sotfq.pl>
 * @version 1.0.0
 */
export const vectorImageOpsMixin = {
    data() {
        return {
            errorTriggered: false,
        }
    },

    methods: {
        /**
         * Change SVG component fill color
         * 
         * @param {string} component - Component unique Id
         * @param {string} color - Hex color value
         */
		changeComponentColor(component, color) {
			let styles = this.$svg.get(component).style().split(';');
            let fill = false;
			for (let i = 0; i < styles.length; i++) {
				if (styles[i].includes('fill:')) {
					styles[i] = `fill:${color}`;
                    fill = true;
				}
			}
            if(!fill) {
                styles.push(`fill:${color}`);
            }
			this.$svg.get(component).style(styles.join(';'));
		},

        /**
         * Change SVG component inner text
         * 
         * @param {string} component - Component unique Id
         * @param {string} text - Text to be provided
         */
		changeComponentText(component, text) {
            try {
                if (this.$svg.get(component).node.textContent)
				    this.$svg.get(component).node.textContent = text;
            } catch (error) {
                if(!this.errorTriggered) {
                    this.errorTriggered = true;
                    console.error("Error while changing SVG component text: ", error);
                }
            }
			
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

    }
}

export default vectorImageOpsMixin;