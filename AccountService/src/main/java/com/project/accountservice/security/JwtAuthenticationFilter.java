package com.project.accountservice.security;

import com.project.accountservice.util.JwtUtil;
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

        String token = (header != null && header.startsWith("Bearer ")) ? header.substring(7) : null;

        if (StringUtils.hasText(token) && SecurityContextHolder.getContext().getAuthentication() == null) {
            System.out.println("✅ Found token: " + token);

            try {
                // sub = accountId
                String accountId = JwtUtil.extractSubject(token);
                System.out.println("✅ Extracted accountId: " + accountId);

                if (JwtUtil.validateToken(token, accountId)) {
                    // ★ 从 JWT 里取 roles，并转换为 GrantedAuthority
                    List<String> roles = JwtUtil.extractRoles(token); // e.g. ["ROLE_USER","ROLE_ADMIN"]
                    System.out.println("✅ Extracted Roles: " + roles);

                    List<GrantedAuthority> authorities = roles.stream()
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());

                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(accountId, null, authorities);
                    System.out.println("✅ Authentication set: " + auth);

                    SecurityContextHolder.getContext().setAuthentication(auth);

                    // ✅ 打印当前登录用户的权限
                    System.out.println("✅ Authorities: " + auth.getAuthorities());
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                } else{
                    System.out.println("❌ Invalid token for accountId: " + accountId);
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token");
                    return; // 直接返回，避免继续处理请求
                }
            } catch (Exception e) {
                System.out.println("❌ JwtAuthenticationFilter caught exception: " + e.getMessage());
                e.printStackTrace(); // 打印完整栈追踪，排查问题关键
            }
        }

        chain.doFilter(request, response);
    }
}