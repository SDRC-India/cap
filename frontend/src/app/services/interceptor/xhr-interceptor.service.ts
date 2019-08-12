import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpErrorResponse, HttpClient, HttpHeaders, HttpSentEvent, HttpHeaderResponse, HttpProgressEvent, HttpResponse, HttpUserEvent } from '@angular/common/http';
import { Observable, BehaviorSubject, Subject, throwError } from 'rxjs';
import { catchError, switchMap, finalize, filter, take } from 'rxjs/operators';
import { StorageService } from '../storage/storage.service';
import { ConstantService } from '../constant/constant.service';

@Injectable()
export class XhrInterceptorService implements HttpInterceptor {

  isRefreshTokenExpired: boolean;
  isRefreshingToken: boolean = false;
  tokenSubject: BehaviorSubject<string> = new BehaviorSubject<string>(null);

  constructor(private http: HttpClient, private storageService: StorageService) { }

  addToken(req: HttpRequest<any>, token: string): HttpRequest<any> {
      if (token)
        return req.clone({ headers: req.headers.set('Authorization', 'Bearer ' + token) })
      else
        return req.clone();

  }

  intercept(req: HttpRequest<any>, next: HttpHandler) {
    return next.handle(this.addToken(req, this.storageService.get(ConstantService.ACCESS_TOKEN)))
      .pipe(catchError(error => {
        if (error instanceof HttpErrorResponse) {
          switch ((<HttpErrorResponse>error).status) {            
            case 401:
              return this.handle401Error(req, next);
          }
          return throwError(error);
        } else {
          return throwError(error);
        }
      }));
  }

  handle401Error(req: HttpRequest<any>, next: HttpHandler) {
    if (!this.isRefreshingToken) {
      this.isRefreshingToken = true;

      // Reset here so that the following requests wait until the token
      // comes back from the refreshToken call.
      this.tokenSubject.next(null);
        return this.refreshToken()
        .pipe(switchMap((refreshToken: string) => {
          if (refreshToken) {
            console.log("refresh token:"+ refreshToken)
            this.tokenSubject.next(refreshToken);
            this.isRefreshingToken = false;
            return next.handle(this.addToken(req, refreshToken))
            
          }
          // If we don't get a new token, we are in trouble so logout.
          return this.logoutUser();
        }))
        .pipe(catchError(error => {
          // If there is an exception calling 'refreshToken', bad news so logout.
          return this.logoutUser();
        }))
        .pipe(finalize(() => {
          this.isRefreshingToken = false;
        }));
      // });
      
    } else {
      return this.tokenSubject
        .pipe(filter(token => token != null))
        .pipe(take(1))
        .pipe(switchMap(token => {
          return next.handle(this.addToken(req, token));
        }));
    }
  }

  handle400Error(error) {
    if (error && error.status === 400 && error.error && error.error.error === 'invalid_grant') {
      // If we get a 400 and the error message is 'invalid_grant', the token is no longer valid so logout.
      return this.logoutUser();
    }

    return throwError(error);
  }

 

  logoutUser() {
    // Route to the login page (implementation up to you)
    this.deleteCookies();
    window.location.href = "login"
    return throwError("");
  }

  deleteCookies() {
    this.storageService.remove(ConstantService.ACCESS_TOKEN)
    this.storageService.remove(ConstantService.REFRESH_TOKEN)
    this.storageService.remove(ConstantService.USER_DETAILS)
  }
  

  refreshToken(): Observable<string> {
    const tokenObsr = new Subject<string>();
    const token_refreshed = this.storageService.get("refresh_token");
  
    if (token_refreshed) {
        console.log("this refreshed token" + token_refreshed);
  
        let URL: string = ConstantService.AUTH_URL
        const httpOptions = {
          headers: new HttpHeaders({
              'Content-type': 'application/x-www-form-urlencoded; charset=utf-8'
          })
      };
        let params = new URLSearchParams();
        params.append('refresh_token', this.storageService.get(ConstantService.REFRESH_TOKEN));
        params.append('grant_type', 'refresh_token')
  
        this.http.post<UserToken>(URL, params.toString(), httpOptions)
            .subscribe(response => {
  
                console.log("successfully received accesstoken: Oauth/token successful")
                this.storageService.set(ConstantService.ACCESS_TOKEN, response.access_token);
  
                tokenObsr.next(response.access_token);
  
            }, err => {
                console.log("User authentication failed! Oauth/token failed");
            });
    }
    return tokenObsr.asObservable();
  }
}
interface UserToken{
  access_token: string;
  refresh_token: string;
}