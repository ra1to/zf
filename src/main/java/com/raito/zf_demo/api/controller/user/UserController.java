package com.raito.zf_demo.api.controller.user;

import com.raito.zf_demo.api.dto.CreateUserCmd;
import com.raito.zf_demo.api.vo.Res;
import com.raito.zf_demo.api.vo.Token;
import com.raito.zf_demo.application.user.handler.UserHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * @author raito
 * @since 2024/09/06
 */
@Tag(name = "user-api")
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserHandler userHandler;

    @Operation(summary = "用户登录")
    @GetMapping("/login")
    public Res<Token> login(@RequestParam("username") String username, @RequestParam("password") String password) {
        return Res.ok(userHandler.login(username, password));
    }

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Res<Token> register(@Valid @RequestBody CreateUserCmd cmd) {
        return Res.ok(userHandler.register(cmd));
    }
}
