import UserList from '../views/Users/UserList';
import UserProfiles from '../views/Users/UserProfiles';
import RecipientList from '../views/Users/RecipientList';

export const routes = [
    {
        path: '/users',
        name: 'users',
        component: UserList,
        meta: {
            requiresAuth: true
        },
    },
    {
        path: '/recipient-list',
        name: 'recipient-list',
        component: RecipientList,
        meta: {
            requiresAuth: true,
            requiresAdmin: true
        },
    },
    {
        path: '/user-profiles',
        name: 'user-profiles',
        component: UserProfiles,
        meta: {
            requiresAuth: true,
            requiresAdmin: true
        },
    },
];

export default routes;
