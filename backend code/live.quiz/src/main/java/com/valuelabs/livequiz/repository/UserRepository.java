package com.valuelabs.livequiz.repository;

import com.valuelabs.livequiz.model.entity.User;
import com.valuelabs.livequiz.model.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
/**
 * The UserRepository interface provides database access operations for User entities.
 */
@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    /**
     * Finds all users by their role and inActive status.
     * @param role     The role of the users.
     * @param inActive The inActive status of the users.
     * @return List of users matching the criteria.
     */
    List<User> findAllByRoleAndInActive(Role role, Boolean inActive);
    /**
     * Finds a user by its userId and inActive status.
     * @param userId   The ID of the user.
     * @param inActive The inActive status of the user.
     * @return Optional containing the found user, or empty if not found.
     */
    Optional<User> findByUserIdAndInActive(Long userId, Boolean inActive);
    /**
     * Finds a user by its emailId and inActive status.
     * @param emailId  The emailId of the user.
     * @param inActive The inActive status of the user.
     * @return The found user, or null if not found.
     */
    User findByEmailIdAndInActive(String emailId, Boolean inActive);
}
