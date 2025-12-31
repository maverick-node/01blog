import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface LoginUser {
  email: string;
  password: string;
}

export interface RegisterUser {
  username: string;
  mail: string;
  password: string;
  age: number;
  bio: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private API_URL = 'http://localhost:8080';

  constructor(private http: HttpClient) {}

  /**
   * Login user
   * Sends email & password to backend
   * Backend sets JWT in HttpOnly cookie
   */
  login(user: LoginUser): Observable<any> {
    return this.http.post(
      `${this.API_URL}/login`,
      user,
      { withCredentials: true }
    );
  }

  /**
   * Register new user
   */
  register(user: RegisterUser): Observable<any> {
    return this.http.post(
      `${this.API_URL}/register`,
      user,
      { withCredentials: true }
    );
  }

  /**
   * Check if user is authenticated
   * Calls backend middleware
   */
  checkAuthentication(): Observable<any> {
    return this.http.get(
      `${this.API_URL}/middleware`,
      {
        withCredentials: true,
        observe: 'response' 
      }
    );
  }

  logout(): Observable<any> {
    return this.http.post(
      `${this.API_URL}/logout`,
      {},
      { withCredentials: true }
    );
  }
  
}
