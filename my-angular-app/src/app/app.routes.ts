import { Routes } from '@angular/router';
import { Login } from './login/login';
import { Register } from './register/register';
import { Dashboard } from './dashboard/dashboard';
import { Profile } from './profile/profile';
import { Admin } from './admin/admin';
import { User } from './user/user';
import { Notification } from './notification/notification';

export const routes: Routes = [
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: 'login', component: Login },
  { path: 'register', component: Register },
  { path: 'dashboard', component: Dashboard },
  { path: 'profile', component: Profile },
  { path: 'admin', component: Admin },
  { path: 'users/:username', component: User },
  { path: 'notifications', component: Notification },
  { path: '**', redirectTo: '/login' }
];
