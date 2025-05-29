package com.example.demo.service;

import com.example.demo.dto.coursedto.CourseDTO;
import com.example.demo.dto.userdto.UserViewDTO;
import com.example.demo.entity.Enrollment;
import com.example.demo.entity.User;
import com.example.demo.enumeration.CourseStatus;
import com.example.demo.enumeration.RequestStatus;
import com.example.demo.errorhandler.EnrollmentException;
import com.example.demo.errorhandler.UserException;
import com.example.demo.repository.EnrollmentRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final WebClient postsMicroserviceClient;

    public void sendEnrollRequest(Long cursantId, Long courseId) {
        User cursant = userRepository.findById(cursantId)
                .orElseThrow(() -> new RuntimeException("Cursant not found"));

        CourseDTO course = postsMicroserviceClient.get()
                .uri("/api/course/{id}", courseId)
                .retrieve()
                .bodyToMono(CourseDTO.class)
                .block();

        if (course == null) {
            throw new RuntimeException("Course not found");
        }

        Optional<Enrollment> exists = enrollmentRepository.findByCursantIdAndCourseId(cursantId, course.getId());
        if (exists.isPresent()) {
            throw new RuntimeException("Already enrolled");
        }

        Enrollment enrollment = Enrollment.builder()
                .cursant(cursant)
                .courseId(course.getId())
                .status(RequestStatus.PENDING)
                .build();
        enrollmentRepository.save(enrollment);
    }

    public void updateEnrollmentStatus(Long mentorId, Long cursantId, Long courseId, RequestStatus newStatus) {
        Enrollment enrollment = enrollmentRepository.findByCursantIdAndCourseId(cursantId, courseId)
                .orElseThrow(() -> new RuntimeException("Enrollment not found"));

        CourseDTO course = postsMicroserviceClient.get()
                .uri("/api/course/{id}", courseId)
                .retrieve()
                .bodyToMono(CourseDTO.class)
                .block();

        if (course == null || !course.getMentorId().equals(mentorId)) {
            throw new RuntimeException("Unauthorized: You are not the mentor of this course");
        }
        CourseStatus courseStatus = course.getStatus();
        if (courseStatus == CourseStatus.CLOSED_FOR_ENROLLMENT) {
            if (newStatus == RequestStatus.ACCEPTED) {
                throw new RuntimeException("Cannot approve enrollment: course is closed for enrollment");
            }
        } else if (courseStatus != CourseStatus.OPEN_FOR_ENROLLMENT) {
            throw new RuntimeException("Cannot update enrollment: course is not open for enrollment");
        }
        enrollment.setStatus(newStatus);
        enrollmentRepository.save(enrollment);
    }

    public List<UserViewDTO> findCursantByCourseId(Long courseId) {
        List<Enrollment> enrollments = enrollmentRepository.findByCourseId(courseId);
        return enrollments.stream()
                .map(enrollment -> {
                    User user = enrollment.getCursant();
                    return UserViewDTO.builder()
                            .id(user.getId())
                            .name(user.getName())
                            .email(user.getEmail())
                            .roleName(user.getRole().getName())
                            .build();
                })
                .sorted()
                .collect(Collectors.toList());
    }

}
