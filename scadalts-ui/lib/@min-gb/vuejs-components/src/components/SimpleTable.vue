<template>
    <div>
        <table>
            <tr>
                <th v-for="(column, index) in columns" :key="index">{{ column.name }}</th>
            </tr>
            <tr v-for="(record, index) in data" :key="index">
                <td v-for="(column,indexColumn) in columns" :key="indexColumn">{{record[column.name]}}</td>
            </tr>
        </table>
    </div>
</template>

<script>
    export default {
        props: [
            'data',
            'columns'
        ],
        data() {
            return {
            }
        },
        methods: {
            load() {
            },
        },
        created() {
            this.load();
        }
    }
</script>

<style scoped>

    table {
        margin: 20px;
        font-family: arial, sans-serif;
        border-collapse: collapse;
        width: 95%;
    }

    td, th {
        border: 1px solid #dddddd;
        text-align: left;
        padding: 8px;
    }

</style>
