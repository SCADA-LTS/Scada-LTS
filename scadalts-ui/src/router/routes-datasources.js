import DataSources from '../views/DataObjects/DataSources';
import DataPointList from '../views/DataObjects/DataPointDetails/DataPointList';
import DataPointDetails from '../views/DataObjects/DataPointDetails';
import PointHierarchy from '../views/DataObjects/PointHierarchy'
import PointLinks from '../views/DataObjects/PointLinks'
import Scripting from '../views/DataObjects/Scripting'

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
