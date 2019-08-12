import { Component, OnInit } from '@angular/core';
import { FormGroup, Validators, NgForm } from '@angular/forms';
import { HttpClient } from '@angular/common/http/';
import { UserManagementService } from '../services/user-management.service';
import { Router, RoutesRecognized } from '@angular/router';
import { filter, pairwise } from 'rxjs/operators';
import { ConstantService } from 'src/app/services/constant/constant.service';

declare var $: any;

@Component({
  selector: 'app-edit-user-details',
  templateUrl: './edit-user-details.component.html',
  styleUrls: ['./edit-user-details.component.scss']
})
export class EditUserDetailsComponent implements OnInit {
  form: FormGroup;
  formFields: any;
  formFieldsAll: any;
  payLoad = '';
  areaDetails: any;  

  newPassword: string;
  confirmPassword: string;
  userId: any;
  validationMsg: any;
  user: any;
  disableUserId: number;
 
  userManagementService: UserManagementService;

  constructor(private http: HttpClient, private userManagementProvider: UserManagementService, private router: Router) {
    this.userManagementService = userManagementProvider;
   }

  ngOnInit() {
    this.router.events
    .pipe(filter((e: any) => e instanceof RoutesRecognized))
    .subscribe((e: any) => {
        if(this.router.url =="/edit-user-details" &&e.url != '/update-user' ){
          this.userManagementService.resetPasswordDetails ={};
        }
    });
    
    
         
               
    if((window.innerWidth)<= 767){
      $(".left-list").attr("style", "display: none !important"); 
      $('.mob-left-list').attr("style", "display: block !important");
    }
    this.getUsers();   
  }

 getUsers(){
    this.userManagementService.getUsers().subscribe(res => {
      this.userManagementService.resetPasswordDetails.allUser  = res;
    }, err=>{
      console.log(err);
    });
 }
 resetModal(user){
  $("#resetPassModal").modal('show');
  this.user = user;
 }
 resetBox(user){
  this.newPassword = "";
  this.confirmPassword = "";
 }
 submitModal(form:NgForm){   
  let passDetails = {
    'userId' : this.user.userId,
    'newPassword': this.newPassword
  };

 if(this.newPassword === this.confirmPassword) {
    this.http.post(ConstantService.RESET_PASSWORD, passDetails).subscribe((data)=>{  
        $("#resetPassModal").modal('hide');
        $("#successMatch").modal('show');
        this.newPassword = "";
        this.confirmPassword = "";
        this.userManagementService.resetPasswordDetails.allUser = undefined;      
    }, err=>{
      $("#oldPassNotMatch").modal('show');
      this.validationMsg ="Error occurred";
    });
  }
}
editUserDetails(data){
  this.userManagementService.editUserDetails = data;  
  this.router.navigateByUrl("update-user");
}
enableUser(id){
  this.http.get(ConstantService.ENABLE_USER + '?userId='+id).subscribe((data)=>{
    $("#enableUserSuccess").modal('show'); 
    this.validationMsg = data;
  }, err=>{      
  }); 
}
disableUser(id){
  this.disableUserId = id;
  this.userManagementService.userIdToDisable = id
  $("#disableUserModal").modal('show');
}
disableUserDetails(id){
  this.http.get(ConstantService.DISABLE_USER + '?userId='+id).subscribe((data)=>{   
   $("#disableUserModal").modal('hide');
   $("#enableUser").modal('show'); 
     this.validationMsg = data;        
   }, err=>{      
     console.log(err);       
     $("#disableUserModal").modal('hide');     
   }); 
}
userStatus(){
  $("#enableUser").modal('hide'); 
  this.getUsers();
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

  /**
   * Will check whether the user is an admin or not. Will return true, if the user is admin. Will return false,
   * if the user is not admin
   * Takes string[] as argument. List of designation names 
   */
  isAdmin(designationNames:string[]): boolean{
    let flag: boolean
    designationNames.forEach(designationName=>{ 
      if(designationName === ConstantService.ADMIN_DESIGNATION_NAME){
        flag = true
      }
    })

    return flag
  }

}
