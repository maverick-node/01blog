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
import { environment } from '../config/environment';

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
  environment = environment;
  selectedMediaName: any = '';
  selectedMedia: File | null = null;
  showPopup = false;
  sidebarOpen = false;
  unreadCount = 0;
  notifications: any[] = [];
  allusernames: string[] = [];
  username = 'User';
  userRole = '';
  token: string = '';
  suggestedUsers: any[] = [];
  mediaFiles: File[] = [];
  mediaFilePreviews: { url: string; type: string }[] = [];
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
  
  // Edit comment
  editingCommentId: string | null = null;
  editCommentText = '';
  editCommentPostId: number | null = null;
  
  // Delete comment
  showDeleteCommentConfirm = false;
  commentToDelete: string | null = null;
  postIdForComment: number | null = null;

  reportingPost: any = null;
  reportReason: string = '';
  isSubmittingReport = false;
  reportMessage = '';
  reportMessageType: 'success' | 'error' = 'success';
  likedPosts: { [key: number]: boolean } = {};
  likeCounts: { [key: number]: number } = {};
  comments: { [key: string]: any[] } = {};
  newComment: { [key: string]: string } = {};
  posts: any[] = [];
  errorMessage = '';
  previewType: any;
  postid: any[] = [];
  searchUsers: string = '';
  
  // Blog Statistics
  blogStats = {
    usersCount: 0,
    postsCount: 0,
    commentsCount: 0
  };

  promotions = [
    {
      id: 1,
      title: 'KALAXIAN CRYSTALS',
      subtitle: 'LIMITED STOCK',
      description: 'Elevate your consciousness to the 5th dimension for only 200 Biemflarcks.',
      badge: 'LIMITED STOCK',
      color: 'from-[#ff6b9d]/30 to-[#a64d79]/30'
    },
    {
      id: 2,
      title: 'FLEEB JUICE',
      subtitle: 'PREMIUM EXTRACT',
      description: 'The secret ingredient for your interdimensional projects.',
      badge: 'PREMIUM',
      color: 'from-[#A2D45E]/20 to-[#7faf3d]/20'
    },
    {
      id: 3,
      title: 'PORTAL GEL',
      subtitle: 'EXCLUSIVE OFFER',
      description: 'Travel across dimensions with ease. Limited quantities available!',
      badge: 'EXCLUSIVE',
      color: 'from-[#00e3fd]/20 to-[#0099cc]/20'
    }
  ];

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
    this.loadSuggestedUsers();
    this.loadBlogStats();
  }

  middleware(): boolean {
    const apiMiddleware = `${environment.apiUrl}/middleware`;
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
             
          this.showNotification(error.error?.message || error.error?.error || 'Authentication failed');
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
    const apiMiddleware = `${environment.apiUrl}/middleware`;
    this.http.get(apiMiddleware, { withCredentials: true }).subscribe((response: any) => {
      this.token = response.token;
    });
  }

  loadPosts() {
   
    const apiPosts = `${environment.apiUrl}/get-followed-posts`;
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

    const apiLike = `${environment.apiUrl}/like-post/${postId}`;
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
        
        this.showNotification(error.error.message || error.error?.error || 'Like failed');
      }
    );
  }

  loadLikeCount(postId: number) {
    const apiCount = `${environment.apiUrl}/likes/count/${postId}`;
    this.http.get(apiCount, { withCredentials: true }).subscribe(
      (response: any) => {
        
        this.likeCounts[postId] = response?.likeCount ?? 0;
      },
      () => {
        this.likeCounts[postId] = 0;
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

  // Rick and Morty character avatar generation (826 total characters)
  private readonly TOTAL_CHARACTERS = 826;

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

  get filteredUsers(): string[] {
    if (!this.searchUsers.trim()) {
      return this.allusernames;
    }
    return this.allusernames.filter(user =>
      user.toLowerCase().includes(this.searchUsers.toLowerCase())
    );
  }

  showNotification(message: any) {
    this.errorMessage = message;
    setTimeout(() => {
      this.errorMessage = '';
    }, 5000);
  }

  getUsernames() {
    const apiUsernames = `${environment.apiUrl}/get-users`;
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

    const apiCreatePost = `${environment.apiUrl}/create-post`;

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

    const apiComment = `${environment.apiUrl}/create-comment`;
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
    const apiGetComments = `${environment.apiUrl}/posts/${postId}/comments`;
    this.http.get(apiGetComments, { withCredentials: true }).subscribe(
      (response: any) => {
        this.comments[postId] = response.comments.sort(
          (a: any, b: any) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime()
        );
        
        
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
    this.middleware();
    const apiLogout = `${environment.apiUrl}/logout`;
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

    const api = `${environment.apiUrl}/notifications/get`;
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
        
          
      this.showNotification(error.error.message|| "Error Showing Notification")
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
      .post(`${environment.apiUrl}/notifications/mark-as-read/${id}`, {}, { withCredentials: true })
      .subscribe({
        next: (res) => {
          const notif = this.notifications.find((n) => n.id === id);

          if (notif) {
            notif.read = !notif.read; 
          }
          this.unreadCount = this.notifications.filter((n) => !n.read).length;
        },
        error: (err: any) => this.showNotification(err.error?.message|| err.error?.error || 'Failed to mark notification as read'),
      });
  }

  loadSuggestedUsers() {
    const apiUrl = `${environment.apiUrl}/get-users`; // Fetch all users
    this.http.get(apiUrl, { withCredentials: true }).subscribe(
      (response: any) => {
        // Filter out current user and get first 5-10 suggestions
        this.suggestedUsers = (response.users || response || []).filter((u: any) => u.username !== this.username).slice(0, 5);
      },
      (error: any) => {
        console.log('Could not load suggested users:', error);
        this.suggestedUsers = [];
      }
    );
  }

  loadBlogStats() {
    // Fetch users count
    this.http.get(`${environment.apiUrl}/get-users`, { withCredentials: true }).subscribe(
      (response: any) => {
        this.blogStats.usersCount = (response.users || response || []).length;
      },
      (err: any) => console.log('Error loading users count')
    );

    // Fetch posts count and comments
    this.http.get(`${environment.apiUrl}/get-followed-posts`, { withCredentials: true }).subscribe(
      (response: any) => {
        const posts = response || [];
        this.blogStats.postsCount = posts.length;
        
        // Count total comments from all posts
        let totalComments = 0;
        posts.forEach((post: any) => {
          if (this.comments[post.id] && Array.isArray(this.comments[post.id])) {
            totalComments += this.comments[post.id].length;
          }
        });
        this.blogStats.commentsCount = totalComments;
      },
      (err: any) => console.log('Error loading posts count')
    );
  }

  onMediaSelected(event: any) {
    const files = event.target.files;
    if (!files) return;

    for (let i = 0; i < files.length; i++) {
      const file = files[i];
      
      // Validate file type
      if (!file.type.startsWith('image') && !file.type.startsWith('video')) {
        this.errorMessage = 'Only images and videos are allowed';
        setTimeout(() => this.errorMessage = '', 3000);
        continue;
      }
      
      // Validate file size (100MB max)
      if (file.size > 100 * 1024 * 1024) {
        this.errorMessage = 'File size too large (max 100MB)';
        setTimeout(() => this.errorMessage = '', 3000);
        continue;
      }

      this.mediaFiles.push(file);

      // Create preview
      const reader = new FileReader();
      reader.onload = (e: any) => {
        this.mediaFilePreviews.push({
          url: e.target.result,
          type: file.type
        });
      };
      reader.readAsDataURL(file);
    }
  }

  removeMediaFile(index: number) {
    this.mediaFiles.splice(index, 1);
    this.mediaFilePreviews.splice(index, 1);
  }

  followUser(userId: number) {
    const apiUrl = `${environment.apiUrl}/follow/${userId}`;
    this.http.post(apiUrl, {}, { withCredentials: true }).subscribe(
      (response: any) => {
        this.showNotification('User followed successfully!');
      },
      (error: any) => {
        this.showNotification(error.error?.message || 'Failed to follow user');
      }
    );
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
  selectedPostForModal: any = null; // For opening post with all media

  openMediaPreview(path: string, type: string) {
    this.previewUrl = environment.apiUrl + path;
    this.previewType = type; // 'image/jpeg' or 'video/mp4'
  }

  openPostWithMedia(post: any) {
    this.selectedPostForModal = post;
  }

  closePostModal() {
    this.selectedPostForModal = null;
  }

  reportPost(post: any) {
    this.reportingPost = post;
    this.reportReason = '';
    this.reportMessage = '';
  }

  submitReport() {
    if (!this.reportReason || !this.reportReason.trim()) {
      this.reportMessage = 'Please provide a reason for reporting';
      this.reportMessageType = 'error';
      return;
    }

    if (this.reportReason.trim().length < 10) {
      this.reportMessage = 'Reason must be at least 10 characters long';
      this.reportMessageType = 'error';
      return;
    }

    this.isSubmittingReport = true;

    const reportData = {
      reportedPostId: this.reportingPost.id,
      reason: this.reportReason.trim()
    };

    this.http.post(`${environment.apiUrl}/reports/report-post`, reportData, { withCredentials: true }).subscribe({
      next: (response: any) => {
        this.isSubmittingReport = false;
        this.reportMessage = 'Post reported successfully. Thank you for helping keep the community safe!';
        this.reportMessageType = 'success';
        setTimeout(() => {
          this.closeReportModal();
        }, 2000);
      },
      error: (err: any) => {
        this.isSubmittingReport = false;
        this.reportMessage = err.error?.message || 'Failed to report post. Please try again.';
        this.reportMessageType = 'error';
      }
    });
  }

  closeReportModal() {
    this.reportingPost = null;
    this.reportReason = '';
    this.reportMessage = '';
  }

  closePreview() {
    this.previewUrl = null;
    this.previewType = null;
  }

  getallcomments() {
    for (let index = 0; index < this.postid.length; index++) {
      this.getComments(this.postid[index]);
    }
  }

  loadLikesPosts() {
    const api = `${environment.apiUrl}/get-all-my-liked-posts`;
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
      .post(`${environment.apiUrl}/reports/report-post`, payload, {
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
      error: (err) => this.showNotification(  err.error?.message || err.error?.error || 'Failed to delete post'),
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

  
// ==================== COMMENT EDIT & DELETE ====================
  startEditComment(comment: any, postId: number) {
    this.editingCommentId = comment.commentID;
    this.editCommentText = comment.comment;
    this.editCommentPostId = postId;
  }

  cancelEditComment() {
    this.editingCommentId = null;
    this.editCommentText = '';
    this.editCommentPostId = null;
  }

  saveEditComment() {
    if (!this.editingCommentId || this.editCommentPostId === null || !this.editCommentText.trim()) {
      this.showNotification('Please enter a comment');
      return;
    }

    const payload = { content: this.editCommentText };
    this.http.put(`${environment.apiUrl}/update-comment/${this.editingCommentId}`, payload, { withCredentials: true }).subscribe({
      next: () => {
        this.showNotification('Comment updated successfully!');
        this.getComments(this.editCommentPostId!.toString());
        this.cancelEditComment();
      },
      error: (err: any) => {
        this.showNotification(err.error?.message || err.error?.error || 'Failed to update comment');
      }
    });
  }

  openDeleteCommentConfirm(commentId: string, postId: number) {
    this.commentToDelete = commentId;
    this.postIdForComment = postId;
    this.showDeleteCommentConfirm = true;
  }

  cancelDeleteComment(): void {
    this.showDeleteCommentConfirm = false;
    this.commentToDelete = null;
    this.postIdForComment = null;
  }

  confirmDeleteCommentAction(): void {
    if (!this.commentToDelete || this.postIdForComment === null) return;

    this.http.delete(`${environment.apiUrl}/delete-comment/${this.commentToDelete}`, { withCredentials: true }).subscribe({
      next: () => {
        this.showNotification('Comment deleted!');
        this.getComments(this.postIdForComment!.toString());
        this.cancelDeleteComment();
      },
      error: (err: any) => {
        this.showNotification(err.error?.message || err.error?.error || 'Failed to delete comment');
      }
    });
  }

  deleteComment(commentid: string, commentpost: string) {
    const id = Number(commentid);
    const post = Number(commentpost);
    this.http.delete(`${environment.apiUrl}/delete-comment/${id}`, { withCredentials: true }).subscribe({
      next: () => {
        this.showNotification('Comment Deleted');
        this.getComments(commentpost);
      },
      error: (err) => {
        this.showNotification(err.error?.message || err.error?.error || 'Error Comment Deleted');
      }
    });
  }

// Optional: Improve your existing deleteComment to refresh UI

}
