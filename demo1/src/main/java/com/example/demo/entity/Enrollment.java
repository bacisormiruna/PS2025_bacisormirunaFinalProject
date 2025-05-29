package com.example.demo.entity;

import com.example.demo.dto.coursedto.CourseDTO;
import com.example.demo.enumeration.RequestStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "enrollment")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Enrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cursant_id", nullable = false)
    private User cursant;

    @Column(name = "course_id", nullable = false)
    private Long courseId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status;


}
