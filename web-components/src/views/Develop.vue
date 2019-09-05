<template>
    <div>
        <p>Testing Vue.js components</p>
        <StepLineChart point-id="DP_260694" refresh-rate="2000" color="#FFDE42" start-date="2019/01/01" end-date="2020/01/01" range-value="20" range-color="#FF0000" range-label="boiling" show-scrollbar-x="false"/>
    </div>
</template>

<script>
    // @ is an alias to /src
    import StepLineChart from '@/components/charts/StepLineChartComponent.vue'

    export default {
        name: 'home',
        components: {
            StepLineChart,
        }
    }
</script>
