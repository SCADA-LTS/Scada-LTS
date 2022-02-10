import Fan from './slts/Fan.vue';
import Valve from './slts/Valve.vue';
import Point from './slts/Point.vue';
import State from './slts/State.vue';
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
		Point,
		'waterlevel': WaterLevel,
		'state': State,
		'slts-default': DefaultComponent,
	},
    
	data() {
		return {
			customComponent: ['fan', 'point', 'valve', 'waterlevel', 'state'],
		};
	},
};
export default customComponentsMixin;
