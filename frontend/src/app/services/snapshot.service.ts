import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import {ConstantService} from './constant/constant.service';
@Injectable({
  providedIn: 'root'
})
export class SnapshotService {
  showCard:boolean=true;
  showMainContent:boolean=false;
  tableData:any;
  selectedIndicatorName:any;
  selectedParentArea:any;
  selectedIndicatorId:any;
  selectedAreaLevelId:any;
 selectedSectorId:any;
 selectedDepartmentId:any;
 
  constructor(private http: HttpClient) { }

  getArea() {
    return this.http.post < IArea[] > (ConstantService.AREA, {})
  }

  getDepartments() {
    return this.http.post < IDepartment[] > (ConstantService.DEPARTMENTS, {})
  }
  getCardViewData(areaCode,departmentId,sectorId,areaLevelId){
    return this.http.get(ConstantService.GETCARDVIEW_DATA+'?areaCode=' +areaCode +'&departmentId='+departmentId +'&sectorId='+sectorId +'&areaLevelId='+areaLevelId);
  }
  getCardViewTimePeriods(parentAreaCode, indicatorId){
    return this.http.get(ConstantService.GETVIEWCARD_TIMEPERIOD+'?parentAreaCode='+parentAreaCode +'&indicatorId='+indicatorId)
  }
  getCardViewTableData(parentAreaCode,indicatorId,timePeridId,areaLevelId ){
    return this.http.get(ConstantService.GETCARDVIEW_TABLEDATA+'?parentAreaCode='+parentAreaCode +'&indicatorId='+indicatorId +'&timePeridId='+timePeridId +'&areaLevelId='+areaLevelId);
  }
}
