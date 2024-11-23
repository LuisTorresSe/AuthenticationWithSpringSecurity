package com.app.SpringSecurityApp.config;

import com.app.SpringSecurityApp.persistence.UserEntity;
import com.app.SpringSecurityApp.persistence.UsersRepository;
import com.app.SpringSecurityApp.utils.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;


@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UsersRepository usersRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,   HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if (request.getServletPath().contains("/auth")
            )
        {
            filterChain.doFilter(request, response);
            return;
        }

        final String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Acceso denegado: se requiere un token válido.");
            return;
        }

        final String jwt = authorizationHeader.substring(7);
        final String username = jwtUtils.extractUsername(jwt);

        final UserEntity user = usersRepository.findByUsername(username).orElseThrow();

        final String stringAuthorities = jwtUtils.extractAuthorities(jwt);

        if(!jwtUtils.validateToken(jwt, user)){
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Token inválido o expirado.");
            return;
        }

        Collection<? extends GrantedAuthority> authorities = AuthorityUtils
                .commaSeparatedStringToAuthorityList(stringAuthorities);
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        Authentication auth = new UsernamePasswordAuthenticationToken(username, null, authorities);
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);
    }

}
