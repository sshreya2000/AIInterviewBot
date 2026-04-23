package com.company.aiinterview.auth.service.impl;

import com.company.aiinterview.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public abstract class AuthServiceImpl implements AuthService {
    // TODO: Inject dependencies: UserRepository, RoleRepository, JwtTokenProvider
}
