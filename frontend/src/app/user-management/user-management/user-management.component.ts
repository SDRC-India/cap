import {
  Component,
  OnInit
} from '@angular/core';
import {
  FormGroup,
  NgForm
} from '@angular/forms';
import {
  UserManagementService
} from '../services/user-management.service';
import { ConstantService } from 'src/app/services/constant/constant.service';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
declare var $: any;

@Component({
  selector: 'app-user-management',
  templateUrl: './user-management.component.html',
  styleUrls: ['./user-management.component.scss']
})
export class UserManagementComponent implements OnInit {
  form: FormGroup;
  formFields: any;
  sdrcForm: FormGroup;

  payLoad = '';

  validationMsg: any;
  btnDisable: boolean = false;
  UserForm: FormGroup;
  selectedRoleId: number
  selectedDepartmentId: number
  selectedStateId: number;
  selectedDistrictId: string;
  selectedBlockId: number;
  selectedVillageId: number;
  distrcitLevelUser: boolean

  fullName: string;
  userName: string;
  password: string;
  email: string;

  newPassword: string;
  confirmPassword: string;
  userId: any;
 
  user: any;
  disableUserId: number;

  constructor(private http: HttpClient, public userManagementService: UserManagementService, private router: Router) {}

  ngOnInit() {
    //Role
    if (!this.userManagementService.formFieldsAll)
      this.userManagementService.getUserRoles().subscribe(data => {
        this.userManagementService.formFieldsAll = data;
      })

      //Department
      if (!this.userManagementService.departments)
      this.userManagementService.getDepartments().subscribe(data => {
        this.userManagementService.departments = data;
      }) 
      
      //Districts
      //Department
      if (!this.userManagementService.districts)
      this.userManagementService.getDistricts().subscribe(data => {
        this.userManagementService.districts = data;
      })  


    if ((window.innerWidth) <= 767) {
      $(".left-list").attr("style", "display: none !important");
      $('.mob-left-list').attr("style", "display: block !important");
    }
  }

  roleSelected(){
    if(this.selectedRoleId === 2){
      this.distrcitLevelUser = true
    }else{
      this.distrcitLevelUser = false
      this.selectedDistrictId = null
    }
  }

  submitForm(form: NgForm) {
    let userDetails: IUserDetails= {
      "userName": this.userName,
      "password": this.password,
      "designationIds": [this.selectedRoleId],
      "email": this.email,
      "name": this.fullName,
      "departmentId": this.selectedDepartmentId,
      "areaCode": this.selectedDistrictId
    }

    this.userManagementService.createUser(userDetails)
    .subscribe(data=>{
      this.validationMsg = data;
      $("#successMatch").modal('show');
      form.resetForm();
    }, err=>{
      $("#oldPassNotMatch").modal('show');
      this.validationMsg = err
    })
    
    
  }
  successModal() {
    $("#successMatch").modal('hide');
  }

  showLists() {
    $(".left-list").attr("style", "display: block !important");
    $('.mob-left-list').attr("style", "display: none !important");
  }

  ngAfterViewInit() {
    $("input, textarea, .select-dropdown").focus(function () {
      $(this).closest(".input-holder").parent().find("> label").css({
        "color": "#4285F4"
      })

    })
    $("input, textarea, .select-dropdown").blur(function () {
      $(this).closest(".input-holder").parent().find("> label").css({
        "color": "#333"
      })
    })
    $('body,html').click(function (e) {
      if ((window.innerWidth) <= 767) {
        if (e.target.className == "mob-left-list") {
          return;
        } else {
          $(".left-list").attr("style", "display: none !important");
          $('.mob-left-list').attr("style", "display: block !important");
        }
      }
    });
  }
  getUsers(){
    this.userManagementService.getUsers().subscribe(res => {
      this.userManagementService.resetPasswordDetails.allUser  = res;
    }, err=>{
      console.log(err);
    });
 }
 resetModal(user){
  $("#resetPassModal").modal('show');
  this.user = user;
 }
 resetBox(user){
  this.newPassword = "";
  this.confirmPassword = "";
 }
 submitModal(form:NgForm){   
  let passDetails = {
    'userId' : this.user.userId,
    'newPassword': this.newPassword
  };

 if(this.newPassword === this.confirmPassword) {
    this.http.post(ConstantService.RESET_PASSWORD, passDetails).subscribe((data)=>{  
        $("#resetPassModal").modal('hide');
        $("#successMatch").modal('show');
        this.newPassword = "";
        this.confirmPassword = "";
        this.userManagementService.resetPasswordDetails.allUser = undefined;
    }, err=>{
      $("#oldPassNotMatch").modal('show');
      this.validationMsg ="Error occurred";
    });
  }
}
editUserDetails(data){
  this.userManagementService.editUserDetails = data;  
  this.router.navigateByUrl("update-user");
}
enableUser(id){
  this.http.get(ConstantService.ENABLE_USER + '?userId='+id).subscribe((data)=>{
    $("#enableUser").modal('show'); 
    this.validationMsg = data;
  }, err=>{      
  }); 
}
disableUser(id){
  this.disableUserId = id;
  $("#disableUserModal").modal('show');
}
disableUserDetails(){
  
  this.http.get(ConstantService.DISABLE_USER + '?userId='+this.userManagementService.userIdToDisable).subscribe((data)=>{   
   $("#disableUserModal").modal('hide');
   $("#enableUser").modal('show'); 
     this.validationMsg = data;        
   }, err=>{      
     console.log(err);       
     $("#disableUserModal").modal('hide');     
   }); 
}
userStatus(){
  $("#enableUser").modal('hide'); 
  this.getUsers();
}
}