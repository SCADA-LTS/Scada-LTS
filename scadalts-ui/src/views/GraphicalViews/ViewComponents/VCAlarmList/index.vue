<template>
	<BaseViewComponent :component="component" @update="$emit('update')">
		<div>Alarm List</div>
        <template v-slot:renderer>
            <v-row>
                <v-col cols="12">
                    <v-text-field
                        label="Max List size"
                        v-model="component.maxListSize"
                    ></v-text-field>
                </v-col>
                <v-col cols="12">
                    <v-select
                        label="Min Alarm Level"
                        v-model="component.minAlarmLevel"
                        :items="alarmLevels"
                    ></v-select>
                </v-col>
            </v-row>
        </template>
	</BaseViewComponent>
</template>
<script>
import BaseViewComponent from '../../BaseViewComponent.vue'
export default {
    components: {
        BaseViewComponent
    },

	props: {
		component: {
			type: Object,
			required: true,
		},
	},

    data() {
        return {
            alarmLevels: [
                {
                    value: 0,
                    text: 'None'
                }, {
                    value: 1,
                    text: 'Information'
                }, {
                    value: 2,
                    text: 'Urgent'
                }, {
                    value: 3,
                    text: 'Critical'
                }, {
                    value: 4,
                    text: 'Life safety'
                }
            ]
        }
    },
};
</script>
<style></style>
