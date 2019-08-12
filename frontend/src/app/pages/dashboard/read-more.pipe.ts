import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'readMore'
})
export class ReadMorePipe implements PipeTransform {

  transform(value: string, args?: any): any {
    if(value.length > 100){
     return value.trim().substring(0, 100)+'...'
      
    }else{
    return value.trim();
      
    }
  }

}
