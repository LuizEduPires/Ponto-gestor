package com.mobile.pontoGestao.Services;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    public UsuarioResponse criarUsuario(UsuarioRequest request) throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = getUsuarioByEmail(request.email());

        QuerySnapshot snapshot = future.get();

        if (!snapshot.isEmpty()) throw new EntityAlreadyExistsException("Email já está sendo utilizado.");

        Usuarios usuario = usuarioMapper.toModel(request);
        String novaSenha = passwordEncoder.encode(usuario.getSenha());
        usuario.setSenha(novaSenha);
        firestore.collection("usuarios").document(usuario.getId()).set(usuario);

        return usuarioMapper.toResponse(usuario);
    }

    public UsuarioToken criarToken(UsuarioLogin login) throws ExecutionException, InterruptedException, FirebaseAuthException {
        ApiFuture<QuerySnapshot> future = getUsuarioByEmail(login.email());

        QuerySnapshot snapshot = future.get();

        if (snapshot.isEmpty()) throw new LoginInvalidException("Email ou senha inválidos.");

        Usuarios usuario = convertDocumentToUsuario(snapshot);

        if (!passwordEncoder.matches(login.senha(), usuario.getSenha()))  throw new LoginInvalidException("Email ou senha inválidos.");

        String token = tokenService.generateToken(usuario);

        return new UsuarioToken(token);
    }

    public UsuarioResponse atualizarSenha(SenhaRequest senha) throws ExecutionException, InterruptedException {
        String id = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal().toString();
        ApiFuture<QuerySnapshot> future = getUsuarioById(id);
        QuerySnapshot snapshot = future.get();
        Usuarios usuario = convertDocumentToUsuario(snapshot);

        String novaSenha = passwordEncoder.encode(senha.senha());
        usuario.setSenha(novaSenha);

        firestore.collection("usuarios").document(usuario.getId()).set(usuario);

        return usuarioMapper.toResponse(usuario);
    }

    public UsuarioResponse atualizarUsuario(UsuarioUpdate update) throws ExecutionException, InterruptedException {
        String id = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal().toString();
        ApiFuture<QuerySnapshot> future = getUsuarioById(id);
        QuerySnapshot snapshot = future.get();
        Usuarios usuario = convertDocumentToUsuario(snapshot);

        if (update.email() != null) {
            future = getUsuarioByEmail(update.email());
            snapshot = future.get();
            if (!snapshot.isEmpty()) throw new EntityAlreadyExistsException("Email já está sendo utilizado.");
        }

        usuarioMapper.updateUsuario(update, usuario);

        firestore.collection("usuarios").document(usuario.getId()).set(usuario);

        return usuarioMapper.toResponse(usuario);
    }

    private static Usuarios convertDocumentToUsuario(QuerySnapshot snapshot) {
        if (snapshot.isEmpty()) throw new EntityNotFoundException("Não foi possível encontrar o usuário");
        return snapshot.getDocuments().getFirst().toObject(Usuarios.class);
    }

    private ApiFuture<QuerySnapshot> getUsuarioByEmail(String login) {
        return firestore.collection("usuarios")
                .whereEqualTo("email", login)
                .get();
    }

    public UsuarioResponse getUsuario(String id) throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = getUsuarioById(id);
        QuerySnapshot snapshot = future.get();
        Usuarios usuario = convertDocumentToUsuario(snapshot);
        return usuarioMapper.toResponse(usuario);
    }

    public List<UsuarioResponse> getUsuarios() throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = firestore.collection("usuarios").get();
        QuerySnapshot snapshot = future.get();
        return snapshot.getDocuments()
                .stream()
                .map(doc -> doc.toObject(Usuarios.class))
                .map(usuarioMapper::toResponse)
                .toList();
    }

    private ApiFuture<QuerySnapshot> getUsuarioById(String id) {
        return firestore.collection("usuarios")
                .whereEqualTo("id", id)
                .get();
    }

    public void deletarUsuario(String id) {
        firestore.collection("usuarios").document(id).delete();
    }
}
