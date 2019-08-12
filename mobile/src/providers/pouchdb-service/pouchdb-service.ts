
import { Injectable } from "@angular/core";
import PouchDB from "pouchdb";
import cordovaSqlitePlugin from "pouchdb-adapter-cordova-sqlite";
import { UtilServiceProvider } from "../util-service/util-service";
import { ConstantServiceProvider } from "../constant-service/constant-service";
import { HttpClient } from "@angular/common/http";
import "rxjs/add/operator/toPromise";
import PouchFind from "pouchdb-find";
import { File } from "@ionic-native/file";
import saveAs from "save-as";
import { Zip } from '@ionic-native/zip';
import { FileTransfer, FileTransferObject } from '@ionic-native/file-transfer';
/**
 * This service is going to deal with all kind of pouchDB configurations
 * @author Ratikanta
 * @since 1.0.0
 */
@Injectable()
export class PouchdbServiceProvider {
  constructor(
    private utilService: UtilServiceProvider,
    private constantService: ConstantServiceProvider,
    private http: HttpClient,
    private file: File,
    private zip: Zip,
    private transfer: FileTransfer
  ) { }

  private db;
  private dataDB;
  _allAreas: IArea[] = [];
  _allIndicator: IIndicator[] = [];
  _allSource: ISource[] = [];
  _allSubgroup: ISubgroup[] = [];

  async setDB() {
    //PouchDB.plugin(PouchFind);
    if (
        this.utilService.getPlatform().android ||
        this.utilService.getPlatform().ios) {
        // Copy database work
        await this.unzipDB();
       // let dbUnzipCopy = await this.copyDB();

            // await this.checkVersion();
        // let dbCopyJob = await this.copyDB()

        // console.log("db copy job" + dbCopyJob);

        // let dataDBCopyJob = await this.copyDataDB()

        // console.log("data db copy job" + dataDBCopyJob);

       // if (dbUnzipCopy) {
            // PouchDB.plugin(cordovaSqlitePlugin);
            // //Create database work

            // this.db = new PouchDB(
            //     this.constantService.getConstantObject().databaseName, { adapter: "cordova-sqlite", revs_limit: 1, auto_compaction: true });
            // // this.dataDB = new PouchDB(this.constantService.getConstantObject().dataDatabaseName, {adapter: 'cordova-sqlite', revs_limit: 1, auto_compaction: true});

            return true;
        //} else {
           // return false;
        //}
    } else {
        // this.db = new PouchDB(
        //     "http://localhost:5984/" +
        //     this.constantService.getConstantObject().databaseName, { revs_limit: 1, auto_compaction: true });
        // this.dataDB = new PouchDB(
        //     "http://localhost:5984/" +
        //     this.constantService.getConstantObject().dataDatabaseName, { revs_limit: 1, auto_compaction: true, ajax: 30000000 });
        return true;
    }
  }
  unzipDB(): Promise<boolean> {
    return new Promise<boolean>((resolve, reject) => {
      try {
        //data-si-rmncha db work
        //cehcking whether we already have the database in data directory or not
        this.file.checkFile(this.file.dataDirectory + 'data/', 'IND010034.json')
          .then(data => {
            resolve(true)
          })
          .catch(err => {
            if (err.code != 1) {
              reject()
            } else {

              this.file.checkFile(this.file.applicationDirectory + 'www/', this.constantService.getConstantObject().dbzippedName)
                .then(data => {
                  this.file.copyFile(this.file.applicationDirectory + 'www/', this.constantService.getConstantObject().dbzippedName, this.file.dataDirectory, this.constantService.getConstantObject().dbzippedName)
                    .then(data => {
                      this.zip.unzip(this.file.dataDirectory +
                        this.constantService.getConstantObject().dbzippedName, this.file.dataDirectory + 'data', (progress) => console.log('Unzipping, ' + Math.round((progress.loaded / progress.total) * 100) + '%'))
                        .then((result) => {
                          console.log(this.file.dataDirectory + this.constantService.getConstantObject().dbzippedName)
                          if (result === 0) {
                            console.log("unzipped"); resolve(true)
                            this.file.removeFile(this.file.dataDirectory, this.constantService.getConstantObject().dbzippedName)
                          }
                          if (result === -1) { console.log("error"); resolve(false) }
                        });
                    }).catch(err => {
                      if (err.code != 1) {
                        console.log("copying error" + this.file.applicationDirectory + 'www/')
                        reject()
                      }
                    });
                }).catch(err => {
                  if (err.code != 1) {
                    console.log("Zip file not found" + this.file.applicationDirectory + 'www/')
                    reject()
                  }
                });

            }
          })
      } catch (err) {
        reject(err)
        console.log("Zip file not found" + this.file.applicationDirectory + 'www/')
      }

    })

  }
  /**
   * This method is going to return the database object
   * @author Ratikanta
   * @since 1.0.0
   */

  getDB() {
    return this.db;
  }

  /**
   * This method is going to return the database object
   * @author Ratikanta
   * @since 1.0.0
   */

  getDataDB() {
    return this.dataDB;
  }

  /**
   * This method is going to write preloaded data into database from file
   * @author Ratikanta
   * @since 1.0.0
   * @memberof PouchdbServiceProvider
   */
  async writeToDatabase() {
    //indicator work
    try {
      await this.getDB().get(
        this.constantService.getConstantObject().docName_indicator
      );
    } catch (err) {
      if (err.status && err.status === 404) {
        //write data
        let indicators: IIndicator[] = (await this.http
          .get("/api/indicators/findAll")
          .toPromise()) as IIndicator[];

        try {
          await this.getDB().put({
            _id: this.constantService.getConstantObject().docName_indicator,
            data: indicators
          });
        } catch (err) {
          console.log(err);
          this.utilService.showErrorToast(
            this.constantService.getConstantObject().dbErrorWhilePuttingDoc +
            err.message
          );
        }
      } else {
        this.utilService.showErrorToast(
          this.constantService.getConstantObject().dbErrorWhileFetchingDoc +
          err.message
        );
      }
    }

    //unit work
    try {
      await this.getDB().get(
        this.constantService.getConstantObject().docName_unit
      );
    } catch (err) {
      if (err.status && err.status === 404) {
        //write data
        let units: IUnit[] = (await this.http
          .get("/api/unit/findAll")
          .toPromise()) as IUnit[];

        try {
          await this.getDB().put({
            _id: this.constantService.getConstantObject().docName_unit,
            data: units
          });
        } catch (err) {
          console.log(err);
          this.utilService.showErrorToast(
            this.constantService.getConstantObject().dbErrorWhilePuttingDoc +
            err.message
          );
        }
      } else {
        this.utilService.showErrorToast(
          this.constantService.getConstantObject().dbErrorWhileFetchingDoc +
          err.message
        );
      }
    }

    //subgroup work
    try {
      await this.getDB().get(
        this.constantService.getConstantObject().docName_subgroup
      );
    } catch (err) {
      if (err.status && err.status === 404) {
        //write data
        let subgroups: ISubgroup[] = (await this.http
          .get("/api/subgroup/findAll")
          .toPromise()) as ISubgroup[];

        try {
          await this.getDB().put({
            _id: this.constantService.getConstantObject().docName_subgroup,
            data: subgroups
          });
        } catch (err) {
          console.log(err);
          this.utilService.showErrorToast(
            this.constantService.getConstantObject().dbErrorWhilePuttingDoc +
            err.message
          );
        }
      } else {
        this.utilService.showErrorToast(
          this.constantService.getConstantObject().dbErrorWhileFetchingDoc +
          err.message
        );
      }
    }

    //area work
    try {
      await this.getDB().get(
        this.constantService.getConstantObject().docName_area
      );
    } catch (err) {
      if (err.status && err.status === 404) {
        //write data
        let areas: IArea[] = (await this.http
          .get("/api/area/findAll")
          .toPromise()) as IArea[];

        try {
          await this.getDB().put({
            _id: this.constantService.getConstantObject().docName_area,
            data: areas
          });
        } catch (err) {
          console.log(err);
          this.utilService.showErrorToast(
            this.constantService.getConstantObject().dbErrorWhilePuttingDoc +
            err.message
          );
        }
      } else {
        this.utilService.showErrorToast(
          this.constantService.getConstantObject().dbErrorWhileFetchingDoc +
          err.message
        );
      }
    }

    //sector work
    try {
      await this.getDB().get(
        this.constantService.getConstantObject().docName_sector
      );
    } catch (err) {
      if (err.status && err.status === 404) {
        //write data
        let sectors: ISector[] = (await this.http
          .get("/api/sector/findAll")
          .toPromise()) as ISector[];

        try {
          await this.getDB().put({
            _id: this.constantService.getConstantObject().docName_sector,
            data: sectors
          });
        } catch (err) {
          this.utilService.showErrorToast(
            this.constantService.getConstantObject().dbErrorWhilePuttingDoc +
            err.message
          );
        }
      } else {
        this.utilService.showErrorToast(
          this.constantService.getConstantObject().dbErrorWhileFetchingDoc +
          err.message
        );
      }
    }

    //sector work
    try {
      await this.getDB().get(
        this.constantService.getConstantObject().docName_source
      );
    } catch (err) {
      if (err.status && err.status === 404) {
        //write data
        let sources: ISource[] = (await this.http
          .get("/api/source/findAll")
          .toPromise()) as ISource[];

        try {
          await this.getDB().put({
            _id: this.constantService.getConstantObject().docName_source,
            data: sources
          });
        } catch (err) {
          this.utilService.showErrorToast(
            this.constantService.getConstantObject().dbErrorWhilePuttingDoc +
            err.message
          );
        }
      } else {
        this.utilService.showErrorToast(
          this.constantService.getConstantObject().dbErrorWhileFetchingDoc +
          err.message
        );
      }
    }

    //area level work
    try {
      await this.getDB().get(
        this.constantService.getConstantObject().docName_areaLevel
      );
    } catch (err) {
      if (err.status && err.status === 404) {
        //write data
        let areaLevels: IAreaLevel[] = (await this.http
          .get("/api/arealevel/findAll")
          .toPromise()) as IAreaLevel[];

        try {
          await this.getDB().put({
            _id: this.constantService.getConstantObject().docName_areaLevel,
            data: areaLevels
          });
        } catch (err) {
          this.utilService.showErrorToast(
            this.constantService.getConstantObject().dbErrorWhilePuttingDoc +
            err.message
          );
        }
      } else {
        this.utilService.showErrorToast(
          this.constantService.getConstantObject().dbErrorWhileFetchingDoc +
          err.message
        );
      }
    }

    //config work
    try {
      await this.getDB().get(
        this.constantService.getConstantObject().docName_config
      );
    } catch (err) {
      if (err.status && err.status === 404) {
        //write data
        let config: IConfig = {
          latestDataSlugId: 0,
          latestAreaSyncDate: "2018-09-06 16:49:25.624",
          latestAreaLevelSyncData: "2018-09-06 16:49:25.624",
          latestDataSyncDate: "2018-09-06 16:49:25.624",
          latestIndicatorSyncDate: "2018-09-06 16:49:25.624",
          latestSectorSyncDate: "2018-09-06 16:49:25.624",
          latestSourceSyncDate: "2018-09-06 16:49:25.624",
          latestSubgroupSyncDate: "2018-09-06 16:49:25.624",
          latestUnitSyncDate: "2018-09-06 16:49:25.624",
          version: "1.0.3"
        };
        try {
          await this.getDB().put({
            _id: this.constantService.getConstantObject().docName_config,
            data: config
          });
        } catch (err) {
          this.utilService.showErrorToast(
            this.constantService.getConstantObject().dbErrorWhilePuttingDoc +
            err.message
          );
        }
      } else {
        this.utilService.showErrorToast(
          this.constantService.getConstantObject().dbErrorWhileFetchingDoc +
          err.message
        );
      }
    }
  }

  async setDefaultArea() {
    // this.utilService.areas = (await this.db.get(
    //   this.constantService.getConstantObject().docName_area
    // )).data;
    let doc = await this.http.get("assets/data/area.json").toPromise() as any;
    this.utilService.areas = doc

    let selectedArea = this.utilService.areas.filter(
      area => area.code === this.utilService.defaultAreaCode
    )[0];
    this.utilService.selectedArea = selectedArea;
    return true;
  }

  copyDB(): Promise<boolean> {
    return new Promise<boolean>((resolve, reject) => {
      try {
        //data-si-rmncha db work
        //cehcking whether we already have the database in data directory or not
        this.file
          .checkFile(
            this.file.dataDirectory,
            this.constantService.getConstantObject().databaseName
          )
          .then(data => {

            resolve(true)
          })
          .catch(err => {
            if (err.code != 1) {
              reject();
            } else {
              this.file
                .checkFile(
                  this.file.applicationDirectory + "www/",
                  this.constantService.getConstantObject().databaseName
                )
                .then(data => {
                  if (data == true) {
                    this.file
                      .copyFile(
                        this.file.applicationDirectory + "www/",
                        this.constantService.getConstantObject().databaseName,
                        this.file.dataDirectory,
                        this.constantService.getConstantObject().databaseName
                      )
                      .then(data => {
                        console.log("database file copied from WWW");

                        this.file
                          .copyDir(
                            this.file.applicationDirectory + "www/assets/",
                            "doc",
                            this.file.dataDirectory, ""
                          )
                          .then(data => {
                            resolve(true);
                          })
                          .catch(err => {
                            reject();
                          });
                      })
                      .catch(err => {
                        reject();
                      });
                  }
                })
                .catch(err => {
                  reject(
                    this.constantService.getConstantObject()
                      .err_databaseFileNotPresentInWWW
                  );
                });
            }
          });
      } catch (err) {
        reject(err);
      }
    });
  }

  async getDataFromServer() {
    let slugIds: Set<number> = new Set();

    this.utilService.showLoader(
      this.constantService.getConstantObject().loadingData
    );

    let sourceIndicatorMap: Map<number, Set<number>> = new Map();

    try {
      let done: boolean = true;
      // let lastFetchedData : IData [] = []
      let j = 0;
      let dbDataList: IDBData[] = [];
      while (done) {
        let URL = "api/data/findAll/" + j++;
        console.log(URL);
        let data: IData[] = <IData[]>await this.http.get(URL).toPromise();

        if (data.length == 0) {
          done = false;
          try {
            {
              console.log(dbDataList.length);
              await this.dataDB.bulkDocs(dbDataList);
              dbDataList = [];
            }
          } catch (err) {
            dbDataList = [];
            console.log(err);
            this.utilService.showErrorToast(
              this.constantService.getConstantObject().dbErrorWhilePuttingDoc +
              err
            );
          }

          let doc = await this.getDB().get(
            this.constantService.getConstantObject().docName_source
          );
          let indicatorsDoc = await this.getDB().get(
            this.constantService.getConstantObject().docName_indicator
          );
          let sectors: ISource[] = doc.data;
          let indicators: IIndicator[] = indicatorsDoc.data;
          //to be rmoved
          let sourceIndicatorDatas: SourceIndicatorData[] = [];

          try {
            await this.getDB().put({
              _id: this.constantService.getConstantObject()
                .docName_sourceIndicatorData,
              data: sourceIndicatorDatas
            });
          } catch (err) {
            this.utilService.showErrorToast(
              this.constantService.getConstantObject().dbErrorWhilePuttingDoc +
              err.message
            );
          }
          this.utilService.stopLoader();
          break;
        }

        data.forEach(serverData => {
          if (slugIds.has(serverData.slugiddata)) {

          }

          else {
            slugIds.add(serverData.slugiddata);
            let dbData: IDBData = {
              _id: serverData.slugiddata.toString(),
              area: serverData.area,
              below: serverData.below,
              indicator: serverData.indicator,
              ius: serverData.ius,
              rank: serverData.rank,
              src: serverData.src,
              subgrp: serverData.subgrp,
              top: serverData.top,
              tp: serverData.tp,
              trend: serverData.trend,
              value: serverData.value,
              slugiddata: serverData.slugiddata,
              createdDate: serverData.createdDate,
              lastModified: serverData.lastModified,
              dKPIRSrs: serverData.dKPIRSrs,
              dNITIRSrs: serverData.dNITIRSrs,
              dTHEMATICRSrs: serverData.dTHEMATICRSrs,
              tps: serverData.tps,
              target: serverData.target,
              baseline: serverData.baseline,
              sectorId: serverData.sectorId
            };
            dbDataList.push(dbData);

            if (sourceIndicatorMap.has(dbData.src.slugidsource)) {
              sourceIndicatorMap
                .get(dbData.src.slugidsource)
                .add(dbData.indicator.slugidindicator);
            } else {
              let Indicator: Set<number> = new Set();
              Indicator.add(dbData.indicator.slugidindicator);
              sourceIndicatorMap.set(dbData.src.slugidsource, Indicator);
            }
          }
        });

        if (dbDataList.length >= 30000) {
          try {
            {
              console.log(dbDataList.length);
              await this.dataDB.bulkDocs(dbDataList);
              // await this.dataDB.compact();
              dbDataList = [];
            }
            //  this.utilService.stopLoader()
          } catch (err) {
            dbDataList = [];
            console.log(err);
            //  this.utilService.stopLoader()
            this.utilService.showErrorToast(
              this.constantService.getConstantObject().dbErrorWhilePuttingDoc +
              err
            );
          }
        }

      }


    } catch (err) {
      console.log("err", err);
    } finally {
    }
  }
  /**
   * This method will save the latest slugid data in config doc
   * @since 1.0.0
   * @author Subhadarshani
   */
  async setLastDataSlugId(slugiddata) {
    try {
      let db = this.getDB();
      let doc = await db.get(
        this.constantService.getConstantObject().docName_config
      );
      return await db.put({
        _id: this.constantService.getConstantObject().docName_config,
        _rev: doc._rev,
        latestDataSlugId: slugiddata
      });
    } catch (err) {
      console.log(err);
    }
  }

  async compactDb() {
    try {
      let db = this.getDataDB();
      let siRmncha = this.getDB();
      await siRmncha.compact();
      return await db.compact();
    } catch (err) {
      console.log(err);
    }
  }



  async writeDataToJsonFile() {
    let dataFileDb = new PouchDB(
      "http://localhost:5984/compressedKPIMapDataSiRmncha",
      { revs_limit: 1, auto_compaction: true }
    );
    let docs = await dataFileDb.allDocs();

    for (let i = 0; i < docs.rows.length; i++) {


      let data = (await dataFileDb.get(docs.rows[i].id)) as any;
      let filterData = [];
      let jsonData = data.data as IDBData[];
      if (docs.rows[i].id.startsWith("IND"))
        filterData = jsonData
      else
        filterData = jsonData.filter(
          d =>
            d.indicator.ssv == true ||
            d.dKPIRSrs === true ||
            d.dTHEMATICRSrs == true ||
            (d.indicator.hmis === true &&
              d.src.slugidsource ==
              this.constantService.getConstantObject().hmisSlugId)
        );

      if (filterData.length) {
        let jsonString = JSON.stringify(filterData);

        const filename = docs.rows[i].id + ".json";
        const blob = new Blob([jsonString.toString()], {
          type: "application/json"
        });
        saveAs(blob, filename);
      }
    }
  }

  async makeMapData() {
    this.utilService.showLoader("Making Map");
    let dataFileDb = new PouchDB(
      "http://localhost:5984/compressedKPIMapDataSiRmncha",
      { revs_limit: 1, auto_compaction: true}
    );

    let dataIndicatorMap: Map<string, IDBData[]> = new Map();

    // let docs = await this.dataDB.allDocs();
    let data;
    let array: IDBData[] = [];
    let lastSlugId = null;
    let tempdata: IDBData[] = [];
    do {
      lastSlugId = tempdata.length ? tempdata[tempdata.length - 1]._id : null;
      data = await this.dataDB.find({
        selector: {
          _id: { $gt: lastSlugId }
        },
        limit: 30000
      });
      tempdata = data.docs;
      Array.prototype.push.apply(array, tempdata);
    } while (tempdata.length);

    for (let i = 0; i < array.length; i++) {
      // let dbData= await this.dataDB.get(docs.rows[i].id) as IDBData
      let dbData = array[i];

      if (
        !(
          dbData.indicator.kpi ||
          dbData.indicator.nitiaayog ||
          dbData.indicator.hmis ||
          dbData.indicator.ssv ||
          dbData.indicator.thematicKpi
        )
      )
        continue;

      if (dataIndicatorMap.has(dbData.area.code)) {
        dataIndicatorMap.get(dbData.area.code).push(array[i]);
      } else {
        dataIndicatorMap.set(dbData.area.code, [array[i]]);
      }

      if (dataIndicatorMap.has(dbData.indicator.slugidindicator.toString())) {
        dataIndicatorMap
          .get(dbData.indicator.slugidindicator.toString())
          .push(dbData);
      } else {
        dataIndicatorMap.set(dbData.indicator.slugidindicator.toString(), [
          dbData
        ]);
      }
    }
    this.utilService.stopLoader();
    let keyArray = Array.from(dataIndicatorMap.keys());
    for (let i = 0; i < keyArray.length; i++) {

      let data = await dataFileDb.put({
        _id: keyArray[i],
        data: dataIndicatorMap.get(keyArray[i])
      });
      console.log(data);
    }

  }

  async checkVersion() {
    PouchDB.plugin(cordovaSqlitePlugin);
    let _db: any = new PouchDB(
      this.constantService.getConstantObject().databaseName,
      { adapter: "cordova-sqlite", revs_limit: 1, auto_compaction: true }
    );


    let doc = await _db.get(this.constantService.getConstantObject().docName_config);
    let version: IConfig = doc.data as IConfig

    return new Promise<boolean>((resolve, reject) => {
      if (version.version && version.version == this.constantService.getConstantObject().appVersion) {
        resolve(true);
      }
      else {
        this.file.removeFile(this.file.dataDirectory,
          this.constantService.getConstantObject().databaseName).then(async data => {
            this.file.listDir(this.file.dataDirectory, 'doc').then(result => {

              for (let file of result) {

                if (file.isFile == true) {

                  this.file.removeFile(this.file.dataDirectory + 'doc', file.name).then(data => {
                    if (file == result[result.length - 1]) {
                      this.file.removeDir(this.file.dataDirectory, 'doc').then(async data => {
                        let copied = await this.copyDB()
                        if (copied) {
                          resolve(true);
                        }



                      }).catch(err => { this.utilService.showSuccessToast(err.message); })
                    }
                  }).catch(error => {
                    file.getMetadata((metadata) => {
                      this.utilService.showSuccessToast('Error deleting file from cache folder: ' + error.message);
                      reject();
                    });
                  });

                }
              }

            }).catch(err => {

            })
          })
          .catch(err => {
            reject();
          });;
      }
    });
  }
  async sync() {
    return new Promise<boolean>((resolve, reject) => {
      try {
        const fileTransfer: FileTransferObject = this.transfer.create();
        const url = this.constantService.getConstantObject().serverUrl + this.constantService.getConstantObject().newDataFilesUrl;
        console.log("url"+url)
        
        fileTransfer.download(url, this.file.dataDirectory + this.constantService.getConstantObject().dbzippedName,true).then((entry) => {
          
          this.zip.unzip(this.file.dataDirectory +
            this.constantService.getConstantObject().dbzippedName, this.file.dataDirectory + 'data', (progress) =>
              console.log('Unzipping, ' + Math.round((progress.loaded / progress.total) * 100) + '%'))
            .then((result) => {
              console.log("After unzipping" + result)
              if (result === 0) {
                resolve(true)
                this.file.removeFile(this.file.dataDirectory, this.constantService.getConstantObject().dbzippedName)
              }
              if (result === -1) {
                console.log(1 + "error");
                resolve(false)
              }
            })
        }, (error) => {
          console.log(3 + "error");
          console.log(error)
          console.log(error.message)
          resolve(false)
        });
      } catch (err) {
        console.log(4 + "error");
        console.log("Cannot sync")
        resolve(false);

      }
    });

  }
  async updateMasterSynchronizeData(dataSyncStatus: MasterDataModel) {


    if (dataSyncStatus.areaLevelList.length > 0) {

      let areaLevelListDoc = await this.db.get(
        this.constantService.getConstantObject()
          .docName_areaLevel);


      await this.db.put({
        _id: this.constantService.getConstantObject()
          .docName_areaLevel,
        data: dataSyncStatus.areaLevelList,
        _rev: areaLevelListDoc._rev
      });
    }

    if (dataSyncStatus.areaList.length > 0) {

      let areaListDoc = await this.db.get(
        this.constantService.getConstantObject()
          .docName_area);


      await this.db.put({
        _id: this.constantService.getConstantObject()
          .docName_area,
        data: dataSyncStatus.areaList,
        _rev: areaListDoc._rev
      });
    }


    if (dataSyncStatus.indicatorList.length > 0) {


      let indicatorDoc = await this.db.get(
        this.constantService.getConstantObject()
          .docName_indicator);


      await this.db.put({
        _id: this.constantService.getConstantObject()
          .docName_indicator,
        data: dataSyncStatus.indicatorList,
        _rev: indicatorDoc._rev
      });
    }

    if (dataSyncStatus.sectorList.length > 0) {

      let sectorDoc = await this.db.get(
        this.constantService.getConstantObject()
          .docName_sector);


      await this.db.put({
        _id: this.constantService.getConstantObject()
          .docName_sector,
        data: dataSyncStatus.sectorList,
        _rev: sectorDoc._rev
      });
    }

    if (dataSyncStatus.sourceList.length > 0) {

      let sourceDoc = await this.db.get(
        this.constantService.getConstantObject()
          .docName_source);


      await this.db.put({
        _id: this.constantService.getConstantObject()
          .docName_source,
        data: dataSyncStatus.sourceList,
        _rev: sourceDoc._rev
      });
    }

    if (dataSyncStatus.subgroupList.length > 0) {
      let subgroupListdoc = await this.db.get(
        this.constantService.getConstantObject()
          .docName_subgroup);


      await this.db.put({
        _id: this.constantService.getConstantObject()
          .docName_subgroup,
        data: dataSyncStatus.subgroupList,
        _rev: subgroupListdoc._rev
      });
    }

    if (dataSyncStatus.unitList.length > 0) {
      let unitDoc = await this.db.get(
        this.constantService.getConstantObject()
          .docName_unit);


      await this.db.put({
        _id: this.constantService.getConstantObject()
          .docName_unit,
        data: dataSyncStatus.unitList,
        _rev: unitDoc._rev
      });
    }

    let doc = await this.db.get(
      this.constantService.getConstantObject()
        .docName_SynChronizationMaster);


    await this.db.put({
      _id: this.constantService.getConstantObject()
        .docName_SynChronizationMaster,
      data: dataSyncStatus.synchronizationDateMasterList,
      _rev: doc._rev
    });
    return true;
  }

  async loadIndicator() {

    // let doc = await this.db.get(
    // this.constantService.getConstantObject().docName_indicator);
    this._allIndicator = await this.http.get<IIndicator[]>("assets/data/indicator.json").toPromise();
  }

  async loadArea() {
    // let doc = await this.db.get(
    // this.constantService.getConstantObject().docName_area);
    this._allAreas = await this.http.get<IArea[]>("assets/data/area.json").toPromise();
  }

  async loadSubGroup() {
    // let doc = await this.db.get(
    // this.constantService.getConstantObject().docName_subgroup);
    this._allSubgroup = await this.http.get<any>("assets/data/subgroup.json").toPromise();
  }

  async loadSource() {
    // let doc = await this.db.get(
    // this.constantService.getConstantObject().docName_source);
    this._allSource = await this.http.get<any>("assets/data/source.json").toPromise();
  }


  convertJsonDataToIDBData(jsonData: IJsonData[]) {
    let dbDatas: IDBData[] = [];
    jsonData.forEach(element => {
      let dbData: IDBData = {
        _id: element._id,
        area: this._allAreas.filter(d => d.code == element.area)[0],
        below: element.below ? this._allAreas.filter(d => element.below.indexOf(d.code) > -1) : [],
        indicator: this._allIndicator.filter(d => d.id == element.indicator)[0],
        ius: element.ius,
        rank: element.rank,
        src: this._allSource.filter(d => d.id == element.src)[0],
        subgrp: this._allSubgroup.filter(d => d.id == element.subgrp)[0],
        top: element.top ? this._allAreas.filter(d => element.top.indexOf(d.code) > -1) : [],
        tp: element.tp,
        trend: element.trend,
        value: element.value,
        slugiddata: element.slugiddata,
        dKPIRSrs: element.dKPIRSrs,
        dNITIRSrs: element.dNITIRSrs,
        tps: element.tps,
        dTHEMATICRSrs: element.dTHEMATICRSrs,
        createdDate: '',
        lastModified: '',
        target: element.target,
        baseline: element.baseline,
        sectorId: element.sectorId
      }

      dbDatas.push(dbData);
    });

    return dbDatas;
  }

}
