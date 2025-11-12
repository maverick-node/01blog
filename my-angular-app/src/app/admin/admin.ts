import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { Router } from '@angular/router';

@Component({
  selector: 'app-admin',
  imports: [CommonModule, HttpClientModule],
  templateUrl: './admin.html',
  styleUrl: './admin.css',
})
export class Admin implements OnInit {
  users: any[] = [];
  posts: any[] = [];
  reports: any[] = [];
  loading = false;
  errorMessage = '';
  private token: string = '';

  constructor(private http: HttpClient, private router: Router) {}

  ngOnInit(): void {
    this.checkAuthentication();
  
  }

checkAuthentication() {
    const apiMiddleware = 'http://localhost:8080/middleware';
    this.http.get(apiMiddleware, { withCredentials: true }).subscribe(
      (response: any) => {
        console.log('User already authenticated:', response);
        this.token = response.token || '';
        if (response.role?.toLowerCase() !== 'admin') {
          console.log('Access denied - redirecting to dashboard');
          this.router.navigate(['/dashboard']);
        } else {
          this.loadUsers();
          this.loadPosts();
            this.loadReports();
        }
      },
      (error) => {
        console.log('User not authenticated:', error.error);
          
      }
    );
  }



  loadUsers() {
    this.http.get<any[]>('http://localhost:8080/admin/users', { headers: { 'Authorization': `Bearer ${this.token}` } }).subscribe({
      next: (res) => (this.users = res || []),
      error: (err) => {
        console.error('Load users failed:', err);
        this.errorMessage = err.error?.message || 'Failed to load users';
      },
    });
  }

  loadReports() {
    this.http.get<any>('http://localhost:8080/admin/reports', { headers: { 'Authorization': `Bearer ${this.token}` } }).subscribe(
     (res) => {
      console.log(res);

       this.reports = res.message || [];
     },
     (err) => {
        console.error('Load reports failed:', err);
        this.errorMessage = err.error?.message || 'Failed to load reports';
      },
    );
  }

  deleteUser(id: number) {
    if (!confirm('Are you sure you want to delete this user?')) return;
    this.http.delete(`http://localhost:8080/admin/users/${id}`, { headers: { 'Authorization': `Bearer ${this.token}` } }).subscribe({
      next: () => {
        this.users = this.users.filter(u => u.id !== id);
      },
      error: (err) => {
        console.error('Delete user failed:', err);
        this.errorMessage = err.error?.message || 'Failed to delete user';
      }
    });
  }

  deletePost(id: number) {
    if (!confirm('Are you sure you want to delete this post?')) return;
    this.http.delete(`http://localhost:8080/admin/posts/${id}`, { headers: { 'Authorization': `Bearer ${this.token}` } }).subscribe({
      next: () => {
        this.posts = this.posts.filter(p => p.id !== id);
      },
      error: (err) => {
        console.error('Delete post failed:', err);
        this.errorMessage = err.error?.message || 'Failed to delete post';
      }
    });
  }

  resolveReport(id: number) {
    this.http.post(`http://localhost:8080/admin/reports/${id}/resolve`, {}, { headers: { 'Authorization': `Bearer ${this.token}` } }).subscribe({
      next: (res: any) => {
        this.reports = res.message;
        this.reports = this.reports.filter(r => r.id !== id);
      },
      error: (err) => {
        console.error('Resolve report failed:', err);
        this.errorMessage = err.error?.message || 'Failed to resolve report';
      }
    });
  }

  loadPosts() {
    this.http.get<any[]>('http://localhost:8080/admin/posts', { headers: { 'Authorization': `Bearer ${this.token}` } }).subscribe({
      next: (res) => (this.posts = res || []),
      error: (err) => {
        console.error('Load posts failed:', err);
        this.errorMessage = err.error?.message || 'Failed to load posts';
      },
    });
  }

}
