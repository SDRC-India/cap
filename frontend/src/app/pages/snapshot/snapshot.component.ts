import { Component, OnInit } from '@angular/core';
import { SnapshotService } from 'src/app/services/snapshot.service';
import { ConstantService } from 'src/app/services/constant/constant.service';
import { DashboardService } from 'src/app/services/dashboard/dashboard.service';
import saveAs from 'save-as';
import html2canvas from 'html2canvas';
import { UtilService } from 'src/app/services/util/util.service';
import {MatSelectModule} from '@angular/material/select'
declare var $:any;

@Component({
  selector: 'app-snapshot',
  templateUrl: './snapshot.component.html',
  styleUrls: ['./snapshot.component.css']
})
export class SnapshotComponent implements OnInit {
  data: any;
  areas: IArea[]
  departments: IDepartment[]
  themes: ITheme[];
  selectedStateCode: string;
  selectedDistrictCode: string;
  selectedBlockCode: string;
  selectedThemeId: number;
  selectedDepartmentId: number
  selectedDistrictName:string;
  districtSelected:string;
  indicatorModels:any;
  cardlist:any[]=[];
  selectedIndicatorId:any;
  tableData:any;
  tableColumns:any;
  tabData: any;
  tabColumns: any;
  tableColumnIndex: any[];
  rowdata: any;
  timepriodList:any;
  showMainContent:boolean= false;
  cardTimePeriodList:any;
  selectedAreaLevelId:number;
  districtName:any;
  outcomeIndicators: IOutcomeIndicator[] ;
  selectedTimePeriodId:any;
 
  
  constructor(public service: DashboardService, private constantService: ConstantService, private utilService: UtilService ) { }

  ngOnInit() {
    this.service.getArea().subscribe(data=>{
      this.areas = data;
      this.setDefaults();
      this.getDepartments();
      this.service.showCard= true;
      this.service.showMainContent= false;
    })
  }

  setDefaults() {
    //set default area codes
    this.selectedStateCode = this.areas.filter(d => d.parentAreaCode === ConstantService.COUNTRY_AREA_CODE)[0].code
    let districts = this.areas.filter(d => d.parentAreaCode === this.selectedStateCode)
    this.service.selectedParentArea = this.selectedStateCode;
    this.selectedDistrictName = districts[0].areaname;
    this.service.selectedAreaLevelId = 2;

  }

  getDepartments(){
    this.service.getDepartments('snapshot')
      .subscribe(data => {
        this.departments = data
        this.service.selectedDepartmentId = this.departments.filter(d=>d.id === 0)[0].id
        this.themes = this.departments.filter(d=>d.id === 0)[0].themes
        this.service.selectedSectorId = this.themes[0].id
        this.cardData();
     
      }, err => {
        console.log(err)
      })
  }
  getTheme() {
    this.service.getTheme()
      .subscribe(data => {
        this.themes = data
        this.service.selectedSectorId = this.themes[0].id;
      
      }, err => {
        console.log(err)
      })
  }

  themeSelected(selectedThemeId: number) {
    this.service.selectedSectorId = selectedThemeId
    this.cardData();
  }



  districtChanged(selectedDistrictCode, event) {
    if(event.isUserInput==true){
      if(selectedDistrictCode != null){
        this.service.selectedParentArea = selectedDistrictCode.code;
        this.service.selectedAreaLevelId = selectedDistrictCode.areaLevelId;
        this.cardData();
      }else{
        this.setDefaults();
        this.cardData();
      }
     
    }
    // this.districtName = this.areas.filter(d => d.code === selectedDistrictCode)[0].areaname;
   
    // this.getDepartments();

  }


  departmentChanged(selectedDepartmentId){

    this.selectedDepartmentId = selectedDepartmentId
    this.themes = this.departments.filter(d=>d.id === selectedDepartmentId)[0].themes
    this.service.selectedSectorId = this.themes[0].id
   this.cardData();
  }

  cardData(){
    // this.service.selectedSectorId=this.selectedThemeId;
    // this.service.selectedParentArea=this.selectedStateCode;
    // this.service.selectedDepartmentId= this.selectedDepartmentId;
    // this.service.selectedAreaLevelId= this.selectedAreaLevelId;

    this.service.getCardViewData(this.service.selectedParentArea, this.service.selectedDepartmentId, this.service.selectedSectorId, this.service.selectedAreaLevelId).subscribe(data=>{
      this.indicatorModels = data;
      this.cardlist= this.indicatorModels.indicators;
    })
  }
  showTable(cardvalue){
    $("#theme-div").hide();
    this.service.showMainContent= true;
    this.service.showCard= false;
    this.service.selectedIndicatorName = cardvalue.indicatorName;
    this.service.selectedParentArea= this.selectedDistrictCode;
    this.service.selectedIndicatorId= cardvalue.indicatorId;
    this.selectedDepartmentId = this.service.selectedDepartmentId;  
    this.service.selectedDepartmentId = cardvalue.departmentId;
    this.selectedThemeId = this.service.selectedSectorId;
    this.service.selectedSectorId= cardvalue.sectorId;
    this.selectedTimePeriodId= cardvalue.timeperiodId;

    this.service.getCardViewTimePeriods(this.service.selectedParentArea, this.service.selectedIndicatorId, this.service.selectedDepartmentId, this.service.selectedSectorId, this.service.selectedAreaLevelId ).subscribe(res=>{
      this.cardTimePeriodList= res;

    })
    this.service.getCardViewTableData(this.service.selectedParentArea, this.service.selectedIndicatorId, cardvalue.timeperiodId, this.service.selectedAreaLevelId,  this.service.selectedDepartmentId, this.service.selectedSectorId).subscribe(res=>{
        this.tableData =res;
        this.tableColumns = Object.keys(this.tableData[0]);
      })
  }

  selectTimeperiod(selectedTimePeriod){
 
    this.service.getCardViewTableData(this.service.selectedParentArea, this.service.selectedIndicatorId, selectedTimePeriod, this.service.selectedAreaLevelId, this.service.selectedDepartmentId, this.service.selectedSectorId).subscribe(res=>{
      this.tableData =res;
    })
  
  
  }

  showHide(){
  $("#theme-div").show();
  this.service.selectedDepartmentId = this.selectedDepartmentId;
  if(this.service.selectedSectorId==0){
    this.service.selectedSectorId = this.themes[0].id
  }else{
    this.service.selectedSectorId = this.selectedThemeId;
  }
  this.service.showCard= true;
  this.service.showMainContent= false;

  }

  async downloadExcel() {

    let selectedDistrict: string = this.areas.filter(d=>d.code === this.selectedDistrictCode)[0].areaname
    let selectedBlock: string = this.selectedBlockCode === ConstantService.ALL_BLOCK_CODE?"All Blocks":this.areas.filter(d=>d.code === this.selectedBlockCode)[0].areaname
    let selectedTheme: string = this.themes.filter(d=>d.id === this.selectedThemeId)[0].name

    let dataForExcel: IDataForExcel[] = [];
    // this.barChartData.forEach(data=>{
    //   let dataList: IBarChartData[] = data;
    //   if(dataForExcel.length == 0){        
    //     dataList.forEach(d=>{          
    //       let dataExcel: IDataForExcel = {          
    //         name : d.axis,
    //         baseline: Math.round(d.value),
    //         mrt: 0,
    //         target: 0,
    //         src: d.src,
    //         unit: "Percentage",
    //         tp: d.tp
    //       }
    //       dataForExcel.push(dataExcel)
    //     })
    //   }else if(dataList && dataList.length> 0 && dataList[0].legend == "MRT"){
    //     for(let i = 0; i < dataList.length;i++){
    //       dataForExcel[i].mrt = Math.round(dataList[i].value)
    //     }
    //   }else if(dataList && dataList.length> 0 && dataList[0].legend == "Target"){
    //     for(let i = 0; i < dataList.length;i++){
    //       dataForExcel[i].target = Math.round(dataList[i].value)
    //     }
    //   }
    // })
    


    
    let svgList: string[] = []
    let canvas = await html2canvas(document.getElementById('outcome-div'))
    let base64encodedstring: string = (canvas.toDataURL("image/png", 1));

    svgList.push(base64encodedstring)


    canvas = await html2canvas(document.getElementById('chart-div'))
    base64encodedstring = (canvas.toDataURL("image/png", 1));

    svgList.push(base64encodedstring)


    let outcome: IDataForExcel[] = [];

    this.outcomeIndicators.forEach(d=>{
      let dataExcel: IDataForExcel = {          
        name : d.name,
        baseline: d.value,
        mrt: 0,
        target: 0,
        src: d.src,
        unit: "Percentage",
        tp: d.tp
      }
      outcome.push(dataExcel)
    })


    
    

    let svgImageTheme: ISVGIMageForExcel = {
      districtName: selectedDistrict,
      blockName: selectedBlock,
      themeName: selectedTheme,
      svg: svgList,
      dataForExcel: dataForExcel,
      outcome: outcome      
    }

    this.service.downloadEXCEL(svgImageTheme)
    .subscribe(data => {
      saveAs(data, "CAP_" + new Date().getTime().toString() + ".xlsx");      
    }, err => {
      console.log(err)
    })
  }

  getMRT(serverData: IServerData[]): number{
    serverData.sort(this.utilService.sortMonthlyArray);
    
    if(serverData.length > 1){
      let data: IServerData = serverData.reverse()[0]
      return data.value;
    }else{
      return 0;
    }
  }
  allDistricts(){

  }




}
