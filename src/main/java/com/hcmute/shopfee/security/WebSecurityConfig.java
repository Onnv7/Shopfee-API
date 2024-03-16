package com.hcmute.shopfee.security;

import com.hcmute.shopfee.security.custom.CustomAuthenticationManager;
import com.hcmute.shopfee.security.custom.MyAuthenticationFilter;
import com.hcmute.shopfee.security.custom.employee.EmployeeCustomAuthenticationProvider;
import com.hcmute.shopfee.security.custom.user.UserCustomAuthenticationProvider;
import com.hcmute.shopfee.security.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

import static com.hcmute.shopfee.constant.SecurityConstant.*;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(prePostEnabled = true) //EnableGlobalMethodSecurity
public class WebSecurityConfig {


    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final String ADMIN = "ADMIN";
    private final String EMPLOYEE = "EMPLOYEE";
    private final String USER = "USER";
    private final String MANAGER = "MANAGER";

//    @Autowired
//    private CustomOidcUserService customOidcUserService;


    @Autowired
    private EmployeeCustomAuthenticationProvider adminCustomAuthenticationProvider;

    @Autowired
    private UserCustomAuthenticationProvider userCustomAuthenticationProvider;

    @Bean
    public MyAuthenticationFilter myAuthenticationFilter() throws Exception {
        MyAuthenticationFilter authenticationFilter = new MyAuthenticationFilter();

        return authenticationFilter;
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(7);
    }

    @Bean
    ApplicationListener<AuthenticationSuccessEvent> doSomething() {
        return new ApplicationListener<AuthenticationSuccessEvent>() {
            @Override
            public void onApplicationEvent(AuthenticationSuccessEvent event) {
                Authentication authentication = event.getAuthentication();
                // get required details from OAuth2Authentication instance and proceed further
            }
        };
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        // Tự custom
        CustomAuthenticationManager authenticationManager = new CustomAuthenticationManager();
        authenticationManager.addProvider(adminCustomAuthenticationProvider);
        authenticationManager.addProvider(userCustomAuthenticationProvider);
        return authenticationManager;
        // Dùng mặc định không custom
//        return http.getSharedObject(AuthenticationManagerBuilder.class)
//                .authenticationProvider(adminCustomAuthenticationProvider)
//                .authenticationProvider(userCustomAuthenticationProvider)
//                .build();
    }
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowCredentials(true);
//        configuration.addAllowedOrigin(corsAllowedOrigin); // @Value: http://localhost:8080
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");

        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
        configuration.setAllowCredentials(true);


//        configuration.setAllowedMethods(Arrays.asList("GET","POST"));
//        configuration.addAllowedHeader(List.of("Authorization"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
    @Bean
    public SecurityFilterChain applicationSecurity(HttpSecurity http) throws Exception {
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(myAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
//        http.headers().httpStrictTransportSecurity().disable();
        // set route sẽ ăn từ trên xuống (ưu tiên cái đầu tiên)

        http
                .cors(Customizer.withDefaults())
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .formLogin().disable()
                .securityMatcher("/**")
                .authorizeHttpRequests(register -> register
                                .requestMatchers("/**").permitAll()
//                        .requestMatchers("/socket.io/**").permitAll()
//                        .requestMatchers("/socket.io").permitAll()

                                // ALL
                                .requestMatchers("/tool/**").permitAll()
                                .requestMatchers(HttpMethod.GET, GET_AUTH_WHITELIST).permitAll()
                                .requestMatchers(HttpMethod.POST, POST_AUTH_WHITELIST).permitAll()
                                .requestMatchers(HttpMethod.PATCH, PATCH_AUTH_WHITELIST).permitAll()

                                // Only USER
                                .requestMatchers(HttpMethod.GET, GET_USER_PATH).hasRole(USER)
                                .requestMatchers(HttpMethod.PATCH, PATCH_USER_PATH).hasRole(USER)
                                .requestMatchers(HttpMethod.PUT, PUT_USER_PATH).hasRole(USER)
                                .requestMatchers(HttpMethod.POST, POST_USER_PATH).hasRole(USER)
                                .requestMatchers(HttpMethod.DELETE, DELETE_USER_PATH).hasRole(USER)
                                // Only EMPLOYEE
                                .requestMatchers(HttpMethod.GET, GET_EMPLOYEE_PATH).hasRole(EMPLOYEE)
                                .requestMatchers(HttpMethod.PATCH, PATCH_EMPLOYEE_PATH).hasRole(EMPLOYEE)

                                // Only ADMIN
                                .requestMatchers(HttpMethod.GET, GET_ADMIN_PATH).hasRole(ADMIN)
                                .requestMatchers(HttpMethod.PUT, PUT_ADMIN_PATH).hasRole(ADMIN)
                                .requestMatchers(HttpMethod.PUT, PATCH_ADMIN_PATH).hasRole(ADMIN)
                                .requestMatchers(HttpMethod.POST, POST_ADMIN_PATH).hasRole(ADMIN)
                                .requestMatchers(HttpMethod.DELETE, DELETE_ADMIN_PATH).hasRole(ADMIN)

                                // Only GET_MANAGER_PATH
                                .requestMatchers(HttpMethod.GET, GET_MANAGER_PATH).hasRole(MANAGER)

                                // ADMIN + EMPLOYEE
                                .requestMatchers(HttpMethod.GET, GET_ADMIN_EMPLOYEE_PATH).hasAnyRole(ADMIN, EMPLOYEE)
                                .requestMatchers(HttpMethod.PUT, PUT_ADMIN_EMPLOYEE_PATH).hasAnyRole(ADMIN, EMPLOYEE)
                                .requestMatchers(HttpMethod.PATCH, PATCH_ADMIN_EMPLOYEE_PATH).hasAnyRole(ADMIN, EMPLOYEE)

                                // ADMIN + USER
                                .requestMatchers(HttpMethod.GET, GET_ADMIN_USER_PATH).hasAnyRole(ADMIN, USER)
                                .requestMatchers(HttpMethod.POST, POST_ADMIN_USER_PATH).hasAnyRole(ADMIN, USER)
                                .requestMatchers(HttpMethod.PUT, PUT_ADMIN_USER_PATH).hasAnyRole(ADMIN, USER)
                                .requestMatchers(HttpMethod.PATCH, PATCH_ADMIN_USER_PATH).hasAnyRole(ADMIN, USER)

                                // EMPLOYEE + USER
                                .requestMatchers(HttpMethod.GET, GET_EMPLOYEE_USER_PATH).hasAnyRole(EMPLOYEE, USER)
                                .requestMatchers(HttpMethod.POST, POST_EMPLOYEE_USER_PATH).hasAnyRole(EMPLOYEE, USER)
                                .requestMatchers(HttpMethod.PATCH, PATCH_EMPLOYEE_USER_PATH).hasAnyRole(EMPLOYEE, USER)

                                // EMPLOYEE + USER
                                .requestMatchers(HttpMethod.GET, GET_ADMIN_EMPLOYEE_USER_PATH).hasAnyRole(ADMIN, EMPLOYEE, USER)


                                // ADMIN + EMPLOYEE + MANAGER
                                .requestMatchers(HttpMethod.GET, GET_ADMIN_MANAGER_EMPLOYEE_PATH).hasAnyRole(ADMIN, MANAGER, EMPLOYEE)


                                // ADMIN + MANAGER
                                .requestMatchers(HttpMethod.GET, GET_ADMIN_MANAGER_PATH).hasAnyRole(ADMIN, MANAGER)
                                .requestMatchers(HttpMethod.PUT, PUT_ADMIN_MANAGER_PATH).hasAnyRole(ADMIN, MANAGER)
                                .requestMatchers(HttpMethod.DELETE, DELETE_ADMIN_MANAGER_PATH).hasAnyRole(ADMIN, MANAGER)
                                .anyRequest().authenticated()

                );

        return http.build();
    }

}
