import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatCardModule } from '@angular/material/card';
import { SettlementsRoutingModule } from './settlements-routing.module';
import { SettlementListComponent } from './components/settlement-list/settlement-list.component';
import { SettlementDetailComponent } from './components/settlement-detail/settlement-detail.component';
import { SettlementPaymentComponent } from './components/settlement-payment/settlement-payment.component';
import { SettlementSummaryComponent } from './components/settlement-summary/settlement-summary.component';
import { SettlementDisputeComponent } from './components/settlement-dispute/settlement-dispute.component';

@NgModule({
  imports: [
    CommonModule,
    SettlementsRoutingModule,
    FormsModule,
    ReactiveFormsModule,
    MatTableModule,
    MatPaginatorModule,
    MatButtonModule,
    MatInputModule,
    MatSelectModule,
    MatCardModule,
    SettlementListComponent,
    SettlementDetailComponent,
    SettlementPaymentComponent,
    SettlementSummaryComponent,
    SettlementDisputeComponent
  ]
})
export class SettlementsModule { }
