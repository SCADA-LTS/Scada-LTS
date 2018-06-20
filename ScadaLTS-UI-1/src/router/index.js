import Vue from 'vue'
import Router from 'vue-router'
import HelloWorld from '@/components/HelloWorld'
import Test from '@/components/Test'
import ExportImportPointHierarchy from '../components/ExportImportPointHierarchy'

Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/',
      name: 'HelloWorld',
      component: HelloWorld
    },
    {
      path: '/test',
      name: 'Test',
      component: Test
    },
    { path: '/export-import-ph',
      name: 'Export import point hierarchy',
      component: ExportImportPointHierarchy
    }
  ]
})
