import { Directive, ViewContainerRef } from '@angular/core';

@Directive({
    selector: '[appItemHost]',
})
export class ItemDirective {
    constructor(public viewContainerRef: ViewContainerRef) { }
}


