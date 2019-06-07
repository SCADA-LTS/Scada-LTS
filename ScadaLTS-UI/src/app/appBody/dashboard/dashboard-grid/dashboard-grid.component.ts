import { Component, ViewChild, OnInit } from '@angular/core';
import { DashboardGridPageComponent } from './page/page.component';
import { DashboardGridService } from './dashboard-grid.service';
import { StorageService } from '../utils/storage.service';
import { ViewPage } from './classes/view-page';
import { MOCKPAGES } from './dashboard.mock';

@Component({
    selector: 'app-dashboard-grid',
    templateUrl: 'dashboard-grid.component.html',
    styles: [`md-sidenav-container { height:calc(100vh - 64px); display: block} #viewPage-name { text-align: center;}`]
})
export class DashboardGridComponent implements OnInit {

    /* viewPage = Current Displayed page */
    viewPage: ViewPage;
    // viewPages = MOCKPAGES; // Mocked data form file //
    viewPages: ViewPage[]; // Standard variable to handle data from Scada //
    editMode: boolean;
    dataPointExportID: string;

    @ViewChild(DashboardGridPageComponent) dashboardGridPageComponent: DashboardGridPageComponent;

    constructor(private dashboardGridService: DashboardGridService, private storageService: StorageService) { }

    ngOnInit(): void {
        this.dataPointExportID = this.storageService.read('config');
        /* Comment or uncomment this section to test data with or without Scada */
        this.viewPages = [];
        this.dashboardGridService.authenticate();
        this.dashboardGridService.getViewPages(this.dataPointExportID).then(response => this.viewPages = response);
        /* --- End section --- */
    }

    updateViewPage(viewPages: ViewPage[]) {
        this.viewPages = viewPages;

    }

    openViewPage(viewPage: ViewPage) {
        this.viewPage = viewPage;
        if (this.dashboardGridPageComponent !== undefined) {
            this.dashboardGridPageComponent.viewPage = this.viewPage;
            this.dashboardGridPageComponent.ngOnInit();
        }

    }

    saveConfiguration() {
        this.storageService.write('config', this.dataPointExportID);
        this.dashboardGridService.setViewPages(this.dataPointExportID, JSON.stringify(this.viewPages));
        console.debug(JSON.stringify(this.viewPages));
    }

    saveViewPage(viewPage: ViewPage) {
        this.viewPage = viewPage;
        const id = this.viewPage.id - 1;
        this.viewPages[id] = viewPage;

    }

    reloadConfiguration() {
        this.dashboardGridService.getViewPages(this.dataPointExportID).then(response => this.viewPages = response);
    }

}
