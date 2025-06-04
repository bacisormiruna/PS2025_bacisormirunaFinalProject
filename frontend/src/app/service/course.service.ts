// course.service.ts
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { CourseDTO } from '../model/course.model';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class CourseService {
  private apiUrl = 'http://localhost:8080/api/user';

  constructor(private http: HttpClient) {}

  getAllCourses(token: string): Observable<CourseDTO[]> {
    return this.http.get<CourseDTO[]>(`${this.apiUrl}/getAllCourses`, {
      headers: new HttpHeaders({
        Authorization: `Bearer ${token}`
      })
    });
  }

  getMyCourses(token: string): Observable<CourseDTO[]> {
    return this.http.get<CourseDTO[]>(`${this.apiUrl}/getMyCourses`, {
      headers: new HttpHeaders({
        Authorization: `Bearer ${token}`
      })
    });
  }

  createCourse(course: CourseDTO, image: File | null, token: string): Observable<any> {
    const formData = new FormData();
    formData.append('courseDto', new Blob([JSON.stringify(course)], { type: 'application/json' }));
    if (image) {
      formData.append('image', image);
    }
    return this.http.post(`${this.apiUrl}/createCourse`, formData, {
      headers: new HttpHeaders({
        Authorization: `Bearer ${token}`
      })
    });
  }


  updateCourse(id: number, course: CourseDTO, token: string): Observable<any> {
    const formData = new FormData();
    formData.append('courseDto', new Blob([JSON.stringify(course)], { type: 'application/json' }));
    if (course.image) {
      formData.append('image', course.image);
    }

    return this.http.put(`${this.apiUrl}/updateCourse/${id}`, formData, {
      headers: new HttpHeaders({
        Authorization: `Bearer ${token}`
      })
    });
  }

  deleteCourse(id: number, token: string): Observable<any> {
    return this.http.delete(`${this.apiUrl}/deleteCourse/${id}`, {
      headers: new HttpHeaders({
        Authorization: `Bearer ${token}`
      })
    });
  }
}
