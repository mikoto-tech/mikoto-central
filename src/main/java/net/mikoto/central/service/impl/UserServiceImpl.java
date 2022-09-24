package net.mikoto.central.service.impl;

import net.mikoto.central.repository.UserRepository;
import net.mikoto.central.service.UserService;
import net.mikoto.central.util.Sha256Util;
import net.mikoto.oauth2.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

import static net.mikoto.central.util.RandomString.getRandomString;
import static net.mikoto.central.util.Sha256Util.getSha256;

/**
 * @author mikoto
 * {@code @time} 2022/9/18
 * Create for mikoto-central
 */
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void register(String userName, String userPassword) {
        User user = new User();
        user.setCreateTime(new Date());
        user.setUserName(userName);
        user.setUserSalt(getRandomString(10));
        user.setUserPassword(
                getSha256(
                        userPassword +
                                "|MIKOTO_PIXIV_OAuth2|" +
                                user.getUserSalt() +
                                "|LOVE YOU FOREVER, Lin."
                )
        );
        user.setUpdateTime(new Date());
        userRepository.saveAndFlush(user);
    }

    @Override
    public User get(String userName) {
        return userRepository.getUserByUserName(userName);
    }

    /**
     * Get the user by user id.
     *
     * @param userId The id of the user.
     * @return The user.
     */
    @Override
    public User get(int userId) {
        return userRepository.getUserByUserId(userId);
    }

    @Override
    public boolean verify(User user, String userRawPassword) {
        if (user != null) {
            String userPassword = Sha256Util.getSha256(userRawPassword +
                    "|MIKOTO_PIXIV_OAuth2|" +
                    user.getUserSalt() +
                    "|LOVE YOU FOREVER, Lin.");
            return user.getUserPassword().equals(userPassword);
        }
        return false;
    }
}
