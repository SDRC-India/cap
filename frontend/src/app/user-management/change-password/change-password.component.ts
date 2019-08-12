import { Component, OnInit } from '@angular/core';
import { FormGroup, NgForm } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';

import { UserManagementService } from '../services/user-management.service';
import { AppService } from '../../app.service';
import { StorageService } from 'src/app/services/storage/storage.service';
import { ConstantService } from 'src/app/services/constant/constant.service';
declare var $: any;

@Component({
  selector: 'app-change-password',
  templateUrl: './change-password.component.html',
  styleUrls: ['./change-password.component.scss']
})
export class ChangePasswordComponent implements OnInit {
  form: FormGroup;
  formFields: any;
  sdrcForm: FormGroup;
  
  validationMsg: any;
  btnDisable: boolean = false;
  UserForm:FormGroup; 

  userName: string;
  password: string;
  newPassword: string;
  confirmPassword: string;
 
  userManagementService: UserManagementService;

  constructor(private http: HttpClient, private userManagementProvider: UserManagementService, private router: Router, private appService: AppService, private storageService: StorageService) { 
    this.userManagementService = userManagementProvider; 
  }

  ngOnInit() {    
    if(this.storageService.get(ConstantService.USER_DETAILS)){
      var token = JSON.parse(this.storageService.get(ConstantService.USER_DETAILS));
      this.userName = token.user_name;     
    } 
              
    if((window.innerWidth)<= 767){
      $(".left-list").attr("style", "display: none !important"); 
      $('.mob-left-list').attr("style", "display: block !important");
    }
  }
 
  changePasswordForm(form: NgForm){ 
    let passDetails = {
      'userName' : this.userName,
      'oldPassword': this.password,
      'newPassword': this.newPassword,
      'confirmPassword':this.confirmPassword
    }; 
    if(this.password != this.newPassword && this.newPassword==this.confirmPassword){
      this.userManagementService.changePassword(passDetails)
      .subscribe((data) => {
        this.validationMsg = data;    
         $("#successMatch").modal('show');      
         form.resetForm();
      }, err=>{
        this.validationMsg = err;
       $("#passNotMatch").modal('show');
     });
    }    
  }
  successModal(){
    $("#successMatch").modal('hide');
    $("#passNotMatch").modal('hide');       
    this.appService.logout();  
    // this.router.navigateByUrl('/login');   
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
