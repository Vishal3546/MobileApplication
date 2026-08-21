import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-page-header',
  standalone: true,
  imports: [CommonModule],
  template: '<div class="page-header">page-header works!</div>'
})
export class PageHeaderComponent {
  @Input() data: any;
}
