import VirtualDataSource from './VirtualDataSource';
import VirtualDataSourceEditor from './VirtualDataSource/config';
import VirtualDataSourcePointEditor from './VirtualDataSource/point';
import VirtualDataSourcePointList from './VirtualDataSource/list';

import SnmpDataSource from './SnmpDataSource';
import SnmpDataSourceEditor from './SnmpDataSource/config';
import SnmpDataSroucePointEditor from './SnmpDataSource/point';

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

        'snmpdatasource': SnmpDataSource,
        'snmpdatasourceeditor': SnmpDataSourceEditor,
        'snmpdatasourcepointeditor': SnmpDataSroucePointEditor,
    },

    computed: {
        dataSourceList() {
            return this.$store.getters.dataSourceList;
        }
    }
}

export default dataSourcesMixin;