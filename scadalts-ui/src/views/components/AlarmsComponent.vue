<template>
    <div class="data">
        <table>
            <tr>
                <th></th>
                <th>Activation Timestamp</th>
                <th>Inactivation Timestamp</th>
                <th>Variable name</th>
            </tr>
            <tr>

            </tr>
            <tr class="activation">
                <td></td>
                <td>2020-03-10 07:13:34</td>
                <td></td>
                <td>Be ST ALG_StoAllg1.0 Durchflussmessung Drosselkammer Störung Steuersicherung ausgelöst</td>
            </tr>
            <tr class="activation activation_alarm">
                <td></td>
                <td>2020-03-10 07:13:24</td>
                <td></td>
                <td>Be AL GM01 gas alarm</td>
            </tr>
            <tr class="inactivation ">
                <td>
                    <input type="checkbox" name="Activation" value="Activation">
                </td>
                <td>2020-03-10 05:13:24</td>
                <td>2020-03-10 06:33:54</td>
                <td>Se AL GM02 gas alarm</td>
            </tr>
            <tr class="inactivation">
                <td>
                    <input type="checkbox" name="Activation" value="Activation">
                </td>
                <td>2020-02-10 05:13:24</td>
                <td>2020-03-10 06:30:54</td>
                <td>Bx AL GM03 gas alarm</td>
            </tr>
        </table>
    </div>
</template>

<script>
    export default {
        el: '#alarms_component',
        name: 'alarmsComponent',
        components: {}
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
