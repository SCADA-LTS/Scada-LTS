import Alarms from '../views/Alarms/Alarms.vue';
import EventList from '../views/Alarms/EventList';
import HistoricalAlarms from '../views/Alarms/HistoricalAlarms';
import AlarmNotifications from '../views/Alarms/AlarmNotifications';

export const routes = [
    {
        path: '/',
        name: 'home',
        component: Alarms,
        meta: {
            requiresAuth: true
        },
    },
    {
        path: '/alarms',
        name: 'alarms',
        component: Alarms,
        meta: {
            requiresAuth: true
        },
    },
    {
        path: '/historical-alarms',
        name: 'historical-alarms',
        component: HistoricalAlarms,
        meta: {
            requiresAuth: true
        },
    },
    {
        path: '/event-list',
        name: 'event-list',
        component: EventList,
        meta: {
            requiresAuth: true
        },
    },
    {
        path: '/alarm-notifications',
        name: 'alarm-notifications',
        component: AlarmNotifications,
        meta: {
            requiresAuth: true
        },
    },
];

export default routes;
