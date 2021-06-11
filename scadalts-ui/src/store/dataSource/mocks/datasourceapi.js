/**
 * @type DataSourceAPI
 */
export const datasourceApiMocks = [
    {
        id: 0,
        xid: "MOCK_DS_01312212",
        enabled: true,
        name: "DataSourceMock VSD - 01",
        type: 1,
        connection: '5 minutes',
        description: 'Mocked VDS',
        activeEvents: 0,
        loaded: false,
        datapoints: [],
    },
    {
        id: 1,
        xid: "MOCK_DS_0154151",
        enabled: false,
        name: "DataSourceMock VSD - 02",
        type: 1,
        connection: '30 seconds',
        description: 'Mocked Disabled VDS',
        activeEvents: 4,
        loaded: false,
        datapoints: [],
    },
    {
        id: 2,
        xid: "MOCK_DS_01415115",
        enabled: true,
        name: "DataSourceMock SNMP - 01",
        type: 5,
        connection: 'localhost',
        description: 'Mocked SNMP',
        activeEvents: 1,
        loaded: false,
        datapoints: [],
    }
]

export const datasourceDetailsMocks = [
    {
        id: 0,
        xid: "MOCK_DS_01312212",
        enabled: true,
        name: "DataSourceMock VSD - 01",
        type: 1,
        connection: '5 minutes',
        description: 'Mocked VDS',
        activeEvents: 0,
        updatePeriod: 5,
        updatePeriodType: 1,
    },
    {
        id: 1,
        xid: "MOCK_DS_0154151",
        enabled: false,
        name: "DataSourceMock VSD - 02",
        type: 1,
        connection: '30 seconds',
        description: 'Mocked Disabled VDS',
        activeEvents: 2,
        updatePeriod: 5,
        updatePeriodType: 1,
    },
    {
        id: 2,
        xid: "MOCK_DS_01415115",
        enabled: true,
        name: "DataSourceMock SNMP - 01",
        type: 5,
        connection: 'localhost',
        description: 'Mocked SNMP',
        activeEvents: 1,
        updatePeriod: 5,
        updatePeriodType: 1,
        host: 'localhost',
        port:'161',
    }
]