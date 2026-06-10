package com.mobile.pontoGestao.Controllers;

import com.google.firebase.auth.FirebaseAuthException;
import com.mobile.pontoGestao.Dtos.Request.UsuarioRequest;
import com.mobile.pontoGestao.Dtos.Request.UsuarioToken;
import com.mobile.pontoGestao.Dtos.Request.UsuarioUpdate;
import com.mobile.pontoGestao.Dtos.Response.UsuarioLogin;
import com.mobile.pontoGestao.Dtos.Response.UsuarioResponse;
import com.mobile.pontoGestao.Services.UsuariosService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/usuarios")
public class UsuariosController {
    private final UsuariosService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponse criarUsuario(@RequestBody @Valid UsuarioRequest usuarioRequest) throws ExecutionException, InterruptedException {
        return service.criarUsuario(usuarioRequest);
    }

    @PostMapping("/login")
    public UsuarioToken login(@RequestBody @Valid UsuarioLogin login) throws ExecutionException, InterruptedException, FirebaseAuthException {
        return service.criarToken(login);
    }

    @GetMapping("/{id}")
    public UsuarioResponse getUsuario(@PathVariable String id) throws ExecutionException, InterruptedException{
        return service.getUsuario(id);
    }

    @PatchMapping
    public UsuarioResponse updateUsuario(@RequestBody @Valid UsuarioUpdate usuarioUpdate) throws ExecutionException, InterruptedException {
        return service.atualizarUsuario(usuarioUpdate);
    }

    @GetMapping
    public List<UsuarioResponse> getUsuarios() throws ExecutionException, InterruptedException {
        return service.getUsuarios();
    }
}
