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
            requiresAuth: true,
            requiresAdmin: true
        },
    },
    {
        path: '/import-export',
        name: 'import-export',
        component: ImportExport,
        meta: {
            requiresAuth: true,
            requiresAdmin: true
        },
    },
    {
        path: '/sql',
        name: 'sql',
        component: SqlView,
        meta: {
            requiresAuth: true,
            requiresAdmin: true
        },
    },
    {
        path: '/publishers',
        name: 'publishers',
        component: Publishers,
        meta: {
            requiresAuth: true,
            requiresAdmin: true
        },
    },
];

export default routes;
