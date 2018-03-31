import { AppErrorCode } from './app-error-code';

export class AppError {
  constructor(
    private _status: AppErrorCode,
    private _message: string
  ) {}

  get status(): AppErrorCode {
    return this._status;
  }

  get message(): string {
    return this._message;
  }
}
