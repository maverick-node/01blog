import { catchError, map, Observable, of } from "rxjs";
import { AuthService } from "../services/auth.service";
import { CanActivate, Router } from "@angular/router";
import { Injectable } from "@angular/core";

@Injectable({
  providedIn: 'root',
})
export class DashboardGuard implements CanActivate {

  constructor(
    private router: Router,
    private authService: AuthService
  ) {}

  canActivate(): Observable<boolean> {
    return this.authService.checkAuthentication().pipe(
      map(res => {
        // User already authenticated → block login page
        if (res.status === 200) {
  
          return true;
        }
        return true;
      }),
      catchError(() => {
        this.router.navigate(['/login']);
        return of(false);
      })
    );
  }
}
