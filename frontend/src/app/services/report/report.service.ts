import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { ConstantService } from '../constant/constant.service';

@Injectable({
  providedIn: 'root'
})
export class ReportService {constructor(private http: HttpClient) { }

getArea() {
  return this.http.post < IArea[] > (ConstantService.AREA, {})
}

downloadReport(selectedDistrictCode: string) {
  return this.http.post(ConstantService.REPORT_DOWNLOAD + "?areaCode=" + selectedDistrictCode, {}, {
    responseType: "blob"
  });
}
}
