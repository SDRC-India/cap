import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { ConstantService } from 'src/app/services/constant/constant.service';

@Injectable({
  providedIn: 'root'
})
export class DataEntryService {

  tableData:any;
  frequency:any;
  timePeriod:any;
  selectedFrequencyName:any;
  SelectedTimePeriodName:any;
  constructor(private http: HttpClient) { }

  getArea() {
    return this.http.post < IArea[] > (ConstantService.AREA, {})
  }

  downloadTemplate() {
    return this.http.post(ConstantService.TEMPLATE_DOWNLOAD, {}, {
      responseType: "blob"
    });
  }

  uploadDataEntryTemplate(fileModal){
    return this.http.post(ConstantService.TEMPLATE_UPLOAD,fileModal);
  }
  getFrequencyData(){
    return this.http.get(ConstantService.GET_FREQUENCY);
  }
  getTimeperiodData(id){
    return this.http.get(ConstantService.GET_TIMEPERIOD + id);
  }
  getTableData(id){
    return this.http.get(ConstantService.GET_DATA+'?id='+ id, {responseType:"json"});
    // return this.http.get('assets/newDataEntry.json');
  }

  getFormData(data){
    return this.http.post(ConstantService.FORM_DATA, data);
  }
}
