import Fan from './slts/Fan.vue';
import Valve from './slts/Valve.vue';
import DefaultComponent from './DefaultComponent.vue';
import WaterLevel from './slts/WaterLevel.vue';

/**
 * Custom Components Mixin
 * 
 * Import speficic custom component that 
 * can be used inside Scada-LTS Synoptic Panels.
 * 
 */
export const customComponentsMixin = {

	components: {
		Fan,
		Valve,
		'waterlevel': WaterLevel,
		'slts-default': DefaultComponent,
	},
    
	data() {
		return {
			customComponent: ['fan', 'valve', 'waterlevel'],
		};
	},
};
export default customComponentsMixin;
