import { Component, OnInit } from '@angular/core';
import { Location } from '@angular/common';
import { TargetServiceService } from '../services/target-service.service';
import { ToastrService } from 'ngx-toastr';
import { Router } from '@angular/router';
import { FormGroup, FormBuilder, Validators, FormControl  } from '@angular/forms';
declare var $:any;
@Component({
  selector: 'app-target-entry-form',
  templateUrl: './target-entry-form.component.html',
  styleUrls: ['./target-entry-form.component.css']
})
export class TargetEntryFormComponent implements OnInit {

  service:TargetServiceService;
  parentAreaCode:any;
  sectorId:any;
  timePeridId:any;
  allData:any;
  allDataDetails:any;
  indicatorList:any;
  tableForm:FormGroup;
  allDataKeys:any;

  constructor(private location: Location, private serviceProvider: TargetServiceService, private toastr: ToastrService,private fb: FormBuilder, private route: Router ) {
    this.service = serviceProvider
   }


  ngOnInit() {
    // this.tableForm = this.fb.group({
    //   targetValue: new FormControl(, [Validators.required])
     
    //   });
    // if(!this.allDataDetails) {
    //   this.route.navigate(['/target-entry']);
    // }

      this.service.getTagetTableData(this.service.districtCode, this.service.sectorId, this.service.financialYearId).subscribe(data=>{
      this.allDataDetails = data;
      // let indicatorList=Object.keys(this.allData)
        this.allData= this.allDataDetails.targetDatas;
        this.allDataKeys = this.service.getKeys(this.allData[0]);
        
      // this.indicatorList = this.tableData.indicatorName;
    })
  }

  goback(){
    this.location.back();
  }

  indicatorValue(rows, td, columnData, e){
    // if(rows[td].value > 100 ){
      
    //   rows[td].value = rows[td].value.toString().substring(0, 1);
    //     $(this).val(rows[td].value);
    
    // } else if(rows[td].value==0){
    //   rows[td].value = "";
    // } 
 
}

  emit(rows,td){
    if(rows[td].value > 100 ){
      
      rows[td].value = rows[td].value.toString().substring(0, 1);
        $(this).val(rows[td].value);
    
    } else if(rows[td].value==0){
      rows[td].value = "";
    } 
  let letters = /^\d{1,6}(\.\d{1,1})?$/;
  if(!rows[td].value.match(letters)){    
    rows[td].value = "";
    }
  } 
  validate(submitData){             
    let flag = false;
    let val;
    for (let i = 0; i < submitData.length; i++) {
      const element = submitData[i];
     for (let j = 0; j < Object.keys(element).length; j++) {
       const key = Object.keys(element)[j];
       if(element[key].value == ""){
         val = element[key].value
        flag= false;
        return flag;
       }
       
     }
    }
    return val==''?flag=false:flag=true
  }
  submit(){
   if(this.validate(this.allData)){
     console.log(this.allData);
    this.service.saveTagetData(this.allDataDetails).subscribe(d=>{
      console.log(d);
      let  msg = JSON.parse(JSON.stringify(d))
      this.toastr.success(msg, '', {
        timeOut: 2000
      });  
       this.route.navigate(['/target-entry']); 
    }, error =>{
      if(error.status == 406){
        // let error= JSON.parse(JSON.stringify(error.error.message))
        let errorMmsg = error.error.split("message")[1].split(",")[0].split(":")[1].replace(/"/g,'');
        this.toastr.error(errorMmsg);       
      }
    });
   
   }else{
     this.toastr.error("All fields are mandatory");
   }
  }

}
