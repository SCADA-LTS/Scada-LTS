
import { ViewPage } from './classes/view-page';

export const MOCKPAGES: ViewPage[] = [
    {
        id: 1, name: 'Test 1', data: [
            {
                type: 'incrementator',
                data: {
                    number: 1,
                    name: 'Example Incrementator 1',
                    datapointXid: 'DP_EX_010203'
                }
            },
            {
                type: 'incrementator',
                data: {
                    number: 2,
                    name: 'Example Incrementator 2',
                    datapointXid: 'DP_EX_010203'
                }
            }
        ]
    },
    {
        id: 2, name: 'Test 2', data: [
            {
                type: 'incrementator',
                data: {
                    number: 2,
                    name: 'Example Incrementator 2',
                    datapointXid: 'DP_EX_010203'
                }
            },
            {
                type: 'camera',
                data: {
                    number: 1,
                    label: 'Camera 1',
                    cameraPath: 'https://kamilmysliwiec.com/wp-content/themes/src/img/background-0@2x.png'
                }
            }
        ]
    },
    { id: 3, name: 'Test 3', data: [] },
];
