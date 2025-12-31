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

    return this.authService.checkAuthentication().pipe(
      map(response => {
        // JWT valid → redirect to dashboard
        this.router.navigate(['/dashboard']);
        return false; // block login/register route
      }),
      catchError(error => {
        return of(true); // JWT invalid → allow login/register
      })
    );
  }
}
