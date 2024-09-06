package com.raito.zf_demo.domain.user.service;

import com.raito.zf_demo.domain.user.entity.User;
import com.raito.zf_demo.domain.user.repo.UserRepo;
import com.raito.zf_demo.infrastructure.exception.ExistException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author raito
 * @since 2024/09/06
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepo userRepo;
    @Override
    public User findByUsername(String username) {
        return userRepo.findByUsername(username);
    }

    @Override
    public User create(User user) {
        User exist = findByUsername(user.getUsername());
        if (exist != null) {
            throw new ExistException("用户名已存在");
        }
        return userRepo.save(user.encodePassword());
    }
}
