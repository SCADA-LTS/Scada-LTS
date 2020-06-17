<template>
    <div class="historical-alarms-components">
        <SimplePanel>
            To refresh {{toRefresh}}
        </SimplePanel>
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
                    <input v-if=" item != undefined && item['inactivation-time'] != undefined && item['inactivation-time'].length>0" type="checkbox" name="Activation"
                           value="Activation" data="{{item.id}}">
                </td>
                <td>{{item["activation-time"]}}</td>
                <td>{{item["inactivation-time"]}}</td>
                <td>{{item.name}}</td>
            </tr>
        </table>
        <SimplePanel>

            <SimplePagination class="min-gb-pagination"
                              :current-page="currentPage"
                              :page-count="pageCount"
                              :visible-pages-count="8"
                              @nextPage="pageChangeHandle('next')"
                              @previousPage="pageChangeHandle('pref')"
                              @loadPage="pageChangeHandle"
            ></SimplePagination>

        </SimplePanel>
    </div>
</template>

<script>
    import store from "../../store"
    import Components from "@min-gb/vuejs-components"

    export default {
        el: '#alarms_component',
        name: 'alarmsComponent',
        components: {
            ...Components,
        },
        data() {
            return {
                alarms: [],
                currentPage: 1,
                pageCount: 10,
                toRefresh: 5
            }
        },
        methods: {
            getAlarms(page) {

                let recordsCount = 20
                let loffset = String(recordsCount * page)
                let llimit = String(recordsCount * (page - 1))

                //store.dispatch('fakeGetLiveAlarms
                store.dispatch('getLiveAlarms', {'offset': loffset, 'limit': llimit}).then((ret) => {
                    this.alarms = ret
                    //console.log(JSON.stringify(this.data))
                })
            },
            isActivation(activationTime, inactivationTime, level) {
                if (activationTime === undefined || inactivationTime === undefined || level === undefined) return false;

                if (activationTime.length > 0 && inactivationTime.length == 0 && level == 5) {
                    return true
                } else {
                    return false
                }
            },
            isActivationAlarm(activationTime, inactivationTime, level) {
                if (activationTime === undefined || inactivationTime === undefined || level === undefined) return false;

                if (activationTime.length > 0 && inactivationTime.length == 0 && level == 4) {
                    return true
                } else {
                    return false
                }

            },
            isInactivation(activationTime, inactivationTime, level) {
                if (activationTime === undefined || inactivationTime === undefined || level === undefined) return false;

                if (activationTime.length > 0 && inactivationTime.length > 0) {
                    return true
                } else {
                    return false
                }
            },
            pageChangeHandle(pr) {
                try {
                    if (pr === 'next') {
                        console.log('pageChangeHandle next')
                        return
                    } else if (pr === 'pref') {
                        console.log('pageChangeHandle pref')
                        return
                    }
                    this.currentPage = Number(pr)
                    this.getAlarms(this.currentPage)
                } catch (e) {
                    console.log(`pageCH:${e}`)
                }
            },
        },

        created() {

        },
        mounted() {
            this.getAlarms();
            setInterval(
                () => {
                    if (this.toRefresh == 0) {
                        this.getAlarms(this.currentPage)
                        this.toRefresh = 5;
                        console.log('getAlarms')
                    } else {
                        this.toRefresh = this.toRefresh - 1;
                    }
                },
                1000
            )

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
        background: white;
    }
</style>
