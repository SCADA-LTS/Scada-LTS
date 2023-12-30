import EventHandlers from '@/views/Events/EventHandlers/index.vue'
import ScheduledEvents from '@/views/Events/ScheduledEvents/index.vue'
import CompountEventDetectors from '@/views/Events/CompoundEventDetectors/index.vue'
import MaintenanceEvents from '@/views/Events/MaintenanceEvents/index.vue'

export const routes = [
    {
        path: '/event-handlers',
        name: 'event-handlers',
        component: EventHandlers,
        meta: {
            requiresAuth: true,
            requiresAdmin: true
            
        },
    },
    {
        path: '/scheduled-events',
        name: 'scheduled-events',
        component: ScheduledEvents,
        meta: {
            requiresAuth: true,
            requiresAdmin: true
        },
    },
    {
        path: '/compound-event-detectors',
        name: 'compound-event-detectors',
        component: CompountEventDetectors,
        meta: {
            requiresAuth: true,
            requiresAdmin: true
        },
    },
    {
        path: '/maitenance-events',
        name: 'maitenance-events',
        component: MaintenanceEvents,
        meta: {
            requiresAuth: true,
            requiresAdmin: true
        },
    },
    
];

export default routes;
