import Fan from "./slts/Fan.vue";
import Valve from "./slts/Valve.vue";
import DefaultComponent from "./DefaultComponent.vue";
import WaterLevel from "./slts/WaterLevel.vue";

export const customComponentsMixin = {
    components: {
        Fan,
        Valve,
        WaterLevel,
        "slts-default": DefaultComponent
    },
    data() {
        return {
            customComponent: [
                'fan',
                'valve',
                'waterlevel'
            ]
        }
    }
};
export default customComponentsMixin;