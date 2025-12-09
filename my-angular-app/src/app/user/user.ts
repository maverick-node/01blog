import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatDividerModule } from '@angular/material/divider';

@Component({
  selector: 'app-user',
  imports: [CommonModule, HttpClientModule, FormsModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatFormFieldModule,
    MatInputModule,
    MatProgressSpinnerModule,
    MatTooltipModule,
    MatDividerModule,
  ],
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
  constructor(private route: ActivatedRoute, private http: HttpClient, private router: Router) { }

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
    const api = `http://localhost:8080/profile/user/${username}`;
    this.http.get(api, { withCredentials: true }).subscribe(
      (response: any) => {
        console.log(response);

        this.profile.username = response.username;
        this.profile.email = response.email;
        this.profile.bio = response.bio;
        this.profile.age = response.age;
        this.profile.id = response.id;
        this.loadposts(username);
        this.loadFollowersandFollowing(username)
        this.checkIfFollowing();
      },
      (error) => {
        console.error('Error loading profile:', error.error);
        this.showNotification(error.error?.message || 'Loading profile failed');
      }
    );
  }
  loadposts(username: string) {
    const api = `http://localhost:8080/get-posts/${username}`;
    this.http.get(api, { withCredentials: true }).subscribe(
      (response: any) => {
        console.log(response);
        this.posts = response || [];
      },
      (error) => {
        console.error('Error loading profile:', error.error);
        this.showNotification(error.error?.message || 'Loading profile failed');
      }
    );

  }
  checkIfFollowing(): boolean {
    const apiMiddleware = 'http://localhost:8080/middleware';
    this.http.get(apiMiddleware, { withCredentials: true }).subscribe((response: any) => {
      const currentUserId = response.username;
      const api = `http://localhost:8080/followers/get-follow/${this.profile.username}`;
      this.http.get(api, { withCredentials: true }).subscribe(
        (subscriptions: any) => {
          console.log('suub', subscriptions);

          this.isFollowing = subscriptions.isFollowing;
        },
        (error) => {
          console.error('Error checking subscriptions:', error);
          return false;
        }
      );
    });
    return this.isFollowing;
  }

  follow() {
    console.log('token', this.token);
    if (!this.profile.username) {
      this.showNotification('Profile not loaded');
      return;
    }

    var checkfollow = this.checkIfFollowing();
    console.log(checkfollow);

    if (checkfollow == true) {
      this.http.delete(`http://localhost:8080/followers/unfollow/${this.profile.username}`, {
        withCredentials: true,
      })
        .subscribe(
          (res: any) => {
            console.log('Unfollow response:', res);
            this.isFollowing = !this.isFollowing;
            this.showNotification(res.message || (this.isFollowing ? 'Followed' : 'Unfollowed'));
          },
          (err) => {
            console.error('Unfollow error:', err.error);
            this.showNotification(err.error?.message || 'Unfollow failed');
          }
        );
      return;
    }

    const api = `http://localhost:8080/followers/follow/${this.profile.username}`;
    this.http.post(api, {}, { withCredentials: true }).subscribe(
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

    const confirmReport = window.prompt('Please enter report reason (optional):', '');
    if (confirmReport === null) return; // user cancelled

    const api = `http://localhost:8080/reports/create/${this.profile.username}`;
    const payload = { reason: confirmReport };
    this.http.post(api, payload, { withCredentials: true }).subscribe(
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
  goBack() {
    window.location.href = '/dashboard';
  }
  getAvatarUrl(seed: string): string {
    return `https://api.dicebear.com/7.x/avataaars/svg?seed=${seed}`;
  }

  loadFollowersandFollowing(username: string) {

    const api = `http://localhost:8080/followers/follow/count/${username}`;
    this.http.get(api, { withCredentials: true }).subscribe(
      (res: any) => {
        console.log(res);
        
        this.profile.followers = res.followers;
        this.profile.following = res.following;

      },
      (error) => {
        console.error('Error checking subscriptions:', error);
        return false;
      }
    );
  };

}
