export interface User {
  username: string;
  password: string;
}

export interface LoginResponse {
  token?: string;
  expiresAt?: string;
  user?: {
    id?: string;
    username?: string;
  };
}
