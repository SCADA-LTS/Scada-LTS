<template>
    <div>
        <p>Testing Vue.js components</p>
        <h4>Chart</h4>
        <StepLineChart point-id="1"/>
        <h4>Test</h4>
        <Test/>
        <h4>CMP</h4>
        <CMP/>
        <h4>IsAlive</h4>
        <is-alive/>
    </div>
</template>

<script>
    // @ is an alias to /src
    import StepLineChart from '@/components/charts/StepLineChartComponent.vue'
    import Test from "../components/gb/Test";
    import CMP from "../components/gb/CMP";
    import IsAlive from "../components/gb/IsAlive";

    export default {
        name: 'home',
        components: {
            CMP,
            IsAlive,
            Test,
            StepLineChart
        }
    }
</script>
