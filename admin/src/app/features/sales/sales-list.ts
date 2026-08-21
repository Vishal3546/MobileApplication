import { Component, inject, OnInit } from '@angular/core';
import { DataTableComponent, TableColumn } from '../../shared/components/data-table/data-table';
import { SalesService } from './sales.service';
import { MatDialog } from '@angular/material/dialog';
import { ConfirmDialogComponent } from '../../shared/components/confirm-dialog/confirm-dialog';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-sales-list',
  standalone: true,
  imports: [DataTableComponent],
  template: `
    <h2>Sales Management</h2>
    <app-data-table 
      [columns]="columns" 
      [displayedColumns]="displayedColumns" 
      [data]="salesData"
      (onAction)="handleAction($event)">
    </app-data-table>
  `
})
export class SalesListComponent implements OnInit {
  private salesService = inject(SalesService);
  private dialog = inject(MatDialog);
  private snackBar = inject(MatSnackBar);

  salesData: any[] = [];

  columns: TableColumn[] = [
    { def: 'id', header: 'ID', cell: (row) => row.id ? row.id.substring(0, 8) + '...' : 'N/A' },
    { def: 'saleNumber', header: 'Sale #', cell: (row) => row.saleNumber },
    { def: 'finalAmount', header: 'Final Amount', cell: (row) => '$' + row.finalAmount },
    { def: 'saleStatus', header: 'Status', cell: (row) => row.saleStatus },
    { def: 'createdAt', header: 'Date', cell: (row) => new Date(row.createdAt).toLocaleDateString() }
  ];
  displayedColumns = ['id', 'saleNumber', 'finalAmount', 'saleStatus', 'createdAt', 'actions'];

  ngOnInit() {
    this.loadSales();
  }

  loadSales() {
    this.salesService.getSalesList(0, 100).subscribe({
      next: (res) => {
        this.salesData = res.content || [];
      },
      error: (err) => console.error(err)
    });
  }

  handleAction(event: {action: string, row: any}) {
    if (event.action === 'delete') {
      const dialogRef = this.dialog.open(ConfirmDialogComponent, {
        data: {
          title: 'Cancel Sale',
          message: `Are you sure you want to cancel sale ${event.row.saleNumber}?`,
          color: 'warn',
          confirmText: 'Cancel Sale'
        }
      });

      dialogRef.afterClosed().subscribe(result => {
        if (result) {
          this.salesService.cancelSale(event.row.id, "Admin cancellation").subscribe({
            next: () => {
              this.snackBar.open('Sale cancelled successfully.', 'Close', { duration: 3000 });
              this.loadSales();
            }
          });
        }
      });
    } else if (event.action === 'edit') {
      this.snackBar.open(`Viewing sale ${event.row.saleNumber}`, 'Close', { duration: 3000 });
    }
  }
}
