import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { adminGuard } from './core/guards/admin.guard';

import { LoginComponent } from './features/auth/pages/login/login.component';
import { RegisterComponent } from './features/auth/pages/register/register.component';

import { MainLayoutComponent } from './layout/main-layout/main-layout.component';

import { DashboardHomeComponent } from './features/dashboard/pages/dashboard-home/dashboard-home.component';
import { TaskListComponent } from './features/tasks/pages/task-list/task-list.component';
import { TaskCreateComponent } from './features/tasks/pages/task-create/task-create.component';
import { TaskDetailComponent } from './features/tasks/pages/task-detail/task-detail.component';
import { TaskEditComponent } from './features/tasks/pages/task-edit/task-edit.component';
import { AnalyticsHomeComponent } from './features/analytics/pages/analytics-home/analytics-home.component';
import { AiInsightsHomeComponent } from './features/ai-insights/pages/ai-insights-home/ai-insights-home.component';
import { NotificationsHomeComponent } from './features/notifications/pages/notifications-home/notifications-home.component';
import { AdminHomeComponent } from './features/admin/pages/admin-home/admin-home.component';
import { UsersManagementComponent } from './features/admin/pages/users-management/users-management.component';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },

  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },

  {
    path: '',
    component: MainLayoutComponent,
    canActivate: [authGuard],
    children: [
      { path: 'dashboard', component: DashboardHomeComponent },
      { path: 'tasks', component: TaskListComponent },
      { path: 'tasks/new', component: TaskCreateComponent },
      { path: 'tasks/:id', component: TaskDetailComponent },
      { path: 'tasks/:id/edit', component: TaskEditComponent },
      { path: 'analytics', component: AnalyticsHomeComponent },
      { path: 'ai-insights', component: AiInsightsHomeComponent },
      { path: 'notifications', component: NotificationsHomeComponent },

      { path: 'admin', component: AdminHomeComponent, canActivate: [adminGuard] },
      { path: 'admin/users', component: UsersManagementComponent, canActivate: [adminGuard] }
    ]
  },

  { path: '**', redirectTo: 'login' }
];