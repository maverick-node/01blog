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
import { ProfileService } from '../services/profile.service';

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
    MatTooltipModule,
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
  // Edit state
  editingPostId: string | null = null;
  editPostData: any = {
    title: '',
    content: '',
    currentMedia: [],
    newFiles: [] as File[],
    newFilePreviews: [] as { url: string; type: string }[],
    filesToRemove: [] as number[],
  };

  // Delete confirmation
  showDeleteConfirm = false;
  postToDelete: number | null = null;
  showReportBox = false;
  currentPostId: number | null = null;

  reportingPostId: number | null = null;
  reportReason: string = '';
  likedPosts: { [key: number]: boolean } = {};
  likeCounts: { [key: number]: number } = {};
  comments: { [key: string]: any[] } = {};
  newComment: { [key: string]: string } = {};
  posts: any[] = [];
  errorMessage = '';
  previewType: any;
  postid: any[] = [];
  constructor(
    private http: HttpClient,
    private router: Router,
    private profileService: ProfileService
  ) { }

  ngOnInit() {
    this.middleware();
    this.loadNotifications();
    this.getToken();
    this.getUsernames();
  }

  middleware(): boolean {
    const apiMiddleware = 'http://localhost:8080/middleware';
    this.http.get(apiMiddleware, { withCredentials: true }).subscribe(
      (response: any) => {
        this.username = response.username;
        this.userRole = (response.role || 'user').toLowerCase();
        this.loadPosts();
        this.loadLikesPosts();
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

        this.posts = response.sort(
          (a: any, b: any) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime()
        );
        this.posts.forEach((p: any) => {
          this.likeCounts[p.id] = this.likeCounts[p.id] || 0;
          this.postid.push(p.id);

          this.loadLikeCount(p.id);
        });
        this.getallcomments();
      },

      (error: any) => {
        this.showNotification(error.error.message);
      }
    );
  }

  likePost(postId: number) {
    var check = this.middleware();

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
      (error: any) => {
        this.showNotification(error.error?.error || error.error.message || 'Like failed');
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
      var check = this.middleware();

    if (check == false) {
      this.router.navigate(['/login']);
      return;
    }
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
        this.showNotification(error.error?.message || error.error?.error || 'Reporting failed');
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
        //exclude my username from the list
        this.allusernames = response.users
          .map((u: any) => u.username)
          .filter((username: string) => username !== this.username);
      },
      (error: any) => {
        this.showNotification(error.error?.error || error.error?.message || 'Loading usernames failed!');
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
      var check = this.middleware();

    if (check == false) {
      this.router.navigate(['/login']);
      return;
    }
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
        formData.append('media', file); // same name → becomes array in Spring
      });
    }

    const apiCreatePost = 'http://localhost:8080/create-post';

    this.http.post(apiCreatePost, formData, { withCredentials: true }).subscribe({
      next: () => {
        this.showNotification('Post created !');
        this.newPost = { title: '', content: '' };
        this.selectedFiles = [];
        this.selectedFilePreviews = [];
        this.loadPosts();
      },
      error: (error: any) => {
       
        
        this.showNotification(error.error?.message || error.error?.error || 'Failed to create post');
      },
    });
  }

  addComment(postId: string) {
      var check = this.middleware();

    if (check == false) {
      this.router.navigate(['/login']);
      return;
    }
    const commentText = this.newComment[postId];
    if (!commentText?.trim()) return;

    const apiComment = `http://localhost:8080/create-comment`;
    const commentPayload = {
      postId: parseInt(postId),
      content: commentText.trim(),
    };
    this.http.post(apiComment, commentPayload, { withCredentials: true }).subscribe(
      () => {
        this.newComment[postId] = '';
        this.getComments(postId);
      },
      (error: any) => {
        this.showNotification(error.error?.message || error.error?.error || 'Adding comment failed!');
      }
    );
  }

  getComments(postId: string) {
    const apiGetComments = `http://localhost:8080/posts/${postId}/comments`;
    this.http.get(apiGetComments, { withCredentials: true }).subscribe(
      (response: any) => {
        this.comments[postId] = response.comments;
        
        
      },
      (error: any) => {
        this.showNotification(error.error?.error || error.error?.message || 'Loading comments failed!');
      }
    );
  }
  showComments: { [key: string]: boolean } = {};

  toggleComments(postId: string | number) {
    const id = String(postId); // ← THIS IS THE KEY FIX

    // Toggle visibility
    this.showComments[id] = !this.showComments[id];
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

    const api = 'http://localhost:8080/notifications/get';
    this.http
      .get(api, {
        withCredentials: true,
      })
      .subscribe(
        (response: any) => {

          this.notifications = response.sort(
            (a: any, b: any) => new Date(b.timestamp).getTime() - new Date(a.timestamp).getTime()
          );

          this.unreadCount = this.notifications.filter((n) => !n.read).length;
        },
        (error: any) => {
          console.error('Error loading notifications:', error);
        }
      );
  }
  markRead(id: number) {
      var check = this.middleware();

    if (check == false) {
      this.router.navigate(['/login']);
      return;
    }
    this.http
      .post(`http://localhost:8080/notifications/mark-as-read/${id}`, {}, { withCredentials: true })
      .subscribe({
        next: (res) => {
          const notif = this.notifications.find((n) => n.id === id);

          if (notif) {
            notif.read = !notif.read; // toggle the read status
          }
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

  getallcomments() {
    for (let index = 0; index < this.postid.length; index++) {
      this.getComments(this.postid[index]);
    }
  }

  loadLikesPosts() {
    const api = 'http://localhost:8080/get-all-my-liked-posts';
    this.http.get(api, { withCredentials: true }).subscribe({
      next: (res: any) => {
        res.likedPosts.forEach((post: any) => {
          this.likedPosts[post] = true;
        });
      },
      error: (err: any) => {
      },
    });
  }
  openReportBox(postId: number) {
    this.currentPostId = postId;
    this.reportReason = '';
    this.showReportBox = true;
  }

  sendReport() {
    if (!this.currentPostId) return;

    const payload = {
      reportedPostId: this.currentPostId,
      reason: this.reportReason,
    };

    this.http
      .post('http://localhost:8080/reports/report-post', payload, {
        withCredentials: true,
      })
      .subscribe({
        next: () => {
          this.showNotification('Post reported successfully');
          this.showReportBox = false;
          this.currentPostId = null;
          this.reportReason = '';
        },
        error: (err) => {
          this.showNotification(err.error?.error || err.error?.message || 'Failed to report post');
        },
      });
  }
  startEditPost(post: any) {
    this.editingPostId = post.id;
    this.editPostData = {
      title: post.title || '',
      content: post.content || '',
      currentMedia: (post.mediaPaths || []).map((path: string, i: number) => ({
        id: post.mediaIds?.[i], // assuming you have mediaIds in post object
        path,
        type: post.mediaTypes?.[i],
      })),
      newFiles: [],
      newFilePreviews: [],
      filesToRemove: [],
    };
  }

  onEditFilesSelected(event: any) {
    const files = event.target.files;
    if (!files.length) return;

    this.editPostData.newFiles = Array.from(files);
    this.editPostData.newFilePreviews = [];

    Array.from(files).forEach((file: any) => {
      const reader = new FileReader();
      reader.onload = (e: any) => {
        this.editPostData.newFilePreviews.push({
          url: e.target.result,
          type: file.type,
        });
      };
      reader.readAsDataURL(file);
    });
  }

  removeExistingMedia(index: number) {
    const mediaId = this.editPostData.currentMedia[index].id;
    if (mediaId) this.editPostData.filesToRemove.push(mediaId);
    this.editPostData.currentMedia.splice(index, 1);
  }

  removeNewFile(index: number) {
    this.editPostData.newFiles.splice(index, 1);
    this.editPostData.newFilePreviews.splice(index, 1);
  }

  cancelPostEdit() {
    this.editingPostId = null;
    this.editPostData = { currentMedia: [], newFiles: [], newFilePreviews: [], filesToRemove: [] };
  }

  savePostEdit(postId: string) {
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
        this.loadPosts();
        this.showNotification('Post updated!');
      },
      error: (err) => this.showNotification('Update failed: ' + (err.error?.message || err.error?.error || 'Error')),
    });
  }

  openDeleteConfirm(postId: number) {
    this.postToDelete = postId;
    this.showDeleteConfirm = true;
  }

  confirmDeletePost() {
    if (!this.postToDelete) return;

    this.profileService.deletePost(this.postToDelete + '').subscribe({
      next: () => {
        this.posts = this.posts.filter((p) => p.id !== this.postToDelete);
        this.showNotification('Post deleted successfully!');
        this.showDeleteConfirm = false;
        this.loadPosts()
      },
      error: (err) => this.showNotification(err.error?.error || err.error?.message || 'Failed to delete post'),
    });
  }
  selectedFilePreviews: { url: string; type: string }[] = [];
  @ViewChild('fileInput') fileInput!: ElementRef<HTMLInputElement>;

  onFilesSelected(event: any) {
    const files: FileList = event.target.files;

    // Always clear previous data
    this.selectedFiles = [];
    this.selectedFilePreviews = [];

    if (files.length === 0) {
      // Reset the input so same file can be selected again later
      this.fileInput.nativeElement.value = '';
      return;
    }

    for (let i = 0; i < files.length; i++) {
      const file = files[i];
      this.selectedFiles.push(file);

      const reader = new FileReader();
      reader.onload = (e: any) => {
        this.selectedFilePreviews.push({
          url: e.target.result,
          type: file.type,
        });
      };
      reader.readAsDataURL(file);
    }

    // Critical: Reset the input value so same files can be re-selected later
    this.fileInput.nativeElement.value = '';
  }

  removeSelectedFile(index: number) {
    this.selectedFiles.splice(index, 1);
    this.selectedFilePreviews.splice(index, 1);

    // Also reset the file input to allow re-uploading the same file
    if (this.fileInput) {
      this.fileInput.nativeElement.value = '';
    }
  }
deleteComment(commentid: string, commentpost: string) {
  const id = Number(commentid);
  const post = Number(commentpost)
  this.http
    .delete(`http://localhost:8080/delete-comment/${id}`, {withCredentials: true})
    .subscribe({
      next: () => {
       this.showNotification("Comment Deleted")
       this.getComments(commentpost)
      },
      error: (err) => {
         this.showNotification(err.error?.error ||err.error?.message ||"Error Comment Deleted")
      }
    });
}

}
