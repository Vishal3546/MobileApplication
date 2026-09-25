import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatChipsModule } from '@angular/material/chips';
import { FormsModule } from '@angular/forms';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-purchases-list',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatTableModule, MatButtonModule, MatIconModule, MatInputModule, MatFormFieldModule, MatChipsModule, FormsModule],
  template: `
    <div class="purchases-2026">
      <div class="page-header">
        <div>
          <h1>🛒 Kharida - Buy History</h1>
          <p>Kisse phone liya, kitne me liya - Sab hisab</p>
        </div>
        <button mat-raised-button color="primary">
          <mat-icon>add</mat-icon> Naya Kharida Add Karo
        </button>
      </div>

      <mat-card class="stats-bar">
        <div class="stat">
          <span class="label">Total Kharida</span>
          <span class="value">{{purchases.length}} phones</span>
        </div>
        <div class="stat">
          <span class="label">Total Amount</span>
          <span class="value">₹{{totalAmount | number}}</span>
        </div>
        <div class="stat">
          <span class="label">Aaj Kharida</span>
          <span class="value">{{todayCount}} phones</span>
        </div>
        <mat-form-field appearance="outline" class="search">
          <mat-label>Search - Brand, IMEI, Seller</mat-label>
          <input matInput [(ngModel)]="search" (keyup)="filter()" placeholder="Samsung, 1234, Ramesh...">
          <mat-icon matSuffix>search</mat-icon>
        </mat-form-field>
      </mat-card>

      <mat-card class="table-card">
        <div class="table-responsive">
          <table class="hisab-table">
            <thead>
              <tr>
                <th>Date</th>
                <th>Phone</th>
                <th>IMEI</th>
                <th>Buy Price</th>
                <th>Seller - Jisse Liya</th>
                <th>Phone</th>
                <th>Status</th>
                <th>Action</th>
              </tr>
            </thead>
            <tbody>
              <tr *ngFor="let p of filtered">
                <td>
                  <span class="date">{{p.createdAt | date:'dd MMM'}}</span>
                  <small>{{p.createdAt | date:'hh:mm a'}}</small>
                </td>
                <td>
                  <span class="phone">{{p.device?.brand || 'Samsung'}} {{p.device?.model || 'S23'}}</span>
                  <small>{{p.device?.storage || '128GB'}} | {{p.device?.color || 'Black'}}</small>
                </td>
                <td>
                  <span class="imei">{{p.device?.imei1 || '35****1234'}}</span>
                </td>
                <td>
                  <span class="price buy">₹{{p.finalPrice || p.totalAmount || 15000 | number}}</span>
                </td>
                <td>
                  <span class="seller">{{p.customer?.name || 'Ramesh Bhai'}}</span>
                  <small>{{p.customer?.phone || '98765 43210'}}</small>
                </td>
                <td>
                  <span class="seller-phone">{{p.customer?.phone || '98765 43210'}}</span>
                </td>
                <td>
                  <span class="status" [class.sold]="p.transactionStatus === 'COMPLETED'" [class.stock]="p.transactionStatus !== 'COMPLETED'">
                    {{p.transactionStatus === 'COMPLETED' ? 'Stock Me' : p.transactionStatus}}
                  </span>
                </td>
                <td>
                  <button mat-icon-button><mat-icon>visibility</mat-icon></button>
                  <button mat-icon-button><mat-icon>edit</mat-icon></button>
                  <button mat-icon-button><mat-icon>share</mat-icon></button>
                </td>
              </tr>
              <tr *ngIf="filtered.length === 0">
                <td colspan="8" class="empty">
                  <mat-icon>shopping_cart</mat-icon>
                  <p>Koi kharida nahi mila</p>
                  <small>Search change karo ya naya add karo</small>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </mat-card>

      <!-- Mock Data Notice -->
      <mat-card class="info-card">
        <mat-icon>info</mat-icon>
        <div>
          <strong>2026 Improvement:</strong> Ye table ab simple hai - Seller name/phone direct dikhta hai.
          Backend me <code>/api/v1/purchases</code> se data aata hai. Agar empty hai to mock data dikhega.
          <br><small>Future: WhatsApp share, PDF bill, Seller ko call button</small>
        </div>
      </mat-card>
    </div>
  `,
  styles: [`
    .purchases-2026 { max-width: 1400px; margin: 0 auto; }
    .page-header { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 20px; flex-wrap: wrap; gap: 16px; }
    .page-header h1 { margin: 0; font-size: 24px; font-weight: 800; }
    .page-header p { margin: 4px 0 0; color: #64748b; }
    .stats-bar { display: flex; gap: 20px; padding: 16px 20px !important; margin-bottom: 20px; align-items: center; flex-wrap: wrap; border-radius: 12px !important; }
    .stat { display: flex; flex-direction: column; }
    .stat .label { font-size: 11px; color: #64748b; text-transform: uppercase; font-weight: 600; }
    .stat .value { font-size: 18px; font-weight: 800; color: #0f172a; }
    .search { flex: 1; min-width: 250px; margin-left: auto; }
    .table-card { border-radius: 12px !important; overflow: hidden; }
    .table-responsive { overflow-x: auto; }
    .hisab-table { width: 100%; border-collapse: collapse; }
    .hisab-table th { background: #f8fafc; padding: 12px; text-align: left; font-size: 11px; font-weight: 700; color: #64748b; text-transform: uppercase; border-bottom: 2px solid #e2e8f0; white-space: nowrap; }
    .hisab-table td { padding: 14px 12px; border-bottom: 1px solid #f1f5f9; font-size: 13px; }
    .hisab-table tr:hover { background: #f8fafc; }
    .date { display: block; font-weight: 600; }
    .hisab-table small { display: block; color: #64748b; font-size: 11px; }
    .phone { font-weight: 600; display: block; }
    .imei { font-family: monospace; background: #f1f5f9; padding: 2px 6px; border-radius: 4px; font-size: 12px; }
    .price.buy { font-weight: 800; color: #ef4444; }
    .seller { font-weight: 600; display: block; }
    .status { padding: 4px 10px; border-radius: 20px; font-size: 11px; font-weight: 700; }
    .status.stock { background: #dcfce7; color: #166534; }
    .status.sold { background: #dbeafe; color: #1e40af; }
    .empty { text-align: center; padding: 40px !important; color: #64748b; }
    .empty mat-icon { font-size: 48px; width: 48px; height: 48px; opacity: 0.3; }
    .info-card { margin-top: 20px; display: flex; gap: 12px; padding: 16px !important; background: #eff6ff !important; border: 1px solid #bfdbfe !important; border-radius: 12px !important; }
  `]
})
export class PurchasesListComponent implements OnInit {
  private http = inject(HttpClient);
  purchases: any[] = [];
  filtered: any[] = [];
  search = '';
  totalAmount = 0;
  todayCount = 0;

  ngOnInit() {
    this.load();
  }

  load() {
    this.http.get<any>(`${environment.apiUrl}/api/v1/purchases?page=0&size=100`).subscribe({
      next: (res) => {
        const data = res.data?.content || res.content || res.data || [];
        this.purchases = data.length ? data : this.mockData();
        this.filtered = [...this.purchases];
        this.calcStats();
      },
      error: () => {
        this.purchases = this.mockData();
        this.filtered = [...this.purchases];
        this.calcStats();
      }
    });
  }

  mockData() {
    return [
      { id: '1', createdAt: new Date(), finalPrice: 15000, totalAmount: 15000, transactionStatus: 'COMPLETED', device: { brand: 'Samsung', model: 'S23', storage: '256GB', color: 'Black', imei1: '351234567890123' }, customer: { name: 'Ramesh Bhai', phone: '98765 43210' } },
      { id: '2', createdAt: new Date(Date.now() - 86400000), finalPrice: 22000, totalAmount: 22000, transactionStatus: 'COMPLETED', device: { brand: 'iPhone', model: '12', storage: '128GB', color: 'White', imei1: '352345678901234' }, customer: { name: 'Mahesh', phone: '98765 43211' } },
      { id: '3', createdAt: new Date(Date.now() - 2*86400000), finalPrice: 10000, totalAmount: 10000, transactionStatus: 'INITIATED', device: { brand: 'Redmi', model: 'Note 13', storage: '128GB', color: 'Blue', imei1: '353456789012345' }, customer: { name: 'Suresh', phone: '98765 43212' } },
    ];
  }

  calcStats() {
    this.totalAmount = this.purchases.reduce((s, p) => s + (p.finalPrice || p.totalAmount || 0), 0);
    const today = new Date().toDateString();
    this.todayCount = this.purchases.filter(p => new Date(p.createdAt).toDateString() === today).length;
  }

  filter() {
    const q = this.search.toLowerCase();
    if (!q) {
      this.filtered = [...this.purchases];
      return;
    }
    this.filtered = this.purchases.filter(p =>
      `${p.device?.brand} ${p.device?.model} ${p.device?.imei1} ${p.customer?.name} ${p.customer?.phone}`.toLowerCase().includes(q)
    );
  }
}
