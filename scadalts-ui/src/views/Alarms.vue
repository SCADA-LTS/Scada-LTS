<template>
    <div>

        <div class="action">
            <input type="checkbox" id="select_all" name="Select_All" value="1">
            <label class="selall" for="select_all">Select All</label><br>
        </div>
        <Refresh></Refresh>
        <AlarmsComponent></AlarmsComponent>
        <div class="action_bottom">
            <button>Acknowledge Störung/Alarms</button>
        </div>

    </div>
</template>

<script>

    import Components from '@min-gb/vuejs-components'
    import AlarmsComponent from "./components/AlarmsComponent"

    export default {
        el: '#alarms',
        name: 'alarms',
        components: {
            AlarmsComponent,
            ...Components
        },
        methods: {
        }
    }
</script>

<style lang="scss" scoped>


    @import '../../node_modules/@min-gb/vuejs-components/dist/min-gb.css';

    .action {
        margin-top: 50px;
        margin-left: 20px;
        padding-top: 10px;

    }
    .action_bottom {
        padding-top:10px;
        margin-left: 20px;
    }


    table {
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
