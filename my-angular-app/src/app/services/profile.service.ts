// src/app/services/profile.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class ProfileService {
  private API = 'http://localhost:8080';

  constructor(private http: HttpClient) {}

  /* =========================
     AUTH / PROFILE
  ========================= */
  middleware() {
    return this.http.get(`${this.API}/middleware`, {
      withCredentials: true
    });
  }

  updateProfile(data: any) {
    return this.http.put(`${this.API}/profile/editmyinfo`, data, {
      withCredentials: true
    });
  }

  /* =========================
     POSTS
  ========================= */
  getMyPosts() {
    return this.http.get(`${this.API}/get-my-posts`, {
      withCredentials: true
    });
  }

  updatePost(postId: string, formData: FormData) {
    return this.http.put(`${this.API}/update-post/${postId}`, formData, {
      withCredentials: true
    });
  }

  deletePost(postId: string) {
    return this.http.delete(`${this.API}/delete-post/${postId}`, {
      withCredentials: true
    });
  }

  /* =========================
     FOLLOWERS
  ========================= */
  getFollowersAndFollowing(username: string) {
    return this.http.get(
      `${this.API}/followers/follow/count/${username}`,
      { withCredentials: true }
    );
  }
}
