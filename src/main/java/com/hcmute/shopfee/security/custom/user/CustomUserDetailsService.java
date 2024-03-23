package com.hcmute.shopfee.security.custom.user;

import com.hcmute.shopfee.constant.ErrorConstant;
import com.hcmute.shopfee.entity.sql.database.UserEntity;
import com.hcmute.shopfee.security.UserPrincipal;
import com.hcmute.shopfee.service.core.impl.UserService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserService userService;

    @SneakyThrows
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userService.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException(ErrorConstant.USER_NOT_FOUND));


        List<String> roleNames = user.getRoleList()
                .stream()
                .map(it -> it.getRoleName().name())
                .toList();

        List<SimpleGrantedAuthority> authorities = roleNames.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        return UserPrincipal.builder()
                .userId(user.getId())
                .username(user.getEmail())
                .authorities(authorities)
                .password(user.getPassword())
                .build();
    }
}
