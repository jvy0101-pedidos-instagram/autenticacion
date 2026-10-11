package cl.duocuc.autenticacion.dto;

import cl.duocuc.autenticacion.model.Rol;
import cl.duocuc.autenticacion.model.Usuario;
import java.time.LocalDateTime;

/** Respuesta sin la password: nunca se devuelve al cliente. */
public record UsuarioResponse(Long id, String username, String email, Rol rol,
                              Boolean activo, LocalDateTime fechaCreacion) {

    public static UsuarioResponse desde(Usuario u) {
        return new UsuarioResponse(u.getId(), u.getUsername(), u.getEmail(), u.getRol(),
                u.getActivo(), u.getFechaCreacion());
    }
}
