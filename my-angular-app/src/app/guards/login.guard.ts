import { catchError, map, Observable, of } from "rxjs";
import { AuthService } from "../services/auth.service";
import { CanActivate, Router } from "@angular/router";
import { Injectable } from "@angular/core";

@Injectable({
  providedIn: 'root',
})
export class LoginGuard implements CanActivate {

  constructor(
    private router: Router,
    private authService: AuthService
  ) {}

  canActivate(): Observable<boolean> {
    return this.authService.checkAuthentication().pipe(
      map(res => {
        // User already authenticated → block login page
        if (res.status === 200) {
          this.router.navigate(['/dashboard']);
          return false;
        }
        return true;
      }),
      catchError(() => {
        // Not authenticated → allow login page
        return of(true);
      })
    );
  }
}
