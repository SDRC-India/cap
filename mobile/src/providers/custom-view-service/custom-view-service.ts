import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { PouchdbServiceProvider } from "../pouchdb-service/pouchdb-service";
import { ConstantServiceProvider } from "../constant-service/constant-service";
import { UtilServiceProvider } from "../util-service/util-service";

/**
 *
 * @author Harsh Pratyush (harsh@sdrc.co.in)
 * @export
 * @class CustomViewServiceProvider
 */
@Injectable()
export class CustomViewServiceProvider {
  //this loadingStatus variable is taken to check the loading status
  loadingStatus: boolean = false;

  constructor(
    public pouchdbService: PouchdbServiceProvider,
    private constantServiceProvider: ConstantServiceProvider,
    private utilServiceProvider: UtilServiceProvider,
    private constantService: ConstantServiceProvider,
    private httpClient: HttpClient
  ) { }

  /**
   *
   *
   * @param {string} areaId
   * @param {Number[]} indicatorIds
   * @param {Number[]} nonKPIIndicator
   * @returns This method will return the data for a area for selected indicators for all source
   * @memberof CustomViewServiceProvider
   */
  async getGeographyWiseComparision( areaId: string, indicatorIds: Number[], nonKPIIndicator: Number[] ) {
    this.utilServiceProvider.showLoader(
      this.constantService.getConstantObject().loadingData
    );

    this.loadingStatus = false;
    let sources: Set<string> = new Set();

    let dataMap: Map<String, Map<String, String>> = new Map();
    let totalDataMap: Map<String, IDBData[]> = new Map();
    let array: IDBData[] = [];

    if (nonKPIIndicator.length && this.utilServiceProvider.checkInternet()) {
      try {
        indicatorIds.forEach(element => {
          nonKPIIndicator.push(element);
        });
        let done = false;
        let i = 0;
        while (!done) {
          let URL =
            this.constantService.getConstantObject().customViewUrl +
            i++ +
            "?indicatorSlugIds=" +
            nonKPIIndicator +
            "&areaCodes=" +
            [areaId] +
            "&searchType=2";

          let data: IData[] = <IData[]>(
            await this.httpClient.get(URL).toPromise()
          );
          if (data.length > 0) Array.prototype.push.apply(array, data);
          else done = true;
        }
      } catch (e) { }
    } else if (
      indicatorIds.length &&
      !(nonKPIIndicator.length && this.utilServiceProvider.checkInternet())
    ) {
      let data: IDBData[] = [];
      try {
        let jsondata = (await this.httpClient
          .get("assets/data/" + areaId + ".json")
          .toPromise()) as IJsonData[];

        data = this.pouchdbService.convertJsonDataToIDBData(jsondata);
      } catch (e) {
        console.log(e)
        data = [];
      }
      array = data.filter(
        d => indicatorIds.indexOf(d.indicator.id) > -1
      );
    }

    array.sort(this.utilServiceProvider.sortMonthlyArray);

    array.forEach(element => {
      sources.add(element.src.sourceName);
      if (dataMap.has(element.indicator.name)) {
        dataMap
          .get(element.indicator.name)
          .set(
            element.src.sourceName,
            element.value + "\n (" + element.tp + ")"
          );
        totalDataMap.get(element.indicator.name).push(element);
      } else {
        let valueMap: Map<String, String> = new Map();
        valueMap.set(
          element.src.sourceName,
          element.value + " \n (" + element.tp + ")"
        );
        dataMap.set(element.indicator.name, valueMap);
        totalDataMap.set(element.indicator.name, [element]);
      }
    });

    let geographyWiseComparision: CustomViewTableData = {
      sources: sources,
      values: dataMap,
      indicatorData: totalDataMap
    };

    this.utilServiceProvider.stopLoader();
    this.loadingStatus = true;
    return geographyWiseComparision;
  }

  /**
   *
   *
   * @param {string[]} areas
   * @param {Number} indicatorId
   * @param {boolean} isOffline
   * @returns This method will return the data for a single indicator for selected areas for all source
   * @memberof CustomViewServiceProvider
   */
  async getIndicatorWiseComparision( areas: string[], indicatorId: Number, sectorId: Number, isOffline: boolean) {
    this.utilServiceProvider.showLoader(
      this.constantService.getConstantObject().loadingData
    );
    this.loadingStatus = false;

    let sources: Set<string> = new Set();

    let dataMap: Map<String, Map<String, String>> = new Map();
    let array: IDBData[] = [];
    let totalDataMap: Map<String, IDBData[]> = new Map();
    if (isOffline) {
      let data: IDBData[] = [];
      {
        try {
          let jsonData = (await this.httpClient
            .get("assets/data/" + indicatorId + ".json")
            .toPromise()) as IJsonData[];
          data = this.pouchdbService.convertJsonDataToIDBData(jsonData);
        } catch (e) {
          console.log(e);

          data = [];
        }
        array = data.filter(d => (d.indicator.id == indicatorId && d.sectorId == sectorId));
      }
    } else {
      try {
        let done = false;
        let i = 0;
        while (!done) {
          let URL =
            this.constantService.getConstantObject().customViewUrl +
            i++ +
            "?indicatorSlugIds=" +
            [indicatorId] +
            "&areaCodes=" +
            areas +
            "&searchType=1";
          let data: IData[] = <IData[]>(
            await this.httpClient.get(URL).toPromise()
          );
          if (data.length > 0) Array.prototype.push.apply(array, data);
          else done = true;
        }
      } catch (e) {
        this.utilServiceProvider.stopLoader();
        this.loadingStatus = true;
      }
    }
    array.sort(this.utilServiceProvider.sortMonthlyArray);

    array.forEach(element => {
      sources.add(element.src.sourceName);
      if (dataMap.has(element.area.areaname)) {
        dataMap
          .get(element.area.areaname)
          .set(
            element.src.sourceName,
            element.value + "\n (" + element.tp + ")"
          );
        totalDataMap.get(element.area.areaname).push(element);
      } else {
        let valueMap: Map<String, String> = new Map();
        valueMap.set(
          element.src.sourceName,
          element.value + " \n (" + element.tp + ")"
        );
        dataMap.set(element.area.areaname, valueMap);
        totalDataMap.set(element.area.areaname, [element]);
      }
    });

    let geographyWiseComparision: CustomViewTableData = {
      sources: sources,
      values: dataMap,
      indicatorData: totalDataMap
    };
    this.utilServiceProvider.stopLoader();
    this.loadingStatus = true;

    return geographyWiseComparision;
  }

  async getSector() {
    // let db = this.pouchdbService.getDB();
    let doc: ISector[] = await this.httpClient.get<ISector[]>("assets/data/sector.json").toPromise();
    // await db.get(
    //   this.constantServiceProvider.getConstantObject().docName_sector
    // );
    let sectors: ISector[] = doc;

    sectors.sort((a: ISector, b: ISector) => {
      if (a.id > b.id) {
        return 1;
      } else if (a.id < b.id) {
        return -1;
      } else {
        return 0;
      }
    });

    return sectors;
  }

  async getIndicators() {
    let doc: IIndicator[] = await this.httpClient.get<IIndicator[]>("assets/data/indicator.json").toPromise();
    let indicators: IIndicator[] = doc;
    return indicators;
  }
  async getDepartmentSectors() {
    let doc: IDepartmentSector[] = await this.httpClient.get<IDepartmentSector[]>("assets/data/departmentSector.json").toPromise();
    let departmentSector: IDepartmentSector[] = doc;
    
    return departmentSector;
  }
  /**
   *
   * This method will return the data in visualization page of thematic view,geographical profile,data repository
   * @param {IArea} areaId
   * @param {IIndicator} indicatorId
   * @param {IDBData[]} data
   * @returns
   * @memberof CustomViewServiceProvider
   */
  async getCustomViewVisualizationData(
    areaId: IArea,
    indicatorId: IIndicator,
    data: IDBData[]
  ) {
    this.utilServiceProvider.showLoader(
      this.constantService.getConstantObject().loadingData
    );
    this.loadingStatus = false;

    let customViewVisualizationData: Map<String, MultiLineChart> = new Map();
    if (data.length > 1) {
      let muktidata = data.filter(item=>item.baseline == false);
      muktidata.forEach(element => {
        let linechart: LineChart = {
            axis: element.tp,
            value: element.value,
            zaxis: element.area.areaname,
            class: indicatorId.recSector.name.replace(" ", "_")
          };

        if (customViewVisualizationData.has(element.src.sourceName)) {
          customViewVisualizationData
            .get(element.src.sourceName)
            .values.push(linechart);
        } else {
          let multiLineChart: MultiLineChart = {
            id: areaId.areaname,
            parentAreaName: areaId.parentAreaCode,
            values: [linechart]
          };
          customViewVisualizationData.set(element.src.sourceName, multiLineChart);
        }
      });
    } else {
      let muktidata = data.filter(item=>item.baseline == true);
      muktidata.forEach(element => {
        let linechart: LineChart = {
          axis: element.tp,
          value: element.value,
          zaxis: element.area.areaname,
          class: indicatorId.recSector.name.replace(" ", "_")
        };

        if (customViewVisualizationData.has(element.src.sourceName)) {
          customViewVisualizationData
            .get(element.src.sourceName)
            .values.push(linechart);
        } else {
          let multiLineChart: MultiLineChart = {
            id: areaId.areaname,
            parentAreaName: areaId.parentAreaCode,
            values: []
          };
          customViewVisualizationData.set(element.src.sourceName, multiLineChart);
        }
      });
    }

    this.utilServiceProvider.stopLoader();
    this.loadingStatus = true;
    return customViewVisualizationData;
  }

  /**
   * This method will return the source and available indicator with a area
   *
   * @returns
   * @memberof CustomViewServiceProvider
   */
  async getSourceIndicators() {
    // let db = this.pouchdbService.getDB();
    let doc: SourceIndicatorData[] = await this.httpClient.get<SourceIndicatorData[]>("assets/data/departmentIndicator.json").toPromise();
    // await db.get(
    //   this.constantServiceProvider.getConstantObject()
    //     .docName_sourceIndicatorData
    // );
    let sourceIndicatorDatas: SourceIndicatorData[] = doc;

    // sourceIndicatorDatas.sort(
    //   (a: SourceIndicatorData, b: SourceIndicatorData) => {
    //     if (a.order > b.order) {
    //       return 1;
    //     } else if (a.order < b.order) {
    //       return -1;
    //     } else {
    //       return 0;
    //     }
    //   }
    // );

    return sourceIndicatorDatas;
  }

  /**
   *
   *
   * @param {String[]} areas
   * @param {Number} indicatorId
   * @param {boolean} isOffline
   * @param {number} source
   * @returns This method will return the data for a single indicator for selected areas for all source for data-repository page
   * @memberof CustomViewServiceProvider
   */
  async getSourceWiseComparision(
    areas: String[],
    indicatorId: Number,
    isOffline: boolean,
    source: number
  ) {
    // this.utilServiceProvider.showLoader(
    //   this.constantService.getConstantObject().loadingData
    // );
    this.loadingStatus = false;

    let sources: Set<string> = new Set();

    let dataMap: Map<String, Map<String, String>> = new Map();
    let array: IDBData[] = [];
    let totalDataMap: Map<String, IDBData[]> = new Map();
    if (isOffline) {
      // let data: IDBData[] = [];
      // for (let i = 0; i < areas.length; i++) {
      //   try {
      //     try {
      //       let areaData = (await this.httpClient
      //         .get("assets/data/" + areas[i] + ".json")
      //         .toPromise()) as IDBData[];
      //       Array.prototype.push.apply(data, areaData);
      //     } catch {
      //       data = [];
      //     }
      //     // array=data.filter(d=>areas.indexOf(d.area.code)>-1);
      //     array = data.filter(d => d.indicator.id == indicatorId);
      //   } catch (e) {}
      // }
      let data: IDBData[] = [];
      // for (let i = 0; i < areas.length; i++)
      //  {
      try {
        try {
          let jsonData = (await this.httpClient
            .get("assets/data/" + indicatorId + ".json")
            .toPromise()) as IJsonData[];
          let areaData = this.pouchdbService.convertJsonDataToIDBData(jsonData);
          Array.prototype.push.apply(data, areaData);
        } catch {
          data = [];
        }
        // array=data.filter(d=>areas.indexOf(d.area.code)>-1);
        array = data.filter(d => d.indicator.id == indicatorId);
      } catch (e) { }
      // }
    } else {
      try {
        let done = false;
        let i = 0;
        while (!done) {
          let URL =
            this.constantService.getConstantObject().customViewUrl +
            i++ +
            "?indicatorSlugIds=" +
            [indicatorId] +
            "&areaCodes=" +
            areas +
            "&searchType=1";
          let data: IData[] = <IData[]>(
            await this.httpClient.get(URL).toPromise()
          );
          if (data.length > 0) Array.prototype.push.apply(array, data);
          else done = true;
        }
      } catch (e) {
        console.log(e)
        array = [];
      }
    }

    let sourceData = array;
    sourceData.sort(this.utilServiceProvider.sortMonthlyArray);

    sourceData.forEach(element => {
      sources.add(element.src.sourceName);
      if (dataMap.has(element.area.areaname)) {
        dataMap
          .get(element.area.areaname)
          .set(
            element.src.sourceName,
            element.value + "\n (" + element.tp + ")"
          );
        totalDataMap.get(element.area.areaname).push(element);
      } else {
        let valueMap: Map<String, String> = new Map();
        valueMap.set(
          element.src.sourceName,
          element.value + " \n (" + element.tp + ")"
        );
        dataMap.set(element.area.areaname, valueMap);
        totalDataMap.set(element.area.areaname, [element]);
      }
    });

    let geographyWiseComparision: CustomViewTableData = {
      sources: sources,
      values: dataMap,
      indicatorData: totalDataMap
    };
    this.utilServiceProvider.stopLoader();
   // this.loadingStatus = true;

    return geographyWiseComparision;
  }
}
