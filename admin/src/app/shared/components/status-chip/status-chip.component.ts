import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-status-chip',
  standalone: true,
  imports: [CommonModule],
  template: '<div class="status-chip">status-chip works!</div>'
})
export class StatusChipComponent {
  @Input() data: any;
}
