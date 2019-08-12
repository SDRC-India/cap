import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { UserManagementRoutingModule } from './user-management-routing.module';
import {  MatInputModule, MatIconModule, MatFormFieldModule, MatSelectModule, MatTabsModule, MatTooltipModule } from '@angular/material';

import { ReactiveFormsModule,FormsModule } from '@angular/forms'; 
import { UserManagementComponent } from './user-management/user-management.component';
import { UserSideMenuComponent } from './user-side-menu/user-side-menu.component';
import { UserManagementService } from './services/user-management.service';
import { EditUserDetailsComponent } from './edit-user-details/edit-user-details.component';
import { FormModule } from 'sdrc-form';
import { UpdateUserDetailsComponent } from './update-user-details/update-user-details.component';
import { ChangePasswordComponent } from './change-password/change-password.component';
import { ForgotpassComponent } from './forgotpass/forgotpass.component';

@NgModule({
  imports: [
    CommonModule,    
    FormsModule,
    UserManagementRoutingModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    FormModule,
    MatTabsModule,
    MatTooltipModule
    
  ],
  declarations: [
    UserManagementComponent,
    EditUserDetailsComponent,
    UserSideMenuComponent,
    UpdateUserDetailsComponent,
    ChangePasswordComponent,
    ForgotpassComponent
  ],
  providers:[UserManagementService]
})
export class UserManagementModule { }
