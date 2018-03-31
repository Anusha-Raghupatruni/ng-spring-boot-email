import { AbstractControl, ValidationErrors, Validators } from '@angular/forms';

export class FieldValidator {
  blankOrEmail(control: AbstractControl): ValidationErrors | null {
    return (control.value || '').trim().length === 0 ?
      null : Validators.email(control);
  }

  blank(control: AbstractControl): ValidationErrors | null {
    return (control.value || '').trim().length > 0 ? null : {
        blank: {
          valid: false
        }
      };
  }
}
