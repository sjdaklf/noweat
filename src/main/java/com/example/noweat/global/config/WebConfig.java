package com.example.noweat.global.config;

import com.example.noweat.global.argumentResolver.AuthArgumentResolver;
import com.example.noweat.global.interceptor.JwtInterceptor;
import com.example.noweat.global.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final JwtUtil jwtUtil;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new JwtInterceptor(jwtUtil))
                .addPathPatterns("/api/signout")
                .addPathPatterns("/api/users/**")
                .addPathPatterns("/api/stores/**")
                .addPathPatterns("/api/menus/**")
                .addPathPatterns("/api/orders/**")
                .addPathPatterns("/api/reviews/**");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new AuthArgumentResolver());
    }
}
