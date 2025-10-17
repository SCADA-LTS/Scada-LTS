import VirtualDataSource from './VirtualDataSource';
import VirtualDataSourceEditor from './VirtualDataSource/config';
import VirtualDataSourcePointEditor from './VirtualDataSource/point';
import VirtualDataSourcePointList from './VirtualDataSource/list';
import VirtualDataPoint from './VirtualDataSource/VirtualDataPoint';

import MetaDataSource from './MetaDataSource';
import MetaDataSourceEditor from './MetaDataSource/config';
import MetaDataSourcePointEditor from './MetaDataSource/point';
import MetaDataSourcePointList from './MetaDataSource/list';
import MetaDataPoint from './MetaDataSource/MetaDataPoint';

import SnmpDataSource from './SnmpDataSource';
import SnmpDataSourceEditor from './SnmpDataSource/config';
import SnmpDataSourcePointEditor from './SnmpDataSource/point';
import SnmpDataSourcePointList from './SnmpDataSource/list';
import SnmpDataPoint from './SnmpDataSource/SnmpDataPoint';

import ModBusDataSource from './ModBusIpDataSource';
import ModBusDataSourceEditor from './ModBusIpDataSource/config';
import ModBusDataSourcePointEditor from './ModBusIpDataSource/point';
import ModBusDataSourcePointList from './ModBusIpDataSource/list';
import ModBusDataPoint from './ModBusIpDataSource/ModBusDataPoint';

/**
 * Data Sources Mixin
 * 
 * That mixin should be placed in every place where custom
 * DataSource components should be rendered. It contains definitions
 * for all available DataSources and their child components.
 * 
 * Remember to define the DataSource Variables in Vuex.
 * [Data Source Vuex]{@link ../../store/dataSource/index.js}
 * 
 */

export const dataSourcesMixin = {
    components: {
        'virtualdatasource': VirtualDataSource,
        'virtualdatasourceeditor': VirtualDataSourceEditor,
        'virtualdatasourcepointeditor': VirtualDataSourcePointEditor,
        'virtualdatasourcepointlist': VirtualDataSourcePointList,

        'metadatasource': MetaDataSource,
        'metadatasourceeditor': MetaDataSourceEditor,
        'metadatasourcepointeditor': MetaDataSourcePointEditor,
        'metadatasourcepointlist': MetaDataSourcePointList,

        'snmpdatasource': SnmpDataSource,
        'snmpdatasourceeditor': SnmpDataSourceEditor,
        'snmpdatasourcepointeditor': SnmpDataSourcePointEditor,
        'snmpdatasourcepointlist': SnmpDataSourcePointList,

        'modbusdatasource': ModBusDataSource,
        'modbusdatasourceeditor': ModBusDataSourceEditor,
        'modbusdatasourcepointeditor': ModBusDataSourcePointEditor,
        'modbusdatasourcepointlist': ModBusDataSourcePointList,
    },

    methods: {
        createInitialDataPoint(datasourceType, itemId) {
            console.log(datasourceType, itemId);
            switch (datasourceType) {
                case 'virtualdatasource':
                    return new VirtualDataPoint(itemId);
                case 'metadatasource':
                    return new MetaDataPoint(itemId);
                case 'snmpdatasource':
                    return new SnmpDataPoint(itemId);
                case 'modbusdatasource':
                    return new ModBusDataPoint(itemId);
                default:
                    return null;
            }
        },
    },

    computed: {
        dataSourceList() {
            return this.$store.getters.dataSourceList;
        }
    },


}

export default dataSourcesMixin;