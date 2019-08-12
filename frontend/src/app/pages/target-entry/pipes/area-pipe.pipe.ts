import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'areaPipe'
})
export class AreaPipePipe implements PipeTransform {

  transform(area: IArea[], parentAreaCode: string): IArea[] {
    if(area != undefined){
      return area.filter(d=> d.parentAreaCode === parentAreaCode);
    }else{
      return null;
    }
  }

}
