import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { EditUserDetailsComponent } from './edit-user-details/edit-user-details.component';
import { UserManagementComponent } from './user-management/user-management.component';
import { UpdateUserDetailsComponent } from './update-user-details/update-user-details.component';
import { ChangePasswordComponent } from './change-password/change-password.component';
import { RoleGuardGuard } from '../guards/role/role-guard.guard';
import { ForgotpassComponent } from './forgotpass/forgotpass.component';

const routes: Routes = [
      { 
        path: 'user-management', 
        pathMatch: 'full', 
        component: UserManagementComponent,
        canActivate: [RoleGuardGuard],
        data: { 
          expectedRoles: ["USER_MGMT_ALL_API"]
        }
      },
      { 
        path: 'edit-user-details', 
        pathMatch: 'full', 
        component: EditUserDetailsComponent,
        canActivate: [RoleGuardGuard],
        data: { 
          expectedRoles: ["USER_MGMT_ALL_API"]
        }
      },
      { 
        path: 'update-user', 
        pathMatch: 'full', 
        component: UpdateUserDetailsComponent,
        canActivate: [RoleGuardGuard],
        data: { 
          expectedRoles: ["USER_MGMT_ALL_API"]
        }
      },
      { 
        path: 'change-password', 
        pathMatch: 'full', 
        component: ChangePasswordComponent, 
        canActivate: [RoleGuardGuard],
        data: { 
          expectedRoles: ["USER_MGMT_ALL_API",'DATA_COLLECTION','DASHBOARD','REPORT']
        }       
      },
      { 
        path: 'forgot-password', 
        pathMatch: 'full', 
        component: ForgotpassComponent,        
      }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class UserManagementRoutingModule { }
