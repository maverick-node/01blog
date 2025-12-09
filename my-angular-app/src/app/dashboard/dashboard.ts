import { Component, ElementRef, HostListener, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { FormsModule } from '@angular/forms';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { Router, RouterModule } from '@angular/router';
import { MatDividerModule } from '@angular/material/divider';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatListModule } from '@angular/material/list';
import { MatBadgeModule } from '@angular/material/badge';
import { MatTooltipModule } from '@angular/material/tooltip';

@Component({
  selector: 'app-dashboard',
  imports: [
    CommonModule,
    FormsModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatInputModule,
    HttpClientModule,
    RouterModule,
    CommonModule,
    MatDividerModule,
    MatSidenavModule,
    MatListModule,
    MatBadgeModule,
    MatTooltipModule
  ],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css',
})
export class Dashboard {
  selectedMediaName: any = '';
  selectedMedia: File | null = null;
  showPopup = false;
  unreadCount = 0;
  notifications: any[] = [];
  allusernames: string[] = [];
  username = 'User';
  userRole = '';
  token: string = '';
  newPost = {
    title: '',
    content: '',
  };
  likedPosts: { [key: number]: boolean } = {};
  likeCounts: { [key: number]: number } = {};
  comments: { [key: string]: any[] } = {};
  newComment: { [key: string]: string } = {};
  posts: any[] = [];
  errorMessage = '';
previewType:any;
  constructor(private http: HttpClient, private router: Router) {}

  ngOnInit() {
    this.middleware();
    
    this.getToken();
    this.getUsernames();
  }

  middleware(): boolean {
    const apiMiddleware = 'http://localhost:8080/middleware';
    this.http.get(apiMiddleware, { withCredentials: true }).subscribe(
      (response: any) => {
        this.username = response.username;
        this.userRole = (response.role || 'user').toLowerCase();
        console.log(this.userRole);
        this.loadPosts();
        return true;
      },
      (error: any) => {
        if (error.status === 401 || error.status === 403) {
          this.router.navigate(['/login']);
        } else {
          this.showNotification('Authentication failed');
          this.router.navigate(['/login']);
        }
        return false;
      }
    );
    return true;
  }

  getToken() {
    const apiMiddleware = 'http://localhost:8080/middleware';
    this.http.get(apiMiddleware, { withCredentials: true }).subscribe((response: any) => {
      this.token = response.token;
    });
  }

  loadPosts() {
    const apiPosts = 'http://localhost:8080/get-followed-posts';
    this.http.get(apiPosts, { withCredentials: true }).subscribe(
      (response: any) => {
        console.log(response);

        this.posts = response;
        this.posts.forEach((p: any) => {
          this.likedPosts[p.id] = this.likedPosts[p.id] || false;
          this.likeCounts[p.id] = this.likeCounts[p.id] || 0;

          this.loadLikeCount(p.id);
        });
      },
      (error: any) => {
        this.showNotification(error.error.message);
      }
    );
  }

  likePost(postId: number) {
    var check = this.middleware();
    console.log(check);

    if (check == false) {
      this.router.navigate(['/login']);
      return;
    }

    const apiLike = `http://localhost:8080/like-post/${postId}`;
    this.http.post(apiLike, {}, { withCredentials: true }).subscribe(
      (response: any) => {
        const msg = response?.message || JSON.stringify(response);
        this.showNotification(msg);

        if (response.message === 'Liked') {
          this.likedPosts[postId] = true;
        } else {
          this.likedPosts[postId] = false;
        }

        this.loadLikeCount(postId);
      },
      () => {
        this.showNotification('Like failed');
      }
    );
  }

  loadLikeCount(postId: number) {
    const apiCount = `http://localhost:8080/likes/count/${postId}`;
    this.http.get(apiCount, { withCredentials: true }).subscribe(
      (response: any) => {
        console.log(response, postId);

        this.likeCounts[postId] = response?.likeCount ?? 0;
      },
      () => {
        this.likeCounts[postId] = 0;
      }
    );
  }

  reportPost(postId: number) {
    //window create prompt to ask for reason
    const reason = window.prompt('Please enter report reason:', '');
    if (reason === null) return; // user cancelled

    var objecte = {
      reportedPostId: postId,
      reason: reason,
    };
    const apiReport = 'http://localhost:8080/reports/report-post';
    this.http.post(apiReport, objecte, { withCredentials: true }).subscribe(
      (response: any) => {
        const msg = response?.message || 'Post reported';
        this.showNotification(msg);
      },
      (error: any) => {
        this.showNotification(error?.error?.message || 'Reporting failed');
      }
    );
  }
  @ViewChild('usersScroll') usersScroll!: ElementRef;

scrollUsersLeft() {
  this.usersScroll.nativeElement.scrollBy({ left: -200, behavior: 'smooth' });
}

scrollUsersRight() {
  this.usersScroll.nativeElement.scrollBy({ left: 200, behavior: 'smooth' });
}



isMobile = window.innerWidth < 1100;

@HostListener('window:resize')
onResize() {
  this.isMobile = window.innerWidth < 1100;
}
getAvatarUrl(seed: string): string {
  return `https://api.dicebear.com/7.x/avataaars/svg?seed=${seed}`;
}
  showNotification(message: any) {
    this.errorMessage = message;
    setTimeout(() => {
      this.errorMessage = '';
    }, 5000);
  }

  getUsernames() {
    const apiUsernames = 'http://localhost:8080/get-users';
    this.http.get(apiUsernames, { withCredentials: true }).subscribe(
      (response: any) => {
        console.log("sss",response.users);
        //exclude my username from the list
        this.allusernames = response.users
          .map((u: any) => u.username)
          .filter((username: string) => username !== this.username);
      },
      (error: any) => {
        this.showNotification(error.error?.message || 'Loading usernames failed!');
      }
    );
  }

  goToUser(username: string) {
    if (username === this.username) {
      this.router.navigate(['/profile']);
      return;
    }
    this.router.navigate(['/users', username]);
  }
createPost() {
  const formData = new FormData();

  // Send title & content as JSON string under "post"
  formData.append(
    'post',
    JSON.stringify({
      title: this.newPost.title,
      content: this.newPost.content,
    })
  );

  // Send ALL selected images (multiple!)
  if (this.selectedFiles && this.selectedFiles.length > 0) {
    this.selectedFiles.forEach((file: File) => {
      formData.append('media', file);  // same name → becomes array in Spring
    });
  }

  const apiCreatePost = 'http://localhost:8080/create-post';

  this.http.post(apiCreatePost, formData, { withCredentials: true }).subscribe({
    next: () => {
      this.showNotification('Post created with images!');
      this.newPost = { title: '', content: '' };
      this.selectedFiles = [];
      this.loadPosts();
    },
    error: (error: any) => {
      this.showNotification(error.error?.message || 'Failed to create post');
    }
  });
}

  addComment(postId: string) {
    const commentText = this.newComment[postId];
    if (!commentText?.trim()) return;

    const apiComment = `http://localhost:8080/create-comment`;
    const commentPayload = {
      postId: parseInt(postId),
      content: commentText.trim(),
    };
    console.log('commentPayload', commentPayload);
    this.http.post(apiComment, commentPayload, { withCredentials: true }).subscribe(
      () => {
        this.newComment[postId] = '';
        this.getComments(postId);
      },
      (error: any) => {
        this.showNotification(error.error?.message || 'Adding comment failed!');
      }
    );
  }

  getComments(postId: string) {
    const apiGetComments = `http://localhost:8080/posts/${postId}/comments`;
    this.http.get(apiGetComments, { withCredentials: true }).subscribe(
      (response: any) => {
        console.log(response);
        this.comments[postId] = response.comments;
      },
      (error: any) => {
        this.showNotification(error.error?.message || 'Loading comments failed!');
      }
    );
  }
showComments: { [key: string]: boolean } = {};

toggleComments(postId: string | number) {
  const id = String(postId); // ← THIS IS THE KEY FIX

  // Toggle visibility
  this.showComments[id] = !this.showComments[id];

  // Load comments only when opening AND not already loaded
  if (this.showComments[id] && (!this.comments[id] || this.comments[id].length === 0)) {
    this.getComments(id);
  }
}

  viewProfile() {
    this.router.navigate(['/profile']);
  }

  goToAdminPanel() {
    this.router.navigate(['/admin']);
  }

  logout() {
    const apiLogout = 'http://localhost:8080/logout';
    this.http.post(apiLogout, {}, { withCredentials: true }).subscribe(
      () => {
        this.username = '';
        this.token = '';
        this.router.navigate(['/login']);
      },
      () => {
        this.username = '';
        this.token = '';
      }
    );
  }
  toggleNotifications() {
    this.showPopup = !this.showPopup;
    if (this.showPopup) {
      this.loadNotifications();
    }
  }

  loadNotifications() {
    console.log('2');

    const api = 'http://localhost:8080/notifications/get';
    this.http
      .get(api, {
        withCredentials: true,
      })
      .subscribe(
        (response: any) => {
          console.log('sadfas', response);

          this.notifications = response;
          this.unreadCount = this.notifications.filter((n) => !n.read).length;
        },
        (error: any) => {
          console.error('Error loading notifications:', error);
        }
      );
  }
  markRead(id: number) {
    this.http
      .post(`http://localhost:8080/notifications/mark-as-read/${id}`, {}, { withCredentials: true })
      .subscribe({
        next: (res) => {
          console.log(res);

          const notif = this.notifications.find((n) => n.id === id);
          if (notif) notif.read = true;

          this.unreadCount = this.notifications.filter((n) => !n.read).length;
        },
        error: (err: any) => console.error('Failed to mark as read', err),
      });
  }

  onFileSelected(event: any) {
    const file = event.target.files[0];
    if (file) {
      this.selectedMedia = file;
      this.selectedMediaName = file.name;
    }
  }
  selectedFiles: File[] = [];


previewUrl: string | null = null;

openMediaPreview(path: string, type: string) {
  this.previewUrl = 'http://localhost:8080' + path;
  this.previewType = type; // 'image/jpeg' or 'video/mp4'
}

closePreview() {
  this.previewUrl = null;
}


onFilesSelected(event: any) {
  const files = event.target.files;
  if (files.length > 0) {
    this.selectedFiles = Array.from(files);
  }
}
}
