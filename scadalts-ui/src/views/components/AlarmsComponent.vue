<template>
    <div class="historical-alarms-components">

        <SimplePanel class="panel panel_top">
            To refresh {{toRefresh}}
            <div class="action">
                <input type="checkbox" id="select_all" name="Select_All" value="1">
                &nbsp;<label class="selall" for="select_all" v-on:click="toogleSelectAll(this)">Select All</label><br>
            </div>
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
                    <input v-if=" item != undefined && item['inactivation-time'] != undefined && item['inactivation-time'].length>0"
                           type="checkbox" name="ActivationAction"
                           :value="item.id" v-model="to_acknowledges">
                </td>
                <td>{{item["activation-time"]}}</td>
                <td>{{item["inactivation-time"]}}</td>
                <td>{{item.name}}</td>
            </tr>
        </table>

        <SimplePanel class="panel">

            <div class="pagination">
                <SimplePagination
                        :current-page="currentPage"
                        :page-count="pageCount"
                        :visible-pages-count="8"
                        @nextPage="pageChangeHandle('next')"
                        @previousPage="pageChangeHandle('pref')"
                        @loadPage="pageChangeHandle"
                ></SimplePagination>
            </div>

        </SimplePanel>
        <div class="action_bottom">
            <button onclick="acknowledge">Acknowledge Störung/Alarms</button>
        </div>
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
                toRefresh: 5,
                to_acknowledges: []
            }
        },
        methods: {
            getAlarms(page) {

                let recordsCount = 20
                let loffset = String(recordsCount * (page - 1))
                let llimit = String(recordsCount)

                //store.dispatch('fakeGetLiveAlarms
                store.dispatch('getLiveAlarms', {'offset': loffset, 'limit': llimit}).then((ret) => {
                    this.alarms = ret
                    //console.log(JSON.stringify(this.data))
                })
            },
            acknowledge() {
                for (let i = 0; i < this.to_acknowledges.length; i++) {
                    store.dispatch('setAcknowledge', this.to_acknowledges[i]).then(
                        ret => {
                            console.log(`ackn:${ret}`)
                        }
                    ).catch(err => {
                            console.log(`ackn-err:${err}`)
                        }
                    )
                }
                this.getAlarms(this.currentPage)

            },
            toogleSelectAll(source) {
                 const checkboxes = document.getElementsByName('ActivationAction');
                    if (checkboxes != null && checkboxes != undefined ) {
                        for (let i = 0; i < checkboxes.length; i++) {
                            console.log(`checked:${JSON.stringify(checkboxes[i])}`)
                            //checkbox.checked = !checkbox.checked;
                            //console.log(`checkbod:${checkbox}`)
                        }
                    }
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
                        if (this.currentPage < 9) {
                            this.currentPage = Number(this.currentPage + 1)
                            this.getAlarms(this.currentPage)
                        }
                        return
                    } else if (pr === 'pref') {
                        if (this.currentPage > 1) {
                            this.currentPage = Number(this.currentPage - 1)
                            this.getAlarms(this.currentPage)
                        }
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
            this.getAlarms(1);
        },
        mounted() {
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
    .pagination {
        margin: 0px 0px 0px 10px;
    }
    .action_bottom {
        padding-top: 10px;
        margin-left: 20px;
    }
    .panel_top {
        margin-top: 45px;
    }
    .panel {
        width: 100%;
    }

</style>
