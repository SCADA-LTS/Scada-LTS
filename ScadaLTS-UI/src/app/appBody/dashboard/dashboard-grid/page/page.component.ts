
import { Component, Input, Output, ViewChild, OnInit, EventEmitter } from '@angular/core';
import { DashboardItem } from '../item/dashboard-item';
import { ItemService } from '../item/item.service';
import { ViewPage } from '../classes/view-page';
import { FrameComponent } from '../item/frame.component';

@Component({
    selector: 'app-dashboard-grid-page',
    templateUrl: 'page.component.html',
    styles: ['md-select { width:33%;}']
})
export class DashboardGridPageComponent implements OnInit {

    @Input() viewPage: ViewPage;
    @Input() editMode: boolean;
    @Output() saveViewPage = new EventEmitter<ViewPage>();
    @ViewChild(FrameComponent) frameComponent: FrameComponent;
    items: DashboardItem[];
    selectedId: number;
    componentTypes;

    ngOnInit() {

        this.items = [];
        this.loadComponents();
        this.componentTypes = ItemService.getDashboardComponentsList();

    }

    loadComponents() {

        for (let x = 0; x < this.viewPage.data.length; x++) {
            const item = this.viewPage.data[x];
            this.items.push(ItemService.getDashboardItem(item.type, item.data));
        }

    }

    addComponent() {
        if (this.selectedId !== undefined) {
            const selectedComponent = this.componentTypes[this.selectedId];
            selectedComponent.data.number = this.items.length + 1;
            this.items.push(ItemService.getDashboardItem(selectedComponent.type, selectedComponent.data));
            this.viewPage.data.push({ type: selectedComponent.type, data: selectedComponent.data });
            console.debug(this.viewPage);
        }


    }

    savedItem(item: DashboardItem) {

        this.saveViewPage.emit(this.viewPage);

    }

    removedItem(item: DashboardItem) {

        this.viewPage.data = this.viewPage.data.filter(page => page.data.number !== item.data.number);

        this.frameComponent.clearView();
        this.ngOnInit();

        this.saveViewPage.emit(this.viewPage);
    }

}
