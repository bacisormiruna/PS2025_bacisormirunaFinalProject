import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from './service/auth.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {
  title = 'demoApp';

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    if (this.authService.isLoggedIn()) {
      this.authService.fetchCurrentUser().subscribe({
        next: () => {
          if (this.router.url === '/') {
            this.router.navigate(['/home']);
          }
        },
        error: () => {
          this.authService.logout();
        }
      });
    } else {
      if (this.router.url !== '/login') {
        this.router.navigate(['/login']);
      }
    }
  }
}
