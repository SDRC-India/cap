import {
  Injectable
} from '@angular/core';
import {
  HttpClient
} from '@angular/common/http';
import {
  ConstantService
} from '../constant/constant.service';
import {
  UtilService
} from '../util/util.service';

@Injectable({
  providedIn: 'root'
})
export class DashboardService {

  showCard:boolean=true;
  showMainContent:boolean=false;
  tableData:any;
  selectedIndicatorName:any;
  selectedParentArea:any;
  selectedIndicatorId:any;
  selectedAreaLevelId:any;
  selectedSectorId:any;
  selectedDepartmentId:any;
  
  
  constructor(private http: HttpClient, private utilService: UtilService) {}

  getChartData(areaCode, sectorId, departmentId, districtAreaCode?: string) {
    if(areaCode === ConstantService.ALL_BLOCK_CODE){
      areaCode = districtAreaCode
      //The following three lines are going to be removed after demo
      // if(districtAreaCode === 'IND010004' && sectorId === 1){
      //   areaCode = "IND010004013"
      // }
      return this.http.post < IServerData[] > (ConstantService.DATA + "?areaCode=" + areaCode + "&sectorId=" + sectorId +"&departmentId=" +departmentId, {})
    }else{
      return this.http.post < IServerData[] > (ConstantService.DATA + "?areaCode=" + areaCode + "&sectorId=" + sectorId +"&departmentId=" +departmentId,{})
    }
    
  }

  getArea() {
    return this.http.post < IArea[] > (ConstantService.AREA, {})
  }

  getDepartments(typeName) {
    return this.http.get < IDepartment[] > (ConstantService.DEPARTMENTS+'?typeName=' +typeName)
  }

  getTheme() {
    return this.http.post < ITheme[] > (ConstantService.THEME,{})
  }

 

  getCardViewData(areaCode,departmentId,sectorId,areaLevelId){
    return this.http.get(ConstantService.GETCARDVIEW_DATA+'?areaCode=' +areaCode +'&departmentId='+departmentId +'&sectorId='+sectorId +'&areaLevelId='+areaLevelId);
  }
  getCardViewTimePeriods(parentAreaCode, indicatorId, departmentId, sectorId, areaLevelId){
    return this.http.get(ConstantService.GETVIEWCARD_TIMEPERIOD+'?parentAreaCode='+parentAreaCode +'&indicatorId='+indicatorId +'&departmentId='+departmentId +'&sectorId='+sectorId +'&areaLevelId='+areaLevelId);
  }
  getCardViewTableData(parentAreaCode,indicatorId,timePeridId,areaLevelId,departmentId,sectorId){
    return this.http.get(ConstantService.GETCARDVIEW_TABLEDATA+'?parentAreaCode='+parentAreaCode +'&indicatorId='+indicatorId +'&timePeridId='+timePeridId +'&areaLevelId='+areaLevelId +'&departmentId='+departmentId +'&sectorId='+sectorId);
  }

  serverDataToChartData(serverData: IServerData[]): IChartData[] {
    let chartDataList: IChartData[] = []
    let iMap: Map < string, IServerData[] > = new Map < string,
      IServerData[] > ();
    //keep data in a map
    serverData.forEach(data => {

        let serverDataList: IServerData[] = iMap.get(data.indicatorName)
        if (serverDataList) {
          serverDataList.push(data)
        } else {
          serverDataList = []
          serverDataList.push(data)
        }

        iMap.set(data.indicatorName, serverDataList)

    })

    iMap.forEach((value: IServerData[], key: string) => {

      value.sort(this.utilService.sortMonthlyArray);
      let data: IServerData = value.reverse()[0];
      let target = data.target - data.value
      if (target < 0) {
        target = 0
      }
      let chartData: IChartData = {
        axis: data.indicatorName,
        value: Math.round(data.value),
        target: target,
        src: data.src,
        tp: data.tp,
        targetOriginal: Math.round(data.target),
        serverData: value
      }
      chartDataList.push(chartData)


    })

    return chartDataList
  }





  serverDataToBarChartData(serverData: IServerData[]) {
    let iMap: Map <string, IServerData[]> = new Map <string, IServerData[]> ();

    //keep data in a map
    serverData.forEach(data => {

        let serverDataList: IServerData[] = iMap.get(data.indicatorName)
        if (serverDataList) {
          serverDataList.push(data)
        } else {
          serverDataList = []
          serverDataList.push(data)
        }

        iMap.set(data.indicatorName, serverDataList)


    })


    
    let iData: any[] = []
    let indicatorArray: IBarChartData[] = [];


    //baseline
    iMap.forEach((value: IServerData[], key: string) => {

      let hasMRT: boolean;
      if(value && value.length > 1){
        hasMRT = true
      }

      value.sort(this.utilService.sortMonthlyArray);
      let data: IServerData = value.length > 0? value.filter(d=>d.baseline)[0]:null
      if(!data){
        console.log("No baseline data found for " + key)
      }

      

      let barChartData: IBarChartData = {
        axis: data.indicatorName,
        value: data.value,
        legend: 'Baseline',
        cssClass: 'firstslices',
        serverData: value,
        src: data.src,
        hasMRT: hasMRT,
        tp: data.tp
      }

      indicatorArray.push(barChartData)
      
    })

    iData.push(indicatorArray)

    indicatorArray = [];

    //MRT
    iMap.forEach((value: IServerData[], key: string) => {

      let hasMRT: boolean;
      if(value && value.length > 1){
        hasMRT = true
      }

      value.sort(this.utilService.sortMonthlyArray);
      let data: IServerData = value.reverse()[0];
      let val = null;
      if(!data.baseline){
        val = data.value
      }

      let barChartData: IBarChartData = {
        axis: data.indicatorName,
        value: val,
        legend: 'MRT',
        cssClass: 'firstslices',
        serverData: value,
        src: data.src,
        hasMRT: hasMRT,
        tp: data.tp
      }

      indicatorArray.push(barChartData)
    

    })

    iData.push(indicatorArray)

    indicatorArray = [];

    //Target
    iMap.forEach((value: IServerData[], key: string) => {

      let hasMRT: boolean;
      if(value && value.length > 1){
        hasMRT = true
      }

      value.sort(this.utilService.sortMonthlyArray);
      

      let barChartData: IBarChartData = {
        axis: value[value.length-1].indicatorName,
        value: value[value.length-1].target,
        legend: 'Target',
        cssClass: 'firstslices',
        serverData: value,
        src: value[value.length-1].src,
        hasMRT: hasMRT,
        tp: value[value.length-1].tp
      }

      indicatorArray.push(barChartData)
      


    })

    iData.push(indicatorArray)



    return iData
  }


  downloadEXCEL(svgImage: ISVGIMageForExcel) {
    return this.http.post(ConstantService.EXCEL_DOWNLOAD, svgImage, {
      responseType: "blob"
    });
  }

  downloadPDF(svgImage: ISVGImage[], stateName: string, districtName: string, blockName: string) {
    return this.http.post(ConstantService.PDF_DOWNLOAD + "?stateName=" + stateName+"&districtName=" + districtName + "&blockName=" + blockName, svgImage, {
      responseType: "blob"
    });
  }

  getOutcomeIndicatorData(){
    return this.http.get(ConstantService.NFHS_DATA);
  }
}