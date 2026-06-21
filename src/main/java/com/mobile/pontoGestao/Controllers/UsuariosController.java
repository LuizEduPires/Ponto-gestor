package com.mobile.pontoGestao.Controllers;

import com.mobile.pontoGestao.Dtos.Request.SenhaRequest;
import com.mobile.pontoGestao.Dtos.Request.UsuarioRequest;
import com.mobile.pontoGestao.Dtos.Request.UsuarioToken;
import com.mobile.pontoGestao.Dtos.Request.UsuarioUpdate;
import com.mobile.pontoGestao.Dtos.Request.ValidarSenhaRequest;
import com.mobile.pontoGestao.Dtos.Response.UsuarioLogin;
import com.mobile.pontoGestao.Dtos.Response.UsuarioResponse;
import com.mobile.pontoGestao.Dtos.Response.ValidarSenhaResponse;
import com.mobile.pontoGestao.Services.UsuariosService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public UsuarioResponse criarUsuario(
            @RequestBody @Valid UsuarioRequest request
    ) throws ExecutionException, InterruptedException {

        return service.criarUsuario(request);
    }

    @PostMapping("/login")
    public UsuarioToken login(
            @RequestBody @Valid UsuarioLogin login
    ) throws ExecutionException, InterruptedException {

        return service.criarToken(login);
    }

    @GetMapping("/{id}")
    public UsuarioResponse getUsuario(
            @PathVariable String id
    ) throws ExecutionException, InterruptedException {

        return service.getUsuario(id);
    }

    @GetMapping
    public List<UsuarioResponse> getUsuarios()
            throws ExecutionException, InterruptedException {

        return service.getUsuarios();
    }

    @PatchMapping
    public UsuarioResponse updateUsuario(
            @RequestBody @Valid UsuarioUpdate request
    ) throws ExecutionException, InterruptedException {

        return service.atualizarUsuario(request);
    }

    @PatchMapping("/atualizar/senha")
    public UsuarioResponse updateSenha(
            @RequestBody @Valid SenhaRequest request
    ) throws ExecutionException, InterruptedException {

        return service.atualizarSenha(request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletarUsuario(
            @PathVariable String id
    ) throws ExecutionException, InterruptedException {

        service.deletarUsuario(id);
    }

    @PostMapping("/validar-senha")
    public ResponseEntity<ValidarSenhaResponse> validarSenha(
            @RequestBody @Valid ValidarSenhaRequest request
    ) {
        boolean isValido = service.validarSenha(request);
        
        return ResponseEntity.ok(new ValidarSenhaResponse(isValido));
    }
}