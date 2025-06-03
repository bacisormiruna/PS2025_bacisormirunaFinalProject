package com.example.demo.controller;


import com.example.demo.dto.coursedto.CourseDTO;
import com.example.demo.dto.enrollmentdto.EnrollmentViewDTO;
import com.example.demo.dto.responsedto.ResponseDTO;
import com.example.demo.dto.userdto.UserDTO;
import com.example.demo.dto.userdto.UserViewDTO;
import com.example.demo.errorhandler.CourseNotFoundException;
import com.example.demo.errorhandler.UnauthorizedException;
import com.example.demo.errorhandler.UserException;
import com.example.demo.service.CourseService;
import com.example.demo.service.EnrollmentService;
import com.example.demo.service.JWTService;
import com.example.demo.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping(value = "/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final CourseService courseService;
    private final JWTService jwtService;
    private final EnrollmentService enrollmentService;

    @RequestMapping(value = "/getAll", method = RequestMethod.GET)
    public ResponseEntity<?> displayAllUserView(){
        return new ResponseEntity<>(userService.findAllUserView(), HttpStatus.OK);
    }

    @RequestMapping(value = "/getUserById/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> displayUserViewById(@PathVariable("id") @NonNull  Long id) throws UserException {
        return new ResponseEntity<>(userService.findUserViewById(id), HttpStatus.OK);
    }

    @RequestMapping(value = "/getUserByEmail/{email}", method = RequestMethod.GET)
    public ResponseEntity<?> displayUserViewByEmail(@PathVariable("email") String email) throws UserException {
        return new ResponseEntity<>(userService.findUserViewByEmail(email), HttpStatus.OK);
    }

    @RequestMapping(value = "/getUserByRoleName/{roleName}", method = RequestMethod.GET)
    public ResponseEntity<?> displayUserViewByRoleName(@PathVariable("roleName") String roleName) throws UserException {
        return new ResponseEntity<>(userService.findUserViewByRoleName(roleName), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, value = "/create")
    public ResponseEntity<?> processAddUserForm(@RequestBody(required = false) UserDTO userDTO) throws UserException {
        return new ResponseEntity<>(userService.createUser(userDTO), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, value = "/update")
    public ResponseEntity<?> processUpdateUserForm(@RequestBody UserDTO userDTO) throws UserException {
        return new ResponseEntity<>(userService.updateUser(userDTO), HttpStatus.OK);
    }

    @RequestMapping(value="/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteUserByIdForm(@PathVariable("id") Long id) throws UserException {
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/status")
    public ResponseEntity<String> getStatus() {
        return ResponseEntity.ok("Service is running!");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserDTO loginDTO) {
        try {
            String token = userService.verify(loginDTO);
            System.out.println("Username: " + jwtService.extractUsername(token));
            System.out.println("UserId: " + jwtService.extractUserId(token));
            System.out.println("UserRole: " + jwtService.extractRoleName(token));
            return ResponseEntity.ok(Map.of("token", token));
        } catch (UserException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @GetMapping("/current")
    public ResponseEntity<UserViewDTO> currentUser(@RequestHeader("Authorization") String token) {
        try {
            String token1 = token.startsWith("Bearer ") ? token.substring(7) : token;
            UserViewDTO user = userService.getUserByToken(token1);
            return ResponseEntity.ok(user);
        } catch (UserException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/getAllCourses")// asta e pentru un user obisnuit sa vada toate cursurile
    public Mono<ResponseEntity<?>> displayAllCourses(@RequestHeader("Authorization") String auth) {
        return courseService.getCoursesFromM2(auth)
                .collectList()
                .map(posts -> ResponseEntity.ok(posts));
    }

    @GetMapping("/getMyCourses")// asta e pentru mentor sa vada ce cursuri a creat
    public Mono<ResponseEntity<?>> displayMyCourses(@RequestHeader("Authorization") String auth) {
        return courseService.getMyCourses(auth)
                .collectList()
                .map(posts -> ResponseEntity.ok(posts));
    }

    @PostMapping(
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            path = "/createCourse"
    )
    public ResponseEntity<?> createCourse(
            @RequestHeader("Authorization") String authHeader,
            @RequestPart("courseDto") CourseDTO courseDTO,
            @RequestPart(value = "image", required = false) MultipartFile imageFile) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Token missing or invalid format");
            }
            String token = authHeader.substring(7);
            String username = jwtService.extractUsername(token);
            Long mentorId = jwtService.extractUserId(token);
            if (username == null || mentorId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid token");
            }
            CourseDTO createdCourse = courseService.createCourse(mentorId,courseDTO, imageFile, authHeader);
            return ResponseEntity.ok(createdCourse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating post: " + e.getMessage());
        }
    }

    //--------------actualizarea unui curs------------------------
    @PutMapping(
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            path = "/updateCourse/{courseId}"
    )
    public ResponseEntity<?> updateCourse(
            @PathVariable Long courseId,
            @RequestHeader("Authorization") String authHeader,
            @RequestPart("courseDto") CourseDTO courseDTO,
            @RequestPart(value = "image", required = false) MultipartFile imageFile) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Token missing or invalid format");
            }
            String token = authHeader.substring(7);
            String username = jwtService.extractUsername(token);
            Long userId = jwtService.extractUserId(token);
            if (username == null || userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid token");
            }
            CourseDTO updatedCourse = courseService.updateCourse(courseId, userId, courseDTO, imageFile, authHeader);
            return ResponseEntity.ok(updatedCourse);
        } catch (CourseNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating post: " + e.getMessage());
        }
    }

    @DeleteMapping("/deleteCourse/{courseId}")
    public ResponseEntity<?> deleteCourse(
            @PathVariable Long courseId,
            @RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Token missing or invalid format");
            }
            String token = authHeader.substring(7);
            String username = jwtService.extractUsername(token);
            Long userId = jwtService.extractUserId(token);
            if (username == null || userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid token");
            }
            courseService.deleteCourse(courseId, userId, authHeader);
            return ResponseEntity.noContent().build();
        } catch (CourseNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting post: " + e.getMessage());
        }
    }


    @PostMapping("/request/{courseId}")
    public ResponseEntity<?> sendEnrollRequest(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable Long courseId
    ) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Token missing or invalid format");
        }

        try {
            String token = authHeader.substring(7);
            String username = jwtService.extractUsername(token);
            Long cursantId = jwtService.extractUserId(token);

            if (username == null || cursantId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid token");
            }
            enrollmentService.sendEnrollRequest(cursantId, courseId);
            return ResponseEntity.ok("Enrollment request sent successfully.");

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/respond/{cursantId}/{courseId}")
    public ResponseEntity<?> respondToEnrollmentRequest(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long cursantId,
            @PathVariable Long courseId,
            @RequestBody ResponseDTO request
    ) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Token missing or invalid format");
        }
        try {
            String token = authHeader.substring(7);
            Long mentorId = jwtService.extractUserId(token);

            if (mentorId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid token");
            }
            enrollmentService.updateEnrollmentStatus(mentorId, cursantId, courseId, request.getStatus());
            //return ResponseEntity.ok("Enrollment status updated successfully.");
            return ResponseEntity.ok(Map.of("message", "Enrollment status updated successfully."));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/users/enrolled/{courseId}")
    public List<UserViewDTO> getUsersEnrolledInCourse(@PathVariable Long courseId) {
        return enrollmentService.findCursantByCourseId(courseId);
    }

    @GetMapping("/users/accepted/{courseId}")
    public List<UserViewDTO> getUsersAcceptedInCourse(@PathVariable Long courseId) {
        return enrollmentService.findCursantAcceptedByCourseId(courseId);
    }
    @GetMapping("/enrollmentRequests/{courseId}")
    public List<EnrollmentViewDTO> getEnrollmentRequestsByCourseId(@PathVariable Long courseId) {
        return enrollmentService.findEnrollmentRequestsByCourseId(courseId);
    }

    @GetMapping("/my-enrollments")
    public ResponseEntity<List<EnrollmentViewDTO>> getMyEnrollments(
            @RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String token = authHeader.substring(7);
        Long userId = jwtService.extractUserId(token);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<EnrollmentViewDTO> enrollments = enrollmentService.getEnrollmentsForUser1(userId);
        return ResponseEntity.ok(enrollments);
    }

}
