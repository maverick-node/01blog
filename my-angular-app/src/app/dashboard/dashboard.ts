import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-dashboard',
  imports: [CommonModule, HttpClientModule, FormsModule],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css',
})
export class Dashboard implements OnInit {
  username = 'User';
  newPost = {
    title: '',
    text: ''
  };
  comments: { [key: string]: any[] } = {};
  newComment: { [key: string]: string } = {};
  posts: any[] = [];
  errorMessage = '';
  constructor(private http: HttpClient) {}

  middleware() {
    const apiMiddleware = 'http://localhost:8080/middleware';
    this.http.get(apiMiddleware, { withCredentials: true }).subscribe(
      (response: any) => {
        console.log(response);
        this.username = response.username;
      },
      (error) => {
        console.log(error.error);
        this.showNotification(error.error.error);
        window.location.href = '/login';
   
      }
    );
  }


  ngOnInit() {
    this.loadPosts();
    this.middleware();
  }
  loadPosts() {
   const apiPosts = 'http://localhost:8080/get-posts';
   this.http.get(apiPosts, { withCredentials: true }).subscribe(
     (response: any) => {
      console.log(response);
       this.posts = response;
     },
     (error: { error: { message: any; }; }) => {
       console.log(error.error.message);
       this.showNotification(error.error.message);
     }
   );
  }
  showNotification(message: any) {
    this.errorMessage = message;
  }

  createPost() {
    const apiCreatePost = 'http://localhost:8080/create-post';
   
    this.http.post(apiCreatePost, this.newPost, { withCredentials: true }).subscribe(
      (response: any) => {
        console.log('Post created successfully:', response);
        this.newPost = { title: '', text: '' };
        this.loadPosts(); 
      },
      (error) => {
        console.error('Error creating post:', error.error);
        this.showNotification(error.error?.message || 'Post creation failed!');
      }
    );
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
        this.getComments(postId)
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
    window.location.href = '/profile';
  }

  logout() {
    
    const apiLogout = 'http://localhost:8080/logout';
    this.http.post(apiLogout, { withCredentials: true }).subscribe(
      (response: any) => {
        console.log('Logout success:', response);
       
      },
      (error) => {
        console.error('Logout error:', error.error);
        this.showNotification(error.error?.message || 'Logout failed!');
      }
    );
  }
}
