import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-search-bar',
  standalone: true,
  imports: [CommonModule],
  template: '<div class="search-bar">search-bar works!</div>'
})
export class SearchBarComponent {
  @Input() data: any;
}
