import VirtualDataSource from './VirtualDataSource';
import VirtualDataSourceEditor from './VirtualDataSource/config';
import VirtualDataSourcePointEditor from './VirtualDataSource/point';
import VirtualDataSourcePointList from './VirtualDataSource/list';

import SnmpDataSource from './SnmpDataSource';
import SnmpDataSourceEditor from './SnmpDataSource/config';
import SnmpDataSroucePointEditor from './SnmpDataSource/point';

export const dataSourcesMixin = {
    components: {
        'virtualdatasource': VirtualDataSource,
        'virtualdatasourceeditor': VirtualDataSourceEditor,
        'virtualdatasourcepointeditor': VirtualDataSourcePointEditor,
        'virtualdatasourcepointlist': VirtualDataSourcePointList,

        'snmpdatasource': SnmpDataSource,
        'snmpdatasourceeditor': SnmpDataSourceEditor,
        'snmpdatasourcepointeditor': SnmpDataSroucePointEditor,
    },

    data() {
        return {
            /* DEFINE A NEW DATASOURCE HERE */
            dataSources: [
                'virtualdatasource',
                'snmpdatasource',
            ]
        }
    },

    computed: {
        dataSourceList() {
            return this.dataSources.map(dsType => {
                return {
                    value: `${dsType}`,
                    text: this.$t(`datasource.type.${dsType}`)
                };
            });
        }
    }
}

export default dataSourcesMixin;