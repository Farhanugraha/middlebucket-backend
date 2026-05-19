package com.middle_bucket.middlebucket.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    private static final List<String> EXCLUDED_PATHS = List.of(
            "/api/auth/login",
            "/api/auth/register",
            "/test"
    );

    public JwtFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();

        if ("OPTIONS".equalsIgnoreCase(method)) return true;

        return EXCLUDED_PATHS.stream().anyMatch(path::startsWith)
                || path.startsWith("/uploads/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            String email = jwtUtil.getEmailFromToken(token);
            String role = jwtUtil.getRoleFromToken(token);

            System.out.println("=== JWT Filter Debug ===");
            System.out.println("Email: " + email);
            System.out.println("Role from token: " + role);
            System.out.println("Request URI: " + request.getRequestURI());

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));

                System.out.println("Authorities set: " + authorities);

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(email, null, authorities);
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);

                System.out.println("Authentication set successfully for: " + email);
            }

            chain.doFilter(request, response);

        } catch (Exception e) {
            System.err.println("JWT Filter error: " + e.getMessage());
            e.printStackTrace();
            chain.doFilter(request, response);
        }
    }
}