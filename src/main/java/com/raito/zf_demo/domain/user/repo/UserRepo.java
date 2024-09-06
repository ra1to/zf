package com.raito.zf_demo.domain.user.repo;

import com.raito.zf_demo.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author raito
 * @since 2024/09/06
 */
@Repository
public interface UserRepo extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
