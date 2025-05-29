package com.example.demo.service;

import com.example.demo.dto.coursedto.CourseDTO;
import com.example.demo.errorhandler.CourseNotFoundException;
import com.example.demo.errorhandler.UnauthorizedException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Service
@AllArgsConstructor
public class CourseService {

    private final ObjectMapper objectMapper;
    private final WebClient webClientBuilder;
    private final UserService userService;

    public Flux<CourseDTO> getCoursesFromM2(String authHeader) {
        return webClientBuilder
                .get()
                .uri("/api/course/all-courses")
                .header("Authorization", authHeader)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response -> {
                    if (response.statusCode() == HttpStatus.UNAUTHORIZED) {
                        return Mono.error(new RuntimeException("Unauthorized access"));
                    }
                    return Mono.error(new RuntimeException("Error fetching posts"));
                })
                .bodyToFlux(CourseDTO.class);
    }


    public CourseDTO createCourse(
            Long mentorId,
            CourseDTO courseCreateDTO,
            MultipartFile imageFile,
            String authHeader) throws Exception {

        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        Map<String, Object> courseData = new HashMap<>();

        courseData.put("mentorId", mentorId);
        courseData.put("title", courseCreateDTO.getTitle());
        courseData.put("description", courseCreateDTO.getDescription());
        courseData.put("category", courseCreateDTO.getCategory());
        courseData.put("duration", courseCreateDTO.getDuration());
        courseData.put("status", courseCreateDTO.getStatus());
        courseData.put("certificateAvailable", courseCreateDTO.getCertificateAvailable());

        builder.part("courseDto", objectMapper.writeValueAsString(courseData))
                .contentType(MediaType.APPLICATION_JSON);

        if (imageFile != null && !imageFile.isEmpty()) {
            builder.part("image", imageFile.getResource())
                    .filename(imageFile.getOriginalFilename());
        }

        return webClientBuilder.post()
                .uri("/api/course")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .header("Authorization", authHeader)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .retrieve()
                .bodyToMono(CourseDTO.class)
                .block();
    }

    //updateCourse
    public CourseDTO updateCourse(
            Long courseId,
            Long mentorId,
            CourseDTO courseDTO,
            MultipartFile imageFile,
            String authHeader) throws Exception {

        MultipartBodyBuilder builder = new MultipartBodyBuilder();

        Map<String, Object> courseData = new HashMap<>();
        courseData.put("title", courseDTO.getTitle());
        courseData.put("description", courseDTO.getDescription());
        courseData.put("category", courseDTO.getCategory());
        courseData.put("duration", courseDTO.getDuration());
        courseData.put("laste", courseDTO.getDuration());
        courseData.put("status", courseDTO.getStatus());
        courseData.put("certificateAvailable", courseDTO.getCertificateAvailable());

        builder.part("courseDto", objectMapper.writeValueAsString(courseData))
                .contentType(MediaType.APPLICATION_JSON);

        if (imageFile != null && !imageFile.isEmpty()) {
            builder.part("image", imageFile.getResource())
                    .filename(imageFile.getOriginalFilename());
        }
        return webClientBuilder.put()
                .uri("/api/course/" + courseId)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .header("Authorization", authHeader)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .retrieve()
                .bodyToMono(CourseDTO.class)
                .block();
    }

    public void deleteCourse(Long postId, Long userId, String authHeader) throws CourseNotFoundException, UnauthorizedException {
        try {
            String userRole = userService.getUserRoleById(userId);
            if (userRole.equals("admin")) {
                webClientBuilder.delete()
                        .uri("/api/course/" + postId)
                        .header("Authorization", authHeader)
                        .retrieve()
                        .toBodilessEntity()
                        .block();
            } else {
                CourseDTO courseDTO = getPostDTOById(postId);
                if (courseDTO.getMentorId().equals(userId)) {
                    webClientBuilder.delete()
                            .uri("/api/course/" + postId)
                            .header("Authorization", authHeader)
                            .retrieve()
                            .toBodilessEntity()
                            .block();
                } else {
                    throw new UnauthorizedException("You can only delete your own posts");
                }
            }
        } catch (WebClientResponseException.NotFound e) {
            throw new CourseNotFoundException("Post not found with id: " + postId);
        } catch (WebClientResponseException.Forbidden e) {
            throw new UnauthorizedException("You can only delete your own posts");
        } catch (Exception e) {
            throw new RuntimeException("Error when deleting post: " + e.getMessage(), e);
        }
    }

    public CourseDTO getPostDTOById(Long postId) {
        return webClientBuilder.get()
                .uri("/api/course/{id}", postId)
                .retrieve()
                .bodyToMono(CourseDTO.class)
                .block();
    }


    public Flux<CourseDTO> getMyCourses(String auth) {
        return webClientBuilder.get()
                .uri("api/course/my-courses")
                .header("Authorization", auth)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response -> {
                    if (response.statusCode() == HttpStatus.UNAUTHORIZED) {
                        return Mono.error(new RuntimeException("Unauthorized access"));
                    }
                    return Mono.error(new RuntimeException("Error fetching posts"));
                })
                .bodyToFlux(CourseDTO.class);

    }
}
