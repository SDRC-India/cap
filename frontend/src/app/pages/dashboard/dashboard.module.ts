import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { DashboardRoutingModule } from './dashboard-routing.module';
import { SnapshotComponent } from '../snapshot/snapshot.component';
import { ViewTableComponent } from '../view-table/view-table.component';
import { CardViewComponent } from '../card-view/card-view.component';
import { DashboardComponent, DialogDataExampleDialog } from './dashboard.component';
import { FormsModule } from '@angular/forms';
import { SdrcLoaderModule } from 'sdrc-loader';
import { HttpClientModule } from '@angular/common/http';
import { MaterialModule } from 'src/app/material';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MatDatepickerModule, MatSelectModule, MatOptionModule } from '@angular/material';
import { LineChartComponent } from 'src/app/components/line-chart/line-chart.component';
import { BarChartComponent } from 'src/app/components/bar-chart/bar-chart.component';
import { AreaPipe } from './area.pipe';
import { TableModule } from 'lib/public_api';
import { RemoveArrayPipe } from './remove-array.pipe';
import { ReadMorePipe } from './read-more.pipe';
// import { AreaPipe } from 'src/app/pipes/area/area.pipe';

@NgModule({
  declarations: [
    DashboardComponent,
    DialogDataExampleDialog,
    SnapshotComponent,
    ViewTableComponent,
    CardViewComponent,
    LineChartComponent,
    BarChartComponent,
    AreaPipe,
    RemoveArrayPipe,
    ReadMorePipe
  ],
  imports: [
    CommonModule,
    DashboardRoutingModule,
    FormsModule,
    SdrcLoaderModule,
    HttpClientModule,
    MaterialModule,
    MatDatepickerModule,
    MatSelectModule,
    MatOptionModule,
    TableModule
    
  ]
})
export class DashboardModule { }
