import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { SettlementListComponent } from './components/settlement-list/settlement-list.component';
import { SettlementDetailComponent } from './components/settlement-detail/settlement-detail.component';

const routes: Routes = [
  { path: '', component: SettlementListComponent },
  { path: ':id', component: SettlementDetailComponent }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class SettlementsRoutingModule { }
