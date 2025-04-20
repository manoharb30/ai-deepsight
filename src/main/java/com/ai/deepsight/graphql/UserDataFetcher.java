package com.ai.deepsight.graphql;

import com.ai.deepsight.model.User;
import com.ai.deepsight.repository.UserRepository;
import com.netflix.graphql.dgs.*;
import org.springframework.beans.factory.annotation.Autowired;

@DgsComponent
public class UserDataFetcher {

    @Autowired
    private UserRepository userRepository;

    @DgsQuery
    public User getUserByEmail(@InputArgument String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    @DgsMutation
    public User createUser(@InputArgument String name, @InputArgument String email) {
        User user = User.builder().name(name).email(email).build();
        return userRepository.save(user);
    }
}
