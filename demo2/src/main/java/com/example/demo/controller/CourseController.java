package com.example.demo.controller;

import com.example.demo.dto.coursedto.CourseDTO;
import com.example.demo.dto.userdto.UserViewDTO;
import com.example.demo.errorhandler.CourseNotFoundException;
import com.example.demo.errorhandler.UnauthorizedException;
import com.example.demo.service.CourseService;
import com.example.demo.service.JWTService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;


@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping(value = "/api/course")
@RequiredArgsConstructor
public class CourseController {
    @Autowired
    private CourseService courseService;
    @Autowired
    private JWTService jwtService;

    @GetMapping("/all-courses")
    public ResponseEntity<List<CourseDTO>> getAllCourses(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        String token = authHeader.substring(7);
        Long mentorId = jwtService.extractUserId(token);

        if (mentorId == null || mentorId <= 0) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        List<CourseDTO> courses = courseService.getAllCourses();
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/my-courses")
    public ResponseEntity<List<CourseDTO>> getMyCourses(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        String token = authHeader.substring(7);
        Long mentorId = jwtService.extractUserId(token);

        if (mentorId == null || mentorId <= 0) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        List<CourseDTO> courses = courseService.getAllCoursesByMentorId(mentorId);
        return ResponseEntity.ok(courses);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CourseDTO> createCourse(
            @RequestHeader ("Authorization") String authHeader,
            @RequestPart("courseDto") String courseDtoJson,
            @RequestPart(value = "image", required = false) MultipartFile imageFile) throws Exception {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        String token = authHeader.substring(7);
        Long userId = jwtService.extractUserId(token);

        if (userId == null || userId <= 0) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        CourseDTO courseDTO = objectMapper.readValue(courseDtoJson, CourseDTO.class);
        CourseDTO createdPost = courseService.createCourse(userId, courseDTO, imageFile);
        return ResponseEntity.ok(createdPost);
    }

    @PutMapping(value = "/{courseId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateCourse(
            @PathVariable Long courseId,
            @RequestHeader("Authorization") String authHeader,
            @RequestPart("courseDto") String postCreateDtoJson,
            @RequestPart(value = "image", required = false) MultipartFile imageFile) {

        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            String token = authHeader.substring(7);
            String username = jwtService.extractUsername(token);
            if (username == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            CourseDTO courseDto = objectMapper.readValue(postCreateDtoJson, CourseDTO.class);
            CourseDTO updatedCourse = courseService.updateCourse(courseId, courseDto, jwtService.extractUserId(token), imageFile);
            return ResponseEntity.ok(updatedCourse);
        } catch (CourseNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (JsonProcessingException e) {
            return ResponseEntity.badRequest().body("Invalid JSON format");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error updating post");
        }
    }

    @DeleteMapping("/{courseId}")
    public ResponseEntity<?> deleteCourse(
            @PathVariable Long courseId,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            Long mentorId = jwtService.extractUserId(token);

            courseService.deleteCourse(courseId, mentorId);
            return ResponseEntity.noContent().build();

        } catch (CourseNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error deleting post");
        }
    }

    @GetMapping("/status")
    public ResponseEntity<String> getStatus() {
        return ResponseEntity.ok("Service is running!");
    }

//    @GetMapping("/{id}")
//    public ResponseEntity<CourseDTO> getCourseById(@PathVariable Long id) throws CourseNotFoundException {
//        return ResponseEntity.ok(courseService.getCourseById(id));
//    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCourseById(@PathVariable Long id) {
        try {
            CourseDTO course = courseService.getCourseById(id);
            return ResponseEntity.ok(course);
        } catch (CourseNotFoundException e) {
            return ResponseEntity.ok("Course not found with this id:"+id);
        }
    }

    @GetMapping("/{courseId}/users")
    public List<UserViewDTO> getUsersForCourse(@PathVariable Long courseId) {
        return courseService.getUsersEnrolledInCourse(courseId);
    }

}
