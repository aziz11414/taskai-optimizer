import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { TokenService } from '../services/token.service';

export const adminGuard: CanActivateFn = () => {
  const tokenService = inject(TokenService);
  const router = inject(Router);

  if (!tokenService.isLoggedIn()) {
    return router.createUrlTree(['/login']);
  }

  return tokenService.isAdmin() ? true : router.createUrlTree(['/dashboard']);
};