package com.imperial.qr.service.impl;

import com.imperial.qr.domain.model.Usuario;
import com.imperial.qr.dto.request.LoginRequest;
import com.imperial.qr.dto.request.UsuarioRequest;
import com.imperial.qr.dto.response.LoginResponse;
import com.imperial.qr.dto.response.UsuarioResponse;
import com.imperial.qr.exception.AccesoDenegadoException;
import com.imperial.qr.exception.ConflictoNegocioException;
import com.imperial.qr.exception.RecursoNoEncontradoException;
import com.imperial.qr.mapper.OrdenMapper;
import com.imperial.qr.repository.UsuarioRepository;
import com.imperial.qr.security.JwtService;
import com.imperial.qr.service.UsuarioService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final OrdenMapper mapper;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository,
                              PasswordEncoder passwordEncoder,
                              JwtService jwtService,
                              OrdenMapper mapper) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.mapper = mapper;
    }

    @Override
    @Transactional(readOnly = true)
    public LoginResponse autenticar(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(request.email())
            .orElseThrow(() -> new AccesoDenegadoException("Credenciales inválidas"));

        if (!usuario.isActivo()) {
            throw new AccesoDenegadoException("El usuario está inactivo");
        }

        if (!passwordEncoder.matches(request.password(), usuario.getPasswordHash())) {
            throw new AccesoDenegadoException("Credenciales inválidas");
        }

        String token = jwtService.generarToken(usuario);
        return new LoginResponse(
            token,
            "Bearer",
            usuario.getId(),
            usuario.getNombre(),
            usuario.getEmail(),
            usuario.getRol()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioResponse> listarTodos() {
        return usuarioRepository.findAll().stream()
            .map(mapper::toUsuarioResponse)
            .toList();
    }

    @Override
    @Transactional
    public UsuarioResponse crear(UsuarioRequest request) {
        if (usuarioRepository.existsByEmail(request.email())) {
            throw new ConflictoNegocioException("Ya existe un usuario con el correo: " + request.email());
        }

        String password = (request.password() != null && !request.password().isBlank())
            ? request.password() : "Imperial123*";

        Usuario u = new Usuario(
            request.nombre(),
            request.email(),
            passwordEncoder.encode(password),
            request.rol()
        );
        if (request.activo() != null) u.setActivo(request.activo());

        return mapper.toUsuarioResponse(usuarioRepository.save(u));
    }

    @Override
    @Transactional
    public UsuarioResponse actualizar(Long id, UsuarioRequest request) {
        Usuario u = usuarioRepository.findById(id)
            .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con ID: " + id));

        u.setNombre(request.nombre());
        u.setEmail(request.email());
        u.setRol(request.rol());
        if (request.password() != null && !request.password().isBlank()) {
            u.setPasswordHash(passwordEncoder.encode(request.password()));
        }
        if (request.activo() != null) {
            u.setActivo(request.activo());
        }

        return mapper.toUsuarioResponse(usuarioRepository.save(u));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("Usuario no encontrado con ID: " + id);
        }
        usuarioRepository.deleteById(id);
    }
}
