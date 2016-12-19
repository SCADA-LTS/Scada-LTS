import { Pipe, PipeTransform } from '@angular/core';
@Pipe({
  name: 'sort'
})
export class SortPipe implements PipeTransform {

  private value: any;

  transform(array: any, args: any): any {
    array.sort((a: any, b: any) =>
      a.value - b.value
    );
    return array;
  }

}
