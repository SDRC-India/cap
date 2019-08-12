import { Component, OnInit } from '@angular/core';
import { ReportService } from 'src/app/services/report/report.service';
import { ConstantService } from 'src/app/services/constant/constant.service';
import saveAs from 'save-as';
import { AppService } from 'src/app/app.service';
import { StorageService } from 'src/app/services/storage/storage.service';

@Component({
  selector: 'app-report',
  templateUrl: './report.component.html',
  styleUrls: ['./report.component.css']
})
export class ReportComponent implements OnInit {
 


  areas: IArea[]

  selectedStateCode: string;
  selectedDistrictCode: string;
  userDetails:any;

  constructor(private service: ReportService, public appService: AppService, private storageService: StorageService) { }

  ngOnInit() {

    if(!(this.appService.checkUserAuthorization(['DATA_COLLECTION']) || this.appService.checkUserAuthorization(['USER_MGMT_ALL_API']))){
      this.service.getArea()
      .subscribe(data => {
        this.areas = data
        this.setDefaults();
      }, err => {
        console.log(err)
      })           
    }

    
  }

  setDefaults() {
    //set default area codes
    this.selectedStateCode = this.areas.filter(d => d.parentAreaCode === ConstantService.COUNTRY_AREA_CODE)[0].code
    let districts: IArea[] = this.areas.filter(d => d.parentAreaCode === this.selectedStateCode)
    this.selectedDistrictCode = districts[0].code
  }
  
  districtChanged(selectedDistrictCode) {
    this.selectedDistrictCode = selectedDistrictCode    
  }


  downloadReport(){
    this.userDetails = JSON.parse(this.storageService.get(ConstantService.USER_DETAILS));
    console.log(this.userDetails['sessionMap'].area);
    let districtCode: string = this.userDetails['sessionMap'].area;
    if(!(this.appService.checkUserAuthorization(['DATA_COLLECTION']) || this.appService.checkUserAuthorization(['USER_MGMT_ALL_API']))){
      let district: IArea = this.areas.filter(d => d.code === this.selectedDistrictCode)[0]
      districtCode = district.code
    }
    
    this.service.downloadReport(districtCode)
    .subscribe(data => {
      saveAs(data, "Report_" + new Date().getTime().toString() + ".xlsx");      
    }, err => {
      console.log(err)
    })
  }

}
