import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { SettlementService, ShopSettlement } from '../../services/settlement.service';
import { PermissionService } from '../../../../core/auth/permission.service';

@Component({
  selector: 'app-settlement-detail',
  templateUrl: './settlement-detail.component.html',
  styleUrls: ['./settlement-detail.component.scss']
})
export class SettlementDetailComponent implements OnInit {
  settlement: ShopSettlement | null = null;
  loading = true;
  id: string = '';

  constructor(
    private route: ActivatedRoute,
    private settlementService: SettlementService,
    public permissionService: PermissionService
  ) {}

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.id = params['id'];
      this.loadSettlement();
    });
  }

  loadSettlement(): void {
    this.loading = true;
    this.settlementService.getSettlement(this.id).subscribe({
      next: (data) => {
        this.settlement = data;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }
}
