package cl.duocuc.autenticacion.controller;

import cl.duocuc.autenticacion.dto.LoginRequest;
import cl.duocuc.autenticacion.dto.RegistroRequest;
import cl.duocuc.autenticacion.dto.TokenResponse;
import cl.duocuc.autenticacion.dto.UsuarioResponse;
import cl.duocuc.autenticacion.service.UsuarioService;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/** Endpoints publicos: registro y login. */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UsuarioService service;

    public AuthController(UsuarioService service) {
        this.service = service;
    }

    // POST /api/v1/auth/registro -> 201 Created
    @PostMapping("/registro")
    public ResponseEntity<UsuarioResponse> registrar(@Valid @RequestBody RegistroRequest req) {
        UsuarioResponse creado = service.registrar(req);
        return ResponseEntity.created(URI.create("/api/v1/usuarios/" + creado.id())).body(creado);
    }

    // POST /api/v1/auth/login -> devuelve el token JWT
    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest req) {
        return service.login(req);
    }
}
