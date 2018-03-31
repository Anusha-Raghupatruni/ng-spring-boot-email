import { FormControl, Validators } from '@angular/forms';
import { ValidationErrors } from '@angular/forms/src/directives/validators';

export class ChipListValidator {
  private static readonly REGEX_EMAIL = /^[a-z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,4}$/;

  validEmails(control: FormControl): ValidationErrors {
    if (ChipListValidator.isArray(control)) {
      const values = control.value as Array<string>;
      if (!ChipListValidator.isValued(values)) {
        return ChipListValidator.validationError();
      }

      if (!ChipListValidator.isEmail(values)) {
        return ChipListValidator.validationError();
      }
    }

    return null;
  }

  private static isArray(control: FormControl) {
    return control && control.value && Array.isArray(control.value);
  }

  private static isValued(values: string[]): boolean {
    return values.length > 0 && (values[0] || '').trim().length > 0;
  }

  private static isEmail(values: string[]): boolean {
    // Only need to validate the current value (i.e. last value in the array)
    // as values are added one at a time, so the previous would have been validated already
    const newValue = values[values.length - 1];
    if ((newValue || '').trim().length === 0) {
      return true;
    }

    if (ChipListValidator.REGEX_EMAIL.test(newValue.trim())) {
      return true;
    }

    return false;
  }

  private static validationError(): ValidationErrors {
    return {
      validEmails: {
        valid: false
      }
    }
  }
}
