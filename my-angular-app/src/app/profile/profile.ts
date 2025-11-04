import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

@Component({
  selector: 'app-profile',
  imports: [CommonModule, HttpClientModule, FormsModule],
  templateUrl: './profile.html',
  styleUrl: './profile.css',
})
export class Profile implements OnInit {
  userProfile: any = {
    username: '',
    mail: '',
    age: null,
    bio: ''
  };
  editProfile: any = {};
  userPosts: any[] = [];
  editingPostId: string | null = null;
  editPostData: any = {};
  loading = false;
  isEditing = false;
  errorMessage = '';

  constructor(private http: HttpClient, private router: Router) {}
    middleware() {
    const apiMiddleware = 'http://localhost:8080/middleware';
    this.http.get(apiMiddleware, { withCredentials: true }).subscribe(
      (response: any) => {
        console.log(response);
        
      },
      (error) => {
        console.log(error.error);
        this.showNotification(error.error.error);
        window.location.href = '/login';
   
      }
    );
  }

  async ngOnInit() {
    
    this.middleware();
    this.loadProfile();

  }

  loadProfile() {
    this.loading = true;
    const apiMiddleware = 'http://localhost:8080/middleware';
    
    this.http.get(apiMiddleware, { withCredentials: true }).subscribe(
      (response: any) => {
        console.log('Profile loaded:', response);
        this.userProfile.username = response.username || 'User';
        // For now, set default values since we don't have a full profile API
        this.userProfile.mail = response.mail || 'user@example.com';
        this.userProfile.age = response.age || null;
        this.userProfile.bio = response.bio || 'No bio available';
        this.loading = false;
        
        this.loadUserPosts();
      },
      (error) => {
        console.error('Error loading profile:', error);
        this.showNotification('Failed to load profile');
        this.loading = false;
        if (error.status === 401) {
          this.router.navigate(['/login']);
        }
      }
    );
  }

   loadUserPosts() {
    if (!this.userProfile.username) {
      console.log('Username not available yet, skipping post loading');
      return;
    }

    const apiPosts = 'http://localhost:8080/posts-by-user';
    this.http.get(apiPosts, { withCredentials: true }).subscribe(
      (response: any) => {
        console.log('User posts loaded:', response);
        console.log('Filtering by username:', this.userProfile.username);
        
        // Filter posts by the current user
        this.userPosts = response.filter((post: any) => {
          console.log('Post author:', post.author, 'Current user:', this.userProfile.username);
          return post.author === this.userProfile.username;
        });
        
        console.log('Filtered user posts:', this.userPosts);
      },
      (error) => {
        console.error('Error loading posts:', error);
        this.showNotification('Failed to load posts');
      }
    );
  }

  toggleEdit() {
    this.isEditing = true;
    this.editProfile = { ...this.userProfile };
  }

  saveProfile() {
    // For now, just simulate saving since we don't have update API
    this.userProfile = { ...this.editProfile };
    this.isEditing = false;
    this.showNotification('Profile updated successfully!', 'success');
  }

  cancelEdit() {
    this.isEditing = false;
    this.editProfile = {};
  }

  viewPost(postId: string) {
    console.log('Viewing post:', postId);
  }

  editPost(postId: string) {
    const post = this.userPosts.find(p => p.id === postId);
    if (post) {
      this.editingPostId = postId;
      this.editPostData = {
        title: post.title,
        text: post.text
      };
    }
  }

  savePost(postId: string) {
    const apiUpdatePost = `http://localhost:8080/update-post/${postId}`;
    
    this.http.put(apiUpdatePost, this.editPostData, { withCredentials: true }).subscribe(
      (response: any) => {
        console.log('Post updated successfully:', response);

        const postIndex = this.userPosts.findIndex(p => p.id === postId);
        if (postIndex !== -1) {
          this.userPosts[postIndex].title = this.editPostData.title;
          this.userPosts[postIndex].text = this.editPostData.text;
        }
        this.editingPostId = null;
        this.editPostData = {};
        this.showNotification('Post updated successfully!', 'success');
      },
      (error) => {
        console.error('Error updating post:', error);
        this.showNotification('Failed to update post');
      }
    );
  }

  cancelPostEdit() {
    this.editingPostId = null;
    this.editPostData = {};
  }

  deletePost(postId: string) {
    if (confirm('Are you sure you want to delete this post?')) {
      console.log('Deleting post:', postId);
      const apiDeletePost = `http://localhost:8080/delete-post/${postId}`;
      this.http.delete(apiDeletePost, { withCredentials: true }).subscribe(
        (response: any) => {
          console.log('Post deleted successfully:', response);
          this.userPosts = this.userPosts.filter(p => p.id !== postId);
          this.showNotification('Post deleted successfully!', 'success');
        },
        (error) => {
          console.error('Error deleting post:', error);
          this.showNotification('Failed to delete post');
        }
      );
    }
  }

  goBack() {
    window.location.href = '/dashboard';
  }

  goToDashboard() {
    window.location.href = '/dashboard';
  }

  showNotification(message: string, type: string = 'error') {
    this.errorMessage = message;
    setTimeout(() => {
      this.errorMessage = '';
    }, 5000);
  }
}
