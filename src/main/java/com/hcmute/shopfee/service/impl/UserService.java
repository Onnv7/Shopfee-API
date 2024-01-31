package com.hcmute.shopfee.service.impl;

import com.hcmute.shopfee.entity.UserEntity;
import com.hcmute.shopfee.repository.database.UserRepository;
import com.hcmute.shopfee.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    private final UserRepository userRepository;

    public Optional<UserEntity> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
