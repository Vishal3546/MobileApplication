import { Component, OnInit, Input } from '@angular/core';
import { SettlementService, ShopLedgerSummary } from '../../services/settlement.service';

import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-settlement-summary',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './settlement-summary.component.html',
  styleUrls: ['./settlement-summary.component.scss']
})
export class SettlementSummaryComponent implements OnInit {
  @Input() shopId?: string;
  summary!: ShopLedgerSummary;
  loading = true;

  constructor(private settlementService: SettlementService) {}

  ngOnInit(): void {
    this.loadSummary();
  }

  loadSummary(): void {
    this.loading = true;
    this.settlementService.getSummary(this.shopId).subscribe({
      next: (data) => {
        this.summary = data;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }
}
