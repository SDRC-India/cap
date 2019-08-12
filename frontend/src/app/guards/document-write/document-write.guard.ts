import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';
import { Observable } from 'rxjs';
import { UtilService } from 'src/app/services/util/util.service';

@Injectable({
  providedIn: 'root'
})
export class DocumentWriteGuard implements CanActivate {
  constructor(private utilService: UtilService, private router:Router){}
  canActivate(
    next: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean> | Promise<boolean> | boolean {
    return new Promise<boolean>((resolve)=>{
      this.utilService.isDocumentAdmin()
      .then(flag=>{
        resolve(flag)
        if(!flag){
          this.router.navigate(['/'])          
        }
      })
    })
  }
}
