import SystemSettings from '../views/System/SystemSettings';
import ImportExport from '../views/System/ImportExport';
import SqlView from '../views/System/SQL';
import Publishers from '../views/System/Publishers';

export const routes = [
    {
        path: '/system-settings',
        name: 'system-settings',
        component: SystemSettings,
        meta: {
            requiresAuth: true
        },
    },
    {
        path: '/import-export',
        name: 'import-export',
        component: ImportExport,
        meta: {
            requiresAuth: true
        },
    },
    {
        path: '/sql',
        name: 'sql',
        component: SqlView,
        meta: {
            requiresAuth: true
        },
    },
    {
        path: '/publishers',
        name: 'publishers',
        component: Publishers,
        meta: {
            requiresAuth: true
        },
    },
];

export default routes;
