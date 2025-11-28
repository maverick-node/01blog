import { Component, OnInit, OnDestroy, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { Renderer2 } from '@angular/core';

@Component({
  selector: 'app-notification',
  standalone: true,
  imports: [CommonModule, HttpClientModule],
  templateUrl: './notification.html',
  styleUrl: './notification.css'
})
export class Notification implements OnInit {
  showPopup = false;
  notifications: any[] = [];
  unreadCount = 0;
  loading = false;
  token: string = '';

  constructor(private http: HttpClient, private el: ElementRef, private renderer: Renderer2) {}

  ngOnInit() {
    this.getToken();
    this.loadNotifications();
    // Move host element to document.body so fixed-position popup won't be clipped
    // by transformed/stacking-context parents (ensures overlay over whole app).
   
  
    setInterval(() => {
      if (!this.showPopup) {
        this.loadNotifications();
      }
    }, 30000);
  }

  ngOnDestroy() {
    // Clean up: remove host from body when component is destroyed
    try {
      if (typeof document !== 'undefined' && this.el && this.el.nativeElement) {
        if (this.el.nativeElement.parentNode === document.body) {
          this.renderer.removeChild(document.body, this.el.nativeElement);
        }
      }
    } catch (e) {
      // ignore
    }
  }

  getToken() {
    const apiMiddleware = 'http://localhost:8080/middleware';
    this.http.get(apiMiddleware, { withCredentials: true }).subscribe(
      (response: any) => {
        this.token = response.token;
      },
      (error) => {
        console.error('Error getting token:', error);
        window.location.href = '/login';
      }
    );
  }

  togglePopup() {
    this.markAsRead();
    
    this.showPopup = !this.showPopup;
    if (this.showPopup) {
      this.loadNotifications();
    }
  }

  loadNotifications() {
    if (!this.token) {
      setTimeout(() => this.loadNotifications(), 500);
      return;
    }

    this.loading = true;
    const api = 'http://localhost:8080/notifications';
    this.http.get(api, { 
      headers: { 'Authorization': `Bearer ${this.token}` }
    }).subscribe(
      (response: any) => {
        this.notifications = response;
        this.unreadCount = this.notifications.filter(n => !n.read).length;
        this.loading = false;
      },
      (error) => {
        console.error('Error loading notifications:', error);
        this.loading = false;
      }
    );
  }

  markAsRead() {
    console.log("markAsRead called (frontend)");
    // ensure we have a token before calling the backend
    if (!this.token) {
      this.getToken();
      // retry shortly after token fetch
      setTimeout(() => this.markAsRead(), 400);
      return;
    }

    const api = `http://localhost:8080/notifications/read`;
    // backend exposes GET /notifications/read to mark all as read
    this.http.get(api, { headers: { 'Authorization': `Bearer ${this.token}` } }).subscribe(
      (response: any) => {

        this.notifications = this.notifications.map(n => ({ ...n, read: true }));
        this.unreadCount = 0;
    
      },
      (error) => {
        console.error('Error marking as read:', error);
      }
    );
  }

  getIcon(type: string): string {
    const icons: any = {
      'NEW_POST': '📝',
      'COMMENT': '💬',
      'LIKE': '❤️',
      'FOLLOW': '👤',
      'MENTION': '@'
    };
    return icons[type] || '🔔';
  }

  getIconClass(type: string): string {
    return `icon-${type.toLowerCase()}`;
  }

  formatTime(timestamp: string): string {
    const date = new Date(timestamp);
    const now = new Date();
    const diffMs = now.getTime() - date.getTime();
    const diffMins = Math.floor(diffMs / 60000);
    const diffHours = Math.floor(diffMs / 3600000);
    const diffDays = Math.floor(diffMs / 86400000);

    if (diffMins < 1) return 'Just now';
    if (diffMins < 60) return `${diffMins}m ago`;
    if (diffHours < 24) return `${diffHours}h ago`;
    if (diffDays < 7) return `${diffDays}d ago`;
    return date.toLocaleDateString();
  }
}
