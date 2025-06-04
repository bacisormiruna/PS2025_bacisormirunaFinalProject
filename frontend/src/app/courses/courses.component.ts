import {Component, Input, OnInit} from '@angular/core';
import { CourseService } from '../service/course.service';
import { CourseDTO } from '../model/course.model';
import {EnrollmentService} from "../service/enrollment.service";

@Component({
  selector: 'app-courses',
  templateUrl: './courses.component.html',
  styleUrls: ['./courses.component.scss'],
})
export class CoursesComponent implements OnInit {
  @Input() roleName: string | null = null;
  courses: CourseDTO[] = [];
  token: string = '';
  notificationMessage: string = '';
  showNotification: boolean = false;

  constructor(private courseService: CourseService, private enrollmentService: EnrollmentService) {}

  ngOnInit(): void {
    this.token = sessionStorage.getItem('token') || '';//localStorage.getItem('token') || '';
    this.getCourses();
  }

  getCourses(): void {
    if (this.roleName === 'MENTOR') {
      this.courseService.getMyCourses(this.token).subscribe(data => {
        this.courses = data;
      });
    } else {
      this.courseService.getAllCourses(this.token).subscribe(data => {
        this.courses = data;
      });
    }
  }

  deleteCourse(id: number): void {
    if (!this.token) return;
    this.courseService.deleteCourse(id, this.token).subscribe(() => {
      this.getCourses();
    });
  }

  enroll(courseId: number): void {
    if (!this.token) return;

    this.enrollmentService.sendEnrollRequest(courseId, this.token).subscribe({
      next: () => {
        this.showNotificationMessage('Cererea de înscriere a fost trimisă.', true);
      },
      error: err => {
        console.error('Eroare completă:', err);
        let msg = 'A apărut o eroare necunoscută.';
        if (err.error && typeof err.error === 'string') {
          msg = err.error;
        } else if (err.error && err.error.message) {
          msg = err.error.message;
        } else if (err.message) {
          msg = err.message;
        }
        this.showNotificationMessage(msg, false);
      }
    });
  }

  showNotificationMessage(message: string, success: boolean) {
    this.notificationMessage = message;
    this.showNotification = true;

    const notifEl = document.getElementById('notification');
    if (notifEl) {
      if (success) {
        notifEl.classList.add('success');
        notifEl.classList.remove('error');
      } else {
        notifEl.classList.add('error');
        notifEl.classList.remove('success');
      }
    }
    setTimeout(() => {
      this.showNotification = false;
    }, 3000);
  }
}
