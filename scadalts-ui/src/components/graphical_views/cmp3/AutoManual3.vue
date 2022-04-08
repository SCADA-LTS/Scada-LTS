<template>
    <v-app>
        <v-container :style="{width: (this.pWidth > 140 ? this.pWidth : 140) + 'px'}">
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
                        <div class="state-container" :class="{ 'state-container-small': this.pWidth < 700, 'state-container--x-small': this.pWidth < 200 }">
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

                    <div class="header-actions" :class="{'actions-hidden': !!disableChange || !!pHideControls, 'header-actions--x-small': this.pWidth < 200 }">
                        <v-menu ref="menu-errors"
                            :close-on-content-click="false"
                            offset-y
                            attach
                        >
                            <template v-slot:activator="{on, attrs}">
                                <v-btn fab x-small elevation="1"                                     
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

                        <v-menu ref="menu-config" v-if="(!disableChange && !pHideControls)"
                            :close-on-content-click="false"
                            offset-y
                            attach
                        >
                            <template v-slot:activator="{on, attrs}">
                                <v-btn fab x-small elevation="1" v-bind="attrs" v-on="on">
                                    <v-icon>mdi-cog</v-icon>
                                </v-btn>
                            </template>

                            <div>
                                <AutoManualControls
                                    :controls="pConfig.control">
                                </AutoManualControls>
                                <AutoManualHistory
                                    :cmpId="pxIdViewAndIdCmp"
                                ></AutoManualHistory>
                            </div>
                        </v-menu>
                        
                    </div>
                    
                </v-card-text>
            </v-card>

        </v-container>
    </v-app>
</template>
<script>
import AutoManualControls from '../cmp2/AutoManualControls.vue';
import AutoManualHistory from '../cmp2/AutoManualHistory.vue'
import AutoManualErrors from '../cmp2/AutoManualErrors.vue'
import httpClient from 'axios';

class ApiCMP {
	get(xIds) {
		return new Promise((resolve, reject) => {
			try {
				const apiCMPCheck = `./api/cmp/get/${xIds}`;
				if (xIds.length > 0) {
					httpClient
						.get(apiCMPCheck, { timeout: 5000 })
						.then((response) => {
							resolve(response);
						})
						.catch((error) => {
							reject(error);
						});
				} else {
					const reason = new Error('Probably not have data');
					reject(reason);
				}
			} catch (e) {
				reject(e);
			}
		});
	}
}

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
        pZeroState: {
            default: "(N/A)",
        },
        pWidth: {
            type: Number,
            default: 700,
        },
        pRequestTimeout: {
            type: Number,
            default: 5000
        },
        pHideControls: {
            type: Boolean,
            default: false,
        },
		pDebugRequest: {
			type: Boolean,
			default: false,
		}
    },

    data() {
        return {
            loading: false,
            disableChange: false,
            disableComponent: false,
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
        if(this.pRequestTimeout !== 5000) {
            this.changeTimeout();
        }
        console.debug(`AutoManual component mounted (XID View and CMPID: ${this.pxIdViewAndIdCmp})`);
        console.debug(`Refresh time: ${this.pTimeRefresh}`);
        this.initializeErrorHandlers();
        this.refreshInterval = setInterval(() => {
             this.componentState = this.pZeroState;
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
            //this.componentState = this.pZeroState;
            const conditions = [...this.pConfig.state.analiseInOrder];

            for(let i = 0; i < conditions.length; i++) {
                try {
                    let check = false;
                    let myXids = [];

                    for (let j =0; j < conditions[i].toChecked.length; j++) {
                        if (!!conditions[i].toChecked[0].xid) {
                            myXids.push(JSON.stringify(conditions[i].toChecked[0].xid))
                            check = true;
                        }
                    }
                    
                    if (check) {

                        this.dataPointValues = await new ApiCMP().get(myXids.join(',').slice(1,-1))

                        let conditionResult = eval(`(${this.dataPointValues.data[0].value}${conditions[i].toChecked[0].equals})`)
                    
                        if (conditionResult) {
                            this.componentState = conditions[i].name;
                            break;
                        }
                    } else {
                        this.componentState = this.pZeroState;
                    }
                    
                } catch (e) {
                    console.debug("Exception occured:",e);
                    if(!!e && e.status === 401) {
                       this.addNetworkError(conditions[i].name, e.message);
                    } else {
                        this.addNetworkError(conditions[i].name, this.$t('component.automanual.errors.conn'));
                    }
                }
            }
            this.disableChange = this.disableComponent;
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
            this.disableComponent = !!condition.disabled;
            return conditionResult;
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
        },

        changeTimeout() {
            this.$store.commit('updateRequestTimeout', this.pRequestTimeout);
        }
    },
    
}
</script>
<style scoped>
.auto-manual--content {
    padding: 10px;
    display: flex;
    justify-content: space-between;
    align-items: center;
}
.auto-manual--content-small {
    padding: 5px;
    flex-direction: column-reverse;
    align-items: flex-start;
}
.auto-manual--content-small > *:last-of-type {
    position: absolute;
    top: 10%;
    right: 3px;
}

.auto-manual--content .header-container {
    display: flex;
    align-items: center;
}
.auto-manual--content .loading-container {
    width: 20px;
}
.auto-manual--content .header-actions {
    min-width: 72px;
    display: flex;
    justify-content: space-between;
    transition: justify-content 0.2s ease-in-out;
}
.auto-manual--content .header-actions--x-small {
    min-width: 36px;
    top: 5px;
}
.header-actions--x-small > button {
    width: 16px;
    height: 16px;
}
.header-actions--x-small > button i.v-icon {
    font-size: 10px;
}
.auto-manual--content .actions-hidden {
    justify-content: flex-end;
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
<style>
#viewContainer .theme--light.v-application {
    background: none;
}
#viewContainer .theme--light.v-application .container {
    padding: 5px;
}
.state-container--x-small .v-alert__wrapper > i {
    display: none;
}
.v-menu--attached > div[role="menu"] {
    z-index: 1000 !important;
    background: #fff;

}
</style>