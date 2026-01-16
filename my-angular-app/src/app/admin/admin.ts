import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { MatChipsModule } from '@angular/material/chips';
import { MatTabsModule } from '@angular/material/tabs';
import { MatTooltipModule } from '@angular/material/tooltip';
import { environment } from '../config/environment';

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatCardModule,
    MatButtonModule,
    HttpClientModule,
    MatIconModule,
    MatTableModule,
    MatTabsModule,
    MatChipsModule,
    MatTooltipModule,
  ],
  templateUrl: './admin.html',
  styleUrl: './admin.css',
})
export class Admin {
  environment = environment;
  users: any[] = [];
  posts: any[] = [];
  reports: any[] = [];
  reportssolved: any[] = [];
  errorMessage = '';

  confirmDialog = {
    show: false,
    title: '',
    message: '',
    color: 'warn' as 'primary' | 'accent' | 'warn',
    icon: '',
    confirmText: '',
    action: () => {},
  };

  constructor(private http: HttpClient, public router: Router) {}

  ngOnInit() {
    this.checkAuthentication();
  }

  checkAuthentication() {
    this.http.get(`${environment.apiUrl}/middleware`, { withCredentials: true }).subscribe({
      next: (res: any) => {
        if (res.role?.toLowerCase() !== 'admin') {
          this.router.navigate(['/dashboard']);
        } else {
          this.loadUsers();
          this.loadPosts();
          this.loadReports();
          this.getSolvedReports();
        }
      },
      error: () => {},
    });
  }

  // GETTERS
  get reportedUsers() {
    return this.reports.filter((r) => !r.reportedPostId);
  }
  get reportedPosts() {
    return this.reports.filter((r) => r.reportedPostId);
  }
  get solvedReports() {
    return this.reportssolved;
  }
  get unsolvedReports() {
    return this.reports.filter((r) => !r.resolved);
  }
  get postsCount() {
    return this.posts.length;
  }
  get usersCount() {
    return this.users.length || 0;
  }
  get bannedUsersCount() {
    return this.users.filter((u) => u.banned).length;
  }

loadUsers() {
  this.http
    .get<any>(`${environment.apiUrl}/get-users`, { withCredentials: true })
    .subscribe(
      (res) => {
        this.users = res.users || [];
      },
      (err) => {
        console.error('Failed to load users', err);
        // Optionally, show a message to the user
        this.users = [];
      }
    );
}

loadPosts() {
  this.http
    .get<any[]>(`${environment.apiUrl}/get-posts`, { withCredentials: true })
    .subscribe(
      (res) => {
        this.posts = res || [];
      },
      (err) => {
        console.error('Failed to load posts', err);
        this.posts = [];
      }
    );
}

loadReports() {
  this.http
    .get<any[]>(`${environment.apiUrl}/admin/reports`, { withCredentials: true })
    .subscribe(
      (res) => {
        this.reports = res || [];
      },
      (err) => {
        console.error('Failed to load reports', err);
        this.reports = [];
      }
    );
}

getSolvedReports() {
  this.http
    .get<any[]>(`${environment.apiUrl}/admin/reports-resolved`, { withCredentials: true })
    .subscribe(
      (res) => {
        this.reportssolved = res || [];
      },
      (err) => {
        console.error('Failed to load solved reports', err);
        this.reportssolved = [];
      }
    );
}


  showNotification(message: string) {
    this.errorMessage = message;
    setTimeout(() => (this.errorMessage = ''), 4000);
  }

  // CONFIRM DIALOG SYSTEM
  confirmAction(
    title: string,
    message: string,
    action: () => void,
    color: 'primary' | 'accent' | 'warn' = 'warn',
    icon = 'check'
  ) {
    this.confirmDialog = {
      show: true,
      title,
      message,
      color,
      icon,
      confirmText: color === 'warn' ? 'Confirm' : 'Yes',
      action: () => {
        action(); // Run the actual action
        this.confirmDialog.show = false; // THIS LINE CLOSES THE DIALOG
      },
    };
  }

  // USER ACTIONS
  confirmUserAction(username: string, action: 'ban' | 'unban' | 'delete' | 'banreport') {
    const titles = {
      ban: 'Ban User',
      unban: 'Unban User',
      delete: 'Delete User',
      banreport: 'Ban User (Report)'
    };
    const messages = {
      ban: `Are you sure you want to ban @${username}?`,
      unban: `Are you sure you want to unban @${username}?`,
      delete: `Permanently delete @${username}? This cannot be undone.`,
      banreport: `Are you sure you want to ban @${username} for their reports?`
    };
    const icons = {
      ban: 'block',
      unban: 'lock_open',
      delete: 'delete',
      banreport: 'block'
    };

    this.confirmAction(
      titles[action],
      messages[action],
      () => {
        if (action === 'delete') this.deleteUser(username);
        else if (action === 'banreport') this.banUserReport(username);
        else this.banUser(username);
      },
      action === 'delete' ? 'warn' : 'warn',
      icons[action]
    );
  }

  banUser(username: string) {
    this.http
      .post(`${environment.apiUrl}/admin/ban-user/${username}`, {}, { withCredentials: true })
      .subscribe({
        next: () => {
          const user = this.users.find((u) => u.username === username);
          if (user) user.banned = !user.banned;
          this.showNotification(user?.banned ? 'User banned' : 'User unbanned');
        },
        error: (error) => this.showNotification(error.error?.message || error.error?.error ||  'Action failed'),
      });
  }

  banUserReport(username: string) {
    this.http
      .post(`${environment.apiUrl}/admin/ban-user-report/${username}`, {}, { withCredentials: true })
      .subscribe({
        next: () => {
         

          this.showNotification('User banned' );
        },
        error: (error) => this.showNotification(error.error?.message || error.error?.error ||  'Action failed'),
      });
  }
 
  deleteUser(username: string) {
    this.http
      .delete(`${environment.apiUrl}/admin/delete-user/${username}`, { withCredentials: true })
      .subscribe({
        next: () => {
          this.users = this.users.filter((u) => u.username !== username);
          this.showNotification('User deleted');
        },
        error: (error) => this.showNotification( error.error?.message  || error.error?.error || 'Delete failed'),
      });
  }

  // POST ACTIONS
  confirmDeletePost(postId: number) {
    this.confirmAction(
      'Delete Post',
      'Are you sure you want to delete this post permanently?',
      () => this.deletePost(postId),
      'warn',
      'delete'
    );
    this.loadPosts();
  }

  confirmResolveReport(reportId: number) {
    this.confirmAction(
      'Mark as Solved',
      'Mark this report as resolved?',
      () => this.resolveReport(reportId),
      'primary',
      'check_circle'
    );
  }

  confirmToggleVisibility(postId: number, isHidden: boolean, reports: string) {
    this.confirmAction(
      isHidden ? 'Unhide Post' : 'Hide Post',
      isHidden ? 'Make this post visible to everyone again?' : 'Hide this post from all users?',
      () => this.togglePostVisibility(postId, isHidden, reports),
      isHidden ? 'primary' : 'accent',
      isHidden ? 'visibility' : 'visibility_off'
    );
  }

  togglePostVisibility(postId: number, currentlyHidden: boolean, reports: string) {
    const endpoint = currentlyHidden
      ? `${environment.apiUrl}/admin/reports/unhide/${postId}`
      : `${environment.apiUrl}/admin/reports/hide/${postId}`;

    this.http
      .post(endpoint, reports, { withCredentials: true, headers: { 'Content-Type': 'text/plain' } })
      .subscribe({
        next: () => {

          const post = this.posts.find((p) => p.id === postId);
          if (post) post.hidden = !currentlyHidden;
          this.showNotification(currentlyHidden ? 'Post unhidden' : 'Post hidden');
          this.loadReports();
        },
        error: (error) => this.showNotification(error.error?.error ||  error.error?.message || 'Failed'),
      });
  }

  deletePost(id: number) {
    this.http
      .delete(`${environment.apiUrl}/admin/delete-post/${id}`, { withCredentials: true })
      .subscribe({
        next: () => {
          this.posts = this.posts.filter((p) => p.id !== id);
          this.showNotification('Post deleted');
        },
        error: (error) => this.showNotification(error.error?.error || error.error?.message || 'Delete failed'),
      });
  }

  resolveReport(id: number) {
    this.http
      .post(`${environment.apiUrl}/admin/reports/${id}/resolve`, {}, { withCredentials: true })
      .subscribe({
        next: () => {
          const r = this.reports.find((x) => x.id === id);
          if (r) r.resolved = true;
          this.showNotification('Report resolved');
        },
        error: (error) => this.showNotification(error.error?.message ||error.error?.error || 'Failed'),
      });
  }
  selectedPost: any = null;
  fullMediaUrl: string | null = null;
  fullMediaType: string | null = null;

  getAvatarUrl(seed: string): string {
    return `https://api.dicebear.com/7.x/avataaars/svg?seed=${seed}`;
  }

  viewPost(post: any) {
    this.selectedPost = post;
  }

  goToProfile(username: string) {
    window.open(`http://localhost:4200/users/${username}`, '_blank');
    // Or navigate in same tab: this.router.navigate(['/users', username]);
  }

  openFullMedia(url: string, type: string) {
    this.fullMediaUrl = url;
    this.fullMediaType = type;
  }

  closeFullMedia() {
    this.fullMediaUrl = null;
    this.fullMediaType = null;
  }
  viewReportedPost(postId: number) {

        this.selectedPost = this.posts.find((p) => p.id === postId);
    
}

}

