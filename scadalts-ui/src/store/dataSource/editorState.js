const DataTypes = {
    UNKNOWN: 0,
    BINARY: 1,
    MULTISTATE: 2,
    NUMERIC: 3,
    APLHANUMERIC: 4
}

const ALTERNATE_BOOLEAN = 1;
const BROWNIAN = 2;
const INCREMENT_ANALOG = 3;
const INCREMENT_MULTISTATE = 4;
const NO_CHANGE = 5;
const RANDOM_ANALOG = 6;
const RANDOM_BOOLEAN = 7;
const RANDOM_MULTISTATE = 8;
const ANALOG_ATTRACTOR = 9;

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
                value: ALTERNATE_BOOLEAN,
                text: 'Alternate'
            },
            {
                value: BROWNIAN,
                text: 'Brownian'
            },
            {
                value: INCREMENT_ANALOG,
                text: 'Increment'
            },
            {
                value: INCREMENT_MULTISTATE,
                text: 'Increment'
            },
            {
                value: NO_CHANGE,
                text: 'No Change'
            },
            {
                value: RANDOM_ANALOG,
                text: 'Random'
            },
            {
                value: RANDOM_BOOLEAN,
                text: 'Random'
            },
            {
                value: RANDOM_MULTISTATE,
                text: 'Random'
            },
            {
                value: ANALOG_ATTRACTOR,
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
                        t.value === ALTERNATE_BOOLEAN || 
                        t.value === NO_CHANGE || 
                        t.value === RANDOM_BOOLEAN
                    ));
                case DataTypes.MULTISTATE:
                    return state.datapointChangeTypes.filter(t => (
                        t.value === INCREMENT_MULTISTATE ||
                        t.value === NO_CHANGE ||
                        t.value === RANDOM_MULTISTATE
                    ));
                case DataTypes.NUMERIC:
                    return state.datapointChangeTypes.filter(t => (
                        t.value === BROWNIAN ||
                        t.value === INCREMENT_ANALOG ||
                        t.value === NO_CHANGE ||
                        t.value === RANDOM_ANALOG ||
                        t.value === ANALOG_ATTRACTOR
                    ));
                case DataTypes.APLHANUMERIC:
                    return state.datapointChangeTypes.filter(t => (
                        t.value === NO_CHANGE
                    ));
                default:
                    console.error("Data Point Type not recognized!");
                    return -1;
            }
        }
    },
}

export default dsDataPointEditor;