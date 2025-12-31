import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { map, catchError } from 'rxjs/operators';

@Injectable({
  providedIn: 'root',
})
export class AdminGuard implements CanActivate {
  constructor(private router: Router, private http: HttpClient) {}

  canActivate(next: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> {
    return this.getUserRole().pipe(
      map((role) => {
        if (role === 'admin') {
          return true;
        } else if (role === 'user') {
          this.router.navigate(['/dashboard']);
          return false;
        } else {
          this.router.navigate(['/login']);

          return false;
        }
      }),
      catchError((error) => {
        console.error('Error fetching user role:', error);
        this.router.navigate(['/login']);
        return [false];
      })
    );
  }

  getUserRole(): Observable<string> {
    const apiMiddleware = 'http://localhost:8080/middleware';
    return this.http.get<{ role: string }>(apiMiddleware, { withCredentials: true }).pipe(
      map((response) => response.role.toLocaleLowerCase() || 'user'), // Return the role from the response or default to 'user'
      catchError((error) => {
        return ['error']; // Default to 'user' if there's an error
      })
    );
  }
}
