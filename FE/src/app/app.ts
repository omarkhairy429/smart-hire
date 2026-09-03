import {
  Component,
  ChangeDetectorRef,
  OnInit,
  OnDestroy
} from '@angular/core';

import { CommonModule } from '@angular/common';

import {
  RouterOutlet,
  RouterLink,
  NavigationEnd,
  Router
} from '@angular/router';

import { AuthService } from './core/services/auth.service';

import {
  filter,
  Subscription
} from 'rxjs';

import { Notification } from './core/models/notification.model';
import { NotificationType } from './core/models/notification-type';

import { NotificationService } from './core/services/notification.service';


@Component({
  selector: 'app-root',
  standalone: true,

  imports: [
    RouterOutlet,
    RouterLink,
    CommonModule
  ],

  templateUrl: './app.html',
  styleUrls: ['./app.css'],
})
export class AppComponent implements OnInit, OnDestroy {

  isLoggedIn = false;

  userRole: string | null = null;

  firstName: string | null = null;


  notifications: Notification[] = [];

  unreadCount = 0;

  showNotifications = false;


  private notificationPolling?: Subscription;

  private unreadCountSubscription?: Subscription;


  constructor(
    private authService: AuthService,
    private router: Router,
    private cdr: ChangeDetectorRef,
    private notificationService: NotificationService
  ) { }


  ngOnInit(): void {

    this.syncAuthState();


    // Keep authentication state synchronized
    // after navigation
    this.router.events
      .pipe(
        filter(
          event => event instanceof NavigationEnd
        )
      )
      .subscribe(() => {

        this.syncAuthState();

        if (this.isLoggedIn) {
          this.loadNotifications();
        }

        this.cdr.markForCheck();
      });


    // Subscribe to unread count
    this.unreadCountSubscription =
      this.notificationService.unreadCount$
        .subscribe(count => {

          this.unreadCount = count;

          this.cdr.markForCheck();
        });


    // Start polling if already logged in
    if (this.isLoggedIn) {

      this.startNotificationPolling();

      this.loadNotifications();
    }
  }


  ngOnDestroy(): void {

    this.notificationPolling?.unsubscribe();

    this.unreadCountSubscription?.unsubscribe();
  }


  syncAuthState(): void {

    this.isLoggedIn =
      this.authService.isLoggedIn();

    const user =
      this.authService.getCurrentUser();

    this.userRole =
      user?.role ?? null;

    this.firstName =
      user?.firstName ?? null;
  }


  startNotificationPolling(): void {

    // Prevent multiple polling subscriptions
    this.notificationPolling?.unsubscribe();

    this.notificationPolling =
      this.notificationService
        .startPolling()
        .subscribe(count => {

          this.notificationService
            .updateUnreadCount(count);
        });
  }


  loadNotifications(): void {

    if (!this.isLoggedIn) {
      return;
    }


    this.notificationService
      .getNotifications()
      .subscribe({

        next: notifications => {

          this.notifications = notifications;

          const unread =
            notifications.filter(
              notification => !notification.read
            ).length;

          this.notificationService
            .updateUnreadCount(unread);

          this.cdr.markForCheck();
        },

        error: error => {

          console.error(
            'Failed to load notifications:',
            error
          );
        }
      });
  }


  toggleNotifications(): void {

    this.showNotifications =
      !this.showNotifications;

    if (this.showNotifications) {
      this.loadNotifications();
    }
  }


  openNotification(
    notification: Notification
  ): void {

    // Mark as read
    this.markAsRead(notification);

    // Close dropdown
    this.showNotifications = false;


    switch (notification.type) {

      case NotificationType.APPLICATION_SUBMITTED:

        this.router.navigate([
          '/hr/applications'
        ]);

        break;


      case NotificationType.APPLICATION_STAGE_CHANGED:

        this.router.navigate([
          '/my-applications'
        ]);

        break;


      case NotificationType.INTERVIEW_SCHEDULED:

        if (this.userRole === 'INTERVIEWER') {

          this.router.navigate([
            '/interviewer/my-interviews'
          ]);

        } else if (this.userRole === 'CANDIDATE') {

          this.router.navigate([
            '/my-applications'
          ]);
        }

        break;


      case NotificationType.INTERVIEW_UPDATED:

        if (this.userRole === 'INTERVIEWER') {

          this.router.navigate([
            '/interviewer/my-interviews'
          ]);

        } else if (this.userRole === 'CANDIDATE') {

          this.router.navigate([
            '/my-applications'
          ]);
        }

        break;


      case NotificationType.JOB_POSTING_CREATED:

        if (notification.relatedEntityId) {

          this.router.navigate([
            '/jobs',
            notification.relatedEntityId
          ]);

        } else {

          this.router.navigate([
            '/jobs'
          ]);
        }

        break;


      case NotificationType.SYSTEM:

        // No navigation
        break;
    }
  }


  markAsRead(
    notification: Notification
  ): void {

    if (notification.read) {
      return;
    }


    this.notificationService
      .markAsRead(notification.id)
      .subscribe({

        next: () => {

          notification.read = true;

          this.unreadCount =
            Math.max(
              0,
              this.unreadCount - 1
            );

          this.notificationService
            .updateUnreadCount(
              this.unreadCount
            );

          this.cdr.markForCheck();
        },

        error: error => {

          console.error(
            'Failed to mark notification as read:',
            error
          );
        }
      });
  }


  markAllAsRead(): void {

    this.notificationService
      .markAllAsRead()
      .subscribe({

        next: () => {

          this.notifications.forEach(
            notification => {
              notification.read = true;
            }
          );

          this.unreadCount = 0;

          this.notificationService
            .updateUnreadCount(0);

          this.cdr.markForCheck();
        },

        error: error => {

          console.error(
            'Failed to mark all notifications as read:',
            error
          );
        }
      });
  }
  getNotificationTime(createdAt: string): string {
    const date = new Date(createdAt);
    const now = new Date();

    const difference =
      now.getTime() - date.getTime();

    const minutes = Math.floor(
      difference / (1000 * 60)
    );

    if (minutes < 1) {
      return 'Just now';
    }

    if (minutes < 60) {
      return `${minutes}m ago`;
    }

    const hours = Math.floor(minutes / 60);

    if (hours < 24) {
      return `${hours}h ago`;
    }

    const days = Math.floor(hours / 24);

    if (days < 7) {
      return `${days}d ago`;
    }

    return date.toLocaleDateString();
  }


  logout(): void {

    this.authService.logout();

    this.notifications = [];

    this.unreadCount = 0;

    this.showNotifications = false;

    this.notificationService
      .updateUnreadCount(0);

    this.notificationPolling?.unsubscribe();

    this.notificationPolling = undefined;

    this.syncAuthState();

    this.cdr.markForCheck();
  }
}