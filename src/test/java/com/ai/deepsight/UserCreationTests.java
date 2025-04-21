package com.ai.deepsight;

import com.ai.deepsight.model.User;
import com.ai.deepsight.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class UserCreationTests {

    @Autowired
    private UserService userService;

    @Test
    void testCreateUserSetsVerificationFields() {
        String name = "Test User";
        String email = "test@example.com";
        String password = "secure123";

        User created = userService.createUser(name, email, password);

        assertThat(created.getVerificationToken()).isNotBlank();
        assertThat(created.getVerificationTokenExpiry()).isNotNull();
        assertThat(created.getIsEmailVerified()).isFalse();
    }
}
