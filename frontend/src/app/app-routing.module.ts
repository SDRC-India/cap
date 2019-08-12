import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { LoginComponent } from './pages/login/login.component';
import { AuthGuard } from './guards/auth.guard';
import { ReportComponent } from './pages/report/report.component';
import { SitemapComponent } from './pages/sitemap/sitemap.component';
import { DisclaimerComponent } from './pages/disclaimer/disclaimer.component';
import { PrivacyPolicyComponent } from './pages/privacy-policy/privacy-policy.component';
import { TermsAndConditionComponent } from './pages/terms-and-condition/terms-and-condition.component';
import { ModalComponent } from './pages/modal/modal.component';


const routes: Routes = [ 
  { path: '', component: LoginComponent },
  { path: 'login', component: LoginComponent },
  // { path: 'aboutus', component: AboutusComponent },
  //{ path: 'home', component: HomeComponent, canActivate:[AuthGuard] },
  { 
    path: 'dashboard', 
    loadChildren:'./pages/dashboard/dashboard.module#DashboardModule',
   
  },
  { 
    path: 'data-entry', 
    loadChildren:'./pages/data-entry/data-entry.module#DataEntryModule',
  },
  {
    path:'target-entry',
    loadChildren:'./pages/target-entry/target-entry.module#TargetEntryModule'
  },

  { 
    path: 'report', 
    component: ReportComponent, 
    canActivate:[AuthGuard] 
  },
  { 
    path: 'sitemap', 
    component: SitemapComponent
  },
  { 
    path: 'disclaimer', 
    component: DisclaimerComponent
  },
  { 
    path: 'privacy-policy', 
    component: PrivacyPolicyComponent
  },
  { 
    path: 'terms-and-conditions', 
    component: TermsAndConditionComponent
  },
  { 
    path: 'user', 
    loadChildren: './user-management/user-management.module#UserManagementModule'
  },
  { 
    path: 'modal', component: ModalComponent},
 
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
