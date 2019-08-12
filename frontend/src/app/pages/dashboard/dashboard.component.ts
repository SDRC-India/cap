import {
  Component,
  OnInit,
  Inject
} from '@angular/core';
import {
  DashboardService
} from 'src/app/services/dashboard/dashboard.service';
import {
  ConstantService
} from 'src/app/services/constant/constant.service';
import {
  MAT_DIALOG_DATA,
  MatDialog
} from '@angular/material';
import {
  UtilService
} from 'src/app/services/util/util.service';
import html2canvas from 'html2canvas';
import saveAs from 'save-as';
import { ToastrService } from 'ngx-toastr';
declare var $: any;
@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {

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

  chartData: IChartData[]
  keys: string[] = ['value', 'target']

  showLineChart: boolean = false
  lineChartData: ILineChartData[]
  barChartData: any[] = []
  outcomeIndicators: IOutcomeIndicator[] 
  blocks: ITArea[];
  quickStart:any;
  districtName:any;

  constructor(private service: DashboardService, public dialog: MatDialog, private utilService: UtilService, 
    private toastr: ToastrService) {}

  ngOnInit() {


    this.service.getArea()
      .subscribe(data => {
        this.areas = data
        this.setDefaults();
        this.getDepartments()
        this.getTheme()
      }, err => {
        console.log(err)
      })


  }

  setDefaults() {
    //set default area codes
    this.selectedStateCode = this.areas.filter(d => d.parentAreaCode === ConstantService.COUNTRY_AREA_CODE)[0].code
    let districts: IArea[] = this.areas.filter(d => d.parentAreaCode === this.selectedStateCode)
    this.selectedDistrictCode = districts[0].code
    this.selectedDistrictName = districts[0].areaname;

    this.blocks = this.areas.filter(d => d.parentAreaCode === this.selectedDistrictCode)
    
    let allBlock: ITArea = {
      areaname: "All Blocks",
      code: ConstantService.ALL_BLOCK_CODE,
      parentAreaCode: "all_block"
    }

    this.blocks.splice(0, 0, allBlock);
    this.selectedBlockCode = ConstantService.ALL_BLOCK_CODE



this.service.getOutcomeIndicatorData().subscribe(res=>{
  this.quickStart=res; 
  this.outcomeIndicators = (this.quickStart as any[])[0][this.selectedDistrictName];
})

  }

  getDepartments(){
    this.service.getDepartments('thematic')
      .subscribe(data => {
        this.departments = data
        this.selectedDepartmentId = this.departments.filter(d=>d.id === 0)[0].id
        this.themes = this.departments.filter(d=>d.id === 0)[0].themes
        this.selectedThemeId = this.themes[0].id
        this.fetchChartData()
      }, err => {
        console.log(err)
      })
  }

  getTheme() {
    this.service.getTheme()
      .subscribe(data => {
        this.themes = data
        this.selectedThemeId = this.themes[0].id;
        // this.fetchChartData();
      }, err => {
        console.log(err)
      })
  }

  districtChanged(selectedDistrictCode) {
    this.districtName = this.areas.filter(d => d.code === selectedDistrictCode)[0].areaname;
    
    this.blocks = this.areas.filter(d => d.parentAreaCode === this.selectedDistrictCode)
    
    let allBlock: ITArea = {
      areaname: "All Blocks",
      code: ConstantService.ALL_BLOCK_CODE,
      parentAreaCode: "all_block"
    }

    this.blocks.splice(0, 0, allBlock);
    this.selectedBlockCode = ConstantService.ALL_BLOCK_CODE

    if(this.quickStart){
      this.outcomeIndicators = (this.quickStart as any[])[0][this.districtName];
    }else{
      this.service.getOutcomeIndicatorData();
    }
    
    this.fetchChartData()
  }

  blockChanged(selectedBlockCode) {
    this.selectedBlockCode = selectedBlockCode
    this.fetchChartData()
  }

  async downloadExcel() {

    let selectedDistrict: string = this.areas.filter(d=>d.code === this.selectedDistrictCode)[0].areaname
    let selectedBlock: string = this.selectedBlockCode === ConstantService.ALL_BLOCK_CODE?"All Blocks":this.areas.filter(d=>d.code === this.selectedBlockCode)[0].areaname
    let selectedTheme: string = this.themes.filter(d=>d.id === this.selectedThemeId)[0].name

    let dataForExcel: IDataForExcel[] = [];
    this.barChartData.forEach(data=>{
      let dataList: IBarChartData[] = data;
      if(dataForExcel.length == 0){        
        dataList.forEach(d=>{          
          let dataExcel: IDataForExcel = {          
            name : d.axis,
            baseline: Math.round(d.value),
            mrt: 0,
            target: 0,
            src: d.src,
            unit: "Percentage",
            tp: d.tp
          }
          dataForExcel.push(dataExcel)
        })
      }else if(dataList && dataList.length> 0 && dataList[0].legend == "MRT"){
        for(let i = 0; i < dataList.length;i++){
          dataForExcel[i].mrt = Math.round(dataList[i].value)
        }
      }else if(dataList && dataList.length> 0 && dataList[0].legend == "Target"){
        for(let i = 0; i < dataList.length;i++){
          dataForExcel[i].target = Math.round(dataList[i].value)
        }
      }
    })
    


    
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

  async downloadPDF() {
    let selectedState: string = this.areas.filter(d=>d.code === this.selectedStateCode)[0].areaname
    let selectedDistrict: string = this.areas.filter(d=>d.code === this.selectedDistrictCode)[0].areaname
    let selectedBlock: string = this.selectedBlockCode === ConstantService.ALL_BLOCK_CODE?"All Blocks":this.areas.filter(d=>d.code === this.selectedBlockCode)[0].areaname
    
    


    let svgList: ISVGImage[] = [];
    //taking outcome indicators
    let canvas = await html2canvas(document.getElementById('outcome-div'))
    let base64encodedstring: string = (canvas.toDataURL("image/png", 1));
    let svgImageTheme: ISVGImage = {
      key: 1,
      value: base64encodedstring
    }

    svgList.push(svgImageTheme)

    //taking themes
    canvas = await html2canvas(document.getElementById('theme-div'))
    base64encodedstring = (canvas.toDataURL("image/png", 1));
    svgImageTheme = {
      key: 2,
      value: base64encodedstring
    }

    svgList.push(svgImageTheme)

    //taking chart
    canvas = await html2canvas(document.getElementById('chart-div'))
    base64encodedstring = (canvas.toDataURL("image/png", 1));
    svgImageTheme = {
      key: 3,
      value: base64encodedstring
    }

    svgList.push(svgImageTheme)

    

    this.service.downloadPDF(svgList, selectedState, selectedDistrict, selectedBlock)
    .subscribe(data => {
      saveAs(data, "CAP_" + new Date().getTime().toString() + ".pdf");      
    }, err => {
      console.log(err)
    })
  }

  themeSelected(selectedThemeId: number) {
    this.selectedThemeId = selectedThemeId
    this.fetchChartData();
  }

  getChar(index) {
    if(index === 11){
      return "K1"  
    }
    if(index > 11){
      index--
    }
    return String.fromCharCode(65 + index)
  }

  fetchChartData() {
    if(this.selectedBlockCode === ConstantService.ALL_BLOCK_CODE){
      this.service.getChartData(this.selectedBlockCode, this.selectedThemeId,  this.selectedDepartmentId, this.selectedDistrictCode)
      .subscribe(data => {
        // this.kpiData = this.service.serverDataToKPIData(data)
        this.barChartData = this.service.serverDataToBarChartData(data)
        this.chartData = this.service.serverDataToChartData(data);
      }, err => {
        console.log(err)
        this.barChartData = []
        this.chartData = [];
      })
    }else{
      this.service.getChartData(this.selectedBlockCode, this.selectedThemeId,  this.selectedDepartmentId)
      .subscribe(data => {
        this.barChartData = this.service.serverDataToBarChartData(data)
        this.chartData = this.service.serverDataToChartData(data);
      }, err => {
        console.log(err)
      })
    }
    
  }

  lineChart() {
    this.showLineChart = true;

    this.lineChartData = [];


    this.chartData.forEach(data => {
      let serverData: IServerData[] = data.serverData
      serverData.sort(this.utilService.sortMonthlyArray)


      let lineData: ILineData[] = []
      let baselineValue = null
      serverData.forEach(data => {
        if(data.baseline){
          baselineValue = data.value
        }
          lineData.push({
            axis: data.tp,
            value: Math.round(data.value),
            target: Math.round(data.target),
            isBaseline: data.baseline,
            baselineValue: baselineValue,
            src: data.src
          })
        

      })

      let lcd: ILineChartData = {
        title: serverData[0].indicatorName,
        data: lineData
      }
      this.lineChartData.push(lcd);


    })


    setTimeout(() => {

      if (this.lineChartData.length > 0) {
        $("body").css("overflow", "hidden");
        $(".line-chart-container").css("overflow", "auto");
      }

    }, 500);



  }

  imageDownload(el, id, fileName) {

    // $('.download-chart').css('display', "none");
    html2canvas(document.getElementById(id)).then((canvas) => {
      canvas.toBlob((blob) => {
        $('.download-chart').css('display', "block");
        saveAs(blob, fileName + ".jpg");
      });
    });
  }

  openIndividualLineChart(serverData: IServerData[]) {

    let sData: IServerData[] = Array.from(serverData);

    sData.sort(this.utilService.sortMonthlyArray)

    this.showLineChart = true;
    this.lineChartData = [];

    let lineData: ILineData[] = []
    let baselineValue: number = null;
    sData.forEach(data => {

      if(data.baseline){
        baselineValue = data.value
      }
        lineData.push({
          axis: data.tp,
          value: Math.round(data.value),
          target: Math.round(data.target),
          isBaseline: data.baseline,
          baselineValue: baselineValue,
          src: data.src
        })
      
      

    })

    let lcd: ILineChartData = {
      title: serverData[0].indicatorName,
      data: lineData
    }
    this.lineChartData.push(lcd);

  }

  closeLineChartPopup() {


    this.lineChartData = []
    this.showLineChart = false;
    $("body").css("overflow", "auto");
    $(".line-chart-container").css("overflow", "hidden");

  }

  departmentChanged(selectedDepartmentId){
    // alert(selectedDepartmentId)

    this.selectedDepartmentId = selectedDepartmentId
    this.themes = this.departments.filter(d=>d.id === selectedDepartmentId)[0].themes
    this.selectedThemeId = this.themes[0].id
    this.fetchChartData()
  }

  allIndicatorlineChart(){
    
  }


}

export interface DialogData {
  title: string,
    content: string
}
@Component({
  selector: 'dialog-data-example-dialog',
  templateUrl: 'dialog-data-example-dialog.html',
})
export class DialogDataExampleDialog {
  constructor(@Inject(MAT_DIALOG_DATA) public data: DialogData) {}
}