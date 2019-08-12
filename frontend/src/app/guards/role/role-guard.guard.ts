import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';
import { Observable } from 'rxjs';
import { ConstantService } from 'src/app/services/constant/constant.service';
import { StorageService } from 'src/app/services/storage/storage.service';
import { AppService } from 'src/app/app.service';

@Injectable({
  providedIn: 'root'
})
export class RoleGuardGuard implements CanActivate {  
  constructor(private appService: AppService, private router: Router, private storageService: StorageService) { }

  canActivate(next: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> | Promise<boolean> | boolean {

    const expectedRoles = next.data.expectedRoles;

    if(this.appService.checkLoggedIn()){
      const token = (JSON.parse(this.storageService.get(ConstantService.USER_DETAILS)) as IUser)
      let flag = false;
      expectedRoles.forEach(expectedRole => {
       for(let i=0; i< token.authorities.length; i++){
        if (token.authorities[i] == expectedRole) {
          flag = true;
        }  
       }           
      });
      if(!flag)
       this.router.navigate(['/exception']);
       return flag;
    }
    else{
      this.router.navigate(['/exception']);
      return false;
    }
  }
}
