import { Component } from '@angular/core';
import { MatCardModule } from '@angular/material/card';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [MatCardModule],
  template: `
    <h2>Dashboard</h2>
    <div class="dashboard-grid">
      <mat-card>
        <mat-card-header>
          <mat-card-title>Total Sales</mat-card-title>
        </mat-card-header>
        <mat-card-content>
          <h1>$0.00</h1>
        </mat-card-content>
      </mat-card>
    </div>
  `,
  styles: [`
    .dashboard-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
      gap: 20px;
    }
  `]
})
export class DashboardComponent {}
