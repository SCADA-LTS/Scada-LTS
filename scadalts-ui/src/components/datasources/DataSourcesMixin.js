import VirtualDataSource from './VirtualDataSource/index.vue';
import VirtualDataSourceEditor from './VirtualDataSource/config.vue';
import VirtualDataSourcePointEditor from './VirtualDataSource/point.vue';
import VirtualDataSourcePointList from './VirtualDataSource/list.vue';
import VirtualDataPoint from './VirtualDataSource/VirtualDataPoint.js';

import MetaDataSource from './MetaDataSource/index.vue';
import MetaDataSourceEditor from './MetaDataSource/config.vue';
import MetaDataSourcePointEditor from './MetaDataSource/point.vue';
import MetaDataSourcePointList from './MetaDataSource/list.vue';
import MetaDataPoint from './MetaDataSource/MetaDataPoint.js';

import SnmpDataSource from './SnmpDataSource/index.vue';
import SnmpDataSourceEditor from './SnmpDataSource/config.vue';
import SnmpDataSourcePointEditor from './SnmpDataSource/point.vue';
import SnmpDataSourcePointList from './SnmpDataSource/list.vue';
import SnmpDataPoint from './SnmpDataSource/SnmpDataPoint.js';

import ModBusDataSource from './ModBusIpDataSource/index.vue';
import ModBusDataSourceEditor from './ModBusIpDataSource/config.vue';
import ModBusDataSourcePointEditor from './ModBusIpDataSource/point.vue';
import ModBusDataSourcePointList from './ModBusIpDataSource/list.vue';
import ModBusDataPoint from './ModBusIpDataSource/ModBusDataPoint.js';

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
