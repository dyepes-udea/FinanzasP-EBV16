package com.finanzas.dto;

public class ActualizarFotoUsuarioDTO {

    private String fotoUrl;

    public ActualizarFotoUsuarioDTO() {}

    public ActualizarFotoUsuarioDTO(String fotoUrl) {
        this.fotoUrl = fotoUrl;
    }

    public String getFotoUrl() { return fotoUrl; }
    public void setFotoUrl(String fotoUrl) { this.fotoUrl = fotoUrl; }
}
