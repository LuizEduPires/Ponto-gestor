package com.mobile.pontoGestao.Services;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.mobile.pontoGestao.Dtos.Request.*;
import com.mobile.pontoGestao.Dtos.Response.UsuarioLogin;
import com.mobile.pontoGestao.Dtos.Response.UsuarioResponse;
import com.mobile.pontoGestao.Erros.EntityAlreadyExistsException;
import com.mobile.pontoGestao.Erros.EntityNotFoundException;
import com.mobile.pontoGestao.Erros.LoginInvalidException;
import com.mobile.pontoGestao.Erros.UnauthorizedException;
import com.mobile.pontoGestao.Infra.TokenService;
import com.mobile.pontoGestao.Mappers.UsuarioMapper;
import com.mobile.pontoGestao.Models.Usuarios;
import lombok.RequiredArgsConstructor;
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

        if ("FUNCIONARIO".equals(usuario.getPermissao()) && update.permissao() != null) {
            if ("ADMIN".equals(update.permissao())) {
                throw new UnauthorizedException("Funcionários não podem se promover a Administrador.");
            }
        }

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

        QuerySnapshot snapshot = getUsuarioById(id).get();

        Usuarios usuario = convertDocumentToUsuario(snapshot);

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

    public void deletarUsuario(
            String id,
            SenhaRequest request
    ) throws ExecutionException, InterruptedException {

        Usuarios usuarioLogado = getUsuarioAutenticado();

        if (!passwordEncoder.matches(
                request.senha(),
                usuarioLogado.getSenha()
        )) {
            throw new LoginInvalidException(
                    "Não foi possível validar a requisição"
            );
        }

        QuerySnapshot snapshot = getUsuarioById(id).get();

        if (snapshot.isEmpty()) {
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

        QuerySnapshot snapshot = getUsuarioById(id).get();

        return convertDocumentToUsuario(snapshot);
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

    private ApiFuture<QuerySnapshot> getUsuarioById(
            String id
    ) {

        return firestore.collection("usuarios")
                .whereEqualTo("id", id)
                .get();
    }
}