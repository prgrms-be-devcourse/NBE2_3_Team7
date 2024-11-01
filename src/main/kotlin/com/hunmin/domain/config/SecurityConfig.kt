package com.hunmin.domain.config

import com.hunmin.domain.jwt.CustomLogoutFilter
import com.hunmin.domain.jwt.JWTFilter
import com.hunmin.domain.jwt.JWTUtil
import com.hunmin.domain.jwt.LoginFilter
import com.hunmin.domain.repository.RefreshRepository
import com.hunmin.domain.service.MemberService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.authentication.logout.LogoutFilter
import org.springframework.web.cors.CorsConfiguration

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val authenticationConfiguration: AuthenticationConfiguration,
    private val jwtUtil: JWTUtil,
    private val refreshRepository: RefreshRepository
) {
    @Bean
    fun bCryptPasswordEncoder(): BCryptPasswordEncoder =
        BCryptPasswordEncoder()

    @Bean
    fun authenticationManager(): AuthenticationManager =
        authenticationConfiguration.authenticationManager

    // SecurityFilterChain 구성
    @Bean
    fun filterChain(http: HttpSecurity, memberService: MemberService): SecurityFilterChain {
        val loginFilter = LoginFilter(authenticationManager(), jwtUtil, refreshRepository).apply {
            setFilterProcessesUrl("/api/members/login")
        }

        http
            .cors { corsCustomizer ->
                corsCustomizer.configurationSource {
                    CorsConfiguration().apply {
                        allowedOrigins = listOf("http://localhost:3000")
                        allowedMethods = listOf("*")
                        allowCredentials = true
                        allowedHeaders = listOf("*")
                        maxAge = 3600L
                        exposedHeaders = listOf("Authorization")
                    }
                }
            }
            .csrf { it.disable() }
            .formLogin { it.disable() }
            .httpBasic { it.disable() }
            .addFilterBefore(loginFilter, UsernamePasswordAuthenticationFilter::class.java)
            .addFilterAfter(
                JWTFilter(jwtUtil, memberService),
                UsernamePasswordAuthenticationFilter::class.java
            )
            .addFilterBefore(
                CustomLogoutFilter(jwtUtil, refreshRepository),
                LogoutFilter::class.java
            )
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers("/api/members/register").permitAll()
                    .requestMatchers("/api/members/login").permitAll()
                    .requestMatchers("/api/members/reissue").permitAll()
                    .requestMatchers("/api/members/admin").hasRole("ADMIN")
                    .requestMatchers("/api/members/password/**").permitAll()
                    .requestMatchers("/webjars/**", "/images/**", "/favicon.ico").permitAll()
                    .requestMatchers("/api/notification/**").permitAll()
                    .requestMatchers("/api/board/uploadImage/**").permitAll()
                    .requestMatchers("uploads/**").permitAll()
                    .requestMatchers("api/members/uploads/**").permitAll()
                    .requestMatchers("/swagger-ui/**").permitAll()
                    .requestMatchers("/v3/api-docs/**").permitAll()
                    .requestMatchers("/swagger-ui.html").permitAll()
                    .requestMatchers("/api/notices/list/**").permitAll()
                    .anyRequest().permitAll()
            }
            .sessionManagement { session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
        return http.build()
    }
}

