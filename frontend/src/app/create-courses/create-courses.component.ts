import { Component } from '@angular/core';
import { CourseService } from '../service/course.service';
import { CourseDTO, CourseStatus } from '../model/course.model';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-create-courses',
  templateUrl: './create-courses.component.html',
  styleUrls: ['./create-courses.component.scss'],
})
export class CreateCoursesComponent {
  title = '';
  description = '';
  category = '';
  duration = 0;
  status: CourseStatus = CourseStatus.OPEN_FOR_ENROLMENT;
  certificateAvailable = false;
  image: File | null = null;
  statusOptions = Object.values(CourseStatus);

  constructor(private courseService: CourseService) {}

  onFileSelected(event: any) {
    this.image = event.target.files[0];
  }

  createCourse() {
    const courseDto: CourseDTO = {
      title: this.title,
      description: this.description,
      category: this.category,
      duration: this.duration,
      status: this.status,
      certificateAvailable: this.certificateAvailable,
      createdDate: undefined,
      lastUpdatedDate: undefined,
      mentorId: undefined,
      image: undefined
    };
    const token = localStorage.getItem('token');
    if (!token) {
      alert('Token missing');
      return;
    }
    this.courseService.createCourse(courseDto, this.image, token).subscribe({
      next: () => alert('Curs creat cu succes!'),
      error: (err) => alert('Eroare la creare curs: ' + err.error)
    });
  }
}
