import Alarms from '../views/Alarms/Alarms.vue';
import AlarmsTabs from '../views/Alarms/AlarmTabs.vue';
import EventList from '../views/Alarms/EventList.vue';
import HistoricalAlarms from '../views/Alarms/HistoricalAlarms.vue';
import AlarmNotifications from '@/views/Alarms/AlarmNotifications/index.vue';

export const routes = [
    {
        path: '/',
        name: 'home',
        component: AlarmsTabs,
        meta: {
            requiresAuth: true
        },
    },
    {
        path: '/alarms',
        name: 'alarms',
        component: AlarmsTabs,
        meta: {
            requiresAuth: true
        },
        children: [
            {
                path: 'plc',
                component: Alarms,
            },
            {
                path: 'plc-history',
                component: HistoricalAlarms,
            },
            {
                path: 'scada',
                name: 'scada',
                component: EventList,
            },
        ]
    },
    {
        path: '/alarm-notifications',
        name: 'alarm-notifications',
        component: AlarmNotifications,
        meta: {
            requiresAuth: true,
            requiresAdmin: true
        },
    },
];

export default routes;
