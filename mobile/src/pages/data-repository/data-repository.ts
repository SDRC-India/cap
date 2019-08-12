import { AreaLevel } from './../../enums/areaLevel';
import { ComparisonServiceProvider } from './../../providers/comparison-service/comparison-service';
import { CustomViewServiceProvider } from './../../providers/custom-view-service/custom-view-service';
import { MessageServiceProvider } from './../../providers/message-service/message-service';
import { UtilServiceProvider } from './../../providers/util-service/util-service';
import { Component, ViewChild } from '@angular/core';
import { IonicPage, NavController, NavParams, AlertController, IonicApp, Platform, Slides, Content } from 'ionic-angular';


@IonicPage()
@Component({
  selector: 'page-data-repository',
  templateUrl: 'data-repository.html',
})
export class DataRepositoryPage {
  @ViewChild('pageSlider') pageSlider: Slides;

  @ViewChild(Content) content: Content;

  area: Area[] = [];
  areaLevels: IAreaLevel[] = [];
  customViewTypeSelection: CustomViewTypeSelection;

  public unregisterBackButtonAction: any;

  indicatorList: any[];

  themeList: ISector;

  index: number = 0;

  segements: String[] = ["Department", "Area"]

  selectedTheme: ISector;

  singleSelectedIndicator: IIndicator;

  areaLevel: IAreaLevel;

  nationalLevel: IAreaLevel;

  stateLevel: IAreaLevel;

  blockLevel: IAreaLevel;

  selectedState: Area;

  selectedAreas: Set<Area> = new Set();departmentSectorList: IDepartmentSector[];


  selectionAreaMap: Map<String, String[]> = new Map();

  height: any;
  searchIndicators: String = '';
  searchState: String = '';
  searchDistrict: String = '';

  departmentSectorName: any;


  constructor(public navCtrl: NavController, public navParams: NavParams, private comparsionServiceProvider: ComparisonServiceProvider,
    public alertCtrl: AlertController, private app: IonicApp, private platform: Platform
    , private utilService: UtilServiceProvider,
    private messageService: MessageServiceProvider, public customViewServiceProvider: CustomViewServiceProvider) {
  }

  

  selectTab(index) {
    this.pageSlider.slideTo(index);
    this.content.scrollToTop();
  }

  changeWillSlide($event) {
    this.index = $event._snapIndex;
    this.content.scrollToTop();
  }


  ngOnInit() {
    this.customViewTypeSelection = {
      areaWise: false, indicatorWise: true
    }

    this.getAreaLevel();
    this.getArea();
    this.getIndicators();
    this.departmentSector();
    this.height = this.platform.height() - 300;
  }

  /**
   * Fired when entering a page, after it becomes the active page.
   * Register the hardware backbutton
   *
   * @author harsh Pratyush
   * @since 1.0.0
   */
  ionViewDidEnter() {
    this.initializeBackButtonCustomHandler();
  }

  /**
   * This method will initialize the hardware backbutton
   *
   * @author harsh Pratyush
   * @since 1.0.0
   */
  public initializeBackButtonCustomHandler(): void {
    this.unregisterBackButtonAction = this.platform.registerBackButtonAction(() => {
      this.customHandleBackButton();
    }, 10);
  }

  /**
   * This method will show a confirmation popup to exit the app, when user click on the hardware back button
   * in the home page
   *
   * @author harsh Pratyush
   * @since 1.0.0
   */
  private customHandleBackButton(): void {
    const overlayView = this.app._overlayPortal._views[0];
    if (overlayView && overlayView.dismiss) {
      overlayView.dismiss();
    } else {
      this.navCtrl.setRoot('HomePage');
    }
  }

  /**
   * Fired when you leave a page, before it stops being the active one
   * Unregister the hardware backbutton
   *
   * @author harsh Pratyush
   * @since 1.0.0
   */
  ionViewWillLeave() {
    // Unregister the custom back button action for this page
    this.unregisterBackButtonAction && this.unregisterBackButtonAction();
  }


  async getIndicators() {
    this.indicatorList = await this.customViewServiceProvider.getIndicators();
  }

  async departmentSector(){
    this.departmentSectorList = await this.customViewServiceProvider.getDepartmentSectors();
    this.departmentSectorName = this.departmentSectorList[0].departmentId
    this.themeList = this.departmentSectorList[0].sectors;
  }


  /**
*  This method will return all the area level
*/
  async getAreaLevel() {
    let areaLevelData: IAreaLevel[] = await this.comparsionServiceProvider.getAreaLevel();
    areaLevelData.forEach(element => {
      if (Number(element.id) != AreaLevel.NITIAYOG) {
        this.areaLevels.push(element);
      }
      if (Number(element.id) == AreaLevel.DISTRICT) {
        this.areaLevel = element;
      }

      if (Number(element.id) == AreaLevel.STATE) {
        this.stateLevel = element
      }

      if (Number(element.id) == AreaLevel.BLOCK) {
        this.blockLevel = element
      }
    });
  }

  /**
  *  This method will return all the area
  */
  async getArea() {
    this.area = await this.comparsionServiceProvider.getArea();
  }

  selectSector(theme: ISector) {
    this.searchIndicators = "";
    if (this.selectedTheme != theme) {
      this.selectedTheme = theme;
      this.singleSelectedIndicator = null;
    }
    else
      this.selectedTheme = null;
  }

  selectIndicator(indicator) {
    this.singleSelectedIndicator = indicator
    this.index = 1;
    this.pageSlider.slideTo(this.index);
  }

  expandArea(area, $event) {
    this.searchDistrict = ""
    if (this.selectedState != area)
      this.selectedState = area;
    else
      this.selectedState = null;

  }

  addArea(area) {
    this.searchDistrict = ""
    let alertCtrl = this.alertCtrl.create({
      title: 'Error!',
      message: 'Maximum 10 Areas can be selected',
      enableBackdropDismiss: false,
      buttons: [
        {
          text: 'Ok',
          handler: () => {
          }
        }]
    })

    if (this.selectedAreas.size == 10 && !area.checked) {
      alertCtrl.present();

    }
    else {
      for (let i = 0; i < this.area.length; i++) {
        if (area.code == this.area[i].code) {
          this.area[i].checked = !this.area[i].checked
          if (this.area[i].checked) {
            this.selectedAreas.add(this.area[i]);
          }
          else {
            this.selectedAreas.delete(this.area[i]);
          }
        }
      }
      this.formatMap();
    }

  }

  addDistrict(district) {
    if (this.selectedAreas.has(district)) {
      if (!district.checked) {
        this.selectedAreas.delete(district)
      }
    }
    else {
      if (district.checked)
        this.selectedAreas.add(district);
    }
    this.formatMap();
  }

  formatMap() {
    this.selectionAreaMap = new Map();
    Array.from(this.selectedAreas).forEach(e => {

      let areaConcatedName = '';
      areaConcatedName = e.areaLevel.districtAvailable ?
        this.area.filter(d => d.code == e.parentAreaCode)[0].areaname + ' >> ' + e.areaname :
        areaConcatedName = e.areaname;
      if (this.selectionAreaMap.has(e.areaLevel.name)) {

        this.selectionAreaMap.get(e.areaLevel.name).push(areaConcatedName);

      }

      else {
        this.selectionAreaMap.set(e.areaLevel.name, [areaConcatedName]);
      }
    })
  }


  getKeys(map) {
    return Array.from(map.keys());
  }

  getValues(key) {
    return Array.from(this.selectionAreaMap.get(key));
  }


  reset() {
    let alert = this.alertCtrl.create({
      title: 'Warning',
      message: 'This will discard all changes made. Do you want to continue ?',
      enableBackdropDismiss: false,
      buttons: [
        {
          text: 'No',
          handler: () => { }
        },
        {
          text: 'Yes',
          handler: () => {
            this.selectedTheme = null;
            this.singleSelectedIndicator = null
            this.selectionAreaMap = new Map();
            this.selectedAreas = new Set();;
            this.selectedState = null;
            for (let i = 0; i < this.area.length; i++) {
              this.area[i].checked = false;
            }
          }
        },
      ]
    }
    )
    if (this.selectionAreaMap.size || this.singleSelectedIndicator) alert.present();

  }

  next() {
    let alert = this.alertCtrl.create({
      title: 'Error!',
      enableBackdropDismiss: false,
      buttons: ['OK']
    });

    if (!this.singleSelectedIndicator) {
      this.index = 0
      this.pageSlider.slideTo(this.index);
      alert.setSubTitle("Select Indicator");
      alert.present();
    }

    else if (this.selectedAreas.size < 1) {
      this.index = 1
      this.pageSlider.slideTo(this.index);
      alert.setSubTitle("Select atleast one area");
      alert.present();
    }

    else {
      if (this.utilService.checkInternet() || this.singleSelectedIndicator.kpi || this.singleSelectedIndicator.nitiaayog || this.singleSelectedIndicator.thematicKpi || this.singleSelectedIndicator.hmis || this.singleSelectedIndicator.ssv) {

        let finalSelectedAreas: Area[] = []
        Array.from(this.selectedAreas).forEach(e => {
          let areaConcatedName = '';
          areaConcatedName = e.areaLevel.districtAvailable ?
            this.area.filter(d => d.code == e.parentAreaCode)[0].areaname + ' >> ' + e.areaname :
            areaConcatedName = e.areaname;
          e.concatenedName = areaConcatedName;
          finalSelectedAreas.push(e);
        })

        this.navCtrl.push("CustomViewDataTablePage", { "area": finalSelectedAreas, "customViewTypeSelection": this.customViewTypeSelection, "indicators": this.singleSelectedIndicator, "sector": this.selectedTheme })
      }
      else {
        alert.setSubTitle(this.messageService.messages.noDataConnectionForIndicator);
        alert.present();
      }
    }

  }

  selectedDepartmentSector(){
    this.themeList = this.departmentSectorList.filter(item => item.departmentId == this.departmentSectorName)[0].sectors;
  }

}