import EventHandlers from '../views/Events/EventHandlers'
import ScheduledEvents from '../views/Events/ScheduledEvents'
import CompountEventDetectors from '../views/Events/CompoundEventDetectors'
import MaintenanceEvents from '../views/Events/MaintenanceEvents'

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
