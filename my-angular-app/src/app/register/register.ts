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

import { AuthService, RegisterUser } from '../services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [
    FormsModule,
    CommonModule,
    RouterLink,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatFormFieldModule,
    MatInputModule,
    MatTooltipModule,
  ],
  templateUrl: './register.html',
  styleUrls: ['./register.css'],
})
export class Register {
  user = {
    username: '',
    mail: '',
    password: '',
    age: null as number | null,
    bio: '',
  };

  errorMessage = '';

  constructor(private authService: AuthService, private router: Router) {}
  register(): void {
    // validate required fields
    if (!this.user.username || !this.user.mail || !this.user.password) {
      this.showNotification('Please fill all required fields.');
      return;
    }

    // create payload compatible with RegisterUser
    const userToSend: RegisterUser = {
      username: this.user.username,
      mail: this.user.mail,
      password: this.user.password,
      age: this.user.age || 0,
      bio: this.user.bio,
    };

    this.authService.register(userToSend).subscribe({
      next: () => {
        this.router.navigate(['/login']);
        this.errorMessage = '';
      },
      error: (err) => {
        this.showNotification(err.error.error || 'Registration failed!');
      },
    });
  }

  showNotification(message: string, duration: number = 5000): void {
    this.errorMessage = message;
    setTimeout(() => (this.errorMessage = ''), duration);
  }
}
