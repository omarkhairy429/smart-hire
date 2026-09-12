import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { ErrorService } from '../services/error.service';
import { ApiError } from '../models/api-error.model';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const errorService = inject(ErrorService);

  return next(req).pipe(
    catchError((httpError: HttpErrorResponse) => {
      const message = extractMessage(httpError);
      errorService.show(message);
      return throwError(() => httpError);
    })
  );
};

function extractMessage(httpError: HttpErrorResponse): string {
  if (httpError.status === 0) {
    return 'Cannot reach the server. Please check your connection.';
  }

  const body = httpError.error;

  if (body) {
    if (typeof body === 'object' && body.message) {
      return (body as ApiError).message;
    }
    if (typeof body === 'string') {
      try {
        const parsed: ApiError = JSON.parse(body);
        if (parsed?.message) return parsed.message;
      } catch {
      }
    }
  }

  switch (httpError.status) {
    case 400: return 'Invalid request. Please check your input.';
    case 401: return 'Session expired. Please log in again.';
    case 403: return 'You do not have permission to perform this action.';
    case 404: return 'The requested resource was not found.';
    case 409: return 'This resource already exists.';
    default:  return httpError.status >= 500
      ? 'A server error occurred. Please try again later.'
      : 'An unexpected error occurred. Please try again.';
  }
}
