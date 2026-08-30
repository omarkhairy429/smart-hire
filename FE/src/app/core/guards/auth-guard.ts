import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';

/** Blocks unauthenticated users and stores returnUrl so they land back after login. */
export const authGuard: CanActivateFn = (route, state) => {
  const router = inject(Router);
  const token = localStorage.getItem('token');

  if (token) {
    return true;
  }

  router.navigate(['/login'], { queryParams: { returnUrl: state.url } });
  return false;
};
