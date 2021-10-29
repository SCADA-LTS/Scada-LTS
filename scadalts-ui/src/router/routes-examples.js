export const exampleRoutes = [{
    path: '/example-ph',
    name: 'example-ph',
    component: () =>
        import(
            /* webpackChunkName: "ph" */ '../views/Test/ExampleExportImportPointHierarchy.vue'
        ),
},
{
    path: '/example-isalive',
    name: 'example-isalive',
    component: () =>
        import(
            /* webpackChunkName: "isalive-component" */ '../views/Test/ExampleIsAlive.vue'
        ),
},
{
    path: '/example-simple-component-svg',
    name: 'example-simple-component-svg',
    component: () =>
        import(
            /* webpackChunkName: "simple-component-svg" */ '../views/Test/ExampleSimpleComponentSVG.vue'
        ),
},
{
    path: '/example-sleep-and-reactivation-ds',
    name: 'example-sleep-and-reactivation-ds',
    component: () =>
        import(
            /* webpackChunkName: "sleep-and-reactivation-ds-component" */ '../views/Test/ExampleSleepAndReactivationDS.vue'
        ),
},
{
    path: '/example-cmp',
    name: 'example-cmp',
    component: () =>
        import(
            /* webpackChunkName: "cmp-component" */ '../views/Test/ExampleCmp.vue'
        ),
},
{
    path: '/example-chart-cmp',
    name: 'example-chart-cmp',
    // component: ExampleChartCmp,
    component: () =>
        import(
            /* webpackChunkName: "example-chart-cmp" */ '../views/Test/ExampleChartCmp.vue'
        ),
},
{
    path: '/example-live-alarms',
    name: 'example-live-alarms',
    component: () =>
        import(
            /* webpackChunkName: "live-alarms-component" */ '../views/Test/ExampleLiveAlarms.vue'
        ),
}];

export default exampleRoutes;