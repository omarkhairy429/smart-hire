import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';

/** Allows only HR_MANAGER and SUPER_ADMIN to access a route. */
export const hrGuard: CanActivateFn = (route, state) => {
  const router = inject(Router);
  const userStr = localStorage.getItem('user');

  if (userStr) {
    try {
      const user = JSON.parse(userStr);
      if (user.role === 'HR_MANAGER' || user.role === 'SUPER_ADMIN') {
        return true;
      }
    } catch {
      // malformed user data
    }
  }

  router.navigate(['/']);
  return false;
};
