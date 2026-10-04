import { Inject, Injectable, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { AuthResponse } from '../models/auth.models';

const TOKEN_KEY = 'taskai_token';
const USER_KEY = 'taskai_user';

@Injectable({
  providedIn: 'root'
})
export class TokenService {
  private readonly isBrowser: boolean;

  constructor(@Inject(PLATFORM_ID) private platformId: object) {
    this.isBrowser = isPlatformBrowser(this.platformId);
  }

  setSession(auth: AuthResponse): void {
    if (!this.isBrowser) return;

    if (auth.token) {
      localStorage.setItem(TOKEN_KEY, auth.token);
    }

    localStorage.setItem(USER_KEY, JSON.stringify(auth));
  }

  getToken(): string | null {
    if (!this.isBrowser) return null;
    return localStorage.getItem(TOKEN_KEY);
  }

  getUser(): AuthResponse | null {
    if (!this.isBrowser) return null;

    const raw = localStorage.getItem(USER_KEY);
    if (!raw) return null;

    try {
      return JSON.parse(raw) as AuthResponse;
    } catch {
      this.clearSession();
      return null;
    }
  }

  getRole(): string | null {
    return this.getUser()?.role ?? null;
  }

  isAdmin(): boolean {
    return this.getRole() === 'ADMIN';
  }

  isManager(): boolean {
    return this.getRole() === 'MANAGER';
  }

  isUser(): boolean {
    return this.getRole() === 'USER';
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  clearSession(): void {
    if (!this.isBrowser) return;

    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
  }
}