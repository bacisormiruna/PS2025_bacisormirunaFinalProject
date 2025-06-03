package com.example.demo.service;

import com.example.demo.dto.coursedto.CourseDTO;
import com.example.demo.dto.hashtagdto.HashtagDTO;
import com.example.demo.dto.userdto.UserViewDTO;
import com.example.demo.entity.Course;
import com.example.demo.entity.Hashtag;
import com.example.demo.errorhandler.CourseNotFoundException;
import com.example.demo.errorhandler.UnauthorizedException;
import com.example.demo.mapper.CourseMapper;
import com.example.demo.repository.CourseRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private CourseMapper courseMapper;
    @Autowired
    private WebClient usersMicroserviceClient;

    public List<CourseDTO> getAllCourses(){
        List<Course> courses = courseRepository.findAll();
        System.out.println("Number of courses found" + courses.size());
        return courses.stream()
                .map(courseMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public CourseDTO createCourse(Long mentorId, CourseDTO courseDTO, MultipartFile imageFile) throws Exception {
        Course course = courseMapper.toEntity(courseDTO, mentorId);
        course.setCreatedDate(LocalDateTime.now());
        course.setLastUpdatedDate(LocalDateTime.now());
        course.setMentorId(mentorId);

        Course savedCourse = courseRepository.save(course);
        if (imageFile != null && !imageFile.isEmpty()) {
            storeImage(savedCourse.getId(), imageFile);
        }
        return courseMapper.toDto(savedCourse);
    }

    @Transactional
    public CourseDTO storeImage(Long courseId, MultipartFile imageFile) throws Exception {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException("Post not found with id: " + courseId));
        byte[] imageData = imageFile.getBytes();
        course.setImage(imageData);
        Course updatedCourse = courseRepository.save(course);
        return courseMapper.toDto(updatedCourse);
    }

    @Transactional
    public CourseDTO updateCourse(Long courseId, CourseDTO courseDto, Long mentorId, MultipartFile imageFile) throws Exception {
        Course existingCourse = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException("Post not found with id: " + courseId));
        if (!existingCourse.getMentorId().equals(mentorId)) {
           throw new UnauthorizedException("You can only update your own posts");
        }
        existingCourse.setTitle(courseDto.getTitle());
        existingCourse.setLastUpdatedDate(LocalDateTime.now());
        existingCourse.setDescription(courseDto.getDescription());
        existingCourse.setCategory(courseDto.getCategory());
        existingCourse.setDuration(courseDto.getDuration());
        existingCourse.setStatus(courseDto.getStatus());
        existingCourse.setCertificateAvailable(courseDto.getCertificateAvailable());

        if (imageFile != null && !imageFile.isEmpty()) {
            storeImage(existingCourse.getId(), imageFile);
        }
        Course updatedCourse = courseRepository.save(existingCourse);
        return courseMapper.toDto(updatedCourse);
    }

    @Transactional
    public void deleteCourse(Long courseId, Long mentorId) throws CourseNotFoundException, UnauthorizedException {
         Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException("Course not found with id: " + courseId));
        if (!course.getMentorId().equals(mentorId)){ //&& !username.equals("Moderator")) {
            throw new UnauthorizedException("You can only delete your own posts or posts as a moderator");
        }
        Long authorId = course.getMentorId();
        courseRepository.saveAndFlush(course);
        courseRepository.delete(course);
    }

    public CourseDTO getCourseById(Long id) throws CourseNotFoundException {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new CourseNotFoundException("Course not found with id: " + id));
        return courseMapper.toDto(course);
    }

    public List<CourseDTO> getAllCoursesByMentorId(Long mentorId) {
        List<Course> courses = courseRepository.findByMentorId(mentorId);
        System.out.println("Number of courses found" + courses.size());
        return courses.stream()
                .map(courseMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<UserViewDTO> getUsersEnrolledInCourse(Long courseId) {
        return usersMicroserviceClient.get()
                .uri("api/user/users/accepted/{courseId}", courseId)
                .retrieve()
                .bodyToFlux(UserViewDTO.class)
                .collectList()
                .block();
    }

//    public List<CourseDTO> getAllCourses(){
//        List<Course> courses = courseRepository.findAll();
//        System.out.println("Number of courses found" + courses.size());
//        return courses.stream()
//                .map(courseMapper::toDto)
//                .collect(Collectors.toList());
//    }
}

