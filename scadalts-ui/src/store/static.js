/**
 * Static Resource Store Module
 * 
 * Provide a static resource URL destination for the application.
 * Includes a alarm flags and other static resources.
 * 
 */
const staticResourcesModule = {

    state: {
        alarmFlags: {
            1: {
                image: "images/flag_blue.png"
            },
            2: {
                image: "images/flag_yellow.png"
            },
            3: {
                image: "images/flag_orange.png"
            },
            4: {
                image: "images/flag_red.png"
            }
        }
    },

    mutations: {},

    actions: {},

    getters: {},
};

export default staticResourcesModule;