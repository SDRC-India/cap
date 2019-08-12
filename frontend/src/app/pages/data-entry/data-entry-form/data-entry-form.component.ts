import { Component, OnInit } from '@angular/core';
import { Location } from '@angular/common';
import { ToastrService } from 'ngx-toastr';
import { Router } from '@angular/router';
import { DataEntryService } from '../services/data-entry.service';

@Component({
  selector: 'app-data-entry-form',
  templateUrl: './data-entry-form.component.html',
  styleUrls: ['./data-entry-form.component.css']
})
export class DataEntryFormComponent implements OnInit {
  tableData:any;
  tableColumns:any;
  tabData: any;
  tabColumns: any;
  tableColumnIndex: any[];
  rowdata: any;
  frequencyData:any;
  constructor(private route: Router, public service: DataEntryService, private location: Location, private toastr: ToastrService) { }

  ngOnInit() { 
    this.tableData = this.service.tableData==null?null:this.service.tableData
    if(!this.tableData) {
      this.route.navigate(['/data-entry']);
    }else{
      return this.tableData;
    }
    // this.service.tableData;
  }
  goback() {
    this.location.back();
  }
  validate(submitData){             
    let flag = false;
    for (let i = 0; i < submitData.length; i++) {
      const element = submitData[i];
     for (let j = 0; j < Object.keys(element).length; j++) {
       const key = Object.keys(element)[j];
       if(key.includes("Value") && element[key] != "" && element[key] != null){
        flag= true;
        // return flag;
       }
     }
    }
    return flag;
  }
  submit(){
    if(this.validate(this.service.tableData.tableData)){
      this.service.getFormData(this.service.tableData).subscribe(d=>{
        console.log(d);
        let  msg = JSON.parse(JSON.stringify(d))
        // this.toastr.success(msg);
        this.toastr.success(msg, '', {
          timeOut: 2000
        });
        this.route.navigate(['/data-entry']);
      }, error => {
        console.log(error)
        if(error.status == 406){
          this.toastr.error(error.error.message);       
        }
        else
        this.toastr.error("Server error");  
      });
    }else{
      this.toastr.error("Please enter atleast one datavalue");
    }
  }
   


}
