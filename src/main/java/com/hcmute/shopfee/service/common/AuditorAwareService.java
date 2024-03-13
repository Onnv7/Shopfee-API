package com.hcmute.shopfee.service.common;

import com.hcmute.shopfee.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuditorAwareService implements AuditorAware<String> {
    @Override
    public Optional<String> getCurrentAuditor() {
        String clientId = null;

        try {
            clientId = SecurityUtils.getCurrentUserId();
        } catch (ClassCastException e) {

        }
        // TODO: nhớ xóa TestID
        if (clientId != null) {
            return Optional.of(clientId);
        } else {
            return Optional.empty();
        }
    }
}
