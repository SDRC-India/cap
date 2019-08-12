import {
  Injectable
} from '@angular/core';
import {
  HttpClient
} from '@angular/common/http';
import {
  ConstantService
} from 'src/app/services/constant/constant.service';
import {
  catchError
} from 'rxjs/operators';
import {
  Observable
} from 'rxjs';
import { UtilService } from 'src/app/services/util/util.service';

@Injectable({
  providedIn: 'root'
})
export class UserManagementService {

  formFieldsAll: any;
  departments: IUserDepartment[];
  districts: IArea[]
  typeDetails: any;
  areaDetails: any;
  allTypes: any;
  editUserDetails: any;
  resetPasswordDetails: any = {};
  userIdToDisable: string
  constructor(private httpClient: HttpClient, private utilService: UtilService) {}

  getUserRoles() {
    return this.httpClient.get(ConstantService.ALL_DESIGNATIONS);
  }

  createUser(userDetails: any): Observable <string> {

    return this.httpClient.post<string>(ConstantService.CREATE_USER_URL, userDetails)
      .pipe(
        catchError(this.utilService.handleError)
      )
  }

  changePassword(passDetails: any): Observable<string>{

    return this.httpClient.post<string>(ConstantService.CHANGE_PASSWORD, passDetails)
    .pipe(
      catchError(this.utilService.handleError)
    )

  }

  getUsers(): Observable<any>{
    return this.httpClient.get(ConstantService.GET_USERS)
  }

  getDepartments() {
    return this.httpClient.post < IUserDepartment[] > (ConstantService.ALL_DEPARTMENTS, {})
  }

  getDistricts() {
    return this.httpClient.post < IArea[] > (ConstantService.DISTRICTS, {})
  }
}