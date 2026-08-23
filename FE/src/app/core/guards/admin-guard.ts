import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';

export const adminGuard: CanActivateFn = (route, state) => {
  const router = inject(Router);
  const userStr = localStorage.getItem('user');

  if (userStr) {
    const user = JSON.parse(userStr);
    if (user.role === 'SUPER_ADMIN') {
      return true; 
    }
  }

  router.navigate(['/']);
  return false; 
};