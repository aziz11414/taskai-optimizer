export type UserRole = 'ADMIN' | 'MANAGER' | 'USER';

export interface UserRequest {
  fullName: string;
  email: string;
  password: string;
  role: UserRole;
}

export interface UserResponse {
  id: number;
  fullName: string;
  email: string;
  role: UserRole;
}