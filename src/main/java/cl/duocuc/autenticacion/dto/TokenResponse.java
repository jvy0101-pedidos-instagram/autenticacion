package cl.duocuc.autenticacion.dto;

public record TokenResponse(String token, String tipo, String username, String rol) {
}
