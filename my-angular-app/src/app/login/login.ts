import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpClient, HttpClientModule } from '@angular/common/http';

@Component({
  selector: 'app-login',
  imports: [FormsModule, HttpClientModule],
  templateUrl: './login.html',
  styleUrls: ['./login.css'],
})

export class Login {
  user = {
    email: '',
    password: '',
  };
  errorMessage="";
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
  login() {
    const apiLogin = 'http://localhost:8080/login';
    this.http.post(apiLogin, this.user, { withCredentials: true }).subscribe(
      (response: any) => {
        console.log(response.message);
        window.location.href = '/dashboard';
      },
      (error) => {
        console.log(error.error.message);
         this.showNotification(error.error.message);
      }
    );
  }

  showNotification(message: string) {
    console.log("error")
    this.errorMessage = message; 
  }
}
