package com.yourhotel.booking.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {
    
    private String name;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    private String password;
    private String phone;
    
    @Enumerated(EnumType.STRING)
    private Role role;
    
    @Builder.Default
    private Boolean enabled = true;
    
    @Builder.Default
    private Boolean emailVerified = false;
    
    @Builder.Default
    private Integer loyaltyPoints = 0;
    
    @Builder.Default
    private String tier = "BRONZE";
    private String resetPin;
    private java.time.LocalDateTime resetPinExpiry;

    public enum Role { USER, ADMIN }

    // 🚀 MANUAL GETTERS AND SETTERS (To fix Lombok issues)
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public Boolean getEnabled() { return enabled; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }

    public Boolean getEmailVerified() { return emailVerified; }
    public void setEmailVerified(Boolean emailVerified) { this.emailVerified = emailVerified; }

    public Integer getLoyaltyPoints() { return loyaltyPoints; }
    public void setLoyaltyPoints(Integer loyaltyPoints) { this.loyaltyPoints = loyaltyPoints; }

    public String getTier() { return tier; }
    public void setTier(String tier) { this.tier = tier; }
}
