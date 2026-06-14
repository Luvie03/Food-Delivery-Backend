package com.fooddelivery.auth_service.config;

import com.fooddelivery.auth_service.security.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;

    public JwtAuthFilter(JwtUtil jwtUtil){
        this.jwtUtil=jwtUtil;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
            final String authHeader=request.getHeader("Authorization");
            final String jwt;
            final String userEmail;

            if (authHeader==null || !authHeader.startsWith("Bearer ")){
                filterChain.doFilter(request,response);
                return;
            }
            jwt=authHeader.substring(7);
            try {
                userEmail=jwtUtil.extractUsername(jwt);
                if(userEmail!=null && SecurityContextHolder.getContext().getAuthentication()==null){
                    String role=jwtUtil.extractClaim(jwt,claims -> claims.get("role",String.class));
                    if(jwtUtil.isTokenValid(jwt,userEmail)){
                        UsernamePasswordAuthenticationToken authToken=new UsernamePasswordAuthenticationToken(
                                userEmail,
                                null,
                                Collections.singletonList(new SimpleGrantedAuthority("ROLE_"+role))
                        );
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                }
            }catch (Exception e){
                // Log the exception or handle it as needed
                System.out.println("JWT validation failed: " + e.getMessage());
                SecurityContextHolder.clearContext();
            }
        filterChain.doFilter(request,response);
    }
}
