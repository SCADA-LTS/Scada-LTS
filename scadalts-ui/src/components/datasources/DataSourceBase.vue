<script>
import DataSourceLoader from './DataSourceLoader.vue';

/**
 * Base Data Source component.
 *
 * It is a base component that should be inheritated by specific
 * DataSource Types. It provide the basic methods and common structure
 * for all DataSources within New User Interface in Scada-LTS.
 *
 * @author Radoslaw Jajko <rjajko@softq.pl>
 */
export default {
	components: {
		DataSourceLoader,
	},

	props: {
		id: {
			type: Number,
			default: -1,
			required: true,
		},
	},

	data() {
		return {
			detailsLoaded: false,
			name: '',
			xid: '',
			type: 0,
			updatePeriods: 5,
			updatePeriodType: 1,
		};
	},

	methods: {
		openEditor() {
			this.$refs.dialog.editorVisible = true;
		},

		closeEditor() {
			console.debug('datasources.DataSourceBase.vue::closeEditor()');
			this.$refs.dialog.editorVisible = false;
		},

		onSaved(event) {
			console.debug('datasources.DataSourceBase.vue::onSaved()');
			this.$refs.dialog.editorVisible = false;
			this.$emit('saved', event);
		},

		/**
		 * Fetch DataSource Details Data
		 *
		 * All Data Sources has common properties like xid, name
		 * and updatePeriod and updatePeriodType. So this method is
		 * fetching that data and returns the while response JSON object.
		 *
		 * When details is ready the "DataSourceLoader" can render the data.
		 *
		 * If there was a problem the error is loged into the browser console.
		 *
		 * @returns {Object} JSON DataSource object.
		 */
		async fetchDataSourceDetails() {
			try {
				this.detailsLoaded = false;
				let response = await this.$store.dispatch('fetchDataSourceDetails', this.id);
				this.name = response.name;
				this.xid = response.xid;
				this.type = response.type;
				this.updatePeriods = response.updatePeriods;
				this.updatePeriodType = response.updatePeriodType;
				this.detailsLoaded = true;
				return response;
			} catch (e) {
				this.detailsLoaded = true;
				console.error(e);
				return null;
			}
		},
	},
};
</script>
