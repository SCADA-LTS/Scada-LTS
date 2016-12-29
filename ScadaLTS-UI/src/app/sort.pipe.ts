import { Pipe, PipeTransform } from '@angular/core';
@Pipe({
  name: 'sort'
})
export class SortPipe implements PipeTransform {

  private name: any;

  transform(array: any, args: any): any {
    array.sort((a: any, b: any) =>
        a.name - b.name
    );
    return array;
  }

}