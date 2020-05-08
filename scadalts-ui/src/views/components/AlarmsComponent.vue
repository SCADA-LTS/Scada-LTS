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
                class="activation activation_alarm inactivation"
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
        background: yellow;
    }

    .activation {
        color: red;
    }

    .inactivation {
        color: green;
    }
</style>
