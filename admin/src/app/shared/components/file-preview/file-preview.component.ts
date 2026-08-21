import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-file-preview',
  standalone: true,
  imports: [CommonModule],
  template: '<div class="file-preview">file-preview works!</div>'
})
export class FilePreviewComponent {
  @Input() data: any;
}
