package com.ai.deepsight.service;

import com.ai.deepsight.dto.AuthPayload;
import com.ai.deepsight.model.User;
import com.ai.deepsight.repository.UserRepository;
import com.ai.deepsight.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class UserServiceTests {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private JwtUtil jwtUtil;
    private UserService userService;

    @BeforeEach
    public void setup() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        jwtUtil = mock(JwtUtil.class);
        userService = new UserService(userRepository, passwordEncoder, jwtUtil);
    }

    @Test
    public void shouldCreateUserSuccessfully() {
        String email = "newuser@example.com";
        String rawPassword = "password123";
        String encodedPassword = "hashed123";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);

        User createdUser = userService.createUser("New User", email, rawPassword);

        assertThat(createdUser.getEmail()).isEqualTo(email);
        assertThat(createdUser.getPassword()).isEqualTo(encodedPassword);
        assertThat(createdUser.getVerificationToken()).isNotBlank();
        assertThat(createdUser.getVerificationTokenExpiry()).isAfter(LocalDateTime.now());
    }

    @Test
    public void shouldThrowExceptionWhenEmailAlreadyExists() {
        String email = "existing@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(new User()));

        assertThrows(RuntimeException.class, () ->
                userService.createUser("Name", email, "pass")
        );
    }

    @Test
    public void shouldVerifyEmailSuccessfully() {
        String token = "valid-token";
        User user = new User();
        user.setVerificationToken(token);
        user.setVerificationTokenExpiry(LocalDateTime.now().plusHours(1));
        user.setEmail("user@example.com");

        when(userRepository.findByVerificationToken(token)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);

        boolean result = userService.verifyEmail(token);
        assertThat(result).isTrue();
        assertThat(user.getIsEmailVerified()).isTrue();
        assertThat(user.getIsActive()).isTrue();
    }

    @Test
    public void shouldFailVerificationForInvalidToken() {
        String token = "invalid-token";
        when(userRepository.findByVerificationToken(token)).thenReturn(Optional.empty());

        boolean result = userService.verifyEmail(token);
        assertThat(result).isFalse();
    }

    @Test
    public void shouldLoginSuccessfully() {
        String email = "user@example.com";
        String rawPassword = "password";
        String encodedPassword = "hashed123";
        String jwtToken = "mock-jwt-token";

        User user = new User();
        user.setEmail(email);
        user.setPassword(encodedPassword);
        user.setIsEmailVerified(true);
        user.setIsActive(true);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(true);
        when(jwtUtil.generateToken(email)).thenReturn(jwtToken);
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);

        AuthPayload result = userService.loginUser(email, rawPassword);

        assertThat(result).isNotNull();
        assertThat(result.getToken()).isEqualTo(jwtToken);
        assertThat(result.getUser().getEmail()).isEqualTo(email);
    }

    @Test
    public void shouldFailLoginWhenEmailNotVerified() {
        String email = "user@example.com";
        User user = new User();
        user.setEmail(email);
        user.setIsEmailVerified(false);
        user.setIsActive(true);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        assertThrows(RuntimeException.class, () ->
                userService.loginUser(email, "password")
        );
    }

    @Test
    public void shouldFailLoginWhenUserInactive() {
        String email = "user@example.com";
        User user = new User();
        user.setEmail(email);
        user.setIsEmailVerified(true);
        user.setIsActive(false);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        assertThrows(RuntimeException.class, () ->
                userService.loginUser(email, "password")
        );
    }

    @Test
    public void shouldFailLoginWithInvalidPassword() {
        String email = "user@example.com";
        String encodedPassword = "hashed123";

        User user = new User();
        user.setEmail(email);
        user.setPassword(encodedPassword);
        user.setIsEmailVerified(true);
        user.setIsActive(true);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", encodedPassword)).thenReturn(false);

        assertThrows(RuntimeException.class, () ->
                userService.loginUser(email, "wrong")
        );
    }
}
