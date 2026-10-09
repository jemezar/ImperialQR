package com.imperial.qr.service;

import com.imperial.qr.domain.enums.Rol;
import com.imperial.qr.domain.model.Usuario;
import com.imperial.qr.dto.request.LoginRequest;
import com.imperial.qr.dto.response.LoginResponse;
import com.imperial.qr.exception.AccesoDenegadoException;
import com.imperial.qr.mapper.OrdenMapper;
import com.imperial.qr.repository.UsuarioRepository;
import com.imperial.qr.security.JwtService;
import com.imperial.qr.service.impl.UsuarioServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceImplTest {

    @Mock private UsuarioRepository usuarioRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtService jwtService;
    @Spy private OrdenMapper mapper = new OrdenMapper();

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    @Test
    @DisplayName("Autenticar con credenciales válidas genera JWT")
    void autenticar_credencialesValidas_retornaToken() {
        Usuario usuario = new Usuario("Admin", "admin@imperial.com", "hashBcrypt", Rol.ADMIN);
        usuario.setId(1L);

        when(usuarioRepository.findByEmail("admin@imperial.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("Imperial123*", "hashBcrypt")).thenReturn(true);
        when(jwtService.generarToken(usuario)).thenReturn("jwt-token-valido");

        LoginRequest req = new LoginRequest("admin@imperial.com", "Imperial123*");
        LoginResponse res = usuarioService.autenticar(req);

        assertNotNull(res);
        assertEquals("jwt-token-valido", res.token());
        assertEquals(Rol.ADMIN, res.rol());
    }

    @Test
    @DisplayName("Autenticar con clave inválida lanza AccesoDenegadoException")
    void autenticar_claveInvalida_lanzaAccesoDenegado() {
        Usuario usuario = new Usuario("Admin", "admin@imperial.com", "hashBcrypt", Rol.ADMIN);

        when(usuarioRepository.findByEmail("admin@imperial.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("clave_errada", "hashBcrypt")).thenReturn(false);

        LoginRequest req = new LoginRequest("admin@imperial.com", "clave_errada");
        assertThrows(AccesoDenegadoException.class, () -> usuarioService.autenticar(req));
    }
}
