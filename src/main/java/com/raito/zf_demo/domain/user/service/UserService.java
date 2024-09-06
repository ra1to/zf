package com.raito.zf_demo.domain.user.service;

import com.raito.zf_demo.domain.user.entity.User;
import org.springframework.stereotype.Service;

/**
 * @author raito
 * @since 2024/09/06
 */
@Service
public interface UserService {
    User findByUsername(String username);

    User create(User user);
}
