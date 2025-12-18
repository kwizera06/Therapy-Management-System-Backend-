package com.tms.backend.dto;

import java.util.List;
import lombok.Data;

@Data
public class JwtResponse {
  private String token;
  private String type = "Bearer";
  private Long id;
  private String email;
  private String fullName;
  private List<String> roles;

  public JwtResponse(String accessToken, Long id, String email, String fullName, List<String> roles) {
    this.token = accessToken;
    this.id = id;
    this.email = email;
    this.fullName = fullName;
    this.roles = roles;
  }
}
