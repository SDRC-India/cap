import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { DataEntryRoutingModule } from './data-entry-routing.module';
import { DataEntryComponent } from './data-entry.component';
import { MatSelectModule } from '@angular/material';
import { FormsModule } from '@angular/forms';
import { MaterialModule } from 'src/app/material';
import { DataEntryFormComponent } from './data-entry-form/data-entry-form.component';
import { TableModule } from 'lib/public_api';

@NgModule({
  declarations: [
    DataEntryFormComponent,
    DataEntryComponent
  ],
  imports: [
    CommonModule,
    DataEntryRoutingModule,
    MatSelectModule,
    FormsModule,
    MaterialModule,
    TableModule,
  ]
})
export class DataEntryModule { }
