import { Injectable } from '@angular/core';
import { ViewPage } from './classes/view-page';
import { ConnectionService } from '../utils/connection.service';

@Injectable()
export class DashboardGridService {

    constructor(private connectionService: ConnectionService) { }

    authenticate(): void {

        this.connectionService.authenticate('admin', 'admin');
    }

    /**
     * Promise: get View Pages Array
     * Connect to remote server and load data from alphanumeric datapoint.
     * Parse the response and retrun the viewPageArray promise data.
     * @param datapointExportID - Data Point Export ID with saved configuration.
     */
    getViewPages(datapointExportID: string): Promise<ViewPage[]> {

        return this.connectionService.getDataFromRemote('point_value/getValue/', datapointExportID).then(
            response => {
                let temp = response.json().value;
                temp = JSON.parse(temp);
                return temp.viewPage;
            }
        );
    }

    /**
     *  Set View Pages and save them into remote data point
     * Connect to remote data point and save data to remote server.
     * @param datapointExportID - Data Point Export ID to save configuration.
     * @param viewPageData - configuration data.
     */
    setViewPages(datapointExportID: string, viewPageData: string) {

        this.connectionService.postDataToRemote('point_value/setValue/', datapointExportID + '/3/{"viewPage":' + viewPageData + '}');

    }

}
