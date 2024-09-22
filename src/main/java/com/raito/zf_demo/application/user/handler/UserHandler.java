package com.raito.zf_demo.application.user.handler;

import com.raito.zf_demo.api.dto.CreateUserCmd;
import com.raito.zf_demo.api.vo.Token;
import com.raito.zf_demo.application.user.validator.UserValidatorFactory;
import com.raito.zf_demo.domain.user.entity.User;
import com.raito.zf_demo.domain.user.service.UserService;
import com.raito.zf_demo.infrastructure.factory.HandlerFactory;
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
        return HandlerFactory.create()
                .validator(UserValidatorFactory.create(username, password), context -> "用户信息格式不正确")
                .executor(context -> context.set(userService.findByUsername(username)))
                .validator(context -> context.get(User.class).validatePassword(password), context -> "用户名或密码不正确")
                .executor(context -> context.set(new Token(config.getHeader(), JwtUtils.createToken(context.get(User.class)))))
                .execute()
                .get(Token.class);
    }

    public Token register(CreateUserCmd cmd) {
        return HandlerFactory.create()
                .validator(UserValidatorFactory.create(cmd.username(), cmd.password(), cmd.email()), context -> "用户信息格式不正确")
                .executor(context -> {
                    User user = userService.create(User.builder()
                            .username(cmd.username())
                            .password(cmd.password())
                            .email(cmd.email())
                            .build());
                    context.set(new Token(config.getHeader(), JwtUtils.createToken(user)));
                })
                .execute()
                .get(Token.class);
    }
}
