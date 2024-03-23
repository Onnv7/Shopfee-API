package com.hcmute.shopfee.security.custom.employee;

import com.hcmute.shopfee.entity.sql.database.EmployeeEntity;
import com.hcmute.shopfee.security.UserPrincipal;
import com.hcmute.shopfee.service.core.impl.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static com.hcmute.shopfee.constant.ErrorConstant.USER_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class CustomEmployeeDetailsService implements UserDetailsService {
    private final EmployeeService employeeService;

    @SneakyThrows
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        EmployeeEntity employee = employeeService.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND));


        List<String> roleNames = employee.getRoleList()
                .stream()
                .map(it -> it.getRoleName().name())
                .toList();

        List<SimpleGrantedAuthority> authorities = roleNames.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        return UserPrincipal.builder()
                .userId(employee.getId())
                .username(employee.getUsername())
                .authorities(authorities)
                .password(employee.getPassword())
                .build();
    }
}
