import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatDividerModule } from '@angular/material/divider';
import { ProfileService } from '../services/profile.service';

@Component({
  selector: 'app-user',
  standalone: true,
  imports: [
    CommonModule,
    HttpClientModule,
    FormsModule,
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
  profile: any = { username: '', bio: '', age: null, id: null, followers: 0, following: 0 };
  posts: any[] = [];
  errorMessage = '';
  isFollowing = false;

  // Report profile variables
  showReportProfileBox = false;
  reportProfileReason = '';

  constructor(
    private route: ActivatedRoute,
    private http: HttpClient,
    private router: Router,
    private profileService: ProfileService
  ) {}

  ngOnInit() {
    this.middleware();

    this.route.paramMap.subscribe((params) => {
      this.usernameParam = params.get('username')?.toLowerCase()+"";

      if (this.usernameParam) {
        this.loadProfile(this.usernameParam);
      }
    });
  }

  middleware() {
    const apiMiddleware = 'http://localhost:8080/middleware';
    this.http.get(apiMiddleware, { withCredentials: true }).subscribe(
      () => {},
      (error) => {
        if (error.status === 401 || error.status === 403) {
          this.router.navigate(['/login']);
        }
      }
    );
  }

  loadProfile(username: string) {
    const api = `http://localhost:8080/profile/user/${username}`;
    this.http.get(api, { withCredentials: true }).subscribe(
      (response: any) => {
        this.profile = {
          username: response.username,
          email: response.email,
          bio: response.bio,
          age: response.age,
          id: response.id,
        };
        this.loadFollowersandFollowing(username);
        this.loadposts(username);
        this.checkIfFollowing();
      },
      (error) => {
        this.router.navigate(['/404']);
        this.showNotification(error.error?.message || 'Profile not found');
      }
    );
  }

  loadposts(username: string) {
    const api = `http://localhost:8080/get-posts/${username}`;
    this.http.get(api, { withCredentials: true }).subscribe(
      (response: any) => {
        this.posts = response || [];
      },
      (error) => {
        this.showNotification(error.error?.message || 'Failed to load posts');
      }
    );
  }

  checkIfFollowing() {
    const api = `http://localhost:8080/followers/get-follow/${this.profile.username}`;
    this.http.get(api, { withCredentials: true }).subscribe((res: any) => {
      this.isFollowing = res.isFollowing === true;
    });
  }

  follow() {
    if (this.isFollowing) {
      this.http
        .delete(`http://localhost:8080/followers/unfollow/${this.profile.username}`, {
          withCredentials: true,
        })
        .subscribe({
          next: () => {
            this.isFollowing = false;
            this.profile.followers--;
            this.showNotification('Unfollowed');
          },
          error: (error) => {
            // Show backend error message if available
            const msg =
              error.error?.message ||
              error.error?.error ||
              'Something went wrong while unfollowing';
            this.showNotification(msg);
          },
        });
    } else {
      this.http
        .post(
          `http://localhost:8080/followers/follow/${this.profile.username}`,
          {},
          {
            withCredentials: true,
          }
        )
        .subscribe({
          next: () => {
            this.isFollowing = true;
            this.profile.followers++;
            this.showNotification('Now following');
          },
          error: (error) => {
            // Show backend error message if available
            const msg =
              error.error?.message || error.error?.error || 'Something went wrong while following';
            this.showNotification(msg);
          },
        });
    }
  }

  // REPORT PROFILE – BEAUTIFUL GLOBAL BOX
  reportProfile() {
    this.reportProfileReason = '';
    this.showReportProfileBox = true;
  }

  sendProfileReport() {
    const payload = { reason: this.reportProfileReason };

    this.http
      .post(`http://localhost:8080/reports/create/${this.profile.username}`, payload, {
        withCredentials: true,
      })
      .subscribe({
        next: () => {
          this.showNotification('Profile reported');
          this.showReportProfileBox = false;
        },
        error: (error) =>
          this.showNotification(error.error?.message || error.error?.error || 'Report failed'),
      });
  }

  goToPost(postId: number) {
    this.router.navigate(['/post', postId]);
  }

  showNotification(message: string) {
    this.errorMessage = message;
    setTimeout(() => (this.errorMessage = ''), 4000);
  }

  goBack() {
    this.router.navigate(['/dashboard']);
  }

  getAvatarUrl(seed: string): string {
    return `https://api.dicebear.com/7.x/avataaars/svg?seed=${seed}`;
  }
  loadFollowersandFollowing(username: string) {
    this.profileService.getFollowersAndFollowing(username).subscribe((res: any) => {
      this.profile.followers = res.followers || 0;
      this.profile.following = res.following || 0;
    });
  }
  // Add these properties to your User component
  selectedPost: any = null;
  previewUrl: string | null = null;
  previewType: string | null = null;

  // Add this method
  viewPost(post: any) {
    this.selectedPost = post;
  }

  // Add these methods for full media preview
  openMediaPreview(url: string, type: string) {
    this.previewUrl = url;
    this.previewType = type;
  }

  closePreview() {
    this.previewUrl = null;
    this.previewType = null;
  }
}
