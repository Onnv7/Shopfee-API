package com.hcmute.shopfee.security.custom.employee;

import com.hcmute.shopfee.security.UserPrincipal;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class EmployeeUsernamePasswordAuthenticationToken extends UsernamePasswordAuthenticationToken
{

    private UserPrincipal principal;

    @Override
    public UserPrincipal getPrincipal() {
        return principal;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    public EmployeeUsernamePasswordAuthenticationToken(UserPrincipal principal)
    {
        super(principal.getUsername(), principal.getPassword());
        this.principal = principal;
    }

    public EmployeeUsernamePasswordAuthenticationToken(UserPrincipal principal, Object credentials,
                                                       Collection<? extends GrantedAuthority> authorities)
    {
        super(principal.getUsername(), credentials, authorities);
        this.principal = principal;
    }
}