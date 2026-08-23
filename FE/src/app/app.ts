import { Component, ChangeDetectorRef, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet, RouterLink, NavigationEnd, Router } from '@angular/router';
import { AuthService } from './core/services/auth.service';
import { filter } from 'rxjs/operators';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RouterLink, CommonModule],
  templateUrl: './app.html',
  styleUrls: ['./app.css']
})
export class AppComponent implements OnInit {
  isLoggedIn = false;
  userRole: string | null = null;
  firstName: string | null = null;

  constructor(
    private authService: AuthService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    this.syncAuthState();
    // Re-evaluate auth state after every navigation (login, logout, etc.)
    this.router.events.pipe(filter((e) => e instanceof NavigationEnd)).subscribe(() => {
      this.syncAuthState();
      this.cdr.markForCheck();
    });
  }

  syncAuthState() {
    this.isLoggedIn = this.authService.isLoggedIn();
    const user = this.authService.getCurrentUser();
    this.userRole = user?.role ?? null;
    this.firstName = user?.firstName ?? null;
  }

  logout() {
    this.authService.logout();
    this.syncAuthState();
    this.cdr.markForCheck();
  }
}