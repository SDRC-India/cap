import { Injectable } from '@angular/core';
import { ConstantService } from '../constant/constant.service';
import { StorageService } from '../storage/storage.service';
import {
  throwError
} from 'rxjs';
import { HttpErrorResponse } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class UtilService {


  constructor(private storageService: StorageService) { }
  
  isAdmin(): Promise<boolean>{

    return new Promise<boolean>((resolve)=>{
      let user: IUser = (JSON.parse(this.storageService.get(ConstantService.USER_DETAILS)) as IUser)
      let flag = false

      user.authorities.forEach(authority=>{
        if(authority === ConstantService.ADMIN_AUTHORITY_NAME){
          flag = true;          
        }
      })
      if(flag){
        resolve(true)
      }else{
        resolve(false)
      }
      
      
    })

    
  }

  isDocumentAdmin(): Promise<boolean>{

    return new Promise<boolean>((resolve)=>{
      let user: IUser = (JSON.parse(this.storageService.get(ConstantService.USER_DETAILS)) as IUser)
      let flag = false

      user.authorities.forEach(authority=>{
        if(authority === ConstantService.DOCUMENT_ADMIN_AUTHORITY_NAME){
          flag = true;          
        }
      })
      if(flag){
        resolve(true)
      }else{
        resolve(false)
      }
      
      
    })

    
  }

  getUser(): IUser{
    return (JSON.parse(this.storageService.get(ConstantService.USER_DETAILS)) as IUser)
  }

  handleError(error: HttpErrorResponse) {

    if (error.error instanceof ErrorEvent) {
      // A client-side or network error occurred. Handle it accordingly.
      console.error('An error occurred:', error.error.message);
      return throwError('An error occurred: ' + error.error.message)
    } else {
      // The backend returned an unsuccessful response code.
      // The response body may contain clues as to what went wrong,
      console.error(
        `Backend returned code ${error.status}, ` +
        `body was: ${error.error}`);

      switch (error.status) {
        case 409:
          return throwError(error.error.message);
        case 403:
          if(error.error.message){
            return throwError(error.error.message);
          }else if(error.error.error_description){
            return throwError(error.error.error_description);
          }else{
            return throwError("Unknown error");
          }
          
        case 400:
          return throwError(error.error.error_description);
        case 504:
          return throwError("Server not found, please try again later");  
        case 0:
          return throwError("Please check your internet connection");    
        default:
          console.log(error)
          return throwError("Some server error occured")

      }


    }

  };


  sortMonthlyArray(a:any,b:any)  {

    let  _monthsArray : string[] =["Jan","Feb","Mar","Apr","May","Jun","Jul","July","Aug","Sep","Oct", "Nov","Dec"];

    if(a.tp){

      if(a.tp.indexOf("-")<0||b.tp.indexOf("-")<0){
        if (a.tp > b.tp) {
          return 1
        } else if (a.tp < b.tp) {
          return -1
        } else {
          return 0;
        }
       } 
       else {

        let monthStart1=a.tp.split("-")[0].trim()
        let year1=a.tp.split("-")[1].trim()

        let monthStart2=b.tp.split("-")[0].trim()
        let year2=b.tp.split("-")[1].trim()


        if ((_monthsArray.indexOf(monthStart1)<_monthsArray.indexOf(monthStart2) && year1 <=year2 ) 
        ||(_monthsArray.indexOf(monthStart1)>=_monthsArray.indexOf(monthStart2) && year1 < year2)
        ) 
          {
            return -1
          } else if (((_monthsArray.indexOf(monthStart1) >= _monthsArray.indexOf(monthStart2))&& year1 >= year2
        )||((_monthsArray.indexOf(monthStart1) <= _monthsArray.indexOf(monthStart2)) && year1 >= year2))
        {
            return 1
          } 
       
  
          else {
            return 0;
          }
      }
    }
    else if(a.axis){

      

      if(a.axis.indexOf("_")<0||b.axis.indexOf("_")<0)
      {
       if (a.axis > b.axis) {
         return 1
       } else if (a.axis < b.axis) {
         return -1
       } else {
         return 0;
       }
      }

      else
      {

       let monthStart1=a.axis.split("_")[0].split("-")[0].trim()
       let year1=a.axis.split("_")[1].trim()


       let monthStart2=b.axis.split("_")[0].split("-")[0].trim()
       let year2=b.axis.split("_")[1].trim()

       let monthEnd1=a.tp.split("_")[0].split("-")[1].trim()
       let monthEnd2=b.tp.split("_")[0].split("-")[1].trim()
  
       if ((_monthsArray.indexOf(monthStart1)<_monthsArray.indexOf(monthStart2) && year1 <=year2 ) ||(_monthsArray.indexOf(monthEnd1)<_monthsArray.indexOf(monthEnd2) && year1 <=year2 ) 
       ||(_monthsArray.indexOf(monthStart1)>=_monthsArray.indexOf(monthStart2) && year1 < year2)  ||(_monthsArray.indexOf(monthEnd1)>=_monthsArray.indexOf(monthEnd2) && year1 < year2)
       ) 
         {
           return -1
         } else if (((_monthsArray.indexOf(monthStart1) >= _monthsArray.indexOf(monthStart2))&& year1 >= year2
       )||((_monthsArray.indexOf(monthEnd1) >= _monthsArray.indexOf(monthEnd2))&& year1 >= year2
       )||((_monthsArray.indexOf(monthStart1) <= _monthsArray.indexOf(monthStart2)) && year1 >= year2)||((_monthsArray.indexOf(monthEnd1) <= _monthsArray.indexOf(monthEnd2)) && year1 >= year2))
       {
           return 1
         } 
      
 
         else {
           return 0;
         }
     }

    }
    else
    {


      if(a.indexOf("_")<0||b.indexOf("_")<0)
      {
       if (a > b) {
         return 1
       } else if (a < b) {
         return -1
       } else {
         return 0;
       }
      }

      else
      {

       let monthStart1=a.split("_")[0].split("-")[0].trim()
       let year1=a.split("_")[1].trim()

       let monthEnd1=a.split("_")[0].split("-")[1].trim()



       let monthStart2=b.split("_")[0].split("-")[0].trim()
       let monthEnd2=b.split("_")[0].split("-")[1].trim()
       let year2=b.split("_")[1].trim()


   
       if ((_monthsArray.indexOf(monthStart1)<_monthsArray.indexOf(monthStart2) && year1 <=year2 ) ||(_monthsArray.indexOf(monthEnd1)<_monthsArray.indexOf(monthEnd2) && year1 <=year2 ) 
       ||(_monthsArray.indexOf(monthStart1)>=_monthsArray.indexOf(monthStart2) && year1 < year2)  ||(_monthsArray.indexOf(monthEnd1)>=_monthsArray.indexOf(monthEnd2) && year1 < year2)
       ) 
         {
           return -1
         } else if (((_monthsArray.indexOf(monthStart1) >= _monthsArray.indexOf(monthStart2))&& year1 >= year2
       )||((_monthsArray.indexOf(monthEnd1) >= _monthsArray.indexOf(monthEnd2))&& year1 >= year2
       )||((_monthsArray.indexOf(monthStart1) <= _monthsArray.indexOf(monthStart2)) && year1 >= year2)||((_monthsArray.indexOf(monthEnd1) <= _monthsArray.indexOf(monthEnd2)) && year1 >= year2))
       {
           return 1
         } 
      
 
         else {
           return 0;
         }
     }
    }



  }
}
