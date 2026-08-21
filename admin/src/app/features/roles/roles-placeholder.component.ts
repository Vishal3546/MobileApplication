import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-roles-placeholder',
  standalone: true,
  imports: [CommonModule],
  template: '<div class="empty-state"><h2>Roles Management</h2><p>Backend API not available.</p></div>'
})
export class RolesPlaceholderComponent {}
