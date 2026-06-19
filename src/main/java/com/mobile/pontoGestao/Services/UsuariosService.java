package com.mobile.pontoGestao.Services;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.mobile.pontoGestao.Dtos.Request.*;
import com.mobile.pontoGestao.Dtos.Response.UsuarioLogin;
import com.mobile.pontoGestao.Dtos.Response.UsuarioResponse;
import com.mobile.pontoGestao.Erros.EntityAlreadyExistsException;
import com.mobile.pontoGestao.Erros.EntityNotFoundException;
import com.mobile.pontoGestao.Erros.LoginInvalidException;
import com.mobile.pontoGestao.Infra.TokenService;
import com.mobile.pontoGestao.Mappers.UsuarioMapper;
import com.mobile.pontoGestao.Models.Usuarios;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class UsuariosService {

    private final Firestore firestore;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public UsuarioResponse criarUsuario(UsuarioRequest request)
            throws ExecutionException, InterruptedException {

        QuerySnapshot snapshot = getUsuarioByEmail(
                request.email()
        ).get();

        if (!snapshot.isEmpty()) {
            throw new EntityAlreadyExistsException(
                    "Email já está sendo utilizado."
            );
        }

        Usuarios usuario = usuarioMapper.toModel(request);

        usuario.setSenha(
                passwordEncoder.encode(usuario.getSenha())
        );

        firestore.collection("usuarios")
                .document(usuario.getId())
                .set(usuario);

        return usuarioMapper.toResponse(usuario);
    }

    public UsuarioToken criarToken(UsuarioLogin login)
        throws ExecutionException, InterruptedException {

        QuerySnapshot loginSnapshot = getUsuarioByEmail(login.email()).get();

        if (loginSnapshot.isEmpty()) {
                throw new LoginInvalidException("Email ou senha inválidos.");
        }

        Usuarios usuario = convertDocumentToUsuario(loginSnapshot);

        if (!passwordEncoder.matches(login.senha(), usuario.getSenha())) {
                throw new LoginInvalidException("Email ou senha inválidos.");
        }

        String token = tokenService.generateToken(usuario);

        return new UsuarioToken(token);
    }

    public Boolean validarSenha(ValidarSenhaRequest request) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        String id = (principal instanceof UserDetails userDetails) 
                ? userDetails.getUsername() 
                : principal.toString();

        Usuarios usuario = obterUsuarioPorIdDirect(id);

        if (!passwordEncoder.matches(request.senha(), usuario.getSenha())) {
                return false; 
        }

        return true;
    }

    public UsuarioResponse atualizarSenha(SenhaRequest senha)
            throws ExecutionException, InterruptedException {

        Usuarios usuario = getUsuarioAutenticado();

        usuario.setSenha(
                passwordEncoder.encode(senha.senha())
        );

        firestore.collection("usuarios")
                .document(usuario.getId())
                .set(usuario);

        return usuarioMapper.toResponse(usuario);
    }

    public UsuarioResponse atualizarUsuario(UsuarioUpdate update)
            throws ExecutionException, InterruptedException {

        Usuarios usuario = getUsuarioAutenticado();

        if (update.email() != null
                && !update.email().equals(usuario.getEmail())) {

            QuerySnapshot snapshot = getUsuarioByEmail(
                    update.email()
            ).get();

            if (!snapshot.isEmpty()) {
                throw new EntityAlreadyExistsException(
                        "Email já está sendo utilizado."
                );
            }
        }

        usuarioMapper.updateUsuario(update, usuario);

        firestore.collection("usuarios")
                .document(usuario.getId())
                .set(usuario);

        return usuarioMapper.toResponse(usuario);
    }

    public UsuarioResponse getUsuario(String id)
            throws ExecutionException, InterruptedException {

        DocumentSnapshot document = firestore.collection("usuarios").document(id).get().get();

        if (!document.exists()) {
            throw new EntityNotFoundException("Não foi possível encontrar o usuário");
        }

        Usuarios usuario = document.toObject(Usuarios.class);
        return usuarioMapper.toResponse(usuario);
    }

    public List<UsuarioResponse> getUsuarios()
            throws ExecutionException, InterruptedException {

        QuerySnapshot snapshot = firestore
                .collection("usuarios")
                .get()
                .get();

        return snapshot.getDocuments()
                .stream()
                .map(doc -> doc.toObject(Usuarios.class))
                .map(usuarioMapper::toResponse)
                .toList();
    }

    public void deletarUsuario(String id) throws ExecutionException, InterruptedException {
        DocumentSnapshot document = firestore.collection("usuarios").document(id).get().get();

        if (!document.exists()) {
            throw new EntityNotFoundException(
                    "Não foi possível encontrar o usuário"
            );
        }

        firestore.collection("usuarios")
            .document(id)
            .delete();
    }

    private Usuarios getUsuarioAutenticado()
            throws ExecutionException, InterruptedException {

        String id = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal()
                .toString();

        DocumentSnapshot document = firestore.collection("usuarios").document(id).get().get();

        if (!document.exists()) {
            throw new EntityNotFoundException("Não foi possível encontrar o usuário");
        }

        return document.toObject(Usuarios.class);
    }

    private static Usuarios convertDocumentToUsuario(
            QuerySnapshot snapshot
    ) {

        if (snapshot.isEmpty()) {
            throw new EntityNotFoundException(
                    "Não foi possível encontrar o usuário"
            );
        }

        return snapshot.getDocuments()
                .getFirst()
                .toObject(Usuarios.class);
    }

    private ApiFuture<QuerySnapshot> getUsuarioByEmail(
            String email
    ) {

        return firestore.collection("usuarios")
                .whereEqualTo("email", email)
                .get();
    }

    private Usuarios obterUsuarioPorIdDirect(String idUsuario) {
        try {
            DocumentSnapshot document = firestore.collection("usuarios").document(idUsuario).get().get();
            if (document.exists()) {
                return document.toObject(Usuarios.class);
            } else {
                throw new EntityNotFoundException("Usuário não encontrado com o ID: " + idUsuario);
            }
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Erro ao buscar usuário no Firestore", e);
        }
    }
}
