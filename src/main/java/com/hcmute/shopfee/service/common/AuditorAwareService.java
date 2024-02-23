package com.hcmute.shopfee.service.common;

import com.hcmute.shopfee.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.AuditorAware;
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
        return Optional.of(clientId != null ? clientId : "TestID");
    }
}
