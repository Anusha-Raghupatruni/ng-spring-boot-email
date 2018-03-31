import { TestBed, inject } from '@angular/core/testing';

import { SendEmailService } from './send-email.service';

describe('SendEmailService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [SendEmailService]
    });
  });

  it('should be created', inject([SendEmailService], (service: SendEmailService) => {
    expect(service).toBeTruthy();
  }));
});
