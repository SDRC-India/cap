import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { DashboardComponent } from './dashboard.component';
import { SnapshotComponent } from '../snapshot/snapshot.component';
import { CardViewComponent } from '../card-view/card-view.component';
import { ViewTableComponent } from '../view-table/view-table.component';
import { AuthGuard } from 'src/app/guards/auth.guard';

const routes: Routes = [
  { 
    path: 'thematic', 
    component: DashboardComponent, 
    data: { title: 'Dashboard' }, 
    canActivate:[AuthGuard]
  },
  { 
    path: 'view-table', 
    component: ViewTableComponent, 
    data: { title: 'viewtable' },
    canActivate:[AuthGuard] 
  },
  { 
    path: 'cardview', 
    component: CardViewComponent, 
    data: { title: 'cardview' },
    canActivate:[AuthGuard] 
  },
  { 
    path: '', 
    component: SnapshotComponent, 
    data: { title: 'snapshot' },
    canActivate:[AuthGuard] 
  }

];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class DashboardRoutingModule { }
