import { TestBed } from '@angular/core/testing';
import { ReportFilterPanelComponent } from './report-filter-panel.component';

describe('ReportFilterPanelComponent', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ReportFilterPanelComponent]
    });
  });

  it('should create', () => {
    const fixture = TestBed.createComponent(ReportFilterPanelComponent);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });
});
