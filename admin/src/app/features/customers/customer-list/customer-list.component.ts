import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-customer-list',
  standalone: true,
  imports: [CommonModule],
  template: '<div class="customer-list">CustomerListComponent</div>'
})
export class CustomerListComponent {}
