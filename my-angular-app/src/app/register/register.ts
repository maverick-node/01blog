import { Component } from '@angular/core';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-register',
  imports: [FormsModule,HttpClientModule],
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
        window.location.href = '/dashboard';
        this.errorMessage = '';
      },
      (error) => {
        console.error('Registration error:', error.error.message);
        this.errorMessage = error.error?.message || 'Registration failed!';
      }
    );
  }
}
