import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { TargetFormComponent } from './target-form/target-form.component';
import { AuthGuard } from 'src/app/guards/auth.guard';
import { TargetEntryFormComponent } from './target-entry-form/target-entry-form.component';

const routes: Routes = [
  { 
    path: '', 
    component: TargetFormComponent, 
    data: { title: 'target-entry' }, 
    canActivate:[AuthGuard]
  },
  { 
    path: 'target-entry-form', 
    component: TargetEntryFormComponent, 
    data: { title: 'target-entry' }, 
    canActivate:[AuthGuard]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class TargetEntryRoutingModule { }
