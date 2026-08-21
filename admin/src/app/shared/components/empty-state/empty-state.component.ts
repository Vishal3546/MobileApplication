import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-empty-state',
  standalone: true,
  imports: [CommonModule],
  template: '<div class="empty-state">empty-state works!</div>'
})
export class EmptyStateComponent {
  @Input() data: any;
}
