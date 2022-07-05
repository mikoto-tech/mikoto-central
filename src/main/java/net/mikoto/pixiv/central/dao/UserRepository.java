package net.mikoto.pixiv.central.dao;

import net.mikoto.pixiv.core.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author mikoto
 * Created at 2022/7/5, 下午6:19
 * For pixiv-central
 */
@Repository("userRepository")
public interface UserRepository extends JpaRepository<User, Integer> {
}
