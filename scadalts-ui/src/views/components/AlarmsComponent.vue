<template>
    <div class="data">
        <table>
            <tr>
                <th></th>
                <th>Activation Timestamp</th>
                <th>Inactivation Timestamp</th>
                <th>Variable name</th>
            </tr>
            <tr v-for="(item, index) in alarms"
                v-bind:class="{
                        activation: isActivation( item['activation-time'],item['inactivation-time'],item.level),
                        activation_alarm: isActivationAlarm(item['activation-time'],item['inactivation-time'],item.level),
                        inactivation: isInactivation(item['activation-time'],item['inactivation-time'],item.level)
                    }"
            >
                <td>
                    <input v-if="item['inactivation-time'].length>0" type="checkbox" name="Activation"
                           value="Activation" data="{{item.id}}">
                </td>
                <td>{{item["activation-time"]}}</td>
                <td>{{item["inactivation-time"]}}</td>
                <td>{{item.name}}</td>
            </tr>
        </table>
    </div>
</template>

<script>
    import store from "../../store"

    export default {
        el: '#alarms_component',
        name: 'alarmsComponent',
        components: {},
        data() {
            return {
                alarms: [],
            }
        },
        methods: {
            getAlarms() {
                store.dispatch('fakeGetLiveAlarms', {'offset': 0, 'limit': 100}).then((ret) => {
                    this.alarms = ret
                    //console.log(JSON.stringify(this.data))
                })
            },
            isActivation(activationTime, inactivationTime, level){
                if (activationTime.length>0 && inactivationTime.length==0 && level == 5) {
                    return true
                } else {
                    return false
                }
            },
            isActivationAlarm(activationTime, inactivationTime, level) {
                if (activationTime.length>0 && inactivationTime.length==0 && level == 4) {
                    return true
                } else {
                    return false
                }
            },
            isInactivation(activationTime, inactivationTime, level) {
                if (activationTime.length>0 && inactivationTime.length>0) {
                    return true
                } else {
                    return false
                }
            }

        },
        created() {
            this.getAlarms();
        },
        mounted() {

        },
    }
</script>

<style lang="scss" scoped>
    data {
        margin: 20px;
    }

    table {
        margin: 20px;
        font-family: arial, sans-serif;
        border-collapse: collapse;
        width: 100%;
    }

    td, th {
        border: 1px solid #dddddd;
        text-align: left;
        padding: 8px;
    }

    .activation_alarm {
        color: red;
        background: yellow;
    }

    .activation {
        color: red;
        background: white;
    }

    .inactivation {
        color: green;
        background:white;
    }
</style>
