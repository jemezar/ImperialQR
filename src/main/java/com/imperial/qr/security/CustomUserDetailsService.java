package com.imperial.qr.security;

import com.imperial.qr.domain.model.Usuario;
import com.imperial.qr.repository.UsuarioRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario u = usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + email));

        if (!u.isActivo()) {
            throw new UsernameNotFoundException("Usuario inactivo");
        }

        return new User(
            u.getEmail(),
            u.getPasswordHash(),
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + u.getRol().name()))
        );
    }
}
