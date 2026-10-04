export type UserRole = 'ADMIN' | 'MANAGER' | 'USER';

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  fullName: string;
  email: string;
  password: string;
}

export interface AuthResponse {
  id: number;
  fullName: string;
  email: string;
  role: UserRole | string;
  token: string;
  message: string;
}