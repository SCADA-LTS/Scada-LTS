import Fan from "./slts/Fan";
import Valve from "./slts/Valve";
import DefaultComponent from "./DefaultComponent";
import Waterlevel from "./slts/Waterlevel";

let customComponentsMixin = {
    components: {
        Fan,
        Valve,
        Waterlevel,
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