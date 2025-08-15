package com.project.security;

import com.project.util.JwtUtil;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        System.out.println("✅ JwtAuthenticationFilter invoked");
        SecurityContextHolder.clearContext();

        String header = request.getHeader("Authorization");
        System.out.println("🧾 Authorization header: " + header);

        if (!StringUtils.hasText(header)) {
            chain.doFilter(request, response);
            return;
        }

        // ✅ Internal Token 分支
        if (header.startsWith("Internal ")) {
            String internalToken = header.substring(9); // Remove "Internal "
            if ("my-internal-secret-token".equals(internalToken)) {
                System.out.println("✅ Internal token authenticated");
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                "internal-service", null, List.of(new SimpleGrantedAuthority("ROLE_INTERNAL"))
                        );
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(auth);
                chain.doFilter(request, response);
                System.out.println("✅ Authorities: " + auth.getAuthorities());
                System.out.println("✅ Authorities on context: " + SecurityContextHolder.getContext().getAuthentication().getAuthorities());
                return;
            } else {
                System.out.println("❌ Invalid internal token");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid internal token");
                return;
            }
        }

        // ✅ Bearer Token 分支
        String token = header.startsWith("Bearer ") ? header.substring(7) : null;
        System.out.println("🔍 Has text in token: " + StringUtils.hasText(token));
        System.out.println("🔍 Existing Authentication: " + SecurityContextHolder.getContext().getAuthentication());

        if (StringUtils.hasText(token) && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                String accountId = JwtUtil.extractSubject(token);
                System.out.println("✅ Extracted accountId: " + accountId);

                if (JwtUtil.validateToken(token, accountId)) {
                    List<String> roles = JwtUtil.extractRoles(token);
                    List<GrantedAuthority> authorities = roles.stream()
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());

                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(accountId, null, authorities);
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                    System.out.println("✅ Authentication set: " + auth);
                } else {
                    System.out.println("❌ Invalid token for accountId: " + accountId);
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token");
                    return;
                }
            } catch (Exception e) {
                System.out.println("❌ JwtAuthenticationFilter caught exception: " + e.getMessage());
                e.printStackTrace();
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token parsing failed");
                return;
            }
        }

        chain.doFilter(request, response);
    }
}