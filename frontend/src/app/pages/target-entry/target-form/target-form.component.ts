import { Component, OnInit } from '@angular/core';
import { TargetServiceService } from '../services/target-service.service';
import { ConstantService } from 'src/app/services/constant/constant.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-target-form',
  templateUrl: './target-form.component.html',
  styleUrls: ['./target-form.component.css']
})
export class TargetFormComponent implements OnInit {
  areas:any;
  themes: ITheme[];
  selectedState:any;
  selectedDistrict:any;
  selectedDistrictName:string;
  selectedSector:any;
  selectedFinancialYear:any;
  states:any;
  districts:any;
  sectors:any;
  financialYear:any;

  service:TargetServiceService
  constructor(private serviceprovider: TargetServiceService, private route: Router) {
    this.service= serviceprovider
   }

  ngOnInit() {
    this.service.getArea().subscribe(data=>{
      this.areas = data;
      this.setDefaults();
      this.getTheme();
      this.getTimeperiodData();
    });
  }

  setDefaults() {
    //set default area codes
    this.selectedState = this.areas.filter(d => d.parentAreaCode === ConstantService.COUNTRY_AREA_CODE)[0].code
    let districts: IArea[] = this.areas.filter(d => d.parentAreaCode === this.selectedState)
    this.service.districtCode = districts[0].code
    this.selectedDistrictName = districts[0].areaname;
  }

  getTheme(){
    this.service.getTheme().subscribe(res =>{
      this.themes = res;
      this.service.sectorId=this.themes[0].id; 
    });
  }
  getTimeperiodData(){
    this.service.getTimeperiodData().subscribe(time=>{
     this.financialYear= time;
    })
  }

  stateChange(){

  }
  districtChange(dist, event){
    if(event.isUserInput==true){
      this.service.districtCode = dist.code;
    }
  }
  sectorChange(sector, event){
    if(event.isUserInput==true){
      this.service.sectorId = sector.id;
    }
  }
  financialYearChange(financialYear, event){
    if(event.isUserInput==true){
      this.service.financialYearId = financialYear.id;
    }
  }
  goTodataEntryForm(){
    // this.service.getTagetTableData(this.service.districtCode, this.service.sectorId, this.service.financialYearId).subscribe(res=>{
    //   this.service.targetDatas = res;
    // })
    this.route.navigate(['/target-entry/target-entry-form']);
  }
}
