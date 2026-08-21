import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-branches-placeholder',
  standalone: true,
  imports: [CommonModule],
  template: '<div class="empty-state"><h2>Branches Management</h2><p>Backend API not available.</p></div>'
})
export class BranchesPlaceholderComponent {}
