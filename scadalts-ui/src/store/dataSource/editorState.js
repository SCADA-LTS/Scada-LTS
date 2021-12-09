import { DataTypes, DataChangeTypes } from "./constants";

const dsDataPointEditor = {
    state: {
        datapointTypes: [
            { 
                value: DataTypes.BINARY,
                text: 'Binary'
            },
            { 
                value: DataTypes.MULTISTATE,
                text: 'Multistate'
            },
            { 
                value: DataTypes.NUMERIC,
                text: 'Numeric'
            },
            { 
                value: DataTypes.APLHANUMERIC,
                text: 'Alphanumeric'
            },
        ],
        //ChangeTypeVO
        datapointChangeTypes: [
            {
                value: DataChangeTypes.ALTERNATE_BOOLEAN,
                text: 'Alternate'
            },
            {
                value: DataChangeTypes.BROWNIAN,
                text: 'Brownian'
            },
            {
                value: DataChangeTypes.INCREMENT_ANALOG,
                text: 'Increment'
            },
            {
                value: DataChangeTypes.INCREMENT_MULTISTATE,
                text: 'Increment'
            },
            {
                value: DataChangeTypes.NO_CHANGE,
                text: 'No Change'
            },
            {
                value: DataChangeTypes.RANDOM_ANALOG,
                text: 'Random'
            },
            {
                value: DataChangeTypes.RANDOM_BOOLEAN,
                text: 'Random'
            },
            {
                value: DataChangeTypes.RANDOM_MULTISTATE,
                text: 'Random'
            },
            {
                value: DataChangeTypes.ANALOG_ATTRACTOR,
                text: 'Attractor'
            },
        ]

    },

    mustations: {},
    actions: {},
    getters: {
        getVirtualDatapointChangeType:(state) => (datapointType) => {
            switch(datapointType) {
                case DataTypes.BINARY:
                    return state.datapointChangeTypes.filter(t => (
                        t.value === DataChangeTypes.ALTERNATE_BOOLEAN || 
                        t.value === DataChangeTypes.NO_CHANGE || 
                        t.value === DataChangeTypes.RANDOM_BOOLEAN
                    ));
                case DataTypes.MULTISTATE:
                    return state.datapointChangeTypes.filter(t => (
                        t.value === DataChangeTypes.INCREMENT_MULTISTATE ||
                        t.value === DataChangeTypes.NO_CHANGE ||
                        t.value === DataChangeTypes.RANDOM_MULTISTATE
                    ));
                case DataTypes.NUMERIC:
                    return state.datapointChangeTypes.filter(t => (
                        t.value === DataChangeTypes.BROWNIAN ||
                        t.value === DataChangeTypes.INCREMENT_ANALOG ||
                        t.value === DataChangeTypes.NO_CHANGE ||
                        t.value === DataChangeTypes.RANDOM_ANALOG ||
                        t.value === DataChangeTypes.ANALOG_ATTRACTOR
                    ));
                case DataTypes.APLHANUMERIC:
                    return state.datapointChangeTypes.filter(t => (
                        t.value === DataChangeTypes.NO_CHANGE
                    ));
                default:
                    console.error("Data Point Type not recognized!");
                    return [];
            }
        }
    },
}

export default dsDataPointEditor;