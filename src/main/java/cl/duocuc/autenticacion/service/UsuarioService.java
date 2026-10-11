package cl.duocuc.autenticacion.service;

import cl.duocuc.autenticacion.dto.LoginRequest;
import cl.duocuc.autenticacion.dto.RegistroRequest;
import cl.duocuc.autenticacion.dto.TokenResponse;
import cl.duocuc.autenticacion.dto.UsuarioResponse;
import cl.duocuc.autenticacion.exception.RecursoNoEncontradoException;
import cl.duocuc.autenticacion.exception.ReglaNegocioException;
import cl.duocuc.autenticacion.model.Rol;
import cl.duocuc.autenticacion.model.Usuario;
import cl.duocuc.autenticacion.repository.UsuarioRepository;
import cl.duocuc.autenticacion.security.JwtService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UsuarioService {

    private final UsuarioRepository repository;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;

    public UsuarioService(UsuarioRepository repository, PasswordEncoder encoder, JwtService jwtService) {
        this.repository = repository;
        this.encoder = encoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public UsuarioResponse registrar(RegistroRequest req) {
        if (repository.existsByUsername(req.username())) {
            throw new ReglaNegocioException("El username " + req.username() + " ya esta en uso");
        }
        Usuario u = new Usuario();
        u.setUsername(req.username());
        u.setPassword(encoder.encode(req.password()));
        u.setEmail(req.email());
        u.setRol(req.rol() != null ? req.rol() : Rol.VENDEDOR);
        return UsuarioResponse.desde(repository.save(u));
    }

    public TokenResponse login(LoginRequest req) {
        Usuario u = repository.findByUsername(req.username())
                .filter(x -> encoder.matches(req.password(), x.getPassword()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales invalidas"));
        if (!u.getActivo()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario desactivado");
        }
        String token = jwtService.generarToken(u.getUsername(), u.getRol().name());
        return new TokenResponse(token, "Bearer", u.getUsername(), u.getRol().name());
    }

    public List<UsuarioResponse> listar() {
        return repository.findAll().stream().map(UsuarioResponse::desde).toList();
    }

    public UsuarioResponse buscarPorId(Long id) {
        return UsuarioResponse.desde(obtener(id));
    }

    @Transactional
    public UsuarioResponse actualizar(Long id, RegistroRequest req) {
        Usuario u = obtener(id);
        if (repository.existsByUsernameAndIdNot(req.username(), id)) {
            throw new ReglaNegocioException("El username " + req.username() + " ya esta en uso");
        }
        u.setUsername(req.username());
        u.setPassword(encoder.encode(req.password()));
        u.setEmail(req.email());
        if (req.rol() != null) u.setRol(req.rol());
        return UsuarioResponse.desde(repository.save(u));
    }

    @Transactional
    public void eliminar(Long id) {
        repository.delete(obtener(id));
    }

    private Usuario obtener(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe usuario con id " + id));
    }
}
