import { Component, Input, Output, EventEmitter, ViewChild, AfterViewInit } from '@angular/core';
import { MatTableModule, MatTableDataSource } from '@angular/material/table';
import { MatPaginatorModule, MatPaginator } from '@angular/material/paginator';
import { MatSortModule, MatSort } from '@angular/material/sort';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { CommonModule } from '@angular/common';
import { MatMenuModule } from '@angular/material/menu';

export interface TableColumn {
  def: string;
  header: string;
  cell: (element: any) => string;
}

@Component({
  selector: 'app-data-table',
  standalone: true,
  imports: [
    CommonModule, MatTableModule, MatPaginatorModule, MatSortModule,
    MatInputModule, MatButtonModule, MatIconModule, MatMenuModule
  ],
  template: `
    <div class="table-container mat-elevation-z8">
      <div class="table-header">
        <mat-form-field appearance="outline" class="search-field">
          <mat-label>Filter</mat-label>
          <input matInput (keyup)="applyFilter($event)" placeholder="Ex. Pixel 8" #input>
          <mat-icon matSuffix>search</mat-icon>
        </mat-form-field>
        <div class="actions">
          <ng-content select="[table-actions]"></ng-content>
        </div>
      </div>

      <table mat-table [dataSource]="dataSource" matSort>
        <ng-container *ngFor="let col of columns" [matColumnDef]="col.def">
          <th mat-header-cell *matHeaderCellDef mat-sort-header> {{col.header}} </th>
          <td mat-cell *matCellDef="let element"> {{col.cell(element)}} </td>
        </ng-container>

        <ng-container matColumnDef="actions">
          <th mat-header-cell *matHeaderCellDef> Actions </th>
          <td mat-cell *matCellDef="let row">
            <button mat-icon-button [matMenuTriggerFor]="menu">
              <mat-icon>more_vert</mat-icon>
            </button>
            <mat-menu #menu="matMenu">
              <button mat-menu-item (click)="onAction.emit({ action: 'edit', row })">
                <mat-icon>edit</mat-icon>
                <span>Edit</span>
              </button>
              <button mat-menu-item (click)="onAction.emit({ action: 'delete', row })">
                <mat-icon color="warn">delete</mat-icon>
                <span>Delete</span>
              </button>
            </mat-menu>
          </td>
        </ng-container>

        <tr mat-header-row *matHeaderRowDef="displayedColumns"></tr>
        <tr mat-row *matRowDef="let row; columns: displayedColumns;"></tr>

        <tr class="mat-row" *matNoDataRow>
          <td class="mat-cell" colspan="4">No data matching the filter "{{input.value}}"</td>
        </tr>
      </table>

      <mat-paginator [pageSizeOptions]="[5, 10, 25, 100]" aria-label="Select page"></mat-paginator>
    </div>
  `,
  styles: [`
    .table-container {
      margin-top: 20px;
      width: 100%;
    }
    .table-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 20px 20px 0 20px;
    }
    .search-field {
      width: 300px;
    }
    table {
      width: 100%;
    }
  `]
})
export class DataTableComponent implements AfterViewInit {
  @Input() columns: TableColumn[] = [];
  @Input() set data(value: any[]) {
    this.dataSource.data = value;
  }
  @Input() displayedColumns: string[] = [];
  @Output() onAction = new EventEmitter<{action: string, row: any}>();

  dataSource = new MatTableDataSource<any>([]);

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();

    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }
}
