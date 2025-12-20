import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { Router, RouterLink } from '@angular/router';

import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatTooltipModule } from '@angular/material/tooltip';
import { CommonModule } from '@angular/common';
import { catchError, of } from 'rxjs';
@Component({
  selector: 'app-login',
  imports: [
    CommonModule,
    FormsModule,
    HttpClientModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatFormFieldModule,
    MatInputModule,
    MatTooltipModule,
    RouterLink
  ],
  templateUrl: './login.html',
  styleUrls: ['./login.css'],
  
})
export class Login {
  user = {
    email: '',
    password: '',
  };

  errorMessage = '';

  constructor(private http: HttpClient, private router: Router) {}

  ngOnInit() {
    this.checkAuthentication();
  }


checkAuthentication() {
  this.http.get('http://localhost:8080/middleware', {
    withCredentials: true,
    observe: 'response' // Full response including status and headers
  }).subscribe({
    next: (response) => {
      if (response.status === 200) {
        this.router.navigate(['/dashboard']);
      } else {
        // Handle non-200 status codes (e.g., 404)
        this.router.navigate(['/login']);
      }
    },
    error: (err) => {
   
    }
  });
}




  login() {
    this.http
      .post('http://localhost:8080/login', this.user, {
        withCredentials: true,
      })
      
      .subscribe({
        
        next: () => {
          this.router.navigate(['/dashboard']);
        },
        error: (err: any) => {
    
          this.showNotification(err.error?.error || 'Login failed. Please try again.');
        },
      });
  }

  showNotification(message: string, duration: number = 5000) {
    this.errorMessage = message;

    // Auto hide after duration
    setTimeout(() => {
      this.errorMessage = '';
    }, duration);
  }
}
