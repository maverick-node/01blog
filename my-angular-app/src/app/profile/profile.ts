import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

import { ProfileService } from '../services/profile.service';

// Material
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatDividerModule } from '@angular/material/divider';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [
    CommonModule,
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
  templateUrl: './profile.html',
  styleUrl: './profile.css',
})
export class Profile implements OnInit {
  /* =========================
     STATE
  ========================= */
  userProfile: any = {
    username: '',
    email: '',
    age: null,
    bio: '',
    userRole: '',
  };

  editProfile: any = {};
  userPosts: any[] = [];
  editingPostId: string | null = null;
  editPostData: any = {};

  loading = false;
  isEditing = false;
  errorMessage = '';
  dbUser: any;

  previewUrl: string | null = null;
  previewType: string | null = null;

  constructor(private profileService: ProfileService, private router: Router) {}

  /* =========================
     LIFECYCLE
  ========================= */
  ngOnInit() {
    this.checkAuthAndLoadProfile();

  }

  /* =========================
     AUTH / PROFILE
  ========================= */
  checkAuthAndLoadProfile() {
    this.profileService.middleware().subscribe({
      next: (res: any) => {
        this.dbUser = res.username;
        this.loadFollowersandFollowing(res.username);
        this.loadProfile(res);
       
      },
      error: (err) => {
        this.showNotification(err.error?.error);
        this.router.navigate(['/login']);
      },
    });
  }

  loadProfile(response: any) {
    this.loading = true;

    this.userProfile.username = response.username || 'User';
    this.userProfile.email = response.mail || 'user@example.com';
    this.userProfile.age = response.age || null;
    this.userProfile.bio = response.bio || 'No bio available';
    this.userProfile.userRole = response.role;

    this.loading = false;
    this.loadUserPosts();
  }

  saveProfile() {
    this.profileService.updateProfile(this.editProfile).subscribe({
      next: () => {
        this.userProfile = { ...this.editProfile };
        this.isEditing = false;
        this.showNotification('Profile updated successfully!', 'success');
      },
      error: (err) => {      
        this.showNotification(err.error.error ||err.error?.fields.error || err.error?.message || 'Failed to update profile')
      }
    });
  }

  toggleEdit() {
    this.isEditing = true;
    this.editProfile = { ...this.userProfile };
  }

  cancelEdit() {
    this.isEditing = false;
    this.editProfile = {};
  }

  getAvatarUrl(seed: string): string {
    return `https://api.dicebear.com/7.x/avataaars/svg?seed=${seed}`;
  }

  /* =========================
     POSTS
  ========================= */
  loadUserPosts() {
    this.profileService.getMyPosts().subscribe({
      next: (posts: any) => (this.userPosts = posts.sort((a: any, b: any) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime())),
      error: (err) => this.showNotification(err.error?.error || err.error?.message ||'Failed to load posts'),
    });
  }

  savePost(postId: string) {
    const formData = new FormData();
    formData.append('title', this.editPostData.title || '');
    formData.append('content', this.editPostData.content || '');

    this.editPostData.newFiles?.forEach((file: File) => {
      formData.append('file', file);
    });

    this.editPostData.filesToRemove?.forEach((id: number) => {
      formData.append('removeMediaIds', id.toString());
    });

    this.profileService.updatePost(postId, formData).subscribe({
      next: () => {
        this.cancelPostEdit();
        this.loadUserPosts();
        this.showNotification('Post updated!', 'success');
      },
      error: (err) => this.showNotification('Update failed: ' + (err.error?.message || err.error?.error ||  'Error')),
    });
  }

  deletePost(postId: string) {
    this.profileService.deletePost(postId).subscribe({
      next: () => {
       
        this.userPosts = this.userPosts.filter((p) => p.id !== postId);
        this.showNotification('Post deleted successfully!', 'success');
      },
      error: (err) => this.showNotification(err.error?.message ||err.error?.error ||  'Failed to delete post'),
    });
  }

  cancelPostEdit() {
    this.editingPostId = null;
    this.editPostData = {};
  }

  /* =========================
     FOLLOWERS
  ========================= */
  loadFollowersandFollowing(username: string) {
    this.profileService.getFollowersAndFollowing(username).subscribe((res: any) => {
      this.userProfile.followers = res.followers;
      this.userProfile.following = res.following;
    });
  }

  /* =========================
     UI / NAVIGATION
  ========================= */
  goBack() {
    this.router.navigate(['/dashboard']);
  }

  goToDashboard() {
    this.router.navigate(['/dashboard']);
  }

  showNotification(message: string, type: string = 'error') {
    this.errorMessage = message;
    setTimeout(() => (this.errorMessage = ''), 5000);
  }
  openMediaPreview(path: string, type: string) {
    this.previewUrl = 'http://localhost:8080' + path;
    this.previewType = type;
  }

  closePreview() {
    this.previewUrl = null;
    this.previewType = null;
  }
  startEditPost(post: any) {
    this.editingPostId = post.id;
    this.editPostData = {
      title: post.title || '',
      content: post.content || '',
      currentMedia: (post.mediaPaths || []).map((path: string, i: number) => ({
        id: post.mediaIds?.[i],
        path,
        type: post.mediaTypes?.[i],
      })),
      newFiles: [] as File[],
      newFilePreviews: [] as { url: string; type: string; name: string }[],
      filesToRemove: [] as number[],
    };
  }

  removeExistingMedia(index: number) {
    const fileId = this.editPostData.currentMedia[index].id;
    this.editPostData.filesToRemove.push(fileId);
    this.editPostData.currentMedia.splice(index, 1);
  }

  onFilesSelected(event: any) {
    const files = event.target.files;
    if (!files?.length) return;

    this.editPostData.newFiles = Array.from(files);
    this.editPostData.newFilePreviews = [];

    Array.from(files).forEach((file: any) => {
      const reader = new FileReader();
      reader.onload = (e: any) => {
        this.editPostData.newFilePreviews.push({
          url: e.target.result,
          type: file.type,
          name: file.name,
        });
      };
      reader.readAsDataURL(file);
    });
  }

  removeNewFile(index: number) {
    this.editPostData.newFiles.splice(index, 1);
    this.editPostData.newFilePreviews.splice(index, 1);
  }
  showDeleteBox = false;
currentPostId: number | null = null;

// Open the delete modal
openDeleteBox(postId: number) {
  this.currentPostId = postId;
  this.showDeleteBox = true;
}

// Confirm deletion
confirmDeletePost() {
  if (this.currentPostId !== null) {
    this.deletePost(this.currentPostId+"");
    this.showDeleteBox = false;
    this.currentPostId = null;
  }
}

}

