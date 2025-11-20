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
export class Admin {
  users: any[] = [];
  posts: any[] = [];
  reports: any[] = [];
  loading = false;
  errorMessage = '';
  reportssolved: any[] = [];
  private token: string = '';

  constructor(private http: HttpClient, private router: Router) {}

  ngOnInit(): void {
    this.checkAuthentication();
  }

  // Derived lists for template (do not duplicate source of truth)
  get reportedUsers() {
    return (this.reports || []).filter((r: any) => r.reportedPostId == 0);
  }

  get reportedPosts() {
 
    
    return (this.reports || []).filter((r: any) => r.reportedPostId !== 0 && r.reportedPostId !== null);
  }

  get solvedReports() {
    return (this.reportssolved || []);
  }

  get unsolvedReports() {
    return (this.reports || []).filter((r: any) => !r.resolved);
  }

  get postsCount() {
    return (this.posts || []).length;
  }

  get usersCount() {
    return (this.users || []).length;
  }


  getsolvedreports() {
    const api = 'http://localhost:8080/admin/reports-resolved';
    this.http.get<any[]>(api, { withCredentials: true }).subscribe(
      (response) => {
        console.log(response);
        
        this.reportssolved = response || [];
      },
      (err) => {
        console.error('Load solved reports failed:', err);
        this.errorMessage = err.error?.message || 'Failed to load solved reports';
      }
    );
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
          this.getsolvedreports();
        }
      },
      (error) => {
        console.log('User not authenticated:', error.error);
      }
    );
  }

  loadUsers() {
    this.http
      .get<{ users: any[] }>('http://localhost:8080/get-users', { withCredentials: true })
      .subscribe({
        next: (res) => {
          console.log(res.users);
          this.users = res.users || [];
        },
        error: (error) => {
          this.errorMessage = error.error?.message || 'Failed to load users';
        },
      });
  }

  loadReports() {
    this.http.get<any>('http://localhost:8080/admin/reports', { withCredentials: true }).subscribe(
      (res) => {
        console.log(res);
        this.reports = res || [];
      },
      (err) => {
        console.error('Load reports failed:', err);
        this.errorMessage = err.error?.error || 'Failed to load reports';
      }
    );
  }

  deleteUser(username: string) {
    if (!confirm('Are you sure you want to delete this user?')) return;
    this.http
      .delete(`http://localhost:8080/admin/delete-user/${username}`, { withCredentials: true })
      .subscribe({
        next: () => {
          this.users = this.users.filter((u) => u.username !== username);
        },
        error: (err) => {
          console.error('Delete user failed:', err);
          this.errorMessage = err.error?.message || 'Failed to delete user';
        },
      });
  }

  deletePost(id: number) {
    if (!confirm('Are you sure you want to delete this post?')) return;
    this.http
      .delete(`http://localhost:8080/admin/delete-post/${id}`, { withCredentials: true })
      .subscribe({
        next: () => {
          this.posts = this.posts.filter((p) => p.id !== id);
        },
        error: (err) => {
          console.error('Delete post failed:', err);
          this.errorMessage = err.error?.message || 'Failed to delete post';
        },
      });
  }

  resolveReport(id: number) {
    this.http.post(`http://localhost:8080/admin/reports/${id}/resolve`, {}, { withCredentials: true }).subscribe({
      next: (res: any) => {
        // mark report resolved locally (keep in list so it appears under solved reports)
        const idx = this.reports.findIndex((r) => r.id === id);
        if (idx !== -1) {
          this.reports[idx].resolved = true;
        }
      },
      error: (err: any) => {
        console.error('Resolve report failed:', err);
        this.errorMessage = err.error?.message || 'Failed to resolve report';
      },
    });
  }

  actionOnUser(targetUsername: string) {
    console.log(targetUsername);

    if (!targetUsername) return;
    if (!confirm('Are you sure you want to take action on this user (delete)?')) return;
    this.deleteUser(targetUsername);
  }

  loadPosts() {
    this.http.get<any[]>('http://localhost:8080/get-posts', { withCredentials: true }).subscribe({
      next: (res) => (this.posts = res || []),
      error: (err) => {
        console.error('Load posts failed:', err);
        this.errorMessage = err.error?.message || 'Failed to load posts';
      },
    });
  }
}
