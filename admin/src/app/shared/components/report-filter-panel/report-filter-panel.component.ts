import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-report-filter-panel',
  standalone: true,
  imports: [CommonModule],
  template: '<div class="report-filter-panel">report-filter-panel works!</div>'
})
export class ReportFilterPanelComponent {
  @Input() data: any;
}
