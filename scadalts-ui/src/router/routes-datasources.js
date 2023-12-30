import DataSources from '@/views/DataObjects/DataSources/index.vue';
import DataPointList from '@/views/DataObjects/DataPointDetails/DataPointList.vue';
import DataPointDetails from '@/views/DataObjects/DataPointDetails/index.vue';
import PointHierarchy from '@/views/DataObjects/PointHierarchy/index.vue'
import PointLinks from '@/views/DataObjects/PointLinks/index.vue'
import Scripting from '@/views/DataObjects/Scripting/index.vue'

export const routes = [
    {
        path: '/datasources',
        name: 'datasources',
        component: DataSources,
        meta: {
            requiresAuth: true
        },
    },
    {
        path: '/datapoint-list',
        name: 'datapoint-list',
        component: DataPointList,
        meta: {
            requiresAuth: true,
            requiresAdmin: true
        },
    },
    {
        path: '/datapoint-details/:id',
        name: 'datapoint-details',
        component: DataPointDetails,
        meta: {
            requiresAuth: true,
            requiresAdmin: true
        },
    },
    {
        path: '/point-hierarchy',
        name: 'point-hierarchy',
        component: PointHierarchy,
        meta: {
            requiresAuth: true,
            requiresAdmin: true
        },
    },
    {
        path: '/point-links',
        name: 'point-links',
        component: PointLinks,
        meta: {
            requiresAuth: true,
            requiresAdmin: true
        },
    },
    {
        path: '/scripts',
        name: 'scripts',
        component: Scripting,
        meta: {
            requiresAuth: true,
            requiresAdmin: true
        },
    },
];

export default routes;
