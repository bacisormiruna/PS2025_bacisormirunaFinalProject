import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { UserViewModel } from '../model/userView.model';
import {EnrollmentViewModel} from "../model/enrollmentView.model";

@Injectable({
  providedIn: 'root',
})
export class EnrollmentService {
  private apiUrl = 'http://localhost:8080/api/user';

  constructor(private http: HttpClient) {}

  sendEnrollRequest(courseId: number, token: string): Observable<string> {
    console.log('Sending enrollment request for course:', courseId);
    console.log('Token:', token);

    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });
    return this.http.post<string>(`${this.apiUrl}/request/${courseId}`, {}, {
      headers,
      responseType: 'text' as 'json'
    });
  }

  respondToRequest(cursantId: number, courseId: number, status: 'ACCEPTED' | 'REJECTED', token: string): Observable<any> {
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });

    return this.http.put(`${this.apiUrl}/respond/${cursantId}/${courseId}`, { status }, { headers });
  }

  getEnrolledUsers(courseId: number, token: string): Observable<UserViewModel[]> {
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
    return this.http.get<UserViewModel[]>(`${this.apiUrl}/users/enrolled/${courseId}`, { headers });
  }

  getUsersForCourse(courseId: number): Observable<UserViewModel[]> {
    return this.http.get<UserViewModel[]>(`http://localhost:8081/api/course/${courseId}/users`);
  }


  getMyEnrollments(token: string): Observable<EnrollmentViewModel[]> {
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
    return this.http.get<EnrollmentViewModel[]>(`${this.apiUrl}/my-enrollments`, { headers });
  }
}
