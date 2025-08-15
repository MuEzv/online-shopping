package com.project.accountservice.config;

import com.project.accountservice.entity.Role;
import com.project.accountservice.repository.RoleRepository;
import com.project.accountservice.security.JwtAuthenticationFilter;
import com.project.accountservice.security.OwnershipChecker;
import com.project.accountservice.service.AccountService;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean("ownershipChecker")
    public OwnershipChecker ownershipChecker(AccountService accountService) {
        return new OwnershipChecker(accountService);
    }

    @Bean
    ApplicationRunner seedRoles(RoleRepository roleRepo) {
        return args -> {
            roleRepo.findByName("ROLE_USER")
                    .orElseGet(() -> roleRepo.save(new Role(null, "ROLE_USER")));
            roleRepo.findByName("ROLE_ADMIN")
                    .orElseGet(() -> roleRepo.save(new Role(null, "ROLE_ADMIN")));
        };
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers("/account/auth/login", "/account/auth/register").permitAll()
                .antMatchers("/account/**").hasAnyRole("USER", "ADMIN")
                .antMatchers("/orders/**", "/payments/**", "/items/**").hasAnyRole("USER", "ADMIN")
                .anyRequest().authenticated()
                .and()
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}