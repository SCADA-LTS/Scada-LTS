import SystemSettings from '@/views/System/SystemSettings/index.vue';
import ImportExport from '@/views/System/ImportExport/index.vue';
import SqlView from '@/views/System/SQL/index.vue';
import Publishers from '@/views/System/Publishers/index.vue';

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
