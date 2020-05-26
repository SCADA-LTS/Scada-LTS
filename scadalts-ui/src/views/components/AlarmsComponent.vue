<template>
    <div class="data">
        <table>
            <tr>
                <th></th>
                <th>Activation Timestamp</th>
                <th>Inactivation Timestamp</th>
                <th>Variable name</th>
            </tr>
            <tr v-for="(item, index) in alarms" :key="index"
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
        <Pagination
                :current-page="currentPage"
                :page-count="pageCount"
                :visible-pages-count="8"
                class=""
                @nextPage="pageChangeHandle('next')"
                @previousPage="pageChangeHandle('pref')"
                @loadPage="pageChangeHandle"
        ></Pagination>
    </div>
</template>

<script>
    import store from "../../store"
    import Pagination from "./base/pagination/Pagination"

    export default {
        el: '#alarms_component',
        name: 'alarmsComponent',
        components: {
            Pagination
        },
        data() {
            return {
                alarms: [],
                currentPage: 1,
                pageCount: 10
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
            },
            pageChangeHandle(pr) {
                if (pr==='next') {
                    console.log('pageChangeHandle next')
                    return
                } else if (pr==='pref') {
                    console.log('pageChangeHandle pref')
                    return
                }
                console.log('pageChangeHandle')
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
