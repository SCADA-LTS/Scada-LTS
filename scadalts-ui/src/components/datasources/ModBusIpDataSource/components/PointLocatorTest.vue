<template>
	<div class="modbus-widged">
        <v-card elevation="1">
            <v-card-text>
                <Point
         :createMode="false"
         :datapoint="datapoint"
         :visible="true"
         :pointLocatorTest="true"
        ></Point>

        <div class="message-box-container">
            <div class="message-box" v-if="loading">
                Loading...
            </div>
            <div v-if="!!status" class="message-box">
                <v-icon v-if="status.type === 'error'">
                    mdi-alert-circle
                </v-icon>
                {{status.message}}
            </div>
        </div>

        <v-btn elevation="1" @click="testPointLocator" class="full-width">
            Read Data
        </v-btn>
            </v-card-text>
        </v-card>
        
        
	</div>
</template>
<script>
import Point from '../point';
import ModBusDataPoint from '../ModBusDataPoint';

export default {
    components: {
        Point
    },

    props: {
        datasource: {
            required: true,
            type: Object,
        }
    },

	data() {
		return {      
            datapoint: new ModBusDataPoint(-1),
            loading: false,
            status: {
                type: '',
                message: ''
            }
		};
	},

    methods: {
        testPointLocator() {
            this.loading = true;
            this.status = null;
            let data = {
                datasource: this.datasource,
                pointLocator: this.datapoint.pointLocator,
            };
            this.$store.dispatch('modbusPointLocatorTest', data)
            .then(r => {
                r.status === 'success' 
                ? this.showMessage('success', r.value)
                : this.showMessage('error', r.description);
            }).catch(() => {
                this.showMessage('error', 'Response not received from server');
            }).finally(() => {
                this.loading = false;
            });
        },

        showMessage(type, message) {
            this.status = {
                type: type,
                message: message
            };
            if(type !== 'success') {
                setTimeout(() => {
                    this.status = null;
                }, 5000);                
            }
            
        }
    }
};
</script>
<style scoped>
.message-box-container {
    min-height: 40px;
    box-shadow: inset 1px 2px 4px 0px #aaa;
    border-radius: 5px;
    background-color: #0000000d;
    margin: 5px;
    display: flex;
    justify-content: center;
}
.message-box {
    margin: 10px;
    font-style: italic;
}
.modbus-widged {
    max-width: 500px;
}
.full-width {
    width: 100%;
}
</style>
