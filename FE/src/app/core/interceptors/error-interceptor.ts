import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { ErrorService } from '../services/error.service';
import { ApiError } from '../models/api-error.model';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const errorService = inject(ErrorService);

  return next(req).pipe(
    catchError((httpError: HttpErrorResponse) => {
      let message = 'An unexpected error occurred. Please try again.';

      if (httpError.error && typeof httpError.error === 'object') {
        const apiError = httpError.error as ApiError;
        if (apiError.message) {
          message = apiError.message;
        }
      } else if (httpError.status === 0) {
        message = 'Cannot reach the server. Please check your connection.';
      } else if (httpError.status === 401) {
        message = 'Session expired. Please log in again.';
      } else if (httpError.status === 403) {
        message = 'You do not have permission to perform this action.';
      } else if (httpError.status === 404) {
        message = 'The requested resource was not found.';
      } else if (httpError.status >= 500) {
        message = 'A server error occurred. Please try again later.';
      }

      errorService.show(message);
      return throwError(() => new Error(message));
    })
  );
};
