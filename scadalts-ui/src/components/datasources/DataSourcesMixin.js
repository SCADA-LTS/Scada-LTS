import VirtualDataSource from './VirtualDataSource';
import VirtualDataSourceEditor from './VirtualDataSource/config';
import SnmpDataSource from './SnmpDataSource';
import SnmpDataSourceEditor from './SnmpDataSource/config';

export const dataSourcesMixin = {
    components: {
        'virtualdatasource': VirtualDataSource,
        'virtualdatasourceeditor': VirtualDataSourceEditor,
        'snmpdatasource': SnmpDataSource,
        'snmpdatasourceeditor': SnmpDataSourceEditor,
    },

    data() {
        return {
            dataSourceList: [
                {
                    value: 'virtualdatasourceeditor',
                    text: "Virtual Data Source"
                },
                {
                    value: 'snmpdatasourceeditor',
                    text: "SNMP Data Source"
                },
            ],
        }
    }

    //TODO: array with ENUMS of DataSources.

}

export default dataSourcesMixin;