package com.raito.zf_demo.application.user.handler;

import com.raito.zf_demo.api.dto.CreateUserCmd;
import com.raito.zf_demo.api.vo.Token;
import com.raito.zf_demo.application.user.validator.UserValidatorFactory;
import com.raito.zf_demo.domain.user.entity.User;
import com.raito.zf_demo.domain.user.service.UserService;
import com.raito.zf_demo.infrastructure.factory.ChainFactory;
import com.raito.zf_demo.infrastructure.factory.Wrapper;
import com.raito.zf_demo.infrastructure.jwt.JwtConfig;
import com.raito.zf_demo.infrastructure.jwt.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author raito
 * @since 2024/09/06
 */
@Service
@RequiredArgsConstructor
public class UserHandler {
    private final UserService userService;
    private final JwtConfig config;

    public Token login(String username, String password) {
        Wrapper<User> wrapper = new Wrapper<>();
        Wrapper<Token> token = new Wrapper<>();
        ChainFactory.create()
                .validator(UserValidatorFactory.create(username, password), "用户信息格式不正确")
                .executor(() -> wrapper.setData(userService.findByUsername(username)))
                .validator(() -> wrapper.getData().validatePassword(password), "用户名或密码不正确")
                .executor(() -> token.setData(new Token(config.getHeader(), JwtUtils.createToken(wrapper.getData()))))
                .execute();
        return token.getData();
    }

    public Token register(CreateUserCmd cmd) {
        final Wrapper<User> wrapper = new Wrapper<>();
        final Wrapper<Token> token = new Wrapper<>();
        ChainFactory.create()
                .validator(UserValidatorFactory.create(cmd.username(), cmd.password(), cmd.email()), "用户信息格式不正确")
                .executor(() -> wrapper.setData(userService.create(User.builder()
                        .username(cmd.username())
                        .password(cmd.password())
                        .email(cmd.email())
                        .build())
                ))
                .executor(() -> token.setData(new Token(config.getHeader(), JwtUtils.createToken(wrapper.getData()))))
                .execute();
        return token.getData();
    }
}
