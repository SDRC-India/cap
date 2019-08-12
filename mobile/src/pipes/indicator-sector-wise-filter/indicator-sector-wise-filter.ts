import { Pipe, PipeTransform } from '@angular/core';

/**
 * Generated class for the IndicatorSectorWiseFilterPipe pipe.
 *
 * See https://angular.io/api/core/Pipe for more info on Angular Pipes.
 */
@Pipe({
  name: 'indicatorSectorWiseFilter',
})
export class IndicatorSectorWiseFilterPipe implements PipeTransform {
 
  transform(indcatorsList : IIndicator[],sector : ISector) {

    return indcatorsList.filter(indicator=>(indicator.sectors[0].id==sector.id || (indicator.sectors.length>1?indicator.sectors[1].id==sector.id:false)))
  }
}
