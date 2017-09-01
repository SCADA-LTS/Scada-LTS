import { Component, Input, OnInit, ViewChild, ComponentFactoryResolver, EventEmitter, Output } from '@angular/core';
import { DashboardItem } from './dashboard-item';
import { ItemDirective } from './item.directive';
import { MdDialog } from '@angular/material';
import { ItemEditorDialogComponent } from '../dialogs/item-editor-dialog.component';

@Component({
    selector: 'app-item-frame',
    templateUrl: 'frame.component.html',
    styles: [`md-card { width:96%;}`],
})
/**
 * Frame Component
 * This is a handler for custom ItemComponents.
 * It loads a data and generate a special componet based on the recived data
 */
export class FrameComponent implements OnInit {

    title: string;
    @Input() item: DashboardItem;
    @Input() editMode: boolean;
    @Output() savedItem = new EventEmitter<DashboardItem>();
    @Output() removedItem = new EventEmitter<DashboardItem>();
    @ViewChild(ItemDirective) itemHost: ItemDirective;

    constructor(
        private componentFactoryResolver: ComponentFactoryResolver,
        private dialog: MdDialog
    ) { }

    ngOnInit() {
        this.loadComponent();
    }

    loadComponent() {

        const viewContainerRef = this.itemHost.viewContainerRef;
        viewContainerRef.clear();
        const componentFactory = this.componentFactoryResolver.resolveComponentFactory(this.item.component);
        const componentRef = viewContainerRef.createComponent(componentFactory);
        (<DashboardItem>componentRef.instance).data = this.item.data;

        // TODO: Make this card title unique for all components for example: ... data: {label:'title', ...}
        // This is a section to display a card title based on the data variable //
        if (this.item.data.label !== undefined) {
            this.title = this.item.data.label;
        }
        if (this.item.data.name !== undefined) {
            this.title = this.item.data.name;
        }

    }

    clearView() {
        this.itemHost.viewContainerRef.clear();
    }

    editDialog() {
        console.debug(this.item.data);
        const dialogReference = this.dialog.open(ItemEditorDialogComponent, { data: this.item.data });
        dialogReference.afterClosed().subscribe(result => {
            if (result !== undefined) {
                // Create a two arrays to handle Object Keys and Values //
                const labels: string[] = [];
                const values: any[] = [];
                result.forEach(element => {
                    labels.push(element.label);
                    values.push(element.value);
                });

                // Update this keys in orginal item.data variable //
                for (let x = 0; x < labels.length; x++) {
                    this.item.data[labels[x]] = values[x];
                }
                this.savedItem.emit(this.item);
            }
        });

    }

    delete() {
        this.removedItem.emit(this.item);
    }
}
