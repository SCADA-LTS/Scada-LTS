<template>
	<div>
		<v-container fluid v-if="componentEditor">
			<v-row>
				<v-col cols="8">
					<DataPointSearchComponent @change="datapointSelected" />
				</v-col>
				<v-col cols="4">
					<v-text-field
						disabled
						v-model="componentData.data.pointXid"
						label="Point Xid"
					/>
				</v-col>

				<v-col cols="6">
                    <v-select 
                        :items="conditionType" 
                        v-model="componentData.data.checkType"
                        label="Condition Type"
                    ></v-select>
				</v-col>
				<v-col cols="6">
                    <v-text-field 
                        type="number"
                        v-model="componentData.data.checkValue"
                        label="Condition check value"
                    ></v-text-field>
				</v-col>

                <v-col cols="4">
					<v-text-field 
                        v-model="componentData.data.enabledLabel"
                        label="Positive condition label and color"
                    ></v-text-field>
				</v-col>
				<v-col cols="2">
					<v-menu offset-y :close-on-content-click="false">
						<template v-slot:activator="{ on }">
							<v-btn :color="enabledColor" v-on="on" block> </v-btn>
						</template>
						<v-color-picker v-model="enabledColor" :close-on-content-click="false">
						</v-color-picker>
					</v-menu>
				</v-col>
				<v-col cols="4">
					<v-text-field 
                        v-model="componentData.data.disabledLabel"
                        label="Negative condition label and color"
                    ></v-text-field>
				</v-col>
				<v-col cols="2">
					<v-menu offset-y :close-on-content-click="false">
						<template v-slot:activator="{ on }">
							<v-btn :color="disabledColor" v-on="on" block> </v-btn>
						</template>
						<v-color-picker v-model="disabledColor" :close-on-content-click="false">
						</v-color-picker>
					</v-menu>
				</v-col>
			</v-row>
		</v-container>
	</div>
</template>
<script>
import CustomComponentsBase from '../CustomComponentBase.vue';
import DataPointSearchComponent from '@/layout/buttons/DataPointSearchComponent';
/**
 * SLTS-STATE Component
 *
 * @author Radoslaw Jajko <rjajko@softq.pl>
 * @version 1.0.0
 */
export default {
	extends: CustomComponentsBase,
	components: {
		DataPointSearchComponent,
	},
	data() {
		return {
			lastValue: undefined,
			refreshInterval: undefined,
            conditionType: [{
                value: '===',
                text: "Equal",
            }, {
                value: '!==',
                text: "Not equal",
            }, {
                value: '>',
                text: "Greater than",
            }, {
                value: '<',
                text: "Less than",
            }]
		};
	},

	mounted() {
		if (!this.componentEditor) {
			this.subscribeToWebSocket();
		}
	},

    computed: {
		enabledColor: {
			get() { return this.componentData.data.colorEnabled || '#1BB350'; },
			set(value) { this.componentData.data.colorEnabled = value; }
		},
		disabledColor: {
			get() { return this.componentData.data.colorDisabled || '#B8150E'; },
			set(value) { this.componentData.data.colorDisabled = value; }
		}
	},

	methods: {
		datapointSelected(datapoint) {
			this.componentData.data.pointXid = datapoint.xid;
		},

		onPointValueUpdate(value) {
            let state = this.checkCondition(value);
            this.changeComponentText(`${this.componentId}_value`, value);
            this.lastValue = value;
            if(state) {
                this.changeComponentColor(`${this.componentId}`, this.enabledColor);
                this.changeComponentText(`${this.componentId}_label`, this.componentData.data.enabledLabel || "Condition failed");
            } else {
                this.changeComponentColor(`${this.componentId}`, this.disabledColor);
                this.changeComponentText(`${this.componentId}_label`, this.componentData.data.disabledLabel || "Condition OK");
            }
		},

		onPointEnabledUpdate(enabled) {
            if(enabled) {
				if(this.lastValue){
					this.onPointValueUpdate(this.lastValue)
				}else{
					// Point does not have any data
					this.changeComponentText(`${this.componentId}_value`, "N/A");
                	this.changeComponentColor(`${this.componentId}`, this.enabledColor);
				}
            } else {
                this.changeComponentText(`${this.componentId}_value`, "N/A");
                this.changeComponentColor(`${this.componentId}`, "#d6d5d5");
            }
		},

        checkCondition(value) {
            let checkType = this.componentData.data.checkType;
            let checkValue = Number(this.componentData.data.checkValue);
            if(value === "true") value = 1;
            if(value === "false") value = 0;
			value = Number(value);
            switch (checkType) {
                case '===':
                    return value === checkValue;
                case '!==':
                    return value !== checkValue;
                case '>':
                    return value > checkValue;
                case '<':
                    return value < checkValue;
                default:
                    return false;
            }
        },
        
	},
};
</script>
