import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-money-display',
  standalone: true,
  imports: [CommonModule],
  template: '<div class="money-display">money-display works!</div>'
})
export class MoneyDisplayComponent {
  @Input() data: any;
}
