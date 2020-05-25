<template>
    <div>
        <h1>Test</h1>
    </div>
</template>

<script>
    import store from "../../store"

    export default {
        el: '#historicalAlarms_component',
        name: 'historicalAlarmsComponent',
        components: {
        },
        data() {
            return {
                historicalAlarms: [],
                currentPage: 1,
                pageCount: 10
            }
        },
        methods: {

            created() {

            },
            mounted() {

            },
        }
    }
</script>

<style lang="scss" scoped>
    data {
        margin: 20px;
    }
</style>
