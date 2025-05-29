package com.example.demo.builder.userbuilder;

import com.example.demo.dto.coursedto.CourseDTO;
import com.example.demo.entity.Course;


public class CourseBuilder {

    public static CourseDTO generateDTOFromEntity(Course course) {
        return CourseDTO.builder()
                .id(course.getId())
                .title(course.getTitle())
                .description(course.getDescription())
                .category(course.getCategory())
                .duration(course.getDuration())
                .status(course.getStatus())
                .certificateAvailable(course.getCertificateAvailable())
                .createdDate(course.getCreatedDate())
                .lastUpdatedDate(course.getLastUpdatedDate())
                .mentorId(course.getMentorId())
                .image(course.getImage())
                .build();
    }
}
