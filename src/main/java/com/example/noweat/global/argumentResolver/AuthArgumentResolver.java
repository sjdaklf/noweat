package com.example.noweat.global.argumentResolver;

import com.example.noweat.domain.user.UserRole;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class AuthArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        if(parameter.getParameterType().equals(AuthUser.class)){
            return true;
        }

        return false;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest httpServletRequest = (HttpServletRequest)webRequest.getNativeRequest();

        Long id = Long.parseLong((String)httpServletRequest.getAttribute("id"));
        String email = (String)httpServletRequest.getAttribute("email");
        UserRole userRole = UserRole.of((String)httpServletRequest.getAttribute("userRole"));

        return AuthUser.builder()
                .id(id)
                .email(email)
                .userRole(userRole)
                .build();
    }
}
