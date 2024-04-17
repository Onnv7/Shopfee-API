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
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;


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

        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "https://admin-shopfee.netlify.app",
                "https://shopfee.netlify.app",
                "https://shopfee.surge.sh",
                "https://admin-shopfee.surge.sh",
                "http://localhost:3001")); //"http://localhost:3000", "http://tender-grackle-gratefully.ngrok-free.app", "https://tender-grackle-gratefully.ngrok-free.app"
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
                );

        return http.build();
    }
//    @Bean
//    public CorsFilter corsFilter() {
//        return new CorsFilter(corsConfigurationSource());
//    }

}
