package com.example.demo.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/enrollment")
@AllArgsConstructor
public class EnrollmentController {

//    private final EnrollmentService enrollmentService;
//    private final JWTService jwtService;
//
//    @PostMapping("/request/{courseId}")
//    public ResponseEntity<?> sendEnrollRequest(
//            @RequestHeader(value = "Authorization", required = false) String authHeader,
//            @PathVariable Long courseId
//    ) {
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                    .body("Token missing or invalid format");
//        }
//
//        try {
//            String token = authHeader.substring(7);
//            String username = jwtService.extractUsername(token);
//            Long cursantId = jwtService.extractUserId(token);
//
//            if (username == null || cursantId == null) {
//                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                        .body("Invalid token");
//            }
//
//            enrollmentService.sendEnrollRequest(cursantId, courseId);
//            return ResponseEntity.ok("Enrollment request sent successfully.");
//
//        } catch (RuntimeException e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
//    }
//
//    @PutMapping("/respond/{cursantId}/{courseId}")
//    public ResponseEntity<?> respondToEnrollmentRequest(
//            @RequestHeader("Authorization") String authHeader,
//            @PathVariable Long cursantId,
//            @PathVariable Long courseId,
//            @RequestBody ResponseDTO request
//    ) {
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                    .body("Token missing or invalid format");
//        }
//
//        try {
//            String token = authHeader.substring(7);
//            Long mentorId = jwtService.extractUserId(token);
//
//            if (mentorId == null) {
//                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                        .body("Invalid token");
//            }
//            enrollmentService.updateEnrollmentStatus(mentorId, cursantId, courseId, request.getStatus());
//            return ResponseEntity.ok("Enrollment status updated successfully.");
//
//        } catch (RuntimeException e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
//    }
//
//    @GetMapping("/users/enrolled/{courseId}")
//    public List<UserViewDTO> getUsersEnrolledInCourse(@PathVariable Long courseId) {
//        return enrollmentService.findCursantByCourseId(courseId);
//    }

}
