import { Component, OnInit, Input } from '@angular/core';
import { Router } from '@angular/router';
import { AppService } from 'src/app/app.service';

@Component({
  selector: 'app-user-side-menu',
  templateUrl: './user-side-menu.component.html',
  styleUrls: ['./user-side-menu.component.scss']
})
export class UserSideMenuComponent implements OnInit {

  router: Router;
  app: AppService;
  showChangePasswordPage: boolean
  showUserManagementPage: boolean
  constructor(router:Router,private appService: AppService) { 
    this.router = router;
    this.app = appService;
  }

  ngOnInit() {
    this.enableOrDisableChangePasswordMenuList()
  }

  /**
   * Decide whether change password item in side menu will be visibale or not
   */
  enableOrDisableChangePasswordMenuList(){
    let path: string = this.router.url
    if(path === '/edit-user-details' || path === '/user-management'){
      this.showChangePasswordPage = false
      this.showUserManagementPage = true
    }
    if(path === '/change-password'){
      this.showChangePasswordPage = true
      this.showUserManagementPage = false
      
    }
    
    
  }

}
