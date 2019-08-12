import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ConstantService {

  static ACCESS_TOKEN:string = "access_token"
  static REFRESH_TOKEN:string = "refresh_token"
  static USER_DETAILS:string = "user_details"
  //server URLS
  static SERVER_URL = '/cap-backend'

  // static SERVER_URL = 'https://testserver.sdrc.co.in:8443/cap-backend'
  // static SERVER_URL = 'https://uat.sdrc.co.in/cap-backend'
  // static SERVER_URL = 'https://prod2.sdrc.co.in/cap-backend'
  static AUTH_URL = ConstantService.SERVER_URL + '/oauth/token'
  static URER_URL = ConstantService.SERVER_URL + '/oauth/user'
  static ROLES_URL = ConstantService.SERVER_URL + '/getRoles'
  static CREATE_USER_URL = ConstantService.SERVER_URL + '/createUser'
  static CHANGE_PASSWORD = ConstantService.SERVER_URL + '/changePassword'
  static RESET_PASSWORD = ConstantService.SERVER_URL + '/resetPassword'
  static ENABLE_USER = ConstantService.SERVER_URL + '/enableUser'
  static DISABLE_USER = ConstantService.SERVER_URL + '/disableUser'
  static UPDATE_USER = ConstantService.SERVER_URL + '/updateUser'
  static ALL_DESIGNATIONS = ConstantService.SERVER_URL + '/getAllDesignations'
  static FORGOT_PASSWORD_SENT_OTP = ConstantService.SERVER_URL + '/bypass/sendOtp'
  static FORGOT_PASSWORD_VALIDATE_OTP = ConstantService.SERVER_URL + '/bypass/validateOtp'
  static FORGOT_PASSWORD = ConstantService.SERVER_URL + '/bypass/forgotPassword'
  static GET_USERS = ConstantService.SERVER_URL + '/getUsers'
  static DATA = ConstantService.SERVER_URL + '/getFilteredData'
  static AREA = ConstantService.SERVER_URL + '/area'
  static DEPARTMENTS = ConstantService.SERVER_URL + '/departments'
  static ALL_DEPARTMENTS = ConstantService.SERVER_URL + '/allDepartments'
  static THEME = ConstantService.SERVER_URL + '/theme'
  static SERVER_DATE = ConstantService.SERVER_URL + '/serverDate'
  static PDF_DOWNLOAD = ConstantService.SERVER_URL + '/pdfDownload'
  static EXCEL_DOWNLOAD = ConstantService.SERVER_URL + '/excelDownload'
  static TEMPLATE_DOWNLOAD = ConstantService.SERVER_URL + '/template'
  static REPORT_DOWNLOAD = ConstantService.SERVER_URL + '/report'
  static TEMPLATE_UPLOAD = ConstantService.SERVER_URL + '/uploadTemplate'
  static DISTRICTS = ConstantService.SERVER_URL + '/districts'
  static GET_FREQUENCY = ConstantService.SERVER_URL + '/getFrequency'
  
  static GET_TIMEPERIOD = ConstantService.SERVER_URL +'/getTimeperiod?id='
  static GET_DATA = ConstantService.SERVER_URL +'/getData'
  static NFHS_DATA = ConstantService.SERVER_URL +'/getNFHSData'
  static FORM_DATA = ConstantService.SERVER_URL +'/saveData'
  static GETVIEWCARD_TIMEPERIOD = ConstantService.SERVER_URL + '/getCardViewTimePeriods'
  static GETCARDVIEW_TABLEDATA = ConstantService.SERVER_URL + '/getCardViewTableData'
  static GETCARDVIEW_DATA = ConstantService.SERVER_URL + '/getCardViewData'
  static GET_FINANCIALYEAR = ConstantService.SERVER_URL + '/financialYear'
  static GETTARGET_DATA = ConstantService.SERVER_URL + '/getTargetData'
  static SAVE_TARGETDATA = ConstantService.SERVER_URL +  '/saveTargetData'

  //Local URLS
  // static SERVER_URL = '/cap'
  // static AUTH_URL = '/cap/oauth/token'
  // static URER_URL = '/cap/oauth/user'
  // static ROLES_URL = '/cap/getRoles'
  // static CREATE_USER_URL = '/cap/createUser'
  // static CHANGE_PASSWORD = '/cap/changePassword'
  // static RESET_PASSWORD = '/cap/resetPassword'
  // static ENABLE_USER = '/cap/enableUser'
  // static DISABLE_USER = '/cap/disableUser'
  // static UPDATE_USER = '/cap/updateUser'
  // static ALL_DESIGNATIONS = '/cap/getAllDesignations'
  // static FORGOT_PASSWORD_SENT_OTP = '/cap/bypass/sendOtp'
  // static FORGOT_PASSWORD_VALIDATE_OTP = '/cap/bypass/validateOtp'
  // static FORGOT_PASSWORD = '/cap/bypass/forgotPassword'
  // static GET_USERS = '/cap/getUsers'
  // static DATA = '/cap/getFilteredData'
  // static AREA = '/cap/area'
  // static DEPARTMENTS = '/cap/departments'
  // static ALL_DEPARTMENTS = '/cap/allDepartments'
  // static THEME = '/cap/theme'
  // static SERVER_DATE = '/cap/serverDate'
  // static PDF_DOWNLOAD = '/cap/pdfDownload'
  // static EXCEL_DOWNLOAD = '/cap/excelDownload'
  // static TEMPLATE_DOWNLOAD = '/cap/template'
  // static REPORT_DOWNLOAD = '/cap/report'
  // static TEMPLATE_UPLOAD =  '/cap/uploadTemplate'
  // static DISTRICTS = '/cap/districts'
  // static GET_FREQUENCY =  '/cap/getFrequency'
  // static GET_TIMEPERIOD = '/cap/getTimeperiod?id='
  // static GET_DATA = '/cap/getData'
  // static FORM_DATA = '/cap/saveData'
  // static NFHS_DATA = '/cap/getNFHSData'
  // static GETVIEWCARD_TIMEPERIOD = '/cap/getCardViewTimePeriods'
  // static GETCARDVIEW_TABLEDATA =  '/cap/getCardViewTableData'
  // static GETCARDVIEW_DATA = '/cap/getCardViewData'
  // static GET_FINANCIALYEAR = '/cap/financialYear'
  // static GETTARGET_DATA = '/cap/getTargetData'
  // static SAVE_TARGETDATA = '/cap/saveTargetData'

  static ADMIN_AUTHORITY_NAME: string = 'USER_MGMT_ALL_API'
  static DOCUMENT_ADMIN_AUTHORITY_NAME: string = 'docs_HAVING_write'
  static ADMIN_DESIGNATION_NAME: string = "ADMIN"
  static COUNTRY_AREA_CODE: string = "IND"
  static ALL_BLOCK_CODE: string = "allBlocks"

  constructor() { }
}

