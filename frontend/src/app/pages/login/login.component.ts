import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AppService } from 'src/app/app.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  credentials: any = {
    username: '',
    password: ''   
  }
  appObj: AppService

  constructor(private appService: AppService, private router: Router) { 
    this.appObj = appService;
  }

  ngOnInit() {
    this.checkLogin()
  }

  /**
   * This method is going to check whether the user is loggedin to the system or not.
   * If user is logged in the control will go to Home page otherwise control will stay in login page
   * @author Ratikanta
   * @memberof LoginComponent
   */
  checkLogin(){
    if(this.appService.checkLoggedIn()){
      this.router.navigateByUrl('/dashboard/snapshot');
    }
  }

  login(){    
    if(this.credentials.username === "" || this.credentials.username === undefined){
      document.getElementById('nameErr').innerHTML  = "Please enter username";    
    } else  if(this.credentials.password === "" || this.credentials.password === undefined){
      document.getElementById('passErr').innerHTML  = "Please enter password";    
    } else{
    this.appService.authenticate(this.credentials, () => {});
    return false;
    }
   }

   passError(){
    if(this.credentials.password  != undefined || this.credentials.password  != "")
    document.getElementById('passErr').innerHTML  = "";
   }
  
   nameError(){
    if(this.credentials.username != undefined || this.credentials.username != "")
    document.getElementById('nameErr').innerHTML  = "";
   }

   forgotPassword(){
    this.router.navigateByUrl('/forgot-password');    
 } 

}
