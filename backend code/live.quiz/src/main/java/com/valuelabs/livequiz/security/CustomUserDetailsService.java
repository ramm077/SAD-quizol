package com.valuelabs.livequiz.security;
import com.valuelabs.livequiz.model.entity.User;
import com.valuelabs.livequiz.service.user.DisplayUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
/**
 * The CustomUserDetailsService class implements the UserDetailsService interface
 * to load user details during authentication.
 */
@Slf4j
@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final DisplayUserService displayUserService;
    @Autowired
    public CustomUserDetailsService(DisplayUserService displayUserService) {
        this.displayUserService = displayUserService;
    }
    /**
     * Loads user details based on the provided emailId.
     * @param emailId The emailId of the user.
     * @return UserDetails representing the loaded user.
     * @throws UsernameNotFoundException If the user is not found.
     */
    @Override
    public UserDetails loadUserByUsername(String emailId) throws UsernameNotFoundException {
        log.info("Inside CustomUserDetailsService, loadUserByUsername method!");
        User user = displayUserService.getUserByEmailIdAndInActive(emailId,false);
        log.debug("User fetched successfully with username "+user.getUserName());
        return new CustomUserDetails(user);
    }
}