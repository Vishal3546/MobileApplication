import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';

@Component({
  selector: 'app-reports-list',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatButtonModule, MatIconModule, MatDatepickerModule, MatFormFieldModule, MatInputModule],
  template: `
    <div class="reports-2026">
      <div class="header">
        <h1>📊 Reports - Profit/Loss Hisab</h1>
        <p>Daily, Weekly, Monthly, Yearly reports</p>
      </div>

      <div class="report-grid">
        <mat-card class="report-card">
          <mat-icon>today</mat-icon>
          <h3>Aaj Ka Hisab</h3>
          <p>Buy: 3 phones ₹45k, Sell: 2 phones ₹35k, Profit ₹5k</p>
          <button mat-raised-button color="primary"><mat-icon>download</mat-icon> PDF Download</button>
        </mat-card>
        <mat-card class="report-card">
          <mat-icon>calendar_month</mat-icon>
          <h3>Month Ka Hisab</h3>
          <p>Buy: 30 phones, Sell: 25, Stock: 5, Profit ₹45k</p>
          <button mat-raised-button color="primary"><mat-icon>download</mat-icon> PDF Download</button>
        </mat-card>
        <mat-card class="report-card">
          <mat-icon>bar_chart</mat-icon>
          <h3>Brand-wise Profit</h3>
          <p>iPhone: ₹20k, Samsung: ₹15k, Redmi: ₹10k</p>
          <button mat-raised-button color="primary"><mat-icon>visibility</mat-icon> View Chart</button>
        </mat-card>
        <mat-card class="report-card">
          <mat-icon>picture_as_pdf</mat-icon>
          <h3>Custom Report</h3>
          <p>Date range select karke report nikalo</p>
          <div style="display:flex; gap:10px; margin-top:10px;">
            <mat-form-field appearance="outline" style="flex:1">
              <mat-label>From</mat-label>
              <input matInput type="date">
            </mat-form-field>
            <mat-form-field appearance="outline" style="flex:1">
              <mat-label>To</mat-label>
              <input matInput type="date">
            </mat-form-field>
          </div>
          <button mat-raised-button color="accent" style="width:100%"><mat-icon>download</mat-icon> Generate Report</button>
        </mat-card>
      </div>

      <mat-card class="info">
        <mat-icon>lightbulb</mat-icon>
        <div>
          <strong>2026 Feature:</strong> Reports abhi mock hai. Backend me <code>/api/v1/reports/profit</code> API banana hai.
          Future: Excel export, WhatsApp share, GST report (if needed).
        </div>
      </mat-card>
    </div>
  `,
  styles: [`
    .reports-2026 { max-width: 1200px; margin: 0 auto; }
    .header h1 { margin: 0; font-size: 24px; font-weight: 800; }
    .header p { color: #64748b; margin-bottom: 20px; }
    .report-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(280px, 1fr)); gap: 20px; margin-bottom: 20px; }
    .report-card { padding: 20px !important; border-radius: 12px !important; text-align: center; }
    .report-card mat-icon { font-size: 40px; width: 40px; height: 40px; background: #e0e7ff; border-radius: 50%; padding: 12px; color: #4338ca; margin-bottom: 12px; }
    .report-card h3 { margin: 0 0 8px; font-weight: 700; }
    .report-card p { color: #64748b; font-size: 13px; margin-bottom: 16px; }
    .info { display: flex; gap: 12px; padding: 16px !important; background: #eff6ff !important; border-radius: 12px !important; }
  `]
})
export class ReportsListComponent {}
