import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-loading-overlay',
  standalone: true,
  imports: [CommonModule],
  template: '<div class="loading-overlay">loading-overlay works!</div>'
})
export class LoadingOverlayComponent {
  @Input() data: any;
}
