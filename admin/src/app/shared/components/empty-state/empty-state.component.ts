import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';

@Component({
  selector: 'app-empty-state',
  standalone: true,
  imports: [CommonModule, MatButtonModule, MatIconModule, MatCardModule],
  template: `
    <mat-card class="empty-card">
      <div class="empty-icon">
        <mat-icon>{{icon}}</mat-icon>
      </div>
      <h3>{{title}}</h3>
      <p>{{message}}</p>
      <small *ngIf="hint">{{hint}}</small>
      <div class="empty-actions" *ngIf="actionLabel">
        <button mat-raised-button color="primary" (click)="action.emit()">
          <mat-icon>{{actionIcon}}</mat-icon> {{actionLabel}}
        </button>
      </div>
      <div *ngIf="showSearchTip" class="search-tip">
        <mat-icon>lightbulb</mat-icon>
        <span>Search ya filter change karke dekho</span>
      </div>
    </mat-card>
  `,
  styles: [`
    .empty-card {
      text-align: center;
      padding: 60px 20px !important;
      border-radius: 16px !important;
      background: #f8fafc !important;
      border: 2px dashed #e2e8f0 !important;
      max-width: 500px;
      margin: 20px auto;
    }
    .empty-icon {
      width: 80px;
      height: 80px;
      background: #e2e8f0;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      margin: 0 auto 20px;
    }
    .empty-icon mat-icon {
      font-size: 40px;
      width: 40px;
      height: 40px;
      color: #64748b;
    }
    h3 { margin: 0 0 8px; font-size: 20px; font-weight: 700; color: #0f172a; }
    p { margin: 0 0 8px; color: #475569; }
    small { display: block; color: #64748b; margin-bottom: 20px; }
    .empty-actions { margin-top: 20px; }
    .search-tip {
      margin-top: 20px;
      display: flex;
      align-items: center;
      gap: 8px;
      justify-content: center;
      font-size: 12px;
      color: #64748b;
      background: #eff6ff;
      padding: 8px 12px;
      border-radius: 20px;
    }
    .search-tip mat-icon { font-size: 16px; width: 16px; height: 16px; }
  `]
})
export class EmptyStateComponent {
  @Input() icon = 'inbox';
  @Input() title = 'Koi Data Nahi Mila';
  @Input() message = 'Abhi koi record nahi hai. Naya add karo.';
  @Input() hint = '';
  @Input() actionLabel = '';
  @Input() actionIcon = 'add';
  @Input() showSearchTip = false;
  @Output() action = new EventEmitter<void>();
}
