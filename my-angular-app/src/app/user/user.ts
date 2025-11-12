import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-user',
  imports: [CommonModule, HttpClientModule, FormsModule],
  templateUrl: './user.html',
  styleUrls: ['./user.css'],
})
export class User {
  usernameParam: string | null = null;
  profile: any = { username: '', bio: '', age: null, id: null };
  posts: any[] = [];
  errorMessage = '';
  isFollowing = false;
  reporting = false;
  token: any;
  dbuser: any;
  constructor(private route: ActivatedRoute, private http: HttpClient, private router: Router) {}

  middleware() {
    const apiMiddleware = 'http://localhost:8080/middleware';
    this.http.get(apiMiddleware, { withCredentials: true }).subscribe(
      (response: any) => {
        console.log('Authentication successful:', response.username, this.usernameParam);

        this.token = response.token;
        this.dbuser = response.username;
      },
      (error) => {
        console.log('Authentication error:', error);

        if (error.status === 401 || error.status === 403) {
          console.log('Unauthorized - redirecting to login');
          this.router.navigate(['/login']);
        } else {
          console.log('Authentication failed with status:', error.status);
          this.showNotification('Authentication failed - please refresh the page');
          this.router.navigate(['/login']);
        }
      }
    );
  }

  ngOnInit() {
    this.middleware();
    this.route.paramMap.subscribe((params) => {
      this.usernameParam = params.get('username');

      if (this.usernameParam) {
        this.loadProfile(this.usernameParam);
      }
    });
  }

  loadProfile(username: string) {
    const api = `http://localhost:8080/users/${username}`;
    this.http.get(api, { withCredentials: true }).subscribe(
      (response: any) => {
        this.profile.username = response.username;
        
        this.profile.bio = response.bio;
        this.profile.age = response.age;
        this.profile.id = response.id;
        this.posts = response.posts || [];
        this.checkIfFollowing();
      },
      (error) => {
        console.error('Error loading profile:', error.error);
        this.showNotification(error.error?.message || 'Loading profile failed');
      }
    );
  }

  checkIfFollowing() {
    const apiMiddleware = 'http://localhost:8080/middleware';
    this.http.get(apiMiddleware, { withCredentials: true }).subscribe((response: any) => {
      const currentUserId = response.username;
      const api = `http://localhost:8080/users/${currentUserId}/subscriptions`;
      this.http.get(api, { headers: { Authorization: `Bearer ${this.token}` } }).subscribe(
        (subscriptions: any) => {
          console.log('suub', subscriptions);

          this.isFollowing = subscriptions.some((sub: any) => sub.targetId === this.profile.id);
        },
        (error) => {
          console.error('Error checking subscriptions:', error);
        }
      );
    });
  }

  follow() {
    console.log('token', this.token);
    if (!this.profile.username) {
      this.showNotification('Profile not loaded');
      return;
    }
    const api = `http://localhost:8080/users/${this.profile.username}/subscribe`;
    this.http.post(api, {}, { headers: { Authorization: `Bearer ${this.token}` } }).subscribe(
      (res: any) => {
        console.log('Follow response:', res);
        this.isFollowing = !this.isFollowing;
        this.showNotification(res.message || (this.isFollowing ? 'Followed' : 'Unfollowed'));
      },
      (err) => {
        console.error('Follow error:', err.error);
        this.showNotification(err.error?.message || 'Follow failed');
      }
    );
  }

  reportProfile() {
    if (!this.profile.id) return;
    const confirmReport = window.prompt('Please enter report reason (optional):', '');
    if (confirmReport === null) return; // user cancelled

    const api = `http://localhost:8080/users/${this.profile.id}/report`;
    const payload = { reason: confirmReport };
    this.http.post(api, payload, { headers: { Authorization: `Bearer ${this.token}` } }).subscribe(
      (res: any) => {
        this.showNotification(res?.message || 'Report submitted');
      },
      (err) => {
        console.error('Report error:', err.error);
        this.showNotification(err.error?.message || 'Report failed');
      }
    );
  }

  goToPost(postId: number) {
    this.router.navigate(['/post', postId]);
  }

  showNotification(message: any) {
    this.errorMessage = message;
    setTimeout(() => (this.errorMessage = ''), 4000);
  }
}
