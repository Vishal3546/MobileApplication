import { Component, inject, OnInit } from '@angular/core';
import { DataTableComponent, TableColumn } from '../../shared/components/data-table/data-table';
import { InventoryService } from './inventory.service';
import { MatDialog } from '@angular/material/dialog';
import { ConfirmDialogComponent } from '../../shared/components/confirm-dialog/confirm-dialog';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-inventory-list',
  standalone: true,
  imports: [DataTableComponent],
  template: `
    <h2>Inventory Management</h2>
    <app-data-table 
      [columns]="columns" 
      [displayedColumns]="displayedColumns" 
      [data]="inventoryData"
      (onAction)="handleAction($event)">

      <!-- Custom Actions -->
      <div table-actions>
        <button mat-raised-button color="primary">Add Stock</button>
      </div>

    </app-data-table>
  `
})
export class InventoryListComponent implements OnInit {
  private inventoryService = inject(InventoryService);
  private dialog = inject(MatDialog);
  private snackBar = inject(MatSnackBar);

  inventoryData: any[] = [];

  columns: TableColumn[] = [
    { def: 'id', header: 'ID', cell: (row) => row.id ? row.id.substring(0, 8) + '...' : 'N/A' },
    { def: 'stockNumber', header: 'Stock #', cell: (row) => row.stockNumber },
    { def: 'brand', header: 'Brand', cell: (row) => row.brand || 'N/A' },
    { def: 'model', header: 'Model', cell: (row) => row.model || 'N/A' },
    { def: 'status', header: 'Status', cell: (row) => row.status }
  ];
  displayedColumns = ['id', 'stockNumber', 'brand', 'model', 'status', 'actions'];

  ngOnInit() {
    this.loadInventory();
  }

  loadInventory() {
    this.inventoryService.getInventoryList(0, 100).subscribe({
      next: (res) => {
        // Handle paginated response
        this.inventoryData = res.content || [];
      },
      error: (err) => console.error(err)
    });
  }

  handleAction(event: {action: string, row: any}) {
    if (event.action === 'delete') {
      const dialogRef = this.dialog.open(ConfirmDialogComponent, {
        data: {
          title: 'Confirm Deletion',
          message: `Are you sure you want to delete stock ${event.row.stockNumber}?`,
          color: 'warn'
        }
      });

      dialogRef.afterClosed().subscribe(result => {
        if (result) {
          this.snackBar.open('Stock deleted successfully.', 'Close', { duration: 3000 });
          // Implement delete via service
        }
      });
    } else if (event.action === 'edit') {
      this.snackBar.open(`Editing stock ${event.row.stockNumber}`, 'Close', { duration: 3000 });
    }
  }
}
