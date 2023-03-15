<template>
    <div class="min-gb-panel">
        <slot></slot>
    </div>
</template>

<script>
    export default {
        props: [
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

    .min-gb-panel {
        margin: 20px;
        font-family: arial, sans-serif;
        border-collapse: collapse;
        width: 95%;
        background-color: #bdb7ab;
        height:3em;
        border-radius: 3px;
    }

    .min-gb-input {
        margin: 0.3em 0.2em 0.2em 0.2em;
        position:relative;
        float: left;
        left:0em;
        width: 9em;
        font-family: inherit;
        font-size: 15px;
        height: 35px;
        outline: 0;
        background-color: #fff;
        border: 1px solid #d4d4d4;
        border-radius: 3px;
        box-shadow: none;
        padding: 0 12px;
    }

    .min-gb-pagination {
        margin: 0.3em 0.2em 0.2em 0.2em;
        position:relative;
        float: left;
        left:0em;
        width: 14em;
        font-family: inherit;
        font-size: 15px;
        height: 35px;
        outline: 0;
        background-color: #bdb7ab;
        border: 1px solid #d4d4d4;
        border-radius: 3px;
        box-shadow: none;
        padding: 0 12px;
    }

    .min-gb-button {
        margin: 0.3em 0.2em 0.2em 0.2em;
        position:relative;
        float: left;
        left:0em;
        width: 4em;
        font-family: inherit;
        font-size: 19px;
        color: #d4d4d4;
        height: 35px;
        outline: 0;
        background-color: green;
        border: 1px solid #d4d4d4;
        border-radius: 3px;
        box-shadow: none;
        padding: 0 12px;
    }


</style>
