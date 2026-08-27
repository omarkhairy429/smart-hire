import { Routes } from '@angular/router';
import { HomeComponent } from './features/public/home/home';
import { JobListComponent } from './features/public/job-list/job-list';
import { JobDetailComponent } from './features/public/job-detail/job-detail';
import { LoginComponent } from './features/auth/login/login';
import { RegisterComponent } from './features/auth/register/register';
import { AdminDashboardComponent } from './features/admin/admin-dashboard/admin-dashboard';
import { HrPostingsComponent } from './features/hr/hr-postings/hr-postings.component';
import { HrApplicationsComponent } from './features/hr/hr-applications/hr-applications.component';
import { ApplyComponent } from './features/candidate/apply/apply.component';
import { MyApplicationsComponent } from './features/candidate/my-applications/my-applications.component';
import { adminGuard } from './core/guards/admin-guard';
import { hrGuard } from './core/guards/hr-guard';
import { authGuard } from './core/guards/auth-guard';
import { PipelineComponent } from './features/applications/pipeline/pipeline';

export const routes: Routes = [
  { path: '', component: HomeComponent },

  // Public job browsing (anyone)
  { path: 'jobs', component: JobListComponent },
  { path: 'jobs/:id', component: JobDetailComponent },

  // Auth
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },

  // Super Admin only
  { path: 'admin', component: AdminDashboardComponent, canActivate: [adminGuard] },

  // HR Manager / Super Admin
  { path: 'hr/postings', component: HrPostingsComponent, canActivate: [hrGuard] },
  { path: 'hr/applications', component: HrApplicationsComponent, canActivate: [hrGuard] },

  // Candidate (must be logged in)
  { path: 'apply/:id', component: ApplyComponent, canActivate: [authGuard] },
  { path: 'my-applications', component: MyApplicationsComponent, canActivate: [authGuard] },

  // Fallback
  { path: '**', redirectTo: '' },

  // Pipeline
  { path: 'postings/:postingId/pipeline', component: PipelineComponent }
];
