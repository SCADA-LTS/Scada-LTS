import { Type } from '@angular/core';

/**
 * Dashboard Item
 * Item class for every component used inside ScadaLTS-Dashboard
 * Most general object of every Dashboard single Item.
 */
export class DashboardItem {
    constructor(public component: Type<any>, public data: any) { }
}
