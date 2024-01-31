package com.hcmute.shopfee.security.custom.user;

import com.hcmute.shopfee.security.UserPrincipal;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class UserUsernamePasswordAuthenticationToken extends UsernamePasswordAuthenticationToken {

    private UserPrincipal principal;

    @Override
    public UserPrincipal getPrincipal() {
        return principal;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    public UserUsernamePasswordAuthenticationToken(UserPrincipal principal) {
        super(principal.getUsername(), principal.getPassword());
        this.principal = principal;
    }

    public UserUsernamePasswordAuthenticationToken(UserPrincipal principal, Object credentials,
                                                   Collection<? extends GrantedAuthority> authorities) {

        super(principal.getUsername(), credentials, authorities);
        this.principal = principal;
    }

}
