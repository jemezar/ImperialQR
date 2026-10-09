package com.imperial.qr.service;

import com.imperial.qr.dto.request.LoginRequest;
import com.imperial.qr.dto.request.UsuarioRequest;
import com.imperial.qr.dto.response.LoginResponse;
import com.imperial.qr.dto.response.UsuarioResponse;

import java.util.List;

public interface UsuarioService {
    LoginResponse autenticar(LoginRequest request);
    List<UsuarioResponse> listarTodos();
    UsuarioResponse crear(UsuarioRequest request);
    UsuarioResponse actualizar(Long id, UsuarioRequest request);
    void eliminar(Long id);
}
