package com.imperial.qr.controller;

import com.imperial.qr.dto.request.LoginRequest;
import com.imperial.qr.dto.response.LoginResponse;
import com.imperial.qr.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Autenticación", description = "Endpoints de inicio de sesión y emisión de JWT para personal del restaurante")
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/login")
    @Operation(summary = "Inicio de sesión de personal", description = "Autentica al personal con email y contraseña, retornando un JWT con rol")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse respuesta = usuarioService.autenticar(request);
        return ResponseEntity.ok(respuesta);
    }
}
