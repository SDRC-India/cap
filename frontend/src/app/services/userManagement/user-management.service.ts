import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { ConstantService } from '../constant/constant.service';
import { IQuestion } from 'sdrc-form';
import { Observable } from 'rxjs';
import { StorageService } from '../storage/storage.service';

@Injectable({
  providedIn: 'root'
})
export class UserManagementService {

  constructor(private http: HttpClient, private storageService: StorageService) { }

  getQuestions(){
    return this.http.get("assets/createUserQuestions.json")
  }

  /**
   * This method will send request to server for user creation
   */
  createUser(formData: IQuestion[]): Observable<string>{

    let obj = {      
        userName:formData[0].value,
        password:formData[1].value,
        designationIds:formData[2].value,
        email:formData[3].value      
    }

    return this.http.post<string>(ConstantService.CREATE_USER_URL, obj)    
  }
  

}
