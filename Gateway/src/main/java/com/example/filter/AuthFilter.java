package com.example.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class AuthFilter extends AbstractGatewayFilterFactory<AuthFilter.Config> {

    public AuthFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            // 获取请求头中的 Token
            String token = exchange.getRequest().getHeaders().getFirst("Authorization");

            // 模拟验证 Token
            if (token == null || !token.equals("valid-token")) {
                // 如果 Token 无效，拦截请求
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            // 如果 Token 有效，继续执行
            return chain.filter(exchange);
        };
    }

    public static class Config {
        // 可以在这里添加配置参数
    }
}