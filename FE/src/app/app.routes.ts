import { Routes } from '@angular/router';
import { HomeComponent } from './features/public/home/home';
import { JobListComponent } from './features/public/job-list/job-list';
import { JobDetailComponent } from './features/public/job-detail/job-detail';
import { LoginComponent } from './features/auth/login/login';
import { RegisterComponent } from './features/auth/register/register';
import { AdminDashboardComponent } from './features/admin/admin-dashboard/admin-dashboard';
import { adminGuard } from './core/guards/admin-guard';

export const routes: Routes = [
  { 
   path: 'admin', 
   component: AdminDashboardComponent,
   canActivate: [adminGuard] // PROTECTED ROUTE
  },
  { path: '', component: HomeComponent },
  { path: 'jobs', component: JobListComponent },
  { path: 'jobs/:id', component: JobDetailComponent },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: '**', redirectTo: '' },
];
