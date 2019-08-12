import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';
import { UtilService } from 'src/app/services/util/util.service';

@Injectable({
  providedIn: 'root'
})
export class AdminGuard implements CanActivate {

  constructor(private utilService: UtilService, private router:Router){}
  canActivate(
    next: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Promise<boolean> {
      return new Promise<boolean>((resolve)=>{
        this.utilService.isAdmin()
        .then(flag=>{
          resolve(flag)
          if(!flag){
            this.router.navigate(['/'])          
          }
        })
      })
      
    
  }
}
