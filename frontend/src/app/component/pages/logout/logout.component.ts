import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../../service/auth.service';

@Component({
  selector: 'app-logout',
  template: `<div class="logout-container">
               <p>Logging out...</p>
             </div>`,
  styles: [`
    .logout-container {
      display: flex;
      justify-content: center;
      align-items: center;
      height: 100vh;
      font-size: 18px;
    }
  `]
})
export class LogoutComponent implements OnInit {

  constructor(
    private authService: AuthService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.authService.logout();
    setTimeout(() => {
      this.router.navigate(['/login']);
    }, 1000);
  }
}
