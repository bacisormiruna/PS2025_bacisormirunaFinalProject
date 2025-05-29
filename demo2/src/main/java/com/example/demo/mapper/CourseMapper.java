package com.example.demo.mapper;

import com.example.demo.dto.coursedto.CourseDTO;
import com.example.demo.entity.Course;
import org.springframework.stereotype.Component;

@Component
public class CourseMapper {

    public CourseDTO toDto(Course course) {
        if (course == null) {
            return null;
        }
        CourseDTO courseDto = new CourseDTO();
        courseDto.setId(course.getId());
        courseDto.setTitle(course.getTitle());
        courseDto.setDescription(course.getDescription());
        courseDto.setCategory(course.getCategory());
        courseDto.setDuration(course.getDuration());
        courseDto.setStatus(course.getStatus());
        courseDto.setCertificateAvailable(course.getCertificateAvailable());
        courseDto.setCreatedDate(course.getCreatedDate());
        courseDto.setLastUpdatedDate(course.getLastUpdatedDate());
        courseDto.setMentorId(course.getMentorId());
        courseDto.setImage(course.getImage());
        return courseDto;
    }

    public Course toEntity(CourseDTO courseDto, Long mentorId) {
        if (courseDto == null) {
            return null;
        }
        Course course = new Course();
        course.setId(courseDto.getId());
        course.setTitle(courseDto.getTitle());
        course.setDescription(courseDto.getDescription());
        course.setCategory(courseDto.getCategory());
        course.setDuration(courseDto.getDuration());
        course.setStatus(courseDto.getStatus());
        course.setCertificateAvailable(courseDto.getCertificateAvailable());
        course.setCreatedDate(courseDto.getCreatedDate());
        course.setLastUpdatedDate(courseDto.getLastUpdatedDate());
        course.setMentorId(mentorId);
        course.setImage(courseDto.getImage());
        return course;
    }
}
