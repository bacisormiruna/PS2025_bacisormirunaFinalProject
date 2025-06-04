import {CourseDTO} from "./course.model";

export interface EnrollmentViewModel {
  course: CourseDTO;
  status: RequestStatus;
  cursantId:number,
  courseId:number,
  cursantName: string;
}
export type RequestStatus = 'PENDING' | 'ACCEPTED' | 'REJECTED';
