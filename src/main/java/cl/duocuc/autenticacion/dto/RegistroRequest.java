package cl.duocuc.autenticacion.dto;

import cl.duocuc.autenticacion.model.Rol;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Datos para registrar o actualizar un usuario. El rol es opcional (VENDEDOR por defecto). */
public record RegistroRequest(
        @NotBlank(message = "El username es obligatorio") String username,
        @NotBlank(message = "La password es obligatoria")
        @Size(min = 4, message = "La password debe tener al menos 4 caracteres") String password,
        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email no es valido") String email,
        Rol rol) {
}
