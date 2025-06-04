import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Router } from '@angular/router';
import { LoginRequest } from "../model/login-request";
import { LoginResponse } from "../model/login-response";
import { Observable, BehaviorSubject, throwError, of } from "rxjs";
import { catchError, tap, switchMap } from 'rxjs/operators';
import { User } from '../model/user.model';
import {UserViewModel} from "../model/userView.model";

const API_URL = 'http://localhost:8080/api/user';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private currentUserSubject: BehaviorSubject<UserViewModel | null>;
  public currentUser: Observable<UserViewModel | null>;

  constructor(private http: HttpClient, private router: Router) {
    this.currentUserSubject = new BehaviorSubject<UserViewModel | null>(this.getUserFromStorage());
    this.currentUser = this.currentUserSubject.asObservable();
  }

  login(request: LoginRequest): Observable<any> {
    const headers = new HttpHeaders({ 'Content-Type': 'application/json' });
    console.log('Sending login request to:', `${API_URL}/login`);
    return this.http.post<LoginResponse>(`${API_URL}/login`, request, { headers }).pipe(
      tap(response => {
        console.log('Received auth response:', response);
        if (!response || !response.token) {
          throw new Error('Invalid response format: missing token');
        }
        this.saveToken(response.token);
      }),
      switchMap(() => {
        console.log('Token saved, fetching user details');
        return this.fetchCurrentUser();
      }),
      tap(user => {
        console.log('User details received:', user);
        this.currentUserSubject.next(user);
        //localStorage.setItem('currentUser', JSON.stringify(user));
        sessionStorage.setItem('currentUser', JSON.stringify(user));
      }),
      catchError(error => {
        console.error('Login process failed:', error);
        this.logout();
        return throwError(() => error);
      })
    );
  }

  fetchCurrentUser(): Observable<UserViewModel> {
    const token = this.getToken();

    if (!token) {
      console.error('No token available for user fetch');
      return throwError(() => new Error('Authentication required'));
    }
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
    return this.http.get<UserViewModel>(`${API_URL}/current`, { headers })
      .pipe(
        tap(userViewModel => {
          this.currentUserSubject.next(userViewModel);
          sessionStorage.setItem('currentUser', JSON.stringify(userViewModel));//localStorage.setItem('currentUser', JSON.stringify(userViewModel));
        }),
        catchError(error => {
          console.error('Failed to fetch user details:', error);
          if (error.status === 401) {
            this.logout();
          }
          return throwError(() => error);
        })
      );
  }

  register(user: User): Observable<any> {
    return this.http.post<any>(`${API_URL}/create`, user);
  }

  saveToken(token: string) {
    //localStorage.setItem('token', token); - aici
    sessionStorage.setItem('token', token); // Use sessionStorage to store the token
  }

  getToken(): string | null {
    return sessionStorage.getItem('token');//localStorage.getItem('token');
  }

  getUserFromStorage(): UserViewModel | null {
    const user = sessionStorage.getItem('currentUser'); //localStorage.getItem('currentUser');
    return user ? JSON.parse(user) : null;
  }

  public get currentUserValue(): UserViewModel | null {
    return this.currentUserSubject.value;
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  logout() {
    //localStorage.removeItem('token');
    //localStorage.removeItem('currentUser');
    sessionStorage.removeItem('token');
    sessionStorage.removeItem('currentUser');
    this.currentUserSubject.next(null);
    this.router.navigate(['/login']);
  }
}
