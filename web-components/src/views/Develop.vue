<template>
    <div>
        <p>Testing Vue.js components</p>
        <h4>Chart</h4>
        <line-chart-component point-id="1"></line-chart-component>
        <h4>Test</h4>
        <Test/>
        <h4>CMP</h4>
        <CMP/>
        <h4>IsAlive</h4>
        <is-alive/>
    </div>
</template>

<script>

    import LineChartComponent from "../components/charts/LineChartComponent";
    import Test from "../components/gb/Test";
    import CMP from "../components/gb/CMP";
    import IsAlive from "../components/gb/IsAlive";

    export default {
        name: 'home',
        components: {
            LineChartComponent,
            CMP,
            IsAlive,
            Test,
        }
    }
</script>
