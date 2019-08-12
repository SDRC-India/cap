import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';
import { Observable } from 'rxjs';
import { ConstantService } from '../services/constant/constant.service';
import { StorageService } from '../services/storage/storage.service';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {

  constructor(private router:Router, private storageService: StorageService){}


  canActivate(
    next: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): boolean {

      let accesToken: string = this.storageService.get(ConstantService.ACCESS_TOKEN)
      if(accesToken != null && accesToken != undefined && accesToken != ""){
        return true 
      } else{
       this.router.navigate(['/'])
       return false
     }
    }

}
