package com.finanzas.dto;

public class ActualizarNombreUsuarioDTO {

    private String nombre;

    public ActualizarNombreUsuarioDTO() {}

    public ActualizarNombreUsuarioDTO(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
}
