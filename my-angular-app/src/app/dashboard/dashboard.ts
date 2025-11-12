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
    text: ''
  };
  likedPosts: { [key: number]: boolean } = {};
  likeCounts: { [key: number]: number } = {};
  selectedFile?: File | null = null;
  comments: { [key: string]: any[] } = {};
  newComment: { [key: string]: string } = {};
  posts: any[] = [];
  errorMessage = '';

  constructor(private http: HttpClient, private router: Router) {}

  middleware() {

    const apiMiddleware = 'http://localhost:8080/middleware';
    this.http.get(apiMiddleware, { withCredentials: true }).subscribe(
      (response: any) => {
        console.log('Authentication successful:', response);
        this.username = response.username;
        this.userRole = (response.role || 'user').toLowerCase();
        console.log('User role:', this.userRole);
        this.loadPosts();
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
    this.getToken();
    this.getUsernames();
  }

  getToken() {
    const apiMiddleware = 'http://localhost:8080/middleware';
    this.http.get(apiMiddleware, { withCredentials: true }).subscribe(
      (response: any) => {
        this.token = response.token;
      },
      (error) => {
        console.error('Error getting token:', error);
        // keep silent; some actions will redirect to login if needed
      }
    );
  }
  loadPosts() {
  
    

    const apiPosts = 'http://localhost:8080/get-posts';
    this.http.get(apiPosts, { withCredentials: true }).subscribe(
      (response: any) => {
        console.log('Posts loaded:', response);
        this.posts = response;
        // initialize liked state map (unknown until user likes/unlikes)
        this.posts.forEach((p: any) => {
          this.likedPosts[p.id] = this.likedPosts[p.id] || false;
          this.likeCounts[p.id] = this.likeCounts[p.id] || 0;
          // fetch current like count for each post
          this.loadLikeCount(p.id);
        });
 
      },
      (error: { error: { message: any; }; }) => {
        console.log('Error loading posts:', error.error.message);
        this.showNotification(error.error.message);
  
      }
    );
  }

  likePost(postId: number) {
    if (!this.token) {
      // try to retrieve token then retry
      this.getToken();
      setTimeout(() => this.likePost(postId), 400);
      return;
    }

    const apiLike = `http://localhost:8080/likes/${postId}`;
    this.http.get(apiLike, { headers: { 'Authorization': `Bearer ${this.token}` } }).subscribe(
      (response: any) => {
        const msg = typeof response === 'string' ? response : (response.message || JSON.stringify(response));
        this.showNotification(msg);
    
        if (/unliked/i.test(msg)) {
          this.likedPosts[postId] = false;
        } else if (/liked/i.test(msg)) {
          this.likedPosts[postId] = true;
        } else {
          // fallback toggle
          this.likedPosts[postId] = !this.likedPosts[postId];
        }
        // refresh the like count for this post
        this.loadLikeCount(postId);
      },
      (error) => {
        console.error('Like error:', error);
        this.showNotification(error?.error?.message || 'Like failed');
      }
    );
  }

  loadLikeCount(postId: number) {
    const apiCount = `http://localhost:8080/likes/count/${postId}`;
    // count endpoint is public; use withCredentials for consistency but no auth required
    this.http.get(apiCount, { withCredentials: true }).subscribe(
      (response: any) => {
        // expect { likeCount: number }
        const count = response?.likeCount ?? (response?.count ?? 0);
        this.likeCounts[postId] = +count;
      },
      (error) => {
        console.error('Error loading like count for', postId, error);
        // default to 0 on error
        this.likeCounts[postId] = this.likeCounts[postId] || 0;
      }
    );
  }

  showNotification(message: any) {
    this.errorMessage = message;
    // Clear the error message after 5 seconds
    setTimeout(() => {
      this.errorMessage = '';
    }, 5000);
  }
  getUsernames(){
    const apiUsernames = 'http://localhost:8080/all-users';
    this.http.get(apiUsernames, { withCredentials: true }).subscribe(
      (response: any) => {
        this.allusernames = response.users;
        console.log('Usernames loaded:', response);
      },
      (error) => {
        console.error('Error loading usernames:', error.error);
        this.showNotification(error.error?.message || 'Loading usernames failed!');
      }
    );
  }

  goToUser(username: string) {
    if (username === this.username) {
      this.router.navigate(['/profile']);
      return;
    }
    if (!username) return;
    // Navigate to the user's public page: /users/:username
    this.router.navigate(['/users', username]);
  }
  createPost() {
    const apiCreatePost = 'http://localhost:8080/create-post';

    // validate file client-side (if present)
    if (this.selectedFile) {
      const maxBytes = 2 * 1024 * 1024; // 2MB
      if (this.selectedFile.size > maxBytes) {
        this.showNotification('Selected file is too large. Max 2MB.');
        return;
      }
      if (!this.selectedFile.type.startsWith('image/') && !this.selectedFile.type.startsWith('video/')) {
        this.showNotification('Invalid file type. Only images and videos are allowed.');
        return;
      }
    }

    const form = new FormData();
    form.append('post', new Blob([JSON.stringify(this.newPost)], { type: 'application/json' }));
    if (this.selectedFile) {
      form.append('file', this.selectedFile, this.selectedFile.name);
    }

    this.http.post(apiCreatePost, form, { withCredentials: true }).subscribe(
      (response: any) => {
        console.log('Post created successfully:', response);
        this.newPost = { title: '', text: '' };
        this.selectedFile = null;
        this.loadPosts();
      },
      (error) => {
        console.error('Error creating post:', error.error || error);
        this.showNotification(error.error?.message || 'Post creation failed!');
      }
    );
  }

  onFileSelected(event: any) {
    const f: File = event?.target?.files?.[0];
    if (!f) {
      this.selectedFile = null;
      return;
    }
    this.selectedFile = f;
  }

  addComment(postId: string) {
    const commentText = this.newComment[postId];
    if (!commentText || !commentText.trim()) {
      return;
    }

    const apiComment = `http://localhost:8080/create-comment`;
    const commentPayload = { 
      postId: parseInt(postId), 
      comment: commentText.trim() 
    };

    this.http.post(apiComment, commentPayload, { withCredentials: true }).subscribe(
      (response: any) => {
        console.log('Comment added successfully:', response);
        // Clear the comment input
        this.newComment[postId] = '';
        // Reload comments for this specific post instead of all posts
        this.getComments(postId);
      },
      (error) => {
        console.error('Error adding comment:', error.error);
        this.showNotification(error.error?.message || 'Adding comment failed!');
      }
    );
  }


  getComments(postId: string) {
    const apiGetComments = `http://localhost:8080/posts/${postId}/comments`;
    this.http.get(apiGetComments, { withCredentials: true }).subscribe(
      (response: any) => {
        console.log('Comments loaded successfully:', response);
        this.comments[postId] = response;
      },
      (error) => {
        console.error('Error loading comments:', error.error);
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
      (response: any) => {
        console.log('Logout success:', response);
        // Clear local state and redirect to login
        this.username = '';
        this.token = '';
        this.router.navigate(['/login']);
      },
      (error) => {
        console.error('Logout error:', error);
        // Even if logout fails on server, clear local state and redirect
        this.showNotification('Logged out (server error, but cleared locally)');
        this.username = '';
        this.token = '';
 
      }
    );
  }
}
