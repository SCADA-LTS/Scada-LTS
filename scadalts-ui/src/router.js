import Vue from 'vue'
import Router from 'vue-router'
import Home from './views/Home.vue'

Vue.use(Router)

export default new Router({
  mode: 'history',
  base: process.env.BASE_URL,
  routes: [
    {
      path: '/',
      name: 'home',
      component: Home
    },
    {
      path: '/about',
      name: 'about',
      // route level code-splitting
      // this generates a separate chunk (about.[hash].js) for this route
      // which is lazy-loaded when the route is visited.
      component: () => import(/* webpackChunkName: "about" */ './views/About.vue')
    },
    {
      path: '/components',
      name: 'components',
      component: () => import(/* webpackChunkName: "components" */ './views/Components.vue')
    },
    {
      path: '/example-ph',
      name: 'example-ph',
      component: () => import(/* webpackChunkName: "ph" */ './views/components/ExampleExportImportPointHierarchy.vue')
    },
    {
      path: '/example-test',
      name: 'example-test',
      component: () => import(/* webpackChunkName: "test-component" */ './views/components/ExampleTest.vue')
    },
    {
      path: '/example-isalive',
      name: 'example-isalive',
      component: () => import(/* webpackChunkName: "isalive-component" */ './views/components/ExampleIsAlive.vue')
    },
    {
      path: '/example-simple-component-svg',
      name: 'example-simple-component-svg',
      component: () => import(/* webpackChunkName: "simple-component-svg" */ './views/components/ExampleSimpleComponentSVG.vue')
    },
    {
      path: '/example-sleep-and-reactivation-ds',
      name: 'example-sleep-and-reactivation-ds',
      component: () => import(/* webpackChunkName: "sleep-and-reactivation-ds-component" */ './views/components/ExampleSleepAndReactivationDS.vue')
    },
    {
      path: '/example-cmp',
      name: 'example-cmp',
      component: () => import(/* webpackChunkName: "cmp-component" */ './views/components/ExampleCmp.vue')
    }
  ]
})
