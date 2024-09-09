package com.raito.zf_demo.infrastructure.jwt;

import com.raito.zf_demo.api.vo.Res;
import com.raito.zf_demo.infrastructure.util.JsonUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * @author raito
 * @since 2024/09/06
 */
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtConfig config;
    private final static String[] IGNORE_URLS = {
            "/api/user/login",
            "/api/pay/wx/notify",
            "/api/user/register",
            "/static",
            "/webjars",
            "/swagger-resources",
            "/v3/api-docs",
            "/doc.html",
            "/swagger-ui",
            "/static/favicon.ico"
    };
    @Override
    protected void doFilterInternal(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
        String url = request.getRequestURI();
        for (String ignoreUrl : IGNORE_URLS) {
            if (url.startsWith(ignoreUrl)) {
                filterChain.doFilter(request, response);
                return;
            }
        }
        String token = request.getHeader(config.getHeader());
        if (token != null && token.startsWith("Bearer")) {
            String authToken = token.substring(7);
            if (new JwtValidator(authToken).validate()) {
                filterChain.doFilter(request, response);
                return;
            }
        }
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(JsonUtils.toJson(Res.fail("token无效")));
    }
}
