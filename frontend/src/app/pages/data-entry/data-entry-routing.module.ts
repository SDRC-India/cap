import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { DataEntryComponent } from './data-entry.component';
import { AuthGuard } from 'src/app/guards/auth.guard';
import { DataEntryFormComponent } from './data-entry-form/data-entry-form.component';

const routes: Routes = [
  { 
    path: '', 
    component: DataEntryComponent, 
    data: { title: 'data-entry' }, 
    canActivate:[AuthGuard]
  },
  { 
    path: 'data-entry-form', 
    component: DataEntryFormComponent, 
    data: { title: 'data-entry-form' }, 
    canActivate:[AuthGuard]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class DataEntryRoutingModule { }
