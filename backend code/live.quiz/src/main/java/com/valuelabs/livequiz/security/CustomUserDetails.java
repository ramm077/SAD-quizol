package com.valuelabs.livequiz.security;

import com.valuelabs.livequiz.model.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
/**
 * The CustomUserDetails class implements the UserDetails interface
 * to provide custom user details during authentication.
 */
@Slf4j
public class CustomUserDetails implements UserDetails {
    private final User user;
    public CustomUserDetails(User user){
        log.info("Inside CustomUserDetails, setting user!");
        this.user = user;
    }
    /**
     * Gets the authorities granted to the user.
     * @return Collection of GrantedAuthority objects.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        log.info("Inside CustomUserDetails, setting user!");
        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(user.getRole().name());
        return List.of(new SimpleGrantedAuthority(user.getRole().name()));
    }
    /**
     * Gets the user's password.
     * @return The user's password.
     */
    @Override
    public String getPassword() {
        log.debug("getting User's password!, confirming with database!");
        return user.getPassword();
    }
    /**
     * Gets the user's emailId.
     * @return The user's emailId.
     */
    @Override
    public String getUsername() {
        log.debug("getting User's emailId!, confirming with database!");
        return user.getEmailId();
    }
    /**
     * Checks if the user's account is not expired.
     * @return true if the account is not expired, false otherwise.
     */
    @Override
    public boolean isAccountNonExpired() {
        log.info("Account not expired, "+true);
        return true;
    }
    /**
     * Checks if the user's account is not locked.
     * @return true if the account is not locked, false otherwise.
     */
    @Override
    public boolean isAccountNonLocked() {
        log.info("Account is not locked, "+true);
        return true;
    }
    /**
     * Checks if the user's credentials are not expired.
     * @return true if the credentials are not expired, false otherwise.
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    /**
     * Checks if the user is enabled.
     * @return true if the user is enabled, false otherwise.
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}
