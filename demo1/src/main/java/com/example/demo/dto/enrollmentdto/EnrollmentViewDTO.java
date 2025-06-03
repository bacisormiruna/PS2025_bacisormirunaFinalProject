package com.example.demo.dto.enrollmentdto;

import com.example.demo.dto.coursedto.CourseDTO;
import com.example.demo.enumeration.RequestStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EnrollmentViewDTO {
    private Long cursantId;
    private CourseDTO course;
    private String cursantName;
    private Long courseId;
    private RequestStatus status;

    public EnrollmentViewDTO(CourseDTO course, RequestStatus status,Long cursantId,
                             Long courseId, String cursantName) {
        this.course = course;
        this.status = status;
        this.cursantId=cursantId;
        this.courseId = courseId;
        this.cursantName = cursantName;
    }
}
