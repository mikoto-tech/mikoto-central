package net.mikoto.central.dao;

import net.mikoto.oauth2.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author mikoto
 * Create for pixiv-central
 * Create at 2022/7/21
 */
@Repository("userRepository")
public interface UserRepository extends JpaRepository<User, Integer> {
    /**
     * Get the user by user's id.
     *
     * @param userId The user's id.
     * @return The user.
     */
    User getUserByUserId(int userId);

    /**
     * Get the user by user's name.
     *
     * @param userName The user's name.
     * @return The user.
     */
    User getUserByUserName(String userName);
}
