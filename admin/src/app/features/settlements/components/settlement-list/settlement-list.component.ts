import { Component, OnInit } from '@angular/core';
import { SettlementService } from '../../services/settlement.service';
import { PermissionService } from '../../../../core/auth/permission.service';

@Component({
  selector: 'app-settlement-list',
  templateUrl: './settlement-list.component.html',
  styleUrls: ['./settlement-list.component.scss']
})
export class SettlementListComponent implements OnInit {
  settlements: any[] = [];
  displayedColumns: string[] = ['settlementNumber', 'sourceShop', 'destinationShop', 'grossAmount', 'remainingAmount', 'status', 'dueAt', 'actions'];
  totalElements = 0;
  pageSize = 10;
  pageIndex = 0;

  constructor(
    private settlementService: SettlementService,
    public permissionService: PermissionService
  ) {}

  ngOnInit(): void {
    this.loadSettlements();
  }

  loadSettlements(): void {
    this.settlementService.getSettlements(undefined, this.pageIndex, this.pageSize).subscribe({
      next: (data) => {
        this.settlements = data.content;
        this.totalElements = data.totalElements;
      }
    });
  }

  onPageChange(event: any): void {
    this.pageIndex = event.pageIndex;
    this.pageSize = event.pageSize;
    this.loadSettlements();
  }
}
