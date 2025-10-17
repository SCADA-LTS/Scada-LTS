<script>
import svgMixin from './SvgOperationsMixin';
/**
 * Synoptic Panel Base  Component.
 * 
 * Abstract component that should not be created but must be 
 * inheritated by child components. It is providing basic methods
 * to manipulate SVG object. Use that methods or try to override them.
 * 
 * @author Radoslaw Jajko <rjajko@softq.pl>
 * @version 1.1.0
 */
export default {
	props: ['componentData', 'componentId', 'componentEditor'],

    mixins: [svgMixin], // <-- Here are all SVG manipulation functions
	
    data() {
		return {
            pointDefinition: null,
            valueSubscription: null,
            enableSubcription: null,
        };
	},

    beforeDestroy() {
        this.unSubscribeForDataPointValue();
        this.unSubscribeForDataPointEnabled();
        this.onComponentDestroy();
    },

	methods: {

        /**
         * Get Point Value by Export ID
         * 
         * @param {string} xid - Export ID
         */
		getDataPointValueXid(xid) {
            if(xid !== undefined) {
                return this.$store.dispatch('getDataPointValueByXid', xid);
            }
		},        

        //TODO: Think about moving this mechanism to the Vuex store.
        //      There may be a lot of components that are subscribed
        //      for the same data point. It is better to have a single
        //      subscription for all of them and only notify the components
        //      that are interested in the data point value.

        /**
         * Subscribe for Data Point details
         * 
         * This method try to subscribe for web socket endpoints
         * to receive the latest data point value and enabled status.
         * If the data point is not enabled, the component will not 
         * be subscribed for the value updates. Using this approach
         * we can avoid sending a lot of requests to the server.
         * 
         */
        async subscribeToWebSocket() {
            if(!!this.componentData.data && !!this.componentData.data.pointXid) {
                
                this.pointDefinition = await this.$store.dispatch('getDataPointValueByXid', this.componentData.data.pointXid);
                this.onPointValueUpdate(this.pointDefinition.value);
                this.onPointEnabledUpdate(this.pointDefinition.enabled);
                this.enableSubcription = this.$store.state.webSocketModule.webSocket.subscribe(
                    `/topic/datapoint/${this.pointDefinition.id}/enabled`,
                    this.componentEnabled
                );
                if(this.pointDefinition.enabled) {
                    this.subscribeForDataPointValue();
                }
            }
        },

        /**
         * Point Enabled Status Update
         * 
         * This method is invoked when the data point enabled
         * status is updated. If user want to take action on
         * the status change, he can override this method in 
         * the child component.
         * 
         * @param {boolean} enabled - Enabled status
         */
        onPointEnabledUpdate(enabled) {
            console.warn("onPointEnabledUpdate method is not implemented!");
        },

        /**
         * Point Value Update
         * 
         * This method is invoked when the data point value
         * has changed. If user want to take action on
         * the value change, he can override this method in 
         * the child component.
         * 
         * @param {string} value - Latest Data Point Value
         */
        onPointValueUpdate(value) {
			console.warn("onPointValueUpdate method is not implemented!");
		},

        /**
         * On Component Destroy
         * 
         * This method is invoked when the data component 
         * is destroyed. Because this component is using
         * the "beforeDestroy()" hook to unsubscribe websocket
         * connections we should not override that method by child.
         * So if user want to take action on component destroy 
         * he can override this method in the child component.
         */
        onComponentDestroy() {},

        // --- PRIVATE METHODS --- //

        /**
         * Subscribe for Data Point Value
         * @private
         * 
         * Creates a subscription for the data point value
         * if it is not already subscribed.
         */
        subscribeForDataPointValue() {
            if(!this.valueSubscription) {
                this.valueSubscription = this.$store.state.webSocketModule.webSocket.subscribe(
                    `/topic/datapoint/${this.pointDefinition.id}/value`,
                    this.updateDataPointValue
                );
            }
        },

        /**
         * Unsubscribe for Data Point Value
         * @private
         */
        unSubscribeForDataPointValue() {
            if(!!this.valueSubscription) {
                this.valueSubscription.unsubscribe();
                this.valueSubscription = null;
            }
        },

        /**
         * Unsubscribe for Data Point Enabled
         * @private
         */
        unSubscribeForDataPointEnabled() {
            if(!!this.enableSubcription) {
                this.enableSubcription.unsubscribe();
                this.enableSubcription = null;
            }
        },

        /**
         * Update Data Point Value
         * @private
         * 
         * This method is called when the data point value is updated
         * by the web socket.
         * 
         * @param {object} data - WebSocket message data
         */
        updateDataPointValue(data) {
            this.onPointValueUpdate(JSON.parse(data.body).value);
        },

        /**
         * Update Data Point Enabled state
         * @private
         * 
         * This method is called when the data point state is updated
         * 
         * @param {object} data - WebSocket message data
         */
        componentEnabled(data) {
            const {enabled} = JSON.parse(data.body);
            enabled
                ? this.subscribeForDataPointValue()
                : this.unSubscribeForDataPointValue();
            this.onPointEnabledUpdate(enabled);
        },

	},
};
</script>
