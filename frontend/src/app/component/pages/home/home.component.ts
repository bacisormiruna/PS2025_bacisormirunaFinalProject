import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {CourseDTO} from "../../../model/course.model";
import {CourseService} from "../../../service/course.service";
import {EnrollmentService} from "../../../service/enrollment.service";
import {EnrollmentViewModel} from "../../../model/enrollmentView.model";
import {UserViewModel} from "../../../model/userView.model";
import {NotificationService} from "../../../service/notification.service";
import {NotificationType} from "../../../model/notification-type.enum";

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
})
export class HomeComponent implements OnInit {
  roleName: string | null = null;
  courses: CourseDTO[] = [];
  token: string = '';
  notificationMessage: string = '';
  showNotification: boolean = false;
  myEnrollments: EnrollmentViewModel[] = [];
  showEnrollments = false;
  showRequestsForCourse: number | null = null;
  pendingRequests: UserViewModel[] = [];
  processedRequests: Set<number> = new Set();
  acceptedUsersForCourse: UserViewModel[] = [];
  showEnrolledForCourse: number | null = null;

  constructor(private router: Router, private courseService: CourseService, private enrollmentService: EnrollmentService, private notificationService: NotificationService) {}

  ngOnInit(): void {
    this.token = sessionStorage.getItem('token') || '';//localStorage.getItem('token') || '';
    this.roleName = sessionStorage.getItem('roleName');// localStorage.getItem('roleName');
    if (this.token) {
      if (this.roleName === 'MENTOR') {
        this.loadMyCourses();
      } else {
        this.loadCourses();
      }
    }
  }

  loadCourses(): void {
    this.courseService.getAllCourses(this.token).subscribe(data => {
      this.courses = data;
    });
  }

  loadMyCourses(): void {
    this.courseService.getMyCourses(this.token).subscribe(data => {
      console.log('Received courses for mentor:', data); // ← aici
      this.courses = data;
    });
  }

  loadMyEnrollments(): void {
    if (!this.isTokenValid()) return;
    this.enrollmentService.getMyEnrollments(this.token).subscribe({
      next: list => {
        this.myEnrollments = list;
        this.showEnrollments = true;
      },
      error: err => console.error('Cannot load enrollments', err)
    });
  }

  private isTokenValid(): boolean {
    const token = sessionStorage.getItem('token');//localStorage.getItem('token');
    if (!token) {
      this.logout();
      return false;
    }
    return true;
  }

  logout(): void {
    sessionStorage.removeItem('token');//localStorage.removeItem('token');
    sessionStorage.removeItem('roleName');//localStorage.removeItem('roleName');
    sessionStorage.removeItem('token');
    this.router.navigate(['/login']);
  }

  goToCreateCourse(): void {
    this.router.navigate(['/createCourse']);
  }

  deleteCourse(id: number): void {
    if (!this.token) return;
    this.courseService.deleteCourse(id, this.token).subscribe(() => {
      this.loadCourses();
    });
  }

  enroll(courseId: number): void {
    this.enrollmentService.sendEnrollRequest(courseId, this.token).subscribe({
      next: () => {
        this.notificationService.notify(NotificationType.SUCCESS, "Successfully sent enrollment request!");
      },
      error: err => {
        console.error('Error complete:', err);
        let msg = 'Unknown error.';
        if (err.error && typeof err.error === 'string') {
          msg = err.error;
        } else if (err.error && err.error.message) {
          msg = err.error.message;
        } else if (err.message) {
          msg = err.message;
        }
        this.notificationService.notify(NotificationType.ERROR, "Eroare la trimiterea cererii de înscriere: " + msg);
      }
    });
  }

  showAllCourses(): void {
    this.showEnrollments = false;
  }

  toggleRequests(courseId: number): void {
    if (this.showRequestsForCourse === courseId) {
      this.showRequestsForCourse = null;
      this.pendingRequests = [];
      this.processedRequests.clear();
    } else {
      this.showRequestsForCourse = courseId;
      this.processedRequests.clear();
      this.enrollmentService
        .getEnrolledUsers(courseId, this.token)
        .subscribe(list => {
          this.pendingRequests = list.filter(user => !this.processedRequests.has(user.id));
        });
    }
  }

  respond(cursantId: number, courseId: number, status: 'ACCEPTED' | 'REJECTED'): void {
    if (this.processedRequests.has(cursantId)) {
      this.notificationService.notify(NotificationType.WARNING, 'Această cerere a fost deja procesată.');
      return;
    }
    this.processedRequests.add(cursantId);

    this.enrollmentService
      .respondToRequest(cursantId, courseId, status, this.token)
      .subscribe({
        next: (res) => {
          console.log('Server response:', res);
          this.notificationService.notify(
            NotificationType.SUCCESS,
            `Cererea a fost ${status === 'ACCEPTED' ? 'acceptată' : 'respinsă'} cu succes.`
          );
          this.pendingRequests = this.pendingRequests.filter(user => user.id !== cursantId);
        },
        error: err => {
          console.error('Error responding to request', err);
          this.processedRequests.delete(cursantId);
          let errorMessage = 'Eroare la procesarea cererii.';
          if (err.error && typeof err.error === 'string') {
            errorMessage = err.error;
          } else if (err.error && err.error.message) {
            errorMessage = err.error.message;
          } else if (err.message) {
            errorMessage = err.message;
          }
          this.notificationService.notify(NotificationType.ERROR, errorMessage);
        }
      });
  }

  loadAcceptedUsers(courseId: number): void {
    if (this.showEnrolledForCourse === courseId) {
      this.showEnrolledForCourse = null;
      this.acceptedUsersForCourse = [];
    } else {
      this.enrollmentService.getUsersForCourse(courseId).subscribe({
        next: (users) => {
          this.acceptedUsersForCourse = users;
          this.showEnrolledForCourse = courseId;
        },
        error: (err) => {
          console.error('Eroare la încărcarea utilizatorilor acceptați:', err);
        }
      });
    }
  }
}
