import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { FormsModule } from '@angular/forms';
import { DeviceService } from '../device.service';

@Component({
  selector: 'app-device-list',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatButtonModule, MatIconModule, MatInputModule, MatFormFieldModule, FormsModule],
  template: `
    <div class="devices-2026">
      <div class="page-header">
        <div>
          <h1>📱 Phone Models - Master List</h1>
          <p>Kaunse models aate hai, unka database</p>
        </div>
        <button mat-raised-button color="primary">
          <mat-icon>add</mat-icon> Naya Model Add
        </button>
      </div>

      <mat-card class="search-card">
        <mat-form-field appearance="outline" style="flex:1">
          <mat-label>Search Model - Brand, Model</mat-label>
          <input matInput [(ngModel)]="search" (keyup)="filter()" placeholder="iPhone 14, Samsung S23...">
          <mat-icon matSuffix>search</mat-icon>
        </mat-form-field>
        <div class="stats">
          <span>{{filtered.length}} models</span>
          <small>{{brands.length}} brands</small>
        </div>
      </mat-card>

      <div class="model-grid">
        <mat-card *ngFor="let d of filtered" class="model-card">
          <div class="model-header">
            <span class="brand">{{d.brand}}</span>
            <span class="ram">{{d.ram || '8GB'}} RAM</span>
          </div>
          <div class="model-name">{{d.model}}</div>
          <div class="specs">
            <span>Storage: {{d.storage || '128GB'}}</span>
            <span>Color: {{d.color || 'Black'}}</span>
          </div>
          <div class="imei">IMEI: {{d.imei1 || '35****1234'}}</div>
          <div class="actions">
            <button mat-stroked-button><mat-icon>edit</mat-icon> Edit</button>
            <button mat-stroked-button><mat-icon>visibility</mat-icon> View Phones</button>
          </div>
        </mat-card>
      </div>

      <mat-card class="info">
        <mat-icon>info</mat-icon>
        <div>Ye master list hai - yaha se phone models manage hote hai. Actual stock <strong>Inventory</strong> me dekho. 2026 me yaha se auto specs aayenge IMEI se.</div>
      </mat-card>
    </div>
  `,
  styles: [`
    .devices-2026 { max-width: 1400px; margin: 0 auto; }
    .page-header { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 20px; flex-wrap: wrap; gap: 16px; }
    .page-header h1 { margin: 0; font-size: 24px; font-weight: 800; }
    .page-header p { margin: 4px 0 0; color: #64748b; }
    .search-card { display: flex; gap: 20px; align-items: center; padding: 16px 20px !important; margin-bottom: 20px; border-radius: 12px !important; }
    .stats { text-align: right; }
    .stats span { display: block; font-weight: 800; font-size: 18px; }
    .stats small { color: #64748b; }
    .model-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(280px, 1fr)); gap: 16px; margin-bottom: 20px; }
    .model-card { border-radius: 12px !important; padding: 16px !important; }
    .model-header { display: flex; justify-content: space-between; margin-bottom: 8px; }
    .brand { background: #e0e7ff; color: #4338ca; padding: 4px 10px; border-radius: 20px; font-size: 11px; font-weight: 700; }
    .ram { font-size: 11px; color: #64748b; }
    .model-name { font-size: 18px; font-weight: 800; margin-bottom: 8px; }
    .specs { display: flex; gap: 12px; font-size: 12px; color: #64748b; margin-bottom: 8px; }
    .imei { font-family: monospace; font-size: 11px; background: #f1f5f9; padding: 4px 8px; border-radius: 6px; margin-bottom: 12px; }
    .actions { display: flex; gap: 8px; }
    .info { display: flex; gap: 12px; padding: 16px !important; background: #eff6ff !important; border-radius: 12px !important; }
  `]
})
export class DeviceListComponent implements OnInit {
  private deviceService = inject(DeviceService);
  devices: any[] = [];
  filtered: any[] = [];
  search = '';
  brands: string[] = [];

  ngOnInit() { this.load(); }

  load() {
    this.deviceService.getDevices(undefined, undefined, undefined, 0, 100).subscribe({
      next: (res: any) => {
        const data = res.content || res.data?.content || res.data || [];
        this.devices = data.length ? data : this.mock();
        this.filtered = [...this.devices];
        this.brands = [...new Set(this.devices.map(d => d.brand))];
      },
      error: () => {
        this.devices = this.mock();
        this.filtered = [...this.devices];
        this.brands = [...new Set(this.devices.map(d => d.brand))];
      }
    });
  }

  mock() {
    return [
      { brand: 'Samsung', model: 'S23', storage: '256GB', ram: '8GB', color: 'Black', imei1: '351234567890123' },
      { brand: 'iPhone', model: '12', storage: '128GB', ram: '4GB', color: 'White', imei1: '352345678901234' },
      { brand: 'Redmi', model: 'Note 13', storage: '128GB', ram: '6GB', color: 'Blue', imei1: '353456789012345' },
      { brand: 'OnePlus', model: '11R', storage: '256GB', ram: '12GB', color: 'Green', imei1: '354567890123456' },
    ];
  }

  filter() {
    const q = this.search.toLowerCase();
    if (!q) { this.filtered = [...this.devices]; return; }
    this.filtered = this.devices.filter(d => `${d.brand} ${d.model}`.toLowerCase().includes(q));
  }
}
