import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-error-state',
  standalone: true,
  imports: [CommonModule],
  template: '<div class="error-state">error-state works!</div>'
})
export class ErrorStateComponent {
  @Input() data: any;
}
