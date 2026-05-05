package com.finanzas.dto;

import jakarta.validation.constraints.NotBlank;
import com.finanzas.entity.TipoCategoria;

public class ActualizarCategoriaDTO {

    @NotBlank(message = "El nombre no puede ser vacío")
    private String nombre;

    private String descripcion;

    private TipoCategoria tipo;

    public ActualizarCategoriaDTO() {}

    public ActualizarCategoriaDTO(String nombre, String descripcion, TipoCategoria tipo) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.tipo = tipo;
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public TipoCategoria getTipo() { return tipo; }
    public void setTipo(TipoCategoria tipo) { this.tipo = tipo; }
}
