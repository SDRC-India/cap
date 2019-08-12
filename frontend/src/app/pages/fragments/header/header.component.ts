import { Component, OnInit } from '@angular/core';
import { AppService } from 'src/app/app.service';
import { Router } from '@angular/router';
@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent {

  isLoggedIn : boolean = false
  userHasUploadRights: boolean = false
  app: AppService  
  router:Router;
  constructor(router:Router, private appService: AppService) {
    this.app = appService;
    this.router = router;
  }


  logout(){
    this.appService.logout()    
  }


//  logout(){
//   var confirmLog=confirm("Are you sure you want to logout?");
//   if(confirm)
//   return this.appService.logout() ;
//   else
//   return false;
//   }

  
}
