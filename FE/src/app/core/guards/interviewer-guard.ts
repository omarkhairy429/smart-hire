import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';

/** Allows only INTERVIEWER to access a route. */
export const interviewerGuard: CanActivateFn = (route, state) => {
  const router = inject(Router);
  const userStr = localStorage.getItem('user');

  if (userStr) {
    try {
      const user = JSON.parse(userStr);
      if (user.role === 'INTERVIEWER') {
        return true;
      }
    } catch {
      // malformed user data
    }
  }

  router.navigate(['/']);
  return false;
};
