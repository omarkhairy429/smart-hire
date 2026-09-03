import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, timer } from 'rxjs';
import { switchMap } from 'rxjs/operators';

import { Notification } from '../models/notification.model';

@Injectable({
    providedIn: 'root'
})
export class NotificationService {

    private readonly apiUrl = '/api/notifications';

    private unreadCountSubject =
        new BehaviorSubject<number>(0);

    unreadCount$ =
        this.unreadCountSubject.asObservable();

    constructor(private http: HttpClient) { }


    getNotifications(): Observable<Notification[]> {

        return this.http.get<Notification[]>(
            this.apiUrl
        );
    }


    getUnreadCount(): Observable<number> {

        return this.http.get<number>(
            `${this.apiUrl}/unread-count`
        );
    }


    markAsRead(id: string): Observable<void> {

        return this.http.patch<void>(
            `${this.apiUrl}/${id}/read`,
            {}
        );
    }


    markAllAsRead(): Observable<void> {

        return this.http.patch<void>(
            `${this.apiUrl}/read-all`,
            {}
        );
    }


    startPolling(): Observable<number> {

        return timer(0, 30000).pipe(
            switchMap(() => this.getUnreadCount())
        );
    }


    updateUnreadCount(count: number): void {

        this.unreadCountSubject.next(count);
    }


    refreshUnreadCount(): void {

        this.getUnreadCount().subscribe({
            next: count => {
                this.updateUnreadCount(count);
            }
        });
    }
}