import { catchError, map, Observable, of } from "rxjs";
import { AuthService } from "../services/auth.service";
import { CanActivate, Router } from "@angular/router";
import { Injectable } from "@angular/core";

@Injectable({ providedIn: 'root' })
export class LoginGuard implements CanActivate {

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  canActivate(): Observable<boolean> {
    console.log('LoginGuard: canActivate called');

    return this.authService.checkAuthentication().pipe(
      map(response => {
        console.log('LoginGuard: JWT valid response', response);
        // JWT valid → redirect to dashboard
        this.router.navigate(['/dashboard']);
        return false; // block login/register route
      }),
      catchError(error => {
        console.warn('LoginGuard: JWT invalid/error, allowing access', error);
        return of(true); // JWT invalid → allow login/register
      })
    );
  }
}
