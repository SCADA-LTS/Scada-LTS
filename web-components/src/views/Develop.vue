<template>
    <div>
        <p>Testing Vue.js components</p>
        <line-chart-component point-id="1"></line-chart-component>
    </div>
</template>

<script>
    // @ is an alias to /src
    import LineChartComponent from "../components/charts/LineChartComponent";

    export default {
        name: 'home',
        components: {
            LineChartComponent,
        }
    }
</script>
