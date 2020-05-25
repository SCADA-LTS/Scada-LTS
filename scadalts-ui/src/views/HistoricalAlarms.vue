<template>
    <div>
<!--        <p>{{$route.name}}</p>-->
        <HistoricalAlarmsComponent></HistoricalAlarmsComponent>

    </div>
</template>

<script>

    import HistoricalAlarmsComponent from "./components/HistoricalAlarmsComponent"

    export default {
        el: '#alarms',
        name: 'alarms',
        components: {
            HistoricalAlarmsComponent
        },
        methods: {
        }
    }
</script>

<style lang="scss" scoped>

</style>
