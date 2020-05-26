<template>
    <div class="data">
        <SimpleTable v-bind:data="data" v-bind:columns="columns" ></SimpleTable>
    </div>
</template>

<script>
    import Components from '@min-gb/vuejs-components'
    import store from "../../store"


    export default {
        el: '#historicalAlarms_component',
        name: 'historicalAlarmsComponent',
        components: {
            ...Components
        },
        data() {
            return {
                data: [],
                columns: [{name: 'time'},
                    {name: 'name'},
                    {name: 'description'}],
                historicalAlarms: [],
                currentPage: 1,
                pageCount: 10
            }
        },
        methods: {

            getHistoricalAlarms() {
                store.dispatch('fakeGetHistoryAlarms',{dateDay:"2020-05-02", filterRLike:"", offset:"0", limit:"100"}).then((ret) => {
                    this.data = ret
                    //console.log(JSON.stringify(this.data))
                })
            },
        },
        created() {
            this.getHistoricalAlarms();
        }
    }
</script>

<style lang="scss" scoped>
    .data {
        margin-top: 40px;
        margin-left: 20px;
    }
</style>
