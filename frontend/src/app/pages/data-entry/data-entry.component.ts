import { Component, OnInit } from '@angular/core';
import { ConstantService } from 'src/app/services/constant/constant.service';
import saveAs from 'save-as';
import { NgForm } from '@angular/forms';
import { Router } from '@angular/router';
import { DataEntryService } from './services/data-entry.service';
declare var $: any;

@Component({
  selector: 'app-data-entry',
  templateUrl: './data-entry.component.html',
  styleUrls: ['./data-entry.component.scss']
})
export class DataEntryComponent implements OnInit {

  uploadFileDetails: any;
  areas: IArea[]
  validationMsg: any;
  selectedStateCode: string;
  selectedDistrictCode: string;
  selectedFrequency:any;
  frequencyModels:any;
  timePeriodTypeModels:any;
  frequencyTypeId:any;
  selectedTimePeriod:any;
  timePeriodId:any;
  tableData:any;
  constructor(private route: Router ,private service: DataEntryService) { }

  ngOnInit() {

    this.service.getArea()
      .subscribe(data => {
        this.areas = data
        this.setDefaults();
      }, err => {
        console.log(err)
      })

    this.service.getFrequencyData().subscribe((data)=>{
      this.frequencyModels = data;
    });
   
  }

  setDefaults() {
    //set default area codes
    this.selectedStateCode = this.areas.filter(d => d.parentAreaCode === ConstantService.COUNTRY_AREA_CODE)[0].code
    let districts: IArea[] = this.areas.filter(d => d.parentAreaCode === this.selectedStateCode)
    this.selectedDistrictCode = districts[0].code
  }
  
  districtChanged(selectedDistrictCode) {
    this.selectedDistrictCode = selectedDistrictCode    
  }

  downloadTemplate(){
    this.service.downloadTemplate()
    .subscribe(data => {
      saveAs(data, "CAP_DATA_ENTRY_TEMPLATE_" + new Date().getTime().toString() + ".xlsx");      
    }, err => {
      console.log(err)
    })
  }


  uploadTemplate(){
    this.uploadFileDetails = null;
    $('#fileUpload').click();
  }

  onFileChange(event,form: NgForm){

    
    this.uploadFileDetails = event.srcElement.files[0];
    const formdata: FormData = new FormData();
    formdata.append('file', this.uploadFileDetails);

    if (((event.srcElement.files[0].name.split('.')[(event.srcElement.files[0].name.split('.') as string[]).length - 1] as String).toLocaleLowerCase() === 'xlsx') ) {




      this.service.uploadDataEntryTemplate(formdata).subscribe(response=>{ 
        
       $("#successModal").modal('show');
        this.validationMsg = response;
        event.srcElement.value = null;
        this.uploadFileDetails = "";
        console.log(this.validationMsg)
      }, err=>{

        if(err.status == 0){
          $("#successModal").modal('show');
        this.validationMsg = "Success";
        event.srcElement.value = null;
        this.uploadFileDetails = "";
        console.log(this.validationMsg)
        }else{
          event.srcElement.value = null;
        
        if(err.status == 406)    
        event.srcElement.value = null;
        $("#errModalDownload").modal('show');
        this.validationMsg = err.error.message;
        this.uploadFileDetails = "";
        console.log(this.validationMsg);
        }
        
      });

    }else{
      $("#errModal").modal('show');
      this.validationMsg="Upload xlsx format file only";
      console.log(this.validationMsg)
      event.srcElement.value = null;
      this.uploadFileDetails = "";
    }
  }  

  saveTextAsFile(filename, text) {
    var element = document.createElement('a');
    element.setAttribute('href', 'data:text/plain;charset=utf-8,' + encodeURIComponent(text));
    element.setAttribute('download', filename);
    element.style.display = 'none';
    document.body.appendChild(element);
    element.click();
    document.body.removeChild(element);
  }
  expFile(value) {
     let valArr: any=[];
     var fileName = "cap_errorlog.txt"
    this.saveTextAsFile(fileName, value);
   }

   frequencySelectionChange(opt){
    this.frequencyTypeId=opt.id;
    this.service.getTimeperiodData(this.frequencyTypeId).subscribe(res=>{
      this.timePeriodTypeModels=res;   
    })
    this.service.selectedFrequencyName =opt.value;
   }
   timePeriodSelectionChange(opt){
    this.timePeriodId=opt.id;
    this.service.SelectedTimePeriodName = opt.value;
   }
   goTodataEntryForm(){
    this.service.getTableData(this.timePeriodId).subscribe(res=>{
      this.service.tableData=res;
      this.route.navigate(['/data-entry/data-entry-form']);

    })
   
   }

}

