package com.hcmute.shopfee.security.custom.employee;

import com.hcmute.shopfee.security.UserPrincipal;
import com.hcmute.shopfee.security.custom.user.UserUsernamePasswordAuthenticationToken;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmployeeCustomAuthenticationProvider implements AuthenticationProvider
{
    private final CustomEmployeeDetailsService employeeDetailsService;
    @Autowired
    @Lazy
    private PasswordEncoder passwordEncoder;
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException
    {
        String username = ((UserPrincipal) authentication.getPrincipal()).getUsername();
        String password = ((UserPrincipal) authentication.getPrincipal()).getPassword();

        if(username == null || password == null) {
            throw new BadCredentialsException("No pre-authenticated credentials found in request.");
        }
        UserPrincipal emplPrincipal = (UserPrincipal) employeeDetailsService.loadUserByUsername(username);
        if(passwordEncoder.matches(password, emplPrincipal.getPassword())) {
            // FIXME: Sao lao dung USER?
            return new UserUsernamePasswordAuthenticationToken(emplPrincipal, emplPrincipal.getPassword(), emplPrincipal.getAuthorities());
        }
        else {
            throw new BadCredentialsException("Authentication failed");
        }
    }

    @Override
    public boolean supports(Class<?> authentication)
    {
        return authentication.equals(EmployeeUsernamePasswordAuthenticationToken.class);
    }
}