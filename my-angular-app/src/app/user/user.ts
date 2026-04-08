import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ProfileService } from '../services/profile.service';
import { environment } from '../config/environment';

@Component({
  selector: 'app-user',
  standalone: true,
  imports: [
    CommonModule,
    HttpClientModule,
    FormsModule,
    RouterModule,
  ],
  templateUrl: './user.html',
  styleUrls: ['./user.css'],
})
export class User {
  private readonly TOTAL_CHARACTERS = 826;
  myusermame= ""; 
  environment = environment;
  usernameParam: string | null = null;
  profile: any = { username: '', bio: '', age: null, id: null, followers: 0, following: 0 };
  posts: any[] = [];
  errorMessage = '';
  isFollowing = false;

  // Report profile variables
  showReportProfileBox = false;
  reportProfileReason = '';

  unreadCount = 0;
  sidebarOpen = false;
  showNotificationPopup = false;
  notifications: any[] = [];

  constructor(
    private route: ActivatedRoute,
    private http: HttpClient,
    private router: Router,
    private profileService: ProfileService
  ) {}

  ngOnInit() {
    this.middleware();
this.loadNotifications()
    this.route.paramMap.subscribe((params) => {
      this.usernameParam = params.get('username')?.toLowerCase()+"";

      if (this.usernameParam) {
        this.loadProfile(this.usernameParam);
      }
    });
  }

  middleware() {
    const apiMiddleware = `${environment.apiUrl}/middleware`;
    this.http.get(apiMiddleware, { withCredentials: true }).subscribe(
      (response: any) => {
        this.myusermame = response.username 
      },
      (error) => {
        if (error.status === 401 || error.status === 403) {
          this.router.navigate(['/login']);
        }
      }
    );
  }

  loadProfile(username: string) {
    const api = `${environment.apiUrl}/profile/user/${username}`;
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
    const api = `${environment.apiUrl}/get-posts/${username}`;
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
    const api = `${environment.apiUrl}/followers/get-follow/${this.profile.username}`;
    this.http.get(api, { withCredentials: true }).subscribe((res: any) => {
      this.isFollowing = res.isFollowing === true;
    });
  }

  follow() {
    if (this.isFollowing) {
      this.http
        .delete(`${environment.apiUrl}/followers/unfollow/${this.profile.username}`, {
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
          `${environment.apiUrl}/followers/follow/${this.profile.username}`,
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
      .post(`${environment.apiUrl}/reports/create/${this.profile.username}`, payload, {
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

  toggleNotifications() {
    this.showNotificationPopup = !this.showNotificationPopup;
    if (this.showNotificationPopup) {
      this.loadNotifications();
    }
  }

  toggleSidebar() {
    this.sidebarOpen = !this.sidebarOpen;
  }

  getNotificationIcon(message: string): string {
    const msg = message.toLowerCase();
    if (msg.includes('follow') || msg.includes('followed')) {
      return 'person_add';
    } else if (msg.includes('comment') || msg.includes('commented')) {
      return 'forum';
    } else if (msg.includes('post') || msg.includes('posted')) {
      return 'article';
    } else if (msg.includes('like') || msg.includes('liked')) {
      return 'favorite';
    }
    return 'mail';
  }

  loadNotifications() {
    this.profileService.getNotifications().subscribe(
      (response: any) => {
        this.notifications = response.sort(
          (a: any, b: any) => new Date(b.timestamp).getTime() - new Date(a.timestamp).getTime()
        );
        this.unreadCount = this.notifications.filter((n) => !n.read).length;
      },
      (error: any) => {
        this.showNotification(error.error?.message || 'Error loading notifications');
      }
    );
  }

  markRead(id: number) {
    this.profileService.markNotificationRead(id).subscribe({
      next: (res) => {
        const notif = this.notifications.find((n) => n.id === id);
        if (notif) {
          notif.read = !notif.read;
        }
        this.unreadCount = this.notifications.filter((n) => !n.read).length;
      },
      error: (err: any) => this.showNotification(err.error?.message || err.error?.error || 'Failed to mark notification as read'),
    });
  }

  getAvatarUrl(userIdentifier: string): string {
    let index;

    // Hash string to number using character codes
    let hash = 0;
    for (let i = 0; i < userIdentifier.length; i++) {
      hash = (hash + userIdentifier.charCodeAt(i)) % this.TOTAL_CHARACTERS;
    }
    index = hash + 1; // API IDs start at 1

    // Return deterministic Rick and Morty character avatar
    return `https://rickandmortyapi.com/api/character/avatar/${index}.jpeg`;
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
