import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from "@angular/forms";
import { Router } from '@angular/router';
import { AuthService } from "../../../service/auth.service";
import { LoginRequest } from "../../../model/login-request";
import { finalize } from 'rxjs/operators';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {
  userForm: FormGroup;
  loading = false;
  errorMessage = '';

  constructor(
    private authService: AuthService,
    private router: Router
  ) {
    this.userForm = new FormGroup({
      name: new FormControl('', [Validators.required]),
      password: new FormControl('', [Validators.required])
    });
  }

  ngOnInit(): void {
    if (this.authService.isLoggedIn()) {
      console.log('You is already logged in, redirecting to home');
      setTimeout(() => {
        this.router.navigate(['/home']);
      }, 100);
    }
  }

  login() {
    this.errorMessage = '';
    if (this.userForm.invalid) {
      this.errorMessage = 'Please fill in all required fields.';
      return;
    }
    const request: LoginRequest = {
      name: this.userForm.get('name')?.value,
      password: this.userForm.get('password')?.value
    };
    this.loading = true;
    this.authService.login(request).pipe(
      finalize(() => this.loading = false)
    ).subscribe({
      next: (response) => {
        console.log('Login successful, got response:', response);
        sessionStorage.setItem('roleName', response.roleName);//localStorage.setItem('roleName', response.roleName);
        this.router.navigate(['/home']);
      },
      error: (error) => {
        console.error('Login error details:', error);
        if (error.status === 401) {
          this.errorMessage = 'Invalid username or password';
        } else if (error.status === 0) {
          this.errorMessage = 'Cannot connect to server. Check your network.';
        } else if (typeof error.error === 'string') {
          this.errorMessage = error.error;
        } else if (error.error && error.error.message) {
          this.errorMessage = error.error.message;
        } else {
          this.errorMessage = 'Login failed. Please try again later.';
        }
      }
    });
  }

}
