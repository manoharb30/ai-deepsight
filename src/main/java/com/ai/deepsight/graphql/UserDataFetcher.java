package com.ai.deepsight.graphql;

import com.ai.deepsight.model.User;
import com.ai.deepsight.service.UserService;
import com.ai.deepsight.repository.UserRepository;
import com.ai.deepsight.dto.AuthPayload;
import com.netflix.graphql.dgs.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@DgsComponent
public class UserDataFetcher {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @DgsQuery
    public User getUserByEmail(@InputArgument String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    @DgsQuery
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @DgsMutation
    public User createUser(@InputArgument String name, @InputArgument String email, @InputArgument String password) {
        return userService.createUser(name, email, password);
    }

    @DgsMutation
    public Boolean verifyEmail(@InputArgument String token) {
        return userService.verifyEmail(token);
    }

    @DgsMutation
    public AuthPayload login(@InputArgument String email, @InputArgument String password) {
        return userService.loginUser(email, password);  // Now returns AuthPayload directly
    }
}
