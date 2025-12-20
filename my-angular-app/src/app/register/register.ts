import { Component } from '@angular/core';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { FormsModule } from '@angular/forms';

import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatTooltipModule } from '@angular/material/tooltip';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
@Component({
  selector: 'app-register',
  imports: [FormsModule,HttpClientModule,CommonModule,
        MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatFormFieldModule,
    MatInputModule,
    MatTooltipModule,
    RouterLink
  ],
  templateUrl: './register.html',
  styleUrls: ['./register.css']
})


export class Register {
  user = {
    username: '',
    mail: '',
    password: '',
    age: null as number | null,
    bio: ''
  };
  errorMessage = '';

  constructor(private http: HttpClient) {}
  ngOnInit() {
    this.checkAuthentication();

  }

  checkAuthentication() {
    const apiMiddleware = 'http://localhost:8080/middleware';
    this.http.get(apiMiddleware, { withCredentials: true }).subscribe(
      (response: any) => {
        console.log('User already authenticated:', response);
        window.location.href = '/dashboard';
      },
      (error) => {
        console.log('User not authenticated:', error.error);
        
      }
    );
  }
  register() {
    const apiRegister = 'http://localhost:8080/register';
    this.http.post(apiRegister, this.user).subscribe(
      (response: any) => {
        console.log('Registration success:', response);
        window.location.href = '/login';
        this.errorMessage = '';
      },
      (error) => {
        console.log('Registration error:', error.error.error);
        this.errorMessage = error.error.error || 'Registration failed!';
      }
    );
  }
}
