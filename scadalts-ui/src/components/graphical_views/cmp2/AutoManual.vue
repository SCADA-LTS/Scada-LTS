<template>
    <v-app>
        <v-container :style="{width: (this.pWidth > 340 ? this.pWidth : 340) + 'px'}">
            <v-card>
                <v-card-text class="auto-manual--content" :class="{ 'auto-manual--content-small': this.pWidth < 450 }">
                    <div class="header-container">
                        <div class="loading-container">
                            <v-progress-circular
                                indeterminate
                                color="primary"
                                :size="15"
                                v-if="loading"
                            ></v-progress-circular>
                        </div>
                        <div class="state-container" :class="{ 'state-container-small': this.pWidth < 700 }">
                            <div>
                                <div v-if="!!pLabel" class="cmp-label">
                                    {{ pLabel }}
                                </div>
                                <transition name="slide-fade" mode="out-in">
                                    <div class="cmp-state" :key="componentState">
                                        {{ componentState }}
                                    </div>
                                </transition>
                            </div>
                            
                            <v-alert
                                    dense
                                    v-if="!!errorActive"
                                    border="left"
                                    colored-border
                                    type="error"
                                    class="error-alert"
                            >{{errorActive}}</v-alert>

                        </div>
                    </div>

                    <div class="header-actions">
                        <v-menu ref="menu-errors"
                            :close-on-content-click="false"
                            offset-y
                            attach
                        >
                            <template v-slot:activator="{on, attrs}">
                                <v-btn fab small elevation="1" 
                                    v-bind="attrs" v-on="on"
                                    :color="!!errorActive ? 'error' : ''" 
                                    @click="errorActive = '';" 
                                    :class="{'error-active-anim': !!errorActive}"
                                >
                                    <v-icon>mdi-alert</v-icon>
                                </v-btn>                                
                            </template>

                            <AutoManualErrors
                                :errorHandler="errorHandlers"
                            ></AutoManualErrors>
                        </v-menu>

                        <v-menu ref="menu-hisotry"
                            :close-on-content-click="false"
                            offset-y
                            attach
                        >
                            <template v-slot:activator="{on, attrs}">
                                <v-btn fab small elevation="1" v-bind="attrs" v-on="on" @click="loadHistoryData">
                                    <v-icon>mdi-clock-time-two</v-icon>
                                </v-btn>
                            </template>

                            <AutoManualHistory
                                :cmpId="pxIdViewAndIdCmp"
                                ref="autoManualHisotry"
                            ></AutoManualHistory>
                        </v-menu>

                        <v-menu ref="menu-config"
                            :close-on-content-click="false"
                            offset-y
                            attach
                        >
                            <template v-slot:activator="{on, attrs}">
                                <v-btn fab small elevation="1" v-bind="attrs" v-on="on">
                                    <v-icon>mdi-cog</v-icon>
                                </v-btn>
                            </template>

                            <AutoManualControls
                                :controls="pConfig.control">
                            </AutoManualControls>
                        </v-menu>
                        
                    </div>
                    
                </v-card-text>
            </v-card>

        </v-container>
    </v-app>
</template>
<script>
import AutoManualControls from './AutoManualControls.vue';
import AutoManualHistory from './AutoManualHistory.vue'
import AutoManualErrors from './AutoManualErrors.vue'

/**
 * Auto Manual Component
 * 
 * Second version of classic CMP component created by @grzesiekb.
 * This component is a main Vue component that is responsible for
 * displaying and computing data.
 * 
 * @version 2.0.0
 * @author Radek Jajko <rjajko@softq.pl>
 */
export default {

    components: {
        AutoManualControls,
        AutoManualHistory,
        AutoManualErrors,
    },

    props: {
        pConfig : {
			required: true,
		},
		pLabel : {
			type: String,
		},
		pTimeRefresh : {
			type: Number,
            default: 10000,
		},
		pxIdViewAndIdCmp: {
			default: null,
		},
        pWidth: {
            type: Number,
            default: 700,
        },
		pDebugRequest: {
			type: Boolean,
			default: false,
		}
    },

    data() {
        return {
            loading: false,
            refreshInterval: null,
            dataPointValues: [],
            errorActive: '',
            errorHandlers: new Map(),
            componentState:'NOT-CHECKED',
        }
    },

    mounted() {
        if(!this.pDebugRequest) {
            console.debug = () => {};
        }
        console.debug(`AutoManual component mounted (XID View and CMPID: ${this.pxIdViewAndIdCmp})`);
        console.debug(`Refresh time: ${this.pTimeRefresh}`);
        this.initializeErrorHandlers();
        this.refreshInterval = setInterval(() => {
            this.checkConditions();
        }, this.pTimeRefresh);
    },

    destroyed() {
        clearInterval(this.refreshInterval);
    },

    methods: {
        
        getDataPointValues(datapointXids) {
            if(!!datapointXids && datapointXids.length > 0) {
                return this.$store.dispatch('requestGet', `/cmp/get/${datapointXids.join(',')}`);
            }
            return Promise.resolve([]);            
        },

        /**
         * Check Conditions
         * 
         * Analyze conditions in order that are defined in config property.
         * Get point values for each condition and compare them 
         * with specific "toCheck" entry. If there is any problem with downloading
         * values or comparing them, display error.
         */
        async checkConditions() {
            this.loading = true;
            this.componentState = '(N/A)';
            const conditions = this.pConfig.state.analiseInOrder;
            for(let i = 0; i < conditions.length; i++) {
                try {
                    this.dataPointValues = await this.getDataPointValues(this.getPointsToBeChecked(conditions[i]));
                    if(!this.checkOneCondition(conditions[i])) {
                        break;
                    }
                } catch (e) {
                    console.debug("Exception occured:",e);
                    if(!!e && e.status === 401) {
                       this.addNetworkError(conditions[i].name, e.message);
                    } else {
                        this.addNetworkError(conditions[i].name, "Failed to establish connection");
                    }
                }
            }
            this.loading = false;
        },

        /**
         * Initialize Error Handlers
         * Error handlers are used to display errors for specific conditions.
         * There are two basic properties like connection to DataPoints and
         * messages related with that specific condition.
         */
        initializeErrorHandlers() {
            const conditions = this.pConfig.state.analiseInOrder;
            conditions.forEach(c => {
                this.errorHandlers.set(c.name, {
                    connection: true,
                    messages: [],
                });
            });
        },

        addNetworkError(name, message) {
            let errorHandler = this.errorHandlers.get(name);
            if(errorHandler.connection) { 
                this.addErrorHandlerEntry(name, message); 
            }
            errorHandler.connection = false;
        },

        addErrorHandlerEntry(name, message) {
            this.errorActive = `${name} - ${message}`;
            this.errorHandlers.get(name).messages.push({
                time: new Date(),
                message: message
            });
        },

        resetNetworkError(name) {
            let errorHandler = this.errorHandlers.get(name);
            if(!errorHandler.connection) { 
                this.addErrorHandlerEntry(name, "Connection restored"); 
            }
            errorHandler.connection = true;
        },

        getPointsToBeChecked(condition) {
            let xIds = new Set();
            condition.toChecked.forEach(c => {
                if(!!c.xid) {
                    xIds.add(c.xid);
                }
            });
            return [...xIds];
        },

        /**
         * Check One Condition
         * If condition is met then set component state to its name.
         * Otherwise this check is breaking and it is stopped on last state.
         *
         * Condition could be reported into the error handler if it has 
         * "toNote" property. 
         *
         * If there is a "toNext" property the component is moving to next condition
         * to check.
         */
        checkOneCondition(condition) {
            console.debug(`Analyzing ${condition.name}`);
            this.resetNetworkError(condition.name);
            let conditionResult = false;
            condition.toChecked.some(c => {
                let dp = this.dataPointValues.find(d => d.xid === c.xid);
                if(!!dp) {
                    conditionResult = eval(`(${dp.value}${c.equals})`)
                    this.debugConditionResult(dp, c, conditionResult);
                    if(!conditionResult && !!c.toNote) {
                        this.addErrorHandlerEntry(condition.name, c.describe);
                    }
                    if(!conditionResult && !!c.toNext) {
                        conditionResult = true;
                        return false;
                    }
                    return !conditionResult;
                } else {
                    return false;
                }
            });
            if(conditionResult) {
                this.componentState = condition.name;
            }
            return conditionResult;
        },

        /**
         * Load History Data when user clicks on history button.
         */
        loadHistoryData() {
            if(!!this.$refs.autoManualHisotry) {
                this.$refs.autoManualHisotry.fetchHisoryData();
            }
        },

        debugConditionResult(dp, c, result) {
            console.debug(`  PointValue:\t${dp.value}`);
            console.debug(`  Chcek:\t\t${c.equals}`);
            console.debug(`  Result:\t\t${result}`);
            if(!result && !!c.toNote) {
                console.debug(`  Noted:\t\t${result}`);
            }
            if(!result && !!c.toNext) {
                console.debug(`  Moving to next check...`);
            }
        }
    },
    
}
</script>
<style scoped>
.auto-manual--content {
    display: flex;
    justify-content: space-between;
    align-items: center;
}
.auto-manual--content-small {
    flex-direction: column-reverse;
    align-items: flex-start;
}
.auto-manual--content-small > *:last-of-type {
    position: absolute;
    top: 15px;
    right: 25px;
}

.auto-manual--content .header-container {
    display: flex;
    align-items: center;
}
.auto-manual--content .loading-container {
    width: 20px;
}
.auto-manual--content .header-actions {
    min-width: 140px;
    display: flex;
    justify-content: space-between;
}

.auto-manual--content .state-container {
    display: flex;
    align-items: center;
}

.auto-manual--content .state-container-small {
    flex-direction: column;
    align-items: flex-start;
}

.auto-manual--content .state-container-small .error-alert {
    margin: 0;
    margin-top: 10px;
}

.state-container .cmp-label {
    font-style: italic;
    font-size: 10px;
    margin-bottom: -6px;
    color: rgba(0,0,0,0.4)
}

.state-container .cmp-state {
    font-weight: bold;
}
.error-alert {
    margin: 0;
    margin-left: 15px;
}

/* Animations */
/* --- State value transition */
.slide-fade-enter-active, .slide-fade-leave-active {
    transition: all 0.2s ease-in-out;
}
.slide-fade-enter {
    opacity: 0;
    transform: translateX(-10px);
}
.slide-fade-enter-to, 
.slide-fade-leave {
    opacity: 100;
    transform: translateX(0px);
}
.slide-fade-leave-to {
    opacity: 0;
    transform: translateX(15px);
}
/* --- Error active animation */
.error-active-anim {
    animation: error-blink 1s ease-in-out infinite;
}
@keyframes error-blink {
    0% { transform: scale(1); }
    50% { transform: scale(1.08); }
    100% { transform: scale(1); }
}

</style>