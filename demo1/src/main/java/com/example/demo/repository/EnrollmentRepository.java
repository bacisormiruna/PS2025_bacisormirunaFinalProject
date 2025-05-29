package com.example.demo.repository;

import com.example.demo.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment,Long> {
    Optional<Enrollment> findByCursantIdAndCourseId(Long cursantId, Long courseId);
    List<Enrollment> findByCourseId(Long courseId);
}
