package com.alfredoeka.spring_assignment_five.service.oauth;

import com.alfredoeka.spring_assignment_five.entity.oauth.Client;
import com.alfredoeka.spring_assignment_five.entity.oauth.User;
import com.alfredoeka.spring_assignment_five.repository.oauth.ClientRepository;
import com.alfredoeka.spring_assignment_five.repository.oauth.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public boolean validateClientCredentials(String clientId, String clientSecret) {
        Client client = clientRepository.findOneByClientId(clientId);
        return client != null && client.getClientSecret().equals(clientSecret);
    }

    public boolean validateUserCredentials(String username, String password) {
        User user = userRepository.findOneByUsername(username);
        return user != null && passwordEncoder.matches(password, user.getPassword());
    }
}
