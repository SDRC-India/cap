import { Component, OnInit } from '@angular/core';
import { SnapshotService } from 'src/app/services/snapshot.service';

@Component({
  selector: 'app-view-table',
  templateUrl: './view-table.component.html',
  styleUrls: ['./view-table.component.css']
})
export class ViewTableComponent implements OnInit {

  tableData:any;
  tableColumns:any;
  tabData: any;
  tabColumns: any;
  tableColumnIndex: any[];
  rowdata: any;
  selectedTimeperiod:any;
  timepriodList:any;
  showMainContent:boolean= false;
  cardTimePeriodList:any;

  service:SnapshotService
  constructor(private serviceprovider :SnapshotService) {
    this.service= serviceprovider
   }

  ngOnInit() {
  
  this.service.getCardViewTimePeriods(this.service.selectedParentArea,this.service.selectedIndicatorId ).subscribe(res=>{
    this.timepriodList= res;

  })
  }

  selectTimeperiod(tp){
    this.timepriodList=tp.id; 
    // this.service.getTimeperiodData(this.timepriodList).subscribe(res=>{
    //   this.cardTimePeriodList = res;
    // })
  }
  showHide(){
  this.service.showCard= true;
  this.service.showMainContent= false;
  }
}
