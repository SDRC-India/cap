import { Pipe, PipeTransform } from '@angular/core';

/**
 * Generated class for the PipesBaselineObjectPipe pipe.
 *
 * See https://angular.io/api/core/Pipe for more info on Angular Pipes.
 */
@Pipe({
  name: 'pipesBaselineObject',
})
export class PipesBaselineObjectPipe implements PipeTransform {
  /**
   * Takes a value and makes it lowercase.
   */
  transform(objectList: IData[]) {
    return objectList.filter(obj=>obj.baseline == true);
  }
}
