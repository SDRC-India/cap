import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HomeComponent } from './pages/home/home.component';
import { LoginComponent } from './pages/login/login.component';
import { FormsModule } from '@angular/forms';
import { HeaderComponent } from './pages/fragments/header/header.component';
import { FooterComponent } from './pages/fragments/footer/footer.component';
import { SdrcLoaderModule } from 'sdrc-loader';
import { AppService } from './app.service';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { UserManagementModule } from './user-management/user-management.module';
import { XhrInterceptorService } from './services/interceptor/xhr-interceptor.service';
// import { DashboardComponent, DialogDataExampleDialog } from './pages/dashboard/dashboard.component';
import { AreaPipe } from './pipes/area/area.pipe';
import { MaterialModule } from './material';
import { AboutusComponent } from './pages/aboutus/aboutus.component';
// import { DataEntryComponent } from './pages/data-entry/data-entry.component';
import { ReportComponent } from './pages/report/report.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ToastrModule } from 'ngx-toastr';
import { SitemapComponent } from './pages/sitemap/sitemap.component';
import { PrivacyPolicyComponent } from './pages/privacy-policy/privacy-policy.component';
import { TermsAndConditionComponent } from './pages/terms-and-condition/terms-and-condition.component';
import { DisclaimerComponent } from './pages/disclaimer/disclaimer.component';
import { MatDatepickerModule } from '@angular/material';
import { ModalComponent } from './pages/modal/modal.component';
// import { DataEntryFormComponent } from './pages/data-entry-form/data-entry-form.component';
import { TableModule } from 'lib/public_api';
// import { SnapshotComponent } from './pages/snapshot/snapshot.component';
// import { CardViewComponent } from './pages/card-view/card-view.component';
// import { ViewTableComponent } from './pages/view-table/view-table.component'

@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    FooterComponent,
    LoginComponent,
    HomeComponent,
    AreaPipe,
    
    
    AboutusComponent,
    ReportComponent,
    SitemapComponent,
    PrivacyPolicyComponent,
    TermsAndConditionComponent,
    DisclaimerComponent,
    ModalComponent

    
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    SdrcLoaderModule,
    HttpClientModule,
    UserManagementModule,
    MaterialModule,
    BrowserAnimationsModule,
    MatDatepickerModule,
    TableModule,
    ToastrModule.forRoot()
    
  ],
  providers: [AppService, { provide: HTTP_INTERCEPTORS, useClass: XhrInterceptorService, multi: true }],
  bootstrap: [AppComponent],
  // entryComponents: [
  //   DialogDataExampleDialog
  // ]
})
export class AppModule { }
