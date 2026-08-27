import { Component, OnInit, Input } from '@angular/core';
import { SettlementService, ShopLedgerSummary } from '../../services/settlement.service';

@Component({
  selector: 'app-settlement-summary',
  templateUrl: './settlement-summary.component.html',
  styleUrls: ['./settlement-summary.component.scss']
})
export class SettlementSummaryComponent implements OnInit {
  @Input() shopId?: string;
  summary: ShopLedgerSummary | null = null;
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
