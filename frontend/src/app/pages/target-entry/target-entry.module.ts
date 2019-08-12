import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { TargetEntryRoutingModule } from './target-entry-routing.module';
import { TargetFormComponent } from './target-form/target-form.component';
import {  MatSelectModule, MatFormFieldModule, MatInputModule, MatOptionModule,  } from '@angular/material';
import { MaterialModule } from 'src/app/material';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { AreaPipePipe } from './pipes/area-pipe.pipe';
import { TargetEntryFormComponent } from './target-entry-form/target-entry-form.component';

@NgModule({
  declarations: [
    TargetFormComponent, 
    AreaPipePipe, TargetEntryFormComponent
  ],

  imports: [
    CommonModule,
    TargetEntryRoutingModule,
    MatSelectModule,
    MaterialModule,
    FormsModule,
    MatInputModule,
    MatFormFieldModule,
    MatOptionModule,
    ReactiveFormsModule
  ]
})
export class TargetEntryModule { }
