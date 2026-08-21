import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-filter-panel',
  standalone: true,
  imports: [CommonModule],
  template: '<div class="filter-panel">filter-panel works!</div>'
})
export class FilterPanelComponent {
  @Input() data: any;
}
