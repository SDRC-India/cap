import { Component, OnInit } from '@angular/core';
import { FormGroup, NgModel } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { UserManagementService } from '../services/user-management.service';
import { Router, RoutesRecognized } from '@angular/router';
import { filter, pairwise } from 'rxjs/operators';
import { ConstantService } from 'src/app/services/constant/constant.service';
declare var $: any;

@Component({
  selector: 'app-update-user-details',
  templateUrl: './update-user-details.component.html',
  styleUrls: ['./update-user-details.component.scss']
})

export class UpdateUserDetailsComponent implements OnInit {  
  form: FormGroup;
  formFields: any;
  sdrcForm: FormGroup;
  selectRole:NgModel;
  validationMsg: any;
  UserForm:FormGroup;
  selectedRoleId: number
  selectedDistrictId: string
  selectedDepartmentId: number
  userDetails: any
  distrcitLevelUser: boolean


  constructor(private http: HttpClient, public userManagementService: UserManagementService, private router: Router) {}

  ngOnInit() {   
    this.router.events
    .pipe(filter((e: any) => e instanceof RoutesRecognized)
    ).subscribe((e: any) => {
        if(this.router.url =="/update-user" && e.url != '/edit-user-details'){
          this.userManagementService.resetPasswordDetails ={};
        }
    }); 
    if(!this.userManagementService.formFieldsAll)
      this.userManagementService.getUserRoles().subscribe(data=>{
        this.userManagementService.formFieldsAll = data;      
      }) 
    if(!this.userManagementService.editUserDetails){
      this.router.navigateByUrl("edit-user-details");
    } 
    this.userDetails=this.userManagementService.editUserDetails;            
    this.selectedRoleId = this.userDetails.roleId[0]
    this.roleSelected()
    this.selectedDistrictId = this.userDetails.areaCode
    this.selectedDepartmentId = this.userDetails.departmentId
   
    if((window.innerWidth)<= 767){
      $(".left-list").attr("style", "display: none !important"); 
      $('.mob-left-list').attr("style", "display: block !important");
    }
  }
  roleSelected(){
    if(this.selectedRoleId === 2){
      this.distrcitLevelUser = true
    }else{
      this.distrcitLevelUser = false
      this.selectedDistrictId = null
    }
  }
  updateUserDetails(){ 
    
     let email: string = this.userManagementService.editUserDetails.email;
     let userDetails = {          
      "id":this.userManagementService.editUserDetails.userId,
      "name":this.userManagementService.editUserDetails.name,
      "email": email,
      "designationIds":this.selectedRoleId,
      "departmentId": this.selectedDepartmentId,
      "areaCode": this.selectedDistrictId
     }
     this.http.post(ConstantService.UPDATE_USER, userDetails).subscribe((data) => {
       this.validationMsg = data;    
        $("#successMatch").modal('show');     
     }, err=>{
      $("#oldPassNotMatch").modal('show');
      this.validationMsg = err.error.message;     
    });
  }
  successModal(){
    $("#successMatch").modal('hide');
    this.router.navigateByUrl("edit-user-details");
  }

  showLists(){    
    $(".left-list").attr("style", "display: block !important"); 
    $('.mob-left-list').attr("style", "display: none !important");
  }
  ngAfterViewInit(){
    $("input, textarea, .select-dropdown").focus(function() {
      $(this).closest(".input-holder").parent().find("> label").css({"color": "#4285F4"})
      
    })
    $("input, textarea, .select-dropdown").blur(function(){
      $(this).closest(".input-holder").parent().find("> label").css({"color": "#333"})
    })
    $('body,html').click(function(e){   
      if((window.innerWidth)<= 767){
      if(e.target.className == "mob-left-list"){
        return;
      } else{ 
          $(".left-list").attr("style", "display: none !important"); 
          $('.mob-left-list').attr("style", "display: block !important");  
      }
     }
    });   
  }

  

}

