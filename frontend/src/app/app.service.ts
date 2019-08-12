import {
  Injectable
} from '@angular/core';
import {
  HttpClient,
  HttpHeaders
} from '@angular/common/http';
import {
  catchError
} from 'rxjs/operators';
import {
  Router
} from '@angular/router'
import {
  StorageService
} from './services/storage/storage.service';
import {
  ConstantService
} from './services/constant/constant.service';
import { UtilService } from './services/util/util.service';

@Injectable()
export class AppService {
  authenticated = false;
  logoutSuccess: boolean = false;
  isValid: boolean = true;
  _data: any;
  validationMsg: any;
  constructor(private http: HttpClient, private router: Router, private storageService: StorageService,
    private utilService: UtilService) {
    // this.userIdle.onTimeout().subscribe(() => console.log('Time is up!'));
  }


  authenticate(credentials, callback) {
    this.isValid = false;
    this.callServer(credentials).subscribe(response => {
      this._data = response; //store the token
      this.storageService.set(ConstantService.ACCESS_TOKEN, this._data.access_token);
      this.storageService.set(ConstantService.REFRESH_TOKEN, this._data.refresh_token);

      const httpOptions = {
        headers: new HttpHeaders({
          'Authorization': 'Bearer ' + this._data.access_token,
          'Content-type': 'application/json'
        })
      };
      this.http.get(ConstantService.URER_URL, httpOptions).subscribe(user => {

        this.storageService.set(ConstantService.USER_DETAILS, JSON.stringify(user));
        this.router.navigateByUrl('/dashboard');
        this.isValid = true;
      });
    }, error => {
      if(error === 'Bad credentials' || error === 'Invalid Credentials !'){
        this.validationMsg = 'Username or password incorrect'
      }else{
        this.validationMsg = error
      }
      
      this.isValid = true
    })
  }


  callServer(userDetails) {

    const httpOptions = {
      headers: new HttpHeaders({
        'Content-type': 'application/x-www-form-urlencoded; charset=utf-8'
      })
    };

    let params = new URLSearchParams();
    params.append('username', userDetails.username);
    params.append('password', userDetails.password);
    params.append('grant_type', 'password');

    return this.http.post(ConstantService.AUTH_URL, params.toString(), httpOptions)
      .pipe(
        catchError(this.utilService.handleError)
      );
  }  

  checkLoggedIn(): boolean {
    let accesToken: string = this.storageService.get(ConstantService.ACCESS_TOKEN)
    if (accesToken != null && accesToken != undefined && accesToken != "") {
      return true
    } else {
      return false
    }
  }

  //handles nav-links which are going to be shown 
  checkUserAuthorization(expectedRoles) {


    if (this.storageService.get(ConstantService.USER_DETAILS)) {
      var token = JSON.parse(this.storageService.get(ConstantService.USER_DETAILS));
    }
    let flag = false;
    if (token !== undefined) {
      if (this.checkLoggedIn() && token.authorities) {
        expectedRoles.forEach(expectedRole => {
          for (let i = 0; i < token.authorities.length; i++) {
            if (token.authorities[i] == expectedRole) {
              flag = true;
            }
          }
        });
      }
    }
    return flag;
  }

  logout() {
    this.deleteSessionStorage();
    this.validationMsg='';
    this.router.navigateByUrl('/');
    this.logoutSuccess = true;
    setTimeout(() => {
      this.logoutSuccess = false;
    }, 2000)
  }

  deleteSessionStorage() {
    this.storageService.remove(ConstantService.ACCESS_TOKEN)
    this.storageService.remove(ConstantService.REFRESH_TOKEN)
    this.storageService.remove(ConstantService.USER_DETAILS)
  }

  getUserDetails() {
    if (this.checkLoggedIn())
      return JSON.parse(this.storageService.get(ConstantService.USER_DETAILS));
    else
      return {}
  }

  /**
   * Will check the current page 
   */
  isPage(pageName: string): boolean{
    let currentPath: string = window.location.pathname
    if(currentPath === pageName){
      return true
    }

    return false
  }

  getUsername(){
    let name: string = ""
    try{
      let user: IUser = JSON.parse(this.storageService.get(ConstantService.USER_DETAILS));
      name = (<any>user.sessionMap).name
    }catch(err){}

    return name
  }

}