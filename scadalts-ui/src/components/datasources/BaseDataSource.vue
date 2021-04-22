<script>
import DataSourceInfo from './DataSourceInfo';
export default {
    
    components: {
        DataSourceInfo
    },

    props: {
        id: {
            type: Number,
            required: true
        },
    },

    data() {
        return {
            detailsLoaded: false,
            name: '',
            xid: '',
            updatePeriod: 5,
            updatePeriodType: 1,
        }
    },

    methods: {
        openEditor() {
            this.$refs.dialog.editorVisible = true;
        },

        closeEditor() {
            this.$refs.dialog.editorVisible = false;
		},

        async fetchDataSourceDetails() {
            try {
                this.detailsLoaded = false;
                let response = await this.$store.dispatch('fetchDataSourceDetails', this.id);
                this.name = response.name;
                this.xid = response.xid;
                this.updatePeriod = response.updatePeriod;
                this.updatePeriodType = response.updatePeriodType;
                this.detailsLoaded = true;
                return response;
            } catch (e) {
                this.detailsLoaded = true;
                console.error(e);
                return null;
            }
        }
    }
    
}
</script>