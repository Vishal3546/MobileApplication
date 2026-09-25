import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';

@Component({
  selector: 'app-error-state',
  standalone: true,
  imports: [CommonModule, MatButtonModule, MatIconModule, MatCardModule],
  template: `
    <mat-card class="error-card">
      <div class="error-icon">
        <mat-icon>{{icon}}</mat-icon>
      </div>
      <h3>{{title}}</h3>
      <p>{{message}}</p>
      <small *ngIf="details">{{details}}</small>
      <div class="error-actions">
        <button mat-raised-button color="primary" (click)="retry.emit()">
          <mat-icon>refresh</mat-icon> Retry Karo
        </button>
        <button mat-stroked-button *ngIf="showHome" (click)="goHome.emit()">
          <mat-icon>home</mat-icon> Dashboard
        </button>
      </div>
      <div *ngIf="errorCode" class="error-code">Error: {{errorCode}}</div>
    </mat-card>
  `,
  styles: [`
    .error-card {
      text-align: center;
      padding: 40px 20px !important;
      border-radius: 16px !important;
      background: #fef2f2 !important;
      border: 1px solid #fecaca !important;
      max-width: 500px;
      margin: 20px auto;
    }
    .error-icon {
      width: 80px;
      height: 80px;
      background: #fee2e2;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      margin: 0 auto 20px;
    }
    .error-icon mat-icon {
      font-size: 40px;
      width: 40px;
      height: 40px;
      color: #dc2626;
    }
    h3 { margin: 0 0 8px; font-size: 20px; font-weight: 700; color: #991b1b; }
    p { margin: 0 0 8px; color: #7f1d1d; }
    small { display: block; color: #991b1b; opacity: 0.8; margin-bottom: 20px; font-family: monospace; background: #fee2e2; padding: 8px; border-radius: 8px; }
    .error-actions { display: flex; gap: 12px; justify-content: center; margin-top: 20px; }
    .error-code { margin-top: 16px; font-size: 11px; color: #991b1b; font-family: monospace; }
  `]
})
export class ErrorStateComponent {
  @Input() icon = 'error_outline';
  @Input() title = 'Kuch Galat Hua - Error';
  @Input() message = 'Data load nahi ho paya. Internet check karo ya retry karo.';
  @Input() details: string = '';
  @Input() errorCode: string | number = '';
  @Input() showHome = true;
  @Output() retry = new EventEmitter<void>();
  @Output() goHome = new EventEmitter<void>();
}
