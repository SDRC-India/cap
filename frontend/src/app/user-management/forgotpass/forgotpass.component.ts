import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { NgxSpinnerService } from 'ngx-spinner';
import { ConstantService } from 'src/app/services/constant/constant.service';

declare var $: any;

@Component({
  selector: 'sdrc-forgotpass',
  templateUrl: './forgotpass.component.html',
  styleUrls: ['./forgotpass.component.scss']
})
export class ForgotpassComponent implements OnInit {
  userName: string;
  show:boolean = false;
  errorMesg: string;
  successMesg: any;
  otpSuccessMesg: any;
  otpErrorMesg: string;
  showSucessMesg:boolean = false;
  showErrMesg:boolean = false;
  showOtpSucessMesg:boolean = false;
  showOtpErrMesg:boolean = false;
  showOtpSucessImg:boolean =false;
  showOtpErrImg:boolean = false;
  showFields:boolean = false;
  showBtn:boolean = false;
  validationMsg : any;

  credentials: any = {
    emailid: '',
    otp:'',
    newPassword: '',
    confirmPassword: ''
  }

constructor(private http: HttpClient, private router: Router, private spinner: NgxSpinnerService) { }

ngOnInit() {}

sendRequest(emailId){
  var emailPattern = /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
   
    if(emailId === "" || emailId === undefined){
      document.getElementById('emailErr1').innerHTML  = "Please enter email Id"; 
      this.showErrMesg = false;
    } else if (!emailPattern.test(emailId)) {
      document.getElementById('emailErr1').innerHTML  = "Please provide a valid email Id";
      this.showErrMesg = false;
    } 
    else {      
      document.getElementById('emailErr1').innerHTML  = "";

    this.spinner.show();   
    this.otpErrorMesg = "";
    this.showOtpErrMesg = false;   

    this.http.get(ConstantService.FORGOT_PASSWORD_SENT_OTP +'?userName='+emailId).subscribe((data)=>{    
      this.show = true;     
      this.showSucessMesg = true;
      this.showErrMesg = false;
      this.successMesg = data;
      this.showBtn = true;
      this.credentials.otp = "";
      this.spinner.hide();      
    }, err=>{      
      this.showErrMesg = true;
      this.showSucessMesg = false;
      this.errorMesg = err.error;
      this.spinner.hide();
    });
  }  
}
checkOTP(emailId, otp){
 
  document.getElementById('otpSec1').innerHTML ="";
  this.successMesg = "";
   if(this.credentials.otp === "" || this.credentials.otp === undefined) {
    document.getElementById('otpSec1').innerHTML  = "Please enter OTP."; 
   } else {
    this.spinner.show(); 
    this.http.get(ConstantService.FORGOT_PASSWORD_VALIDATE_OTP +'?userName='+emailId + '&otp='+otp).subscribe((data)=>{         
      this.showFields=true;
      this.showOtpSucessMesg = true;
      this.showOtpErrMesg = false;
      this.showOtpSucessImg = true;
      this.showOtpErrImg = false;
      this.otpSuccessMesg = data;   
      this.spinner.hide();    
    }, err=>{      
      this.showOtpSucessMesg = false;
      this.showOtpErrMesg = true;
      this.showOtpSucessImg = false;
      this.showOtpErrImg = true;
      this.otpErrorMesg = err.error;
      this.spinner.hide();  
    });  
  }
}

submitForm(f){
  console.log("Submit forgot password form")
}
sendAllData(email, otp, newPassword, confirmPassword){
  var passPattern: RegExp = /^\S*$/;

   let forgotPasswordDetails: any = {
    'emailId' : email,
    'otp': otp,
    'newPassword': newPassword,
    'confirmPassword': confirmPassword
   }
 
  this.otpSuccessMesg = "";
  this.successMesg = "";

  if(this.credentials.newPassword === "" || this.credentials.newPassword === undefined){
    document.getElementById('newPasserror').innerHTML  = "Please enter new password."; 
  } else if (!passPattern.test(newPassword)) {
    document.getElementById('newPasserror').innerHTML  = "New password will not allow space.";
  } else if(this.credentials.confirmPassword === "" || this.credentials.confirmPassword === undefined){
    document.getElementById('confirmPasserror').innerHTML  = "Please enter confirm password."; 
  } else if(this.credentials.confirmPassword === this.credentials.newPassword){   
    document.getElementById('newPasserror').innerHTML  = "";
    document.getElementById('confirmPasserror').innerHTML  = "";
    
   this.spinner.show(); 
   this.http.post(ConstantService.FORGOT_PASSWORD, forgotPasswordDetails).subscribe((data)=>{  
    this.validationMsg = data;   
    $("#successMatch").modal("show");
    this.spinner.hide();
   }, err=>{     
    this.validationMsg = err.error;
    $("#passNotMatch").modal("show");
    this.spinner.hide();
   });
  }
}
pageRefer(){
  this.router.navigateByUrl('/login');    
  $("#successMatch").modal("hide");
  $("#passNotMatch").modal("hide");   
}

newPassError(){
 if(this.credentials.password != undefined || this.credentials.password != "")
  document.getElementById('newPasserror').innerHTML  = "";
  this.otpSuccessMesg = "";
  this.showOtpSucessImg = false;
  this.showOtpErrImg = false;
};

confirmPassError(){
 if(this.credentials.confirmPassword != undefined || this.credentials.confirmPassword != "")
  document.getElementById('confirmPasserror').innerHTML  = "";
  this.otpSuccessMesg = "";
  this.showOtpSucessImg = false;
  this.showOtpErrImg = false;
};
 
}