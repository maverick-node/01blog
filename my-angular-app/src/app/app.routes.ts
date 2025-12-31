import { Routes } from '@angular/router';
import { Login } from './login/login';
import { Register } from './register/register';
import { Dashboard } from './dashboard/dashboard';
import { Profile } from './profile/profile';
import { Admin } from './admin/admin';
import { User } from './user/user';
import { Notification } from './notification/notification';
import { AdminGuard } from './guards/admin.guard';
import { NotFoundComponent } from './not-found/not-found';
import { LoginGuard } from './guards/login.guard';
import { DashboardGuard } from './guards/dashboard.guard';

export const routes: Routes = [
  { path: '', redirectTo: '/login',pathMatch: 'full' },
  { path: 'login', component: Login, canActivate: [LoginGuard] },
  { path: 'register', component: Register, canActivate: [LoginGuard] },
  { path: 'dashboard', component: Dashboard , canActivate: [DashboardGuard]},
  { path: 'profile', component: Profile,canActivate: [DashboardGuard] },
  {
    path: 'admin', component: Admin, canActivate: [AdminGuard],
  },
  { path: 'users/:username', component: User,canActivate: [DashboardGuard] },
  { path: 'notifications', component: Notification },
 { path: '**', component: NotFoundComponent }
];
