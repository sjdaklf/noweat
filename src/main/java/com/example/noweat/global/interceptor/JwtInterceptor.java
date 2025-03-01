package com.example.noweat.global.interceptor;

import com.example.noweat.global.jwt.JwtUtil;
import com.example.noweat.service.exception.UnauthorizedException;
import com.example.noweat.service.exception.enums.ErrorCode;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.servlet.HandlerInterceptor;

@RequiredArgsConstructor
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        String requestMethod = request.getMethod();
        String authorizationHeader = request.getHeader("Authorization");

        String[] strs = requestURI.split("/");

        if(strs[2].equals("stores") && requestMethod.equals("GET")){
            return HandlerInterceptor.super.preHandle(request, response, handler);
        }

        if(authorizationHeader == null){
            throw new UnauthorizedException(ErrorCode.MISSING_AUTHORIZATION_HEADER);
        }

        if(!authorizationHeader.startsWith("Bearer ")){
            throw new UnauthorizedException(ErrorCode.INVALID_BEARER_TOKEN);
        }

        String accessToken = authorizationHeader.substring(7);

        Claims claims = jwtUtil.getAccessTokenClaims(accessToken);
        request.setAttribute("id", claims.getSubject());
        request.setAttribute("email", claims.get("email"));
        request.setAttribute("userRole", claims.get("userRole"));

        return HandlerInterceptor.super.preHandle(request, response, handler);
    }
}
