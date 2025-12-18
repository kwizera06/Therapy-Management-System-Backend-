package com.tms.backend.security;

import com.tms.backend.model.User;
import com.tms.backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);
    
    @Autowired
    UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        logger.info("Loading user by email: {}", email);
        
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.error("User not found with email: {}", email);
                    return new UsernameNotFoundException("User Not Found with email: " + email);
                });
        
        logger.info("User found: {} - Role: {} - Enabled: {} - Password starts with: {}", 
                    user.getEmail(), 
                    user.getRole(), 
                    user.isEnabled(),
                    user.getPassword() != null ? user.getPassword().substring(0, Math.min(10, user.getPassword().length())) : "null");

        return UserDetailsImpl.build(user);
    }
}
