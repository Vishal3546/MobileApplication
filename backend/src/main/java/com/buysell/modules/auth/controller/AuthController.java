package com.buysell.modules.auth.controller;

import com.buysell.common.dto.ApiResponse;
import com.buysell.modules.auth.dto.JwtResponse;
import com.buysell.modules.auth.dto.LoginRequest;
import com.buysell.modules.auth.dto.TokenRefreshRequest;
import com.buysell.modules.auth.dto.TokenRefreshResponse;
import com.buysell.modules.auth.service.RefreshTokenService;
import com.buysell.modules.user.repository.UserRepository;
import com.buysell.security.JwtUtils;
import com.buysell.security.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        String jwt = jwtUtils.generateJwtToken(userDetails);

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        String rawRefreshToken = UUID.randomUUID().toString();
        refreshTokenService.createRefreshToken(userDetails.getId(), rawRefreshToken);

        JwtResponse response = JwtResponse.builder()
                .token(jwt)
                .refreshToken(rawRefreshToken)
                .id(userDetails.getId())
                .username(userDetails.getUsername())
                .roles(roles)
                .permissions(roles)
                .build();

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshtoken(@Valid @RequestBody TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshToken -> {
                    refreshTokenService.deleteByToken(requestRefreshToken);

                    var user = userRepository.findById(refreshToken.getUserId()).orElseThrow();
                    UserDetailsImpl userDetails = UserDetailsImpl.build(user);

                    String newRawRefresh = UUID.randomUUID().toString();
                    refreshTokenService.createRefreshToken(refreshToken.getUserId(), newRawRefresh);
                    
                    String newJwt = jwtUtils.generateJwtToken(userDetails);

                    TokenRefreshResponse response = TokenRefreshResponse.builder()
                            .accessToken(newJwt)
                            .refreshToken(newRawRefresh)
                            .build();

                    return ResponseEntity.ok(ApiResponse.success(response));
                })
                .orElseGet(() -> ResponseEntity.badRequest().body(ApiResponse.error("Refresh token is expired or invalid!")));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            refreshTokenService.deleteByUserId(userDetails.getId());
        }
        return ResponseEntity.ok(ApiResponse.success(null, "Log out successful!"));
    }
}
