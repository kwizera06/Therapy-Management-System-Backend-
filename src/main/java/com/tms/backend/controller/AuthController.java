package com.tms.backend.controller;

import java.util.List;
import java.util.UUID;
import java.util.Random;
import java.util.stream.Collectors;

import jakarta.validation.Valid;
import jakarta.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.tms.backend.dto.JwtResponse;
import com.tms.backend.dto.LoginRequest;
import com.tms.backend.dto.MessageResponse;
import com.tms.backend.dto.SignupRequest;
import com.tms.backend.model.Role;
import com.tms.backend.model.User;
import com.tms.backend.model.PasswordResetToken;
import com.tms.backend.model.TwoFactorToken;
import com.tms.backend.repository.UserRepository;
import com.tms.backend.repository.PasswordResetTokenRepository;
import com.tms.backend.repository.TwoFactorTokenRepository;
import com.tms.backend.security.JwtUtils;
import com.tms.backend.security.UserDetailsImpl;
import com.tms.backend.service.EmailService;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
  private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
  
  @Autowired
  AuthenticationManager authenticationManager;

  @Autowired
  UserRepository userRepository;

  @Autowired
  PasswordEncoder encoder;

  @Autowired
  JwtUtils jwtUtils;
  
  @Autowired
  EmailService emailService;
  
  @Autowired
  PasswordResetTokenRepository passwordResetTokenRepository;
  
  @Autowired
  TwoFactorTokenRepository twoFactorTokenRepository;

  @PostMapping("/signin")
  public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
    try {
      logger.info("Login attempt for email: {}", loginRequest.getEmail());
      
      Authentication authentication = authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
      
      logger.info("Authentication successful for email: {}", loginRequest.getEmail());

      UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();    
      User user = userRepository.findById(userDetails.getId()).orElseThrow();
      
      // Check if 2FA is enabled
      if (user.isTwoFactorEnabled()) {
        // Generate 6-digit code
        String code = String.format("%06d", new Random().nextInt(999999));
        
        // Delete any existing tokens for this user
        twoFactorTokenRepository.deleteByUser(user);
        
        // Create and save new token
        TwoFactorToken token = new TwoFactorToken(code, user);
        twoFactorTokenRepository.save(token);
        
        // Send email
        logger.info("Sending 2FA code to: {}", user.getEmail());
        emailService.send2FACode(user.getEmail(), code, user.getFullName());
        logger.info("2FA code sent successfully to: {}", user.getEmail());
        
        // Return response indicating 2FA is required
        Map<String, Object> response = new HashMap<>();
        response.put("requires2FA", true);
        response.put("email", user.getEmail());
        response.put("message", "2FA code sent to your email");
        
        return ResponseEntity.ok(response);
      }
      
      // No 2FA, proceed with normal login
      SecurityContextHolder.getContext().setAuthentication(authentication);
      String jwt = jwtUtils.generateJwtToken(authentication);
      
      List<String> roles = userDetails.getAuthorities().stream()
          .map(item -> item.getAuthority())
          .collect(Collectors.toList());
      
      logger.info("Login successful for user: {} with role: {}", user.getEmail(), user.getRole());

      return ResponseEntity.ok(new JwtResponse(jwt, 
                           userDetails.getId(), 
                           userDetails.getEmail(),
                           user.getFullName(),
                           roles));
    } catch (Exception e) {
      logger.error("Authentication failed for email: {} - Error: {}", loginRequest.getEmail(), e.getMessage());
      e.printStackTrace(); // Log full stack trace
      return ResponseEntity.status(401).body(new MessageResponse("Error: Invalid email or password"));
    }
  }

  @PostMapping("/signup")
  public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
    if (userRepository.existsByEmail(signUpRequest.getEmail())) {
      return ResponseEntity
          .badRequest()
          .body(new MessageResponse("Error: Email is already in use!"));
    }

    // Create new user's account
    User user = new User();
    user.setFullName(signUpRequest.getFullName());
    user.setEmail(signUpRequest.getEmail());
    user.setPassword(encoder.encode(signUpRequest.getPassword()));
    
    // Set role
    if (signUpRequest.getRole() != null && signUpRequest.getRole().contains("therapist")) {
        user.setRole(Role.THERAPIST);
        user.setSpecialization(signUpRequest.getSpecialization());
        user.setQualifications(signUpRequest.getQualifications());
        user.setVerified(false); // Admin must verify therapists
    } else if (signUpRequest.getRole() != null && signUpRequest.getRole().contains("admin")) {
        user.setRole(Role.ADMIN);
    } else {
        user.setRole(Role.CLIENT);
        user.setPhoneNumber(signUpRequest.getPhoneNumber());
    }

    userRepository.save(user);

    return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
  }
  
  // Password Reset Endpoints
  @PostMapping("/forgot-password")
  public ResponseEntity<?> forgotPassword(@RequestParam String email) {
    try {
      User user = userRepository.findByEmail(email)
          .orElseThrow(() -> new RuntimeException("User not found"));
      
      // Generate unique token
      String token = UUID.randomUUID().toString();
      
      // Delete any existing tokens for this user
      passwordResetTokenRepository.deleteByUser(user);
      
      // Create and save new token
      PasswordResetToken resetToken = new PasswordResetToken(token, user);
      passwordResetTokenRepository.save(resetToken);
      
      // Send email
      logger.info("Sending password reset email to: {}", user.getEmail());
      emailService.sendPasswordResetEmail(user.getEmail(), token);
      logger.info("Password reset email sent successfully to: {}", user.getEmail());
      
      return ResponseEntity.ok(new MessageResponse("Password reset link sent to your email"));
    } catch (Exception e) {
      logger.error("Failed to send password reset email to: {} - Error: {}", email, e.getMessage());
      e.printStackTrace();
      // Don't reveal if email exists or not for security
      return ResponseEntity.ok(new MessageResponse("If the email exists, a password reset link has been sent"));
    }
  }
  
  @PostMapping("/reset-password")
  @Transactional
  public ResponseEntity<?> resetPassword(@RequestParam String token, @RequestParam String newPassword) {
    try {
      PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
          .orElseThrow(() -> new RuntimeException("Invalid token"));
      
      if (resetToken.isExpired()) {
        return ResponseEntity.badRequest().body(new MessageResponse("Token has expired"));
      }
      
      if (resetToken.isUsed()) {
        return ResponseEntity.badRequest().body(new MessageResponse("Token has already been used"));
      }
      
      User user = resetToken.getUser();
      user.setPassword(encoder.encode(newPassword));
      userRepository.save(user);
      
      resetToken.setUsed(true);
      passwordResetTokenRepository.save(resetToken);
      
      return ResponseEntity.ok(new MessageResponse("Password reset successfully"));
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(new MessageResponse("Invalid or expired token"));
    }
  }
  
  // 2FA Endpoints
  @PostMapping("/verify-2fa")
  public ResponseEntity<?> verify2FA(@RequestParam String email, @RequestParam String code) {
    try {
      User user = userRepository.findByEmail(email)
          .orElseThrow(() -> new RuntimeException("User not found"));
      
      TwoFactorToken token = twoFactorTokenRepository.findByUserAndVerifiedFalse(user)
          .orElseThrow(() -> new RuntimeException("No pending 2FA token"));
      
      if (token.isExpired()) {
        return ResponseEntity.badRequest().body(new MessageResponse("Code has expired. Please request a new one"));
      }
      
      if (token.isMaxAttemptsReached()) {
        return ResponseEntity.badRequest().body(new MessageResponse("Maximum attempts reached. Please request a new code"));
      }
      
      if (!token.getCode().equals(code)) {
        token.incrementAttempts();
        twoFactorTokenRepository.save(token);
        return ResponseEntity.badRequest().body(new MessageResponse("Invalid code. Attempts remaining: " + (3 - token.getAttempts())));
      }
      
      // Code is valid
      token.setVerified(true);
      twoFactorTokenRepository.save(token);
      
      // Generate JWT using UserDetailsImpl
      UserDetailsImpl userDetails = UserDetailsImpl.build(user);
      Authentication authentication = new UsernamePasswordAuthenticationToken(
          userDetails, null, userDetails.getAuthorities());
      SecurityContextHolder.getContext().setAuthentication(authentication);
      
      String jwt = jwtUtils.generateJwtToken(authentication);
      
      List<String> roles = userDetails.getAuthorities().stream()
          .map(item -> item.getAuthority())
          .collect(Collectors.toList());
      
      return ResponseEntity.ok(new JwtResponse(jwt, 
                           user.getId(), 
                           user.getEmail(),
                           user.getFullName(),
                           roles));
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(new MessageResponse("Verification failed: " + e.getMessage()));
    }
  }
  
  @PostMapping("/enable-2fa")
  public ResponseEntity<?> enable2FA(@RequestParam Long userId) {
    try {
      User user = userRepository.findById(userId)
          .orElseThrow(() -> new RuntimeException("User not found"));
      
      user.setTwoFactorEnabled(true);
      userRepository.save(user);
      
      return ResponseEntity.ok(new MessageResponse("2FA enabled successfully"));
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(new MessageResponse("Failed to enable 2FA"));
    }
  }
  
  @PostMapping("/disable-2fa")
  public ResponseEntity<?> disable2FA(@RequestParam Long userId) {
    try {
      User user = userRepository.findById(userId)
          .orElseThrow(() -> new RuntimeException("User not found"));
      
      user.setTwoFactorEnabled(false);
      userRepository.save(user);
      
      // Delete any pending tokens
      twoFactorTokenRepository.deleteByUser(user);
      
      return ResponseEntity.ok(new MessageResponse("2FA disabled successfully"));
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(new MessageResponse("Failed to disable 2FA"));
    }
  }
}
