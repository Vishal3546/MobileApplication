package com.buysell.modules.auth.service;

import com.buysell.modules.auth.entity.RefreshToken;
import com.buysell.modules.auth.repository.RefreshTokenRepository;
import com.buysell.security.RefreshTokenHasher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    @Value("${jwt.refreshExpirationMs}")
    private Long refreshTokenDurationMs;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private RefreshTokenHasher refreshTokenHasher;

    public RefreshToken createRefreshToken(UUID userId, String rawToken) {
        String hashedToken = refreshTokenHasher.hash(rawToken);
        RefreshToken refreshToken = RefreshToken.builder()
                .id(hashedToken)
                .userId(userId)
                .expiration(refreshTokenDurationMs / 1000) // TTL in seconds for Redis
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    public Optional<RefreshToken> findByToken(String rawToken) {
        String hashedToken = refreshTokenHasher.hash(rawToken);
        return refreshTokenRepository.findById(hashedToken);
    }

    public void deleteByUserId(UUID userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }

    public void deleteByToken(String rawToken) {
        String hashedToken = refreshTokenHasher.hash(rawToken);
        refreshTokenRepository.deleteById(hashedToken);
    }
}
