package com.app.SpringSecurityApp.services;

import com.app.SpringSecurityApp.persistence.UserEntity;
import com.app.SpringSecurityApp.persistence.UsersRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = usersRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException(username)
        );

        List<SimpleGrantedAuthority> authorities = getAuthorities(user);

        return new User(
                user.getUsername(),
                user.getPassword(),
                user.getIsEnabled(),
                user.getIsAccountNonExpired(),
                user.getIsCredentialsNonExpired(),
                user.getIsAccountNonLocked(),
                authorities
        );
    }


    public Authentication authenticate(String username, String password) {
        UserDetails user = loadUserByUsername(username);

        if(user == null){
            throw new UsernameNotFoundException(username);
        }


        // aca se deberia decodificar la contraseña que esta
        // en la base de datos

        if(!passwordEncoder.matches(password, user.getPassword())){
            throw new BadCredentialsException("Wrong password");
        }

        return new UsernamePasswordAuthenticationToken(username, password, user.getAuthorities());

    }

    public List<SimpleGrantedAuthority> getAuthorities(UserEntity user) {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        user.getRoles().forEach(role-> authorities.add(new SimpleGrantedAuthority("ROLE_"+role.getRole().name())));
        user.getRoles().stream()
                .flatMap(role-> role.getPermissions().stream())
                .forEach(permission-> authorities.add(new SimpleGrantedAuthority(permission.getPermission())));

        return List.copyOf(authorities);
    }

}
