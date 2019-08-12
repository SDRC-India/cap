import { Pipe, PipeTransform } from '@angular/core';
import { DateTime } from 'ionic-angular';

/**
 * Generated class for the PipesMrtObjectPipe pipe.
 *
 * See https://angular.io/api/core/Pipe for more info on Angular Pipes.
 */
@Pipe({
  name: 'pipesMrtObject',
})
export class PipesMrtObjectPipe implements PipeTransform {
  /**
   * Takes a value and makes it lowercase.
   */
  transform(objectList: CustomViewTableData,areaName:string) {
    let dataObj2:IData[] = objectList.indicatorData.get(areaName).sort((a: IData, b: IData) => {
     let tp1 = new Date(a.tp);
     let tp2 = new Date(b.tp);

      if (tp1 > tp2) {
        return -1
      } else if (tp1 < tp2) {
        return 1
      } else {
        return 0;
      }
    })
    return Array.of(dataObj2[0]);
  }
}
