import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';

import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatTooltipModule } from '@angular/material/tooltip';

import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    RouterLink,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatFormFieldModule,
    MatInputModule,
    MatTooltipModule
  ],
  templateUrl: './login.html',
  styleUrls: ['./login.css']
})
export class Login implements OnInit {

  user = {
    email: '',
    password: ''
  };

  errorMessage = '';

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.checkAuthentication();
  }

  
  checkAuthentication(): void {
    this.authService.checkAuthentication().subscribe({
      next: (res) => {
        if (res.status === 200) {
          this.router.navigate(['/dashboard']);
        }
      },
      error: () => {
       
        this.showNotification('Please log in to continue.');
      }
    });
  }


  login(): void {
    this.authService.login(this.user).subscribe({
      next: () => {
        this.router.navigate(['/dashboard']);
      },
      error: (err) => {
        this.showNotification(
          err.error?.error || 'Login failed. Please try again.'
        );
      }
    });
  }

  /**
   * Show error message
   */
  showNotification(message: string, duration: number = 5000): void {
    this.errorMessage = message;

    setTimeout(() => {
      this.errorMessage = '';
    }, duration);
  }
}
