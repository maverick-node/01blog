import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { Notification } from '../notification/notification';

@Component({
  selector: 'app-dashboard',
  imports: [CommonModule, HttpClientModule, FormsModule, Notification],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css',
})
export class Dashboard {
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
      (error) => {
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
    const apiPosts = 'http://localhost:8080/get-posts';
    this.http.get(apiPosts, { withCredentials: true }).subscribe(
      (response: any) => {
        this.posts = response;
        this.posts.forEach((p: any) => {
          this.likedPosts[p.id] = this.likedPosts[p.id] || false;
          this.likeCounts[p.id] = this.likeCounts[p.id] || 0;
          this.loadLikeCount(p.id);
        });
      },
      (error) => {
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
   this.http.post(apiLike, {},   { withCredentials: true })
    .subscribe(
      (response: any) => {
        const msg = response?.message || JSON.stringify(response);
        this.showNotification(msg);

        if (/unliked/i.test(msg)) {
          this.likedPosts[postId] = false;
        } else if (/liked/i.test(msg)) {
          this.likedPosts[postId] = true;
        } else {
          this.likedPosts[postId] = !this.likedPosts[postId];
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

    var objecte ={
      reportedPostId: postId,
      reason: reason
    }
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
        console.log(response);
        
        this.allusernames = response.users.map((u: any) => u.username);
      },
      (error) => {
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
    const apiCreatePost = 'http://localhost:8080/create-post';

    // Only JSON — no media
    this.http.post(apiCreatePost, this.newPost, { withCredentials: true }).subscribe(
      () => {
        this.loadPosts();
      },
      (error) => {
        this.showNotification(error.error?.message || 'Post creation failed!');
      }
    );
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
      (error) => {
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
      (error) => {
        this.showNotification(error.error?.message || 'Loading comments failed!');
      }
    );
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

  
}
