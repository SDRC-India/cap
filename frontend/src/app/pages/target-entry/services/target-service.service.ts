import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { ConstantService } from 'src/app/services/constant/constant.service';

@Injectable({
  providedIn: 'root'
})
export class TargetServiceService {

  districtCode:string;
  sectorId:number;
  financialYearId:number;
  targetDatas:any;
  constructor( private httpClient: HttpClient) { }


  getArea(){
    return this.httpClient.post < IArea[] > (ConstantService.AREA, {})
  }
  getTheme() {
    return this.httpClient.post < ITheme[] > (ConstantService.THEME,{})
  }

  getTimeperiodData(){
    return this.httpClient.get(ConstantService.GET_FINANCIALYEAR);
  }

  getTagetTableData(parentAreaCode,sectorId,timePeridId){
    return this.httpClient.get(ConstantService.GETTARGET_DATA + '?parentAreaCode='+parentAreaCode +'&sectorId='+sectorId +'&timePeridId='+timePeridId);
  }
  getKeys(obj): any[]{
    return Object.keys(obj);
    }

   saveTagetData(data){
     return this.httpClient.post(ConstantService.SAVE_TARGETDATA, data,{responseType: 'text'})
   } 
}
