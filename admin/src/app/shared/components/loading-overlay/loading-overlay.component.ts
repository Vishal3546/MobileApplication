import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-loading-overlay',
  standalone: true,
  imports: [CommonModule, MatProgressSpinnerModule, MatIconModule],
  template: `
    <div class="loading-overlay" [class.fullscreen]="fullscreen" [class.transparent]="transparent">
      <div class="loading-content">
        <mat-spinner [diameter]="diameter" [color]="color"></mat-spinner>
        <h3 *ngIf="title">{{title}}</h3>
        <p *ngIf="message">{{message}}</p>
        <small *ngIf="hint">{{hint}}</small>
      </div>
    </div>
  `,
  styles: [`
    .loading-overlay {
      display: flex;
      align-items: center;
      justify-content: center;
      padding: 40px;
      background: rgba(255,255,255,0.9);
      border-radius: 12px;
      min-height: 200px;
    }
    .loading-overlay.fullscreen {
      position: fixed;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      z-index: 9999;
      background: rgba(255,255,255,0.95);
      backdrop-filter: blur(4px);
    }
    .loading-overlay.transparent {
      background: transparent;
    }
    .loading-content {
      text-align: center;
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 12px;
    }
    h3 { margin: 0; font-size: 16px; font-weight: 600; color: #0f172a; }
    p { margin: 0; color: #475569; font-size: 14px; }
    small { color: #64748b; font-size: 12px; }
  `]
})
export class LoadingOverlayComponent {
  @Input() title = 'Loading...';
  @Input() message = 'Hisab load ho raha hai, thoda wait karo...';
  @Input() hint = '';
  @Input() diameter = 50;
  @Input() color: 'primary' | 'accent' | 'warn' = 'primary';
  @Input() fullscreen = false;
  @Input() transparent = false;
}
