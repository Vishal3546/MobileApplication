import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DashboardService } from './dashboard.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  template: '<div class="dashboard"><h2>Dashboard</h2><div *ngIf="summary"><div>Sales Amount: {{ summary.salesAmount?.currentPeriod }}</div><div>Purchases Amount: {{ summary.purchasesAmount?.currentPeriod }}</div><div>Gross Profit: {{ summary.grossProfit?.currentPeriod }}</div></div></div>'
})
export class DashboardComponent implements OnInit {
  summary: any;
  constructor(private dashboardService: DashboardService) {}
  
  ngOnInit() {
    this.dashboardService.getDashboardSummary().subscribe(data => {
      this.summary = data.data;
    });
  }
}
