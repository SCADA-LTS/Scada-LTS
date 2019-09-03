<template>
    <div>
        <p>Testing Vue.js components</p>
        <StepLineChart point-id="1"/>
    </div>
</template>

<script>
    // @ is an alias to /src
    import StepLineChart from '@/components/charts/StepLineChartComponent.vue'

    export default {
        name: 'home',
        components: {
            StepLineChart
        }
    }
</script>
