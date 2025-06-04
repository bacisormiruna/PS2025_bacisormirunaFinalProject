export interface CourseDTO {
  id?: number;
  title: string;
  description: string;
  category: string;
  duration: number;
  status: CourseStatus;
  certificateAvailable: boolean;
  createdDate?: string;
  lastUpdatedDate?: string;
  mentorId?: number;
  image?: File | null;
}
export enum CourseStatus {
  IN_PROGRESS = 'IN_PROGRESS',
  OPEN_FOR_ENROLMENT= 'OPEN_FOR_ENROLMENT',
  CLOSED_FOR_ENROLLMENT = 'CLOSED_FOR_ENROLLMENT'
}
