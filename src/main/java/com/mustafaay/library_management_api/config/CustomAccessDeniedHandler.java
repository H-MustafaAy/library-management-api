package com.mustafaay.library_management_api.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
// yetkisi olmayan kullanıcılara hata döndürüyorum.
    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException, ServletException {

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        //cevap jsan ve türkçe karakter içerecek
        response.setContentType("application/json;charset=UTF-8");

        String jsonResponse = """
                {
                  "timestamp": "%s",
                  "status": 403,
                  "error": "FORBIDDEN",
                  "message": "Bu işlem için yetkiniz yok.",
                  "path": "%s"
                }
                """.formatted(
                LocalDateTime.now(),
                request.getRequestURI()
        );

        response.getWriter().write(jsonResponse);
    }
}