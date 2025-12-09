import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

// MATERIAL IMPORTS (standalone)
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatDividerModule } from '@angular/material/divider';

// Your notification component
import { Notification } from '../notification/notification';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,

    // Material Modules
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatFormFieldModule,
    MatInputModule,
    MatProgressSpinnerModule,
    MatTooltipModule,
    MatDividerModule,

    Notification
  // your bell popup
  ],
  templateUrl: './profile.html',
  styleUrl: './profile.css'
})
export class Profile implements OnInit {
  userProfile: any = {
    username: '',
    email: '',
    age: null,
    bio: '',
    userRole:''
  };

  editProfile: any = {};
  userPosts: any[] = [];
  editingPostId: string | null = null;
  editPostData: any = {};
  loading = false;
  isEditing = false;
  errorMessage = '';
   dbUser : any;

  constructor(private http: HttpClient, private router: Router) {}
    middleware() {
    const apiMiddleware = 'http://localhost:8080/middleware';
    this.http.get(apiMiddleware, { withCredentials: true }).subscribe(
      (response: any) => {
        console.log(response);
        this.dbUser = response.username;
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
getAvatarUrl(seed: string): string {
  return `https://api.dicebear.com/7.x/avataaars/svg?seed=${seed}`;
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
        this.userProfile.userRole = response.role
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

    const apiPosts = 'http://localhost:8080/get-my-posts';
    this.http.get(apiPosts, { withCredentials: true }).subscribe(
      (response: any) => {
        console.log('User posts loaded:', response);
        this.userPosts = response;
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
        content: post.text
        
      };
    }
  }
savePost(postId: string) {
  const formData = new FormData();
  formData.append('title', this.editPostData.title || '');
  formData.append('content', this.editPostData.content || '');

  // Send ALL new files (even 10 at once!)
  if (this.editPostData.newFiles?.length > 0) {
    this.editPostData.newFiles.forEach((file: File) => {
      formData.append('file', file); // same name → becomes array in Spring
 

    });
  }

  // Send IDs to remove
  this.editPostData.filesToRemove?.forEach((id: number) => {
    formData.append('removeMediaIds', id.toString());
  });

  this.http.put(`http://localhost:8080/update-post/${postId}`, formData, {
    withCredentials: true
  }).subscribe({
    next: (res: any) => {
      // Update local post
      const idx = this.userPosts.findIndex(p => p.id === postId);
      if (idx !== -1) {
        this.userPosts[idx].title = this.editPostData.title;
        this.userPosts[idx].content = this.editPostData.content;
        // If backend returns updated media lists
        this.userPosts[idx].mediaPaths = res.mediaPaths || this.userPosts[idx].mediaPaths;
        this.userPosts[idx].mediaTypes = res.mediaTypes || this.userPosts[idx].mediaTypes;
        this.userPosts[idx].mediaIds = res.mediaIds || this.userPosts[idx].mediaIds;
      }
      this.editingPostId = null;
      this.editPostData = { title: '', content: '', currentMedia: [], newFiles: [], filesToRemove: [] };
      this.showNotification('Post updated!', 'success');
    },
    error: (err) => this.showNotification('Update failed: ' + (err.error?.message || 'Error'))
  });
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
// Replace your current methods with these:


// Remove single file from current media
removeExistingMedia(index: number) {
  const fileId = this.editPostData.currentMedia[index].id;
  this.editPostData.filesToRemove.push(fileId);
  this.editPostData.currentMedia.splice(index, 1);
}

// Optional: clear all new files
clearNewFiles() {
  this.editPostData.newFiles = [];
}



previewUrl: string | null = null;
previewType: string | null = null;

openMediaPreview(path: string, type: string) {
  this.previewUrl = 'http://localhost:8080' + path;
  this.previewType = type;
}

closePreview() {
  this.previewUrl = null;
  this.previewType = null;
}

// In startEditPost():
startEditPost(post: any) {
  this.editingPostId = post.id;
  this.editPostData = {
    title: post.title || '',
    content: post.content || '',
    currentMedia: (post.mediaPaths || []).map((path: string, i: number) => ({
      id: post.mediaIds[i],
      path,
      type: post.mediaTypes[i]
    })),
    newFiles: [] as File[],
    newFilePreviews: [] as { url: string; type: string; name: string }[],
    filesToRemove: [] as number[]
  };
}

// Real-time preview
onFilesSelected(event: any) {
  const files = event.target.files;
  if (!files?.length) return;

  this.editPostData.newFilePreviews = [];
  this.editPostData.newFiles = Array.from(files);

  Array.from(files).forEach((file: any) => {
    const reader = new FileReader();
    reader.onload = (e: any) => {
      this.editPostData.newFilePreviews.push({
        url: e.target.result,
        type: file.type,
        name: file.name
      });
    };
    reader.readAsDataURL(file);
  });
}

removeNewFile(index: number) {
  this.editPostData.newFiles.splice(index, 1);
  this.editPostData.newFilePreviews.splice(index, 1);
}
}
