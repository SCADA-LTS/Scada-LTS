import DataSourceConfig from './DataSourceConfig';

/**
 * DataSource Configuration Mixin
 * 
 * Base methods and properties for DataSource Creation and Configuration
 * component. Every DataSource/config.vue file should have this mixin.
 * This provide a set of methods that should be common for every DataSource.
 */
export const dataSourceConfigMixin = {

    components: {
        DataSourceConfig
    },

    props: {
        /**
         * Create Mode
         * 
         * Is component in create or in a edit mode?
         */
        createMode: {
            type: Boolean,
            default: true,
        },

        /**
         * DataSource object
         * Received from parent component
         * if not it is initalized with default values.
         */
        datasource: {
            required: false,
            type: Object,
            default: () => {
                return {
                    name: '',
                    xid: 'DS_',
                    updatePeriods: 5,
                    updatePeriodType: 1,
                };
            },
        }
    },

    methods: {

        /**
         * Hook that is invoked when dialog with component 
         * is opened and the DataSource config component is created.
         */
        onShow() {
            this.generateUniqueXid();
        },

        /**
         * Generate a unique XID for the DataSource
         * if the DataSource is in create mode.
         */
        generateUniqueXid() {
            if (this.createMode) {
                this.$store.dispatch('getUniqueDataSourceXid').then(resp => {
                    this.datasource.xid = resp;
                });
            }
        },

        cancel() {
            this.$emit('canceled');
        },

        save() {
            this.$emit('saved', this.datasource);
        },
    },
}

export default dataSourceConfigMixin;