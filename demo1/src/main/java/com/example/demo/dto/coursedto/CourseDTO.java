package com.example.demo.dto.coursedto;

import com.example.demo.enumeration.CourseStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseDTO {
    private Long id;
    private String title;
    private String description;
    private String category;
    private Integer duration;
    private CourseStatus status;
    private Boolean certificateAvailable;
    private LocalDateTime createdDate;
    private LocalDateTime lastUpdatedDate;
    private Long mentorId;
    private byte[] image;
}
