import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-audit-timeline',
  standalone: true,
  imports: [CommonModule],
  template: '<div class="audit-timeline">audit-timeline works!</div>'
})
export class AuditTimelineComponent {
  @Input() data: any;
}
