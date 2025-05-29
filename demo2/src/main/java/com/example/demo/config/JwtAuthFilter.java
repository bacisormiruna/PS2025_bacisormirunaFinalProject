package com.example.demo.config;

import com.example.demo.service.JWTService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
//pentru frontend
//@Component
//public class JwtAuthFilter extends OncePerRequestFilter {
//
//    @Autowired
//    private JWTService jwtService;
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request,
//                                    HttpServletResponse response,
//                                    FilterChain filterChain) throws ServletException, IOException {
//
//        String authHeader = request.getHeader("Authorization");
//
//        if (authHeader != null && authHeader.startsWith("Bearer ")) {
//            String token = authHeader.substring(7);
//
//            try {
//                String username = jwtService.extractUsername(token);
//                Long userId = jwtService.extractUserId(token);
//                String role = jwtService.extractRoleName(token);
//
//                // Poți loga sau salva informațiile în request pentru a le folosi ulterior
//                request.setAttribute("userId", userId);
//                request.setAttribute("username", username);
//                request.setAttribute("role", role);
//
//                // Dacă vrei autentificare reală, setezi în SecurityContext aici
//
//            } catch (Exception e) {
//                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token");
//                return;
//            }
//        }
//
//        filterChain.doFilter(request, response);
//    }
//}

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JWTService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        String token = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            try {
                if (jwtService.validateToken(token)) {
                    System.out.println("Token valid în Microserviciul 2");
                    String username = jwtService.extractUsername(token);
                    Long userId = jwtService.extractUserId(token);
                    String userRole = jwtService.extractRoleName(token);
                    System.out.println("Username extras: " + username + " | UserId extras: " + userId+ " | UserRole extras: " + userRole);
                } else {
                    System.out.println("Token invalid");
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token invalid");
                    return;
                }
            } catch (Exception e) {
                System.out.println("Eroare la validarea tokenului: " + e.getMessage());
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token invalid sau expirat");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}

