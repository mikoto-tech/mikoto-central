package net.mikoto.central.service;

import net.mikoto.oauth2.model.User;

/**
 * @author mikoto
 * {@code @time} 2022/9/18
 * Create for mikoto-central
 */
public interface UserService {
    /**
     * Register a user by userName and userPassword.
     *
     * @param userName     The name of the user.
     * @param userPassword The password of the user.
     */
    void register(String userName, String userPassword);

    /**
     * Get the user by name.
     *
     * @param userName The name of the user.
     * @return The user.
     */
    User get(String userName);

    /**
     * Get the user by user id.
     *
     * @param userId The id of the user.
     * @return The user.
     */
    User get(int userId);

    /**
     * Verify if the user password is correct by userName and rawPassword.
     *
     * @param userName        The name of the user.
     * @param userRawPassword The password of the user.
     * @return To verify result.
     */
    default boolean verify(String userName, String userRawPassword) {
        return verify(get(userName), userRawPassword);
    }

    /**
     * Verify if the user password is correct by userId and rawPassword.
     *
     * @param userId          The id of the user.
     * @param userRawPassword The password of the user.
     * @return To verify result.
     */
    default boolean verify(int userId, String userRawPassword) {
        return verify(get(userId), userRawPassword);
    }

    /**
     * Verify if the user password is correct by user and rawPassword.
     *
     * @param user            The user object.
     * @param userRawPassword The password of the user.
     * @return To verify result.
     */
    boolean verify(User user, String userRawPassword);
}
