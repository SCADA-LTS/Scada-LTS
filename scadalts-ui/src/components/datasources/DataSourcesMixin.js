import VirtualDataSource from './VirtualDataSource';
import VirtualDataSourceEditor from './VirtualDataSource/config';
import VirtualDataSourcePointEditor from './VirtualDataSource/point';

import SnmpDataSource from './SnmpDataSource';
import SnmpDataSourceEditor from './SnmpDataSource/config';
import SnmpDataSroucePointEditor from './SnmpDataSource/point';

export const dataSourcesMixin = {
    components: {
        'virtualdatasource': VirtualDataSource,
        'virtualdatasourceeditor': VirtualDataSourceEditor,
        'virtualdatasourcepointeditor': VirtualDataSourcePointEditor,

        'snmpdatasource': SnmpDataSource,
        'snmpdatasourceeditor': SnmpDataSourceEditor,
        'snmpdatasourcepointeditor': SnmpDataSroucePointEditor,
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